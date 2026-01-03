package demo.npcs

import demo.activeNpcs
import demo.item.toSmallCaps
import demo.lobbyInstance
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.PlayerMeta
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.tag.Tag

enum class NPCs (
    val id: String,
    val displayname: String,
    val displaytitle: String,
    val displayskin: NPCSkins,
    val interruptmsg: String,
    val walkawaymsg: String
) {
    CLERIC(
        "cleric",
        "<#c46fe8>Willow",
        "cleric",
        NPCSkins.CLERIC,
        "-where are you going?",
        "Hey, come back!"
    ),
    BARTENDER(
        "bartender",
        "<#e38f4b>Wheatley",
        "bartender",
        NPCSkins.BARTENDER,
        "-oh, bye.",
        "I'll see you later, I suppose."
    ),
    BLACKSMITH(
        "blacksmith",
        "<#3452bf>Edon Rust",
        "blacksmith",
        NPCSkins.BLACKSMITH,
        "-oh.",
        "...okay."
    ),
    FLORIST(
        "florist",
        "<yellow>Eddie",
        "florist",
        NPCSkins.FLORIST,
        "-wait, no!",
        "Smell you later!"
    ),
    GARDENER(
        "gardener",
        "<red>Corey",
        "gardener",
        NPCSkins.GARDENER,
        "-god dammit.",
        "Fuck you too."
    ),
    CRYPTKEEPER(
        "cryptkeeper",
        "<#993e29>Crimson",
        "cryptkeeper",
        NPCSkins.CRYPTKEEPER,
        "-...okay, then.",
        "To each their own."
    )
}

class NPC (
    npc: NPCs,
    var pos: Pos
) {
    val npcEntity = PlaybackPlayer("", npc.displayskin.texture, npc.displayskin.signature)
    val textEntity = Entity(EntityType.TEXT_DISPLAY)

    val mm = MiniMessage.miniMessage()

    init {
        npcEntity.setTag(Tag.String("isnpc"), "true")
        npcEntity.setTag(Tag.String("npc"), npc.id)
        npcEntity.setTag(Tag.String("restyaw"), pos.yaw.toString())
        npcEntity.setTag(Tag.String("restpitch"), pos.pitch.toString())

        (npcEntity.entityMeta as PlayerMeta).isCapeEnabled = false

        npcEntity.setInstance(lobbyInstance, pos)

        val text = mm.deserialize("${npc.displayname}<newline><reset><white>${toSmallCaps(npc.displaytitle)}")

        (textEntity.entityMeta as TextDisplayMeta).text = text
        (textEntity.entityMeta as TextDisplayMeta).scale = Vec(0.7, 0.7, 0.7)
        (textEntity.entityMeta as TextDisplayMeta).billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL

        pos = pos.withPitch(0f)
        pos = pos.withYaw(0f)
        pos = pos.add(0.0, 2.0, 0.0)

        textEntity.setNoGravity(true)

        textEntity.setInstance(lobbyInstance, pos)

        activeNpcs += this
    }
}