package demo.death

import demo.events.javaMilliseconds
import demo.events.mm
import demo.game.getProfile
import demo.item.ItemWidthPx
import demo.item.getTextPillows
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.lang.Thread.sleep

fun String.pillowCenter(ln: Int = ItemWidthPx): Component {
    val pillows = getTextPillows(this.replace(
        Regex("<.+>"),
        ""
    ), ln)

    return mm.deserialize("${pillows[0]}$this${pillows[1]}")
}

fun Player.youAreNowDead() {
    val spec = Entity(EntityType.TEXT_DISPLAY)

    spec.isAutoViewable = false
    spec.setNoGravity(true)

    (spec.entityMeta as TextDisplayMeta).text = mm.deserialize("")

    spec.setInstance(this.instance, this.position.add(0.0, this.eyeHeight, 0.0))

    spec.addViewer(this)

    this.gameMode = GameMode.SPECTATOR

    this.spectate(spec)

    this.showTitle(
        Title.title(
            mm.deserialize(""),
            mm.deserialize("<font:spacing:spacing>f</font>"),
            Title.Times.times(1000.javaMilliseconds(), 100000.javaMilliseconds(), 0.javaMilliseconds()
            )
        )
    )

    sleep(2000L)

    for (p in 0..100) {
        val phase = p.toDouble() / 100

        this.showTitle(
            Title.title(
                mm.deserialize("<transition:black:white:$phase>You are dead."),
                mm.deserialize("<font:spacing:spacing>f</font>"),
                Title.Times.times(0.javaMilliseconds(), 100000.javaMilliseconds(), 0.javaMilliseconds()
                )
            )
        )

        this.sendMessage("cock $phase")

        sleep(50L)
    }
}