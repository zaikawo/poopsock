package demo.events

import demo.game.getProfile
import demo.items.Cooldown
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import java.lang.Thread.sleep
import java.time.Duration
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

fun Int.javaMilliseconds(): Duration {
    return Duration.ofMillis(this.toLong())!!
}

fun Player.setCooldown(item: ItemStack, ticks: Int) {
    this.getProfile().activecooldowns += Cooldown(item.getTag(Tag.String("id")), ticks, this)
}

val mm = MiniMessage.miniMessage()

fun Player.hasCooldown(item: ItemStack): Boolean {
    var has = false
    val id = item.getTag(Tag.String("id"))

    for (cd in this.getProfile().activecooldowns) {
        if (id == cd.id) {
            has = true
            break
        }
    }

    return has
}

val itemEvents = mapOf<String, (ItemStack, Player) -> Unit>(
    "heal" to fun (item: ItemStack, player: Player) {
        val health = item.getTag(Tag.String("health"))
        player.sendMessage("you got healed for $health")
    },

    "cointoss" to fun (item: ItemStack, player: Player) {
        if (player.hasCooldown(item)) {
            player.sendMessage("ay-yo, bro, this is on cooldown.")
        } else {
            val flipsound = Sound.sound(Key.key("item.armor.equip_chain"), Sound.Source.MASTER, 1f, 1.7f)
            val tailsound = Sound.sound(Key.key("entity.vex.hurt"), Sound.Source.MASTER, 1f, 0.7f)
            val headsound = Sound.sound(Key.key("entity.illusioner.cast_spell"), Sound.Source.MASTER, 1f, 1.2f)

            val headSym = "⏼"
            val tailSym = "⭘"
            val sideSym = "⏽"

            val faceCol = "#f5bf4c"
            val sideCol = "#e8962a"

            player.setCooldown(item, 20)
            player.swingMainHand()

            var time = 75
            var face = Random.nextBoolean()

            for (i in 0..15+Random.nextInt(5)) {
                face = !face
                time += 25
                val times = Title.Times.times(0.javaMilliseconds(), 1000.javaMilliseconds(), 0.javaMilliseconds())
                val top = mm.deserialize("")
                val sym = if (face) {
                    headSym
                } else {
                    tailSym
                }
                var bottom = mm.deserialize("<$faceCol>$sym")
                var title = Title.title(top, bottom, times)
                player.showTitle(title)
                player.playSound(flipsound)
                sleep((time/2).toLong())
                bottom = mm.deserialize("<$sideCol>$sideSym")
                title = Title.title(top, bottom, times)
                player.showTitle(title)
                player.playSound(flipsound)
                sleep((time/2).toLong())
            }

            sleep(1000L)

            val times = Title.Times.times(0.javaMilliseconds(), 1000.javaMilliseconds(), 0.javaMilliseconds())
            val top = mm.deserialize("")

            if (face) {
                //heads
                var bottom = mm.deserialize("<$faceCol>$headSym")
                var title = Title.title(top, bottom, times)
                player.showTitle(title)
                player.playSound(headsound)
            } else {
                //tails
                var bottom = mm.deserialize("<$faceCol>$headSym")
                var title = Title.title(top, bottom, times)
                player.showTitle(title)
                player.playSound(tailsound)
            }
        }
    }
)