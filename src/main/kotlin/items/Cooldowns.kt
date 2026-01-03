package demo.items

import demo.game.getProfile
import net.minestom.server.entity.Player

class Cooldown (
    val id: String,
    val duration: Int,
    val player: Player
) {
    var dura = duration

    fun tick() {
        dura -= 1
        if (dura == 0) {
            player.getProfile().activecooldowns -= this
        }
    }
}