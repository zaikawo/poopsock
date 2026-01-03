package demo.player

import demo.effects.Effect
import demo.game.GameInstance
import demo.game.timesource
import demo.gameUserToPlayer
import demo.item.Slot
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EquipmentSlot
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import java.util.*
import kotlin.time.TimeMark

fun Player.getTotalDamage(dmg: Double): Double {
    val armor = this.getCustomAttribute("armor").toDouble()
    val total = dmg / ( (armor+25)/25 )
    return total
}

fun Player.getCustomAttribute(attribute: String): Int {
    val main = this.itemInMainHand
    val off = this.itemInOffHand
    val armor = listOf(
        this.getEquipment(EquipmentSlot.HELMET),
        this.getEquipment(EquipmentSlot.CHESTPLATE),
        this.getEquipment(EquipmentSlot.LEGGINGS),
        this.getEquipment(EquipmentSlot.BOOTS)
    )

    var totalstat = 0

    totalstat += checkTag(main, attribute, Slot.MAINHAND)
    totalstat += checkTag(off, attribute, Slot.OFFHAND)
    totalstat += checkTag(armor[0], attribute, Slot.EQUIPMENT)
    totalstat += checkTag(armor[1], attribute, Slot.EQUIPMENT)
    totalstat += checkTag(armor[2], attribute, Slot.EQUIPMENT)
    totalstat += checkTag(armor[3], attribute, Slot.EQUIPMENT)

    if (attribute in setOf(
        "damage"
    )) {
        return if (totalstat == 0) {
            1
        } else {
            totalstat
        }
    } else {
        return totalstat
    }
}

fun checkTag(item: ItemStack, attribute: String, slot: Slot): Int {
    var ret = 0
    if (item.hasTag(Tag.String(attribute))) {
        if (item.getTag(Tag.String("slot")) == slot.slot) {
            ret = item.getTag(Tag.String(attribute)).toInt()
        }
    }

    return ret
}