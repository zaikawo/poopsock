package demo.player

import demo.game.getProfile
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.entity.Player

val mm = MiniMessage.miniMessage()

fun Player.updateTablist() {
    val upper = mutableListOf<String>("", "<white>welcome to this shit", "")
    val lower = mutableListOf<String>("")

    val active = this.getProfile().activeeffects

    if (active.size == 0) {
        lower += "<white>No active effects."
    } else {
        lower += "<white>Active effects:"
        for (effect in active) {
            lower += "${effect.getString()} <red>${effect.getTime()}"
        }
    }

    this.sendPlayerListHeaderAndFooter(
        mm.deserialize(upper.joinToString("<newline>")),
        mm.deserialize(lower.joinToString("<newline>"))
    )
}