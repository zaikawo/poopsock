package demo.chests

import demo.game.getProfile
import demo.item.getItemFromId
import demo.item.toSmallCaps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.ItemEntity
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.BlockActionPacket
import java.lang.Thread.sleep
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

data class LootItem (
    val item: String,
    val weight: Int,
    val maxqty: Int
)

class Loottable (
    val items: Array<LootItem>
) {
    fun getTotalWeight(): Int {
        var weight = 0

        items.forEach {
            weight += it.weight
        }

        return weight
    }

    fun getLootItem(n: Int): LootItem {
        var reg = n

        items.forEach {
            if (reg < it.weight) {
                return it
            } else {
                reg -= it.weight
            }
        }

        return items[0]
    }

    fun roll(): String {
        val tw = getTotalWeight()
        return getLootItem(Random.nextInt(tw)).item
    }
}

val lootTableMap: Map<String, Loottable> = mapOf(
    "chest_normal" to Loottable(
        arrayOf(
            LootItem("paper", 10, 3)
        )
    )
)


class Chest (
    val loc: Pos,
    val player: Player,
    val loottable: Loottable
) {
    fun openInv() {
        CoroutineScope(EmptyCoroutineContext).launch {
            if (loc in player.getProfile().chests) {
                val open = Sound.sound(Key.key("block.chest.locked"), Sound.Source.BLOCK, 1f, 1f)
                player.playSound(open)

                player.sendActionBar(Component.text("You've already looted this chest."))

                return@launch
            }

            player.getProfile().chests += loc

            val open = Sound.sound(Key.key("block.chest.open"), Sound.Source.BLOCK, 1f, 1f)
            player.playSound(open)

            player.sendPacket(
                BlockActionPacket(loc, 1, 1, Block.CHEST)
            )

            sleep(200L)

            val itemid = loottable.roll()
            val item = getItemFromId(itemid).build().build()
            val itemname = toSmallCaps(getItemFromId(itemid).data.name)

            val drop = ItemEntity(item)
            drop.customName = Component.text(itemname)
            drop.isCustomNameVisible = true
            drop.setInstance(player.instance, loc.add(0.5, 0.5, 0.5))
            drop.velocity = Vec(0.0, 5.0, 0.0)

            sleep(600L)

            player.sendPacket(
                BlockActionPacket(loc, 1, 0, Block.CHEST)
            )
        }
    }
}