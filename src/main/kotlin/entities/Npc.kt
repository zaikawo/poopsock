package demo.entities

import demo.item.getTextPillows
import demo.item.getTextPixelLen
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.entity.Player
import java.lang.Thread.sleep

fun Player.npcTalk(text: String) {
    val mm = MiniMessage.miniMessage()

    val full = "\" $text \""
    val len = getTextPixelLen(full)

    var curr = ""

    for (char in text) {
        curr += char

        val pillows = getTextPillows("    \" $curr \"", len+32)

        val show = "    <dark_gray>\" <white>$curr <dark_gray>\"${pillows[0]}${pillows[1]}"

        this.sendActionBar(mm.deserialize(show))

        sleep(50L)
    }
}