package demo

import demo.command.*
import demo.entities.GameEntity
import demo.events.*
import demo.game.GameInstance
import demo.game.GameUser
import demo.npcs.NPC
import demo.npcs.NPCs
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.entity.EntityTickEvent
import net.minestom.server.event.item.ItemUsageCompleteEvent
import net.minestom.server.event.item.PickupItemEvent
import net.minestom.server.event.player.*
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import noise.FastNoiseLite
import java.util.*

fun getNoise(noise: FastNoiseLite, x: Double, y: Double): Float {
    val gen = noise.GetNoise(x.toFloat(), y.toFloat())

    return (gen*0.5+0.5).toFloat()
}

var gameUserToPlayer: MutableMap<UUID, GameUser> = mutableMapOf()

var puppetToGameEntity: MutableMap<UUID, GameEntity> = mutableMapOf()

var activeGames: MutableSet<GameInstance> = mutableSetOf()

var activeNpcs: MutableSet<NPC> = mutableSetOf()

val minecraftServer: MinecraftServer = MinecraftServer.init();
val instanceManager = MinecraftServer.getInstanceManager();
val lobbyInstance = instanceManager.createInstanceContainer();

fun main() {
    lobbyInstance.setChunkSupplier(::LightingChunk);

    lobbyInstance.time = 18000L

    lobbyInstance.timeRate = 0

    lobbyInstance.chunkLoader = AnvilLoader("/home/zaikawo/.local/share/PrismLauncher/instances/nya meow meow nya/.minecraft/saves/dungeon_lobby_world")

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        val player = event.player
        event.spawningInstance = lobbyInstance
        player.gameMode = GameMode.CREATIVE
        player.respawnPoint = Pos(40.5, 38.0, 26.0, -90f, 0f)
    }

    for (z in setOf(0, 1, 2)) {
        for (x in setOf(1, 2, 3, 4, 5)) {
            lobbyInstance.loadChunk(x, z)
        }
    }

    NPC(NPCs.CLERIC, Pos(57.5, 38.0, 38.5, -172f, 0f))

    NPC(NPCs.BARTENDER, Pos(77.0, 38.5, 24.5, 169f, 7f))

    NPC(NPCs.BLACKSMITH, Pos(44.5, 38.0, 32.5, -84f, 25f))

    NPC(NPCs.FLORIST, Pos(36.0, 38.0, 15.8, -86f, 2f))

    NPC(NPCs.GARDENER, Pos(49.5, 38.0, 18.7, -121f, 44f))

    NPC(NPCs.CRYPTKEEPER, Pos(59.7, 38.0, 11.2, 75.7f, 14.4f))

    globalEventHandler.addListener(PlayerChatEvent::class.java) { event ->
        chatEvent(event)
    }

    globalEventHandler.addListener(PlayerBlockBreakEvent::class.java) { event ->
        breakBlockEvent(event)
    }

    globalEventHandler.addListener(PlayerBlockInteractEvent::class.java) { event ->
        blockInteractEvent(event)
    }

    globalEventHandler.addListener(PlayerUseItemEvent::class.java) { event ->
        useItemEvent(event)
    }

    globalEventHandler.addListener(PlayerStartDiggingEvent::class.java) { event ->
        startDiggingEvent(event)
    }

    globalEventHandler.addListener(ItemUsageCompleteEvent::class.java) { event ->
        consumeEvent(event)
    }

    globalEventHandler.addListener(PlayerSpawnEvent::class.java) { event ->
        spawnEvent(event)
    }

    globalEventHandler.addListener(EntityTickEvent::class.java) { event ->
        entityTickEvent(event)
    }

    globalEventHandler.addListener(EntityAttackEvent::class.java) { event ->
        entityAttackEvent(event)
    }

    globalEventHandler.addListener(PickupItemEvent::class.java) { event ->
        pickupItemEvent(event)
    }

    globalEventHandler.addListener(PlayerTickEvent::class.java) { event ->
        tickEvent(event)
    }

    globalEventHandler.addListener(PlayerEntityInteractEvent::class.java) { event ->
        playerInteractEvent(event)
    }

    val commandManager = MinecraftServer.getCommandManager()
    commandManager.register(getCommand())
    commandManager.register(gamemodeCommand())
    commandManager.register(getStatCommand())
    commandManager.register(registerCommand())
    commandManager.register(startGameCommand())
    commandManager.register(spawnEntityCommand())
    commandManager.register(dungeonTestCommand())
    commandManager.register(schemPlaceTest())
    commandManager.register(effectTest())
    commandManager.register(soundTestCommand())
    commandManager.register(disbandGameCommand())
    commandManager.register(npcTest())
    commandManager.register(killYourself())

    MojangAuth.init()

    minecraftServer.start("0.0.0.0", 25565);
}