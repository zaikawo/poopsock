package demo.gui

import net.kyori.adventure.text.Component
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType

fun craftingInventory(): Inventory {
    val inv = Inventory(InventoryType.CHEST_6_ROW, Component.text("whats up ni"))

    return inv
}