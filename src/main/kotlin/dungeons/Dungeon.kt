package demo.dungeons

import demo.entities.GameEntity
import demo.entities.vecBetween
import demo.game.GameInstance
import demo.registries.Infected
import net.hollowcube.schem.Rotation
import net.hollowcube.schem.SchematicReader
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.collision.Aerodynamics
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.InteractionMeta
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.tag.Tag
import java.lang.Thread.sleep
import kotlin.io.path.Path
import kotlin.reflect.KFunction0

fun IPos(x: Int, y: Int, z: Int): Pos {
    return Pos(x.toDouble(), y.toDouble(), z.toDouble())
}

data class DungeonEnemy(
    val position: Pos,
    val type: KFunction0<GameEntity>
)

enum class DungeonSchem (
    val filename: String,
    val origin: Pos,
    val start: Pos,
    val end: Pos,
    vararg val enemies: DungeonEnemy,
    val usable: Boolean = true
) {
    TEST(
        "testdungeon",
        IPos(154, -61, 82),
        IPos(173, -58, 91),
        IPos(154, -60, 92),
        DungeonEnemy(
            IPos(162, -60, 85),
            ::Infected
        ),
        DungeonEnemy(
            IPos(161, -60, 92),
            ::Infected
        )
    ),
    TEST2(
        "testdungeon2",
        IPos(153, -61, 67),
        IPos(167, -60, 73),
        IPos(153, -57, 69)
    ),
    ENTRANCE(
        "entrance",
        IPos(163, -61, 101),
        IPos(170, -29, 106),
        IPos(163, -60, 106),
        usable=false
    ),
    FOUNTAIN(
        "fountain",
        IPos(153, -61, 46),
        IPos(165, -60, 52),
        IPos(153, -60, 52)
    ),
    LOBBY(
        "lobby",
        IPos(115, -64, 66),
        IPos(127, -49, 67),
        IPos(127, -49, 67),
        usable=false
    )
}

class Dungeon (
    val start: Pos,
    val type: DungeonSchem,
    val game: GameInstance
) {
    val instance = game.instance

    val startVec: Vec = vecBetween(type.origin, type.start)
    val endVec: Vec = vecBetween(type.origin, type.end)

    val startToEndVec: Vec = vecBetween(startVec.asPosition(), endVec.asPosition())

    val end: Pos = start.add(startToEndVec)

    fun build() {
        val schempath = Path("/home/zaikawo/IdeaProjects/suckingdick/schematics/${type.filename}.schem")

        val schem = SchematicReader().read(schempath)

        val placeloc = start.sub(startVec)

        schem.build(Rotation.NONE, false).apply(instance, placeloc) {
            for (enemy in type.enemies) {
                val eVec = vecBetween(type.start, enemy.position)

                val eLoc = start.add(eVec)

                game.createEntity(enemy.type(), eLoc)
            }
        }
    }

    fun close() {
        instance.setBlock(start, Block.SPRUCE_DOOR.withProperty("facing", "east").withProperty("half", "lower"))
        instance.setBlock(start.add(0.0, 1.0, 0.0), Block.SPRUCE_DOOR.withProperty("facing", "east").withProperty("half", "upper"))
    }
}

class DungeonGenerator (
    val startloc: Pos,
    val instance: Instance,
    val game: GameInstance
) {
    var pastdungeon: Dungeon? = null
    var currentdungeon: Dungeon? = null
    var doorinter: Entity? = null

    fun createDoor() {
        doorinter?.remove()

        sleep(100L)

        val end = currentdungeon!!.end

        instance.setBlock(end, Block.SPRUCE_DOOR.withProperty("facing", "west").withProperty("half", "lower"))
        instance.setBlock(end.add(0.0, 1.0, 0.0), Block.SPRUCE_DOOR.withProperty("facing", "west").withProperty("half", "upper"))

        doorinter = Entity(EntityType.INTERACTION)
        (doorinter?.entityMeta as InteractionMeta).height = 2.0f
        (doorinter?.entityMeta as InteractionMeta).width = 1.0f
        doorinter?.setTag(Tag.String("isdoor"), "yeah")
        doorinter?.aerodynamics = Aerodynamics(0.0, 0.0, 0.0)
        doorinter?.setInstance(instance, currentdungeon!!.end.add(0.6, 0.0, 0.5))

        val open = Sound.sound(Key.key("block.ender_chest.open"), Sound.Source.BLOCK, 1f, 0.6f)

        game.audience.playSound(open)

        if (pastdungeon != null) {
            val nend = pastdungeon!!.end
            instance.setBlock(nend, Block.SPRUCE_DOOR.withProperty("facing", "west").withProperty("half", "lower").withProperty("open", "true"))
            instance.setBlock(nend.add(0.0, 1.0, 0.0), Block.SPRUCE_DOOR.withProperty("facing", "west").withProperty("half", "upper").withProperty("open", "true"))
        }
    }

    fun genDungeon() {
        pastdungeon?.close()
        pastdungeon = currentdungeon
        var loc = startloc
        var rng = DungeonSchem.ENTRANCE
        if (currentdungeon != null) {
            loc = currentdungeon!!.end.add(-1.0, 0.0, 0.0)
            while (!rng.usable) {
                rng = DungeonSchem.entries.random()
                println(rng.filename)
            }
        }

        println(rng)

        currentdungeon = Dungeon(loc, rng, game)
        currentdungeon?.build()
        createDoor()
    }
}