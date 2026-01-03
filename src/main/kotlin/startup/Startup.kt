package demo.startup

import demo.dungeons.Dungeon
import demo.dungeons.DungeonSchem
import demo.game.GameInstance
import demo.game.getProfile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import java.lang.Thread.sleep

fun startup(player: Player) {
    runBlocking {
        launch {
            player.sendMessage("Building lobby structures..")
            player.gameMode = GameMode.CREATIVE
            player.addEffect(Potion(PotionEffect.BLINDNESS, 100, 1000))
            player.teleport(Pos(1.0, 201.0, 1.0))
            println("it startid")
            sleep(3000L)
            println("it endid")
            val temp = GameInstance()
            Dungeon(Pos(0.0, 200.0, 1.0), DungeonSchem.LOBBY, temp).build()
            temp.disband()
            player.sendMessage("Building lobby structures finished.")
            player.clearEffects()
        }
    }
}