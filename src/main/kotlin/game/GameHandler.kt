package demo.game

import demo.*
import demo.chat.ChatTags
import demo.dungeons.DungeonGenerator
import demo.dungeons.NDungeonGenerator
import demo.effects.Effect
import demo.entities.GameEntity
import demo.events.javaMilliseconds
import demo.events.mm
import demo.items.Cooldown
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.EquipmentSlot
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.lang.Thread.sleep
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.TimeMark
import kotlin.time.TimeSource

val timesource = TimeSource.Monotonic

val fixedRanks = mapOf(
    "zaikawouwuu" to ChatTags.ADMIN,
    "rat7underscores" to ChatTags.NAME_PICKER,
    "awolen" to ChatTags.EMOTIONAL_SUPPORT
)

fun Player.registerProfile() {
    if (this.username == "zaikawouwuu") {
        this.inventory.setEquipment(EquipmentSlot.HELMET, ItemStack.of(Material.HEAVY_CORE))
    }

    val uuid: UUID = this.uuid
    val player = gameUserToPlayer[uuid]
    if (player != null) {
        player.player = this
    } else {
        val profile = GameUser(this, 0, null)
        if (this.username in fixedRanks.keys) {
            profile.tag = fixedRanks[this.username]!!
        }
        gameUserToPlayer[uuid] = profile
    }
}

fun Player.getProfile(): GameUser {
    return gameUserToPlayer[this.uuid]!!
}

fun Player.createGameInstance(): GameInstance {
    this.showTitle(Title.title(
        mm.deserialize("<font:spacing:spacing>f</font>"),
        mm.deserialize(""),
        Title.Times.times(1000.javaMilliseconds(), 100000.javaMilliseconds(), 0.javaMilliseconds())
    ))
    val game = GameInstance()
    game.addPlayer(this)
    return game
}

fun Player.inGame(): Boolean {
    return this.getProfile().game != null
}

data class GameUser (
    var player: Player? = null,
    var money: Int = 0,
    var game: GameInstance? = null,
    var lasthit: TimeMark = timesource.markNow(),
    var chests: MutableSet<Pos> = mutableSetOf(),
    val activeeffects: MutableSet<Effect> = mutableSetOf(),
    val activecooldowns: MutableSet<Cooldown> = mutableSetOf(),
    var tag: ChatTags = ChatTags.DEFAULT,
    var isindialogue: String? = null,
    var spokennpcs: MutableSet<String> = mutableSetOf()
)

class GameInstance () {
    var rooms: Int = 0
    val players: MutableSet<Player> = mutableSetOf()
    val entities: MutableSet<GameEntity> = mutableSetOf()
    var audience: Audience = Audience.empty()
    val instance: Instance = createInstance()
    val dungeon: NDungeonGenerator = NDungeonGenerator(Pos(0.0, 50.0, 0.0), this)

    init {
        activeGames += this

        println(instance)

        dungeon.build()
    }

    fun createInstance(): Instance {
        val instance: Instance = instanceManager.createInstanceContainer()
        println(instance)
        instance.setChunkSupplier(::LightingChunk);
        instance.time = 18000L
        instance.timeRate = 0

        instance.setGenerator { unit ->
            val start = unit.absoluteStart()
            for (x in 0..<unit.size().x().toInt()) {
                for (z in 0..<unit.size().z().toInt()) {
                    val block = start.add(x.toDouble(), 0.0, z.toDouble())
                    unit.modifier().fill(block, block.add(1.0, 20.0, 1.0), Block.MOSS_BLOCK)
                    unit.modifier().fill(block, block.add(1.0, 200.0, 1.0), Block.MOSS_BLOCK)
                }
            }
        }

        val chunkload = setOf(-2, -1, 0, 1)

        for (z in chunkload) {
            for (x in chunkload) {
                instance.loadChunk(x, z)
            }
        }

        return instance
    }

    fun advance() {
        dungeon.build()
        rooms += 1
    }

    private fun updateAudience() {
        audience = Audience.audience(players)
    }

    fun addPlayer(pl: Player) {
        this.players += pl
        pl.getProfile().game = this
        updateAudience()
        println(this.instance)
        pl.setInstance(this.instance, dungeon.currentstart)
    }

    fun remPlayer(pl: Player) {
        this.players -= pl
        pl.getProfile().game = null
        updateAudience()

        CoroutineScope(EmptyCoroutineContext).launch {
            pl.showTitle(Title.title(
                mm.deserialize("<font:spacing:spacing>f</font>"),
                mm.deserialize(""),
                Title.Times.times(1000.javaMilliseconds(), 100000.javaMilliseconds(), 0.javaMilliseconds())
            ))

            sleep(1200L)

            pl.setInstance(lobbyInstance, Pos(40.5, 38.0, 26.0, -90f, 0f))
        }
    }

    fun createEntity(en: GameEntity, loc: Pos) {
        this.addEntity(en)
        en.create(loc, this)
    }

    fun addEntity(en: GameEntity) {
        this.entities += en
    }

    fun remEntity(en: GameEntity) {
        this.entities -= en
        en.remove()
    }

    fun disband() {
        for (player in this.players) {
            this.remPlayer(player)
        }
        for (entity in this.entities) {
            this.remEntity(entity)
        }
        activeGames -= this
        instanceManager.unregisterInstance(instance)
    }
}