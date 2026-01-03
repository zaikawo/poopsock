package demo.dialogue

import demo.game.getProfile
import demo.npcs.NPCs
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.tag.Tag
import java.lang.Thread.sleep
import kotlin.math.round

class Dialogue (
    var pos: Pos,
    val player: Player,
    npc: NPCs
) {
    val mm = MiniMessage.miniMessage()
    val entity = Entity(EntityType.TEXT_DISPLAY)
    var stop: Boolean = false
    val interruptmsg = npc.interruptmsg
    val walkawaymsg = npc.walkawaymsg
    var walkaway = false
    var kill = false

    init {
        if (player.getProfile().isindialogue != null) {
            kill = true
        }

        player.getProfile().isindialogue = npc.id

        if (!kill) {
            pos = pos.withPitch(0f)
            pos = pos.withYaw(0f)

            entity.setNoGravity(true)
            entity.setTag(Tag.String("isdialogue"), "yas queen")

            entity.isAutoViewable = false

            (entity.entityMeta as TextDisplayMeta).billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
            (entity.entityMeta as TextDisplayMeta).scale = Vec(0.7, 0.7, 0.7)
            (entity.entityMeta as TextDisplayMeta).textOpacity = 0.toByte()
            (entity.entityMeta as TextDisplayMeta).isSeeThrough = true

            entity.setInstance(player.instance, pos)

            entity.addViewer(player)
        }
    }

    fun format(text: String): String {
        return "<dark_gray>“ <white><italic>$text <!italic><dark_gray>”"
    }

    fun puncWait(letter: String) {
        when (letter) {
            ".", "!", "?" -> sleep(500L)
            ",", "-" -> sleep(250L)
        }
    }

    fun choice(vararg texts: String): Choice {
        return Choice(this, *texts)
    }

    fun display(text: String) {
        if (stop || kill) {
            return
        }
        val letters = text.split("")
        var current = ""
        for (letter in letters) {
            current += letter

            (entity.entityMeta as TextDisplayMeta).text = mm.deserialize(format(current))

            if (player.getDistanceSquared(pos) > 25) {
                stop = true
                break
            }
            walkaway = false
            puncWait(letter)
            sleep(50L)
        }
        if (stop) {
            val interrupttext = if (walkaway) {
                walkawaymsg
            } else {
                interruptmsg
            }
            for (letter in interrupttext.split("")) {
                current += letter

                (entity.entityMeta as TextDisplayMeta).text = mm.deserialize(format(current))

                puncWait(letter)
                sleep(50L)
            }
            sleep(500L)
            this.destroy()
            return
        }
        walkaway = true
        sleep(500L)
    }

    fun destroy() {
        if (entity.isRemoved || kill) {
            return
        }
        player.getProfile().isindialogue = null
        for (n in 20 downTo 0) {
            val opacity = ((n.toDouble()/20)*255).toInt()
            val up = (20 - n).toDouble() / 25

            (entity.entityMeta as TextDisplayMeta).textOpacity = (opacity).toByte()
            entity.velocity = Vec(0.0, -up, 0.0)
            sleep(50L)
        }
        entity.teleport(Pos(0.0, 0.0, 0.0))
        entity.remove()
    }
}