package demo.dungeons

import demo.entities.GameEntity
import demo.entities.vecBetween
import demo.game.GameInstance
import demo.registries.Infected
import demo.registries.Rodent
import kotlinx.coroutines.runBlocking
import net.hollowcube.schem.Rotation
import net.hollowcube.schem.SchematicReader
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.InteractionMeta
import net.minestom.server.instance.block.Block
import net.minestom.server.tag.Tag
import kotlin.io.path.Path
import kotlin.math.PI
import kotlin.math.round
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction0

data class NDungeonEntity (
    val entity: KFunction0<GameEntity>,
    val loc: Pos
)

data class NDungeonExit (
    val loc: Pos,
    val rot: Int
)

data class NDungeon (
    val schem: String,
    val origin: Pos,
    val start: Pos,
    val exits: Array<NDungeonExit>,
    val entities: Array<NDungeonEntity>
)

fun Double.toDegrees(): Double {
    return this * (PI/180)
}

val rotationMap: Map<Int, Rotation> = mapOf(
    0 to Rotation.NONE,
    90 to Rotation.CLOCKWISE_90,
    180 to Rotation.CLOCKWISE_180,
    -90 to Rotation.CLOCKWISE_270
)


val publicDungeons: Map<String, NDungeon> = mapOf(
    "fountain" to NDungeon(
        "fountain",
        IPos(153, -61, 46),
        IPos(165, -60, 52),
        arrayOf(
            NDungeonExit(
                IPos(153, -60, 52),
                0
            )
        ),
        arrayOf()
    ),
    "one" to NDungeon(
        "one",
        IPos(154, -61, 82),
        IPos(173, -58, 91),
        arrayOf(
            NDungeonExit(
                IPos(154, -60, 92),
                0
            ),
            NDungeonExit(
                IPos(162, -60, 82),
                90
            )
        ),
        arrayOf(
            NDungeonEntity(
                ::Infected,
                IPos(162, -60, 85)
            ),
            NDungeonEntity(
                ::Infected,
                IPos(162, -60, 85)
            )
        )
    ),
    "two" to NDungeon(
        "testdungeon2",
        IPos(153, -61, 67),
        IPos(167, -60, 73),
        arrayOf(
            NDungeonExit(
                IPos(153, -57, 69),
                0
            )
        ),
        arrayOf(
            NDungeonEntity(
                ::Rodent,
                IPos(159, -59, 74)
            )
        )
    )
)

val privateDungeons: Map<String, NDungeon> = mapOf(
    "entrance" to NDungeon(
        "entrance",
        IPos(163, -61, 101),
        IPos(170, -30, 106),
        arrayOf(
            NDungeonExit(
                IPos(163, -60, 106),
                0
            )
        ),
        arrayOf()
    )
)

class NDungeonGenerator (
    val startloc: Pos,
    val gameinstance: GameInstance
) {
    val instance = gameinstance.instance
    var angle = 0
    var pastangle = 0
    var current: NDungeon = privateDungeons["entrance"]!!
    var currentstart = startloc.add(0.5, 0.0, 0.5)
    var currentend = startloc.add(0.5, 0.0, 0.5)
    val Upward = Vec(0.0, 1.0, 0.0)
    var currentdoor = startloc
    var doorentity = Entity(EntityType.INTERACTION)

    val door = Block.BARREL.withProperty("facing", "up")

    init {
        doorentity.setTag(Tag.Boolean("isdoor"), true)

        doorentity.setNoGravity(true)

        (doorentity.entityMeta as InteractionMeta).height = 2f
        (doorentity.entityMeta as InteractionMeta).width = 1.1f

        doorentity.setInstance(instance, startloc)
    }

    fun closedoor() {
        val bwd = Vec(1.0, 0.0, 0.0).rotateAroundAxis(Upward, -angle.toDouble().toDegrees())

        val end = currentend.add(bwd)

        instance.setBlock(currentstart, door)
        instance.setBlock(currentstart.add(0.0, 1.0, 0.0), door)

        val opendir = getdirection(fixAngle(angle+180))

        val opendoor = Block.AIR

        instance.setBlock(end, opendoor)
        instance.setBlock(end.add(0.0, 1.0, 0.0), opendoor)
    }

    fun movedoor() {
        val bwd = Vec(1.0, 0.0, 0.0).rotateAroundAxis(Upward, -angle.toDouble().toDegrees())

        val end = currentend.add(bwd)

        instance.setBlock(end, door)
        instance.setBlock(end.add(0.0, 1.0, 0.0), door)

        doorentity.teleport(end)
    }

    fun getdirection(angle: Int): String {
        when (angle) {
            90 -> {
                return "west"
            }

            180 -> {
                return "north"
            }

            -90 -> {
                return "east"
            }

            0 -> {
                return "south"
            }
        }

        return "north"
    }

    fun fixAngle(an: Int): Int {
        var a = an
        if (a > 180) {
            a -= 360
        }
        if (a < -180) {
            a += 360
        }
        a /= 90
        a = round(a.toDouble()).toInt()
        a *= 90
        return a
    }

    fun build() {
        println(instance)
        val exit = current.exits.random()

        var originVec = vecBetween(current.start, current.origin)
        var exitVec = vecBetween(current.start, exit.loc)

        val fillVecs = mutableListOf<Vec>()
        for (ex in current.exits) {
            if (ex != exit) {
                fillVecs += vecBetween(current.start, ex.loc)
            }
        }

        originVec = originVec.rotateAroundAxis(Upward, -angle.toDouble().toDegrees())
        exitVec = exitVec.rotateAroundAxis(Upward, -angle.toDouble().toDegrees())

        for (idx in 0..<fillVecs.size) {
            fillVecs[idx] = fillVecs[idx].rotateAroundAxis(Upward, -angle.toDouble().toDegrees())
        }

        val schempath = Path("/home/zaikawo/IdeaProjects/suckingdick/schematics/${current.schem}.schem")

        val schem = SchematicReader().read(schempath)

        val placeloc = currentend.add(originVec)

        angle = fixAngle(angle)

        println(rotationMap[angle]!!)

        closedoor()

        runBlocking {
            schem.build(rotationMap[angle]!!, false).apply(instance, placeloc) {
                println("dungon genated!!")

                fillVecs.forEach {
                    val fill = currentend.add(it)

                    instance.setBlock(fill, Block.STONE_BRICKS)
                    instance.setBlock(fill.add(0.0, 1.0, 0.0), Block.STONE_BRICKS)

                    println("filled exite")
                }

                current.entities.forEach {
                    var loc = vecBetween(current.start, it.loc)
                    loc = loc.rotateAroundAxis(Upward, -angle.toDouble().toDegrees())

                    gameinstance.createEntity(it.entity(), currentend.add(loc))
                }

                currentstart = currentend

                pastangle = angle
                angle += exit.rot
                val fwd = Vec(-1.0, 0.0, 0.0).rotateAroundAxis(Upward, -angle.toDouble().toDegrees())

                currentend = currentend.add(exitVec).add(fwd)

                movedoor()
                current = publicDungeons.values.random()
                println("Has it ended yet?")
            }
        }
    }
}