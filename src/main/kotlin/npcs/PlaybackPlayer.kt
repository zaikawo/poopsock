package demo.npcs

import net.minestom.server.entity.*
import net.minestom.server.entity.metadata.PlayerMeta
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket


class PlaybackPlayer(
    private val username: String,
    private val skinTexture: String?,
    private val skinSignature: String?
) : Entity(EntityType.PLAYER) {
    init {
        setNoGravity(true)
    }

    override fun updateNewViewer(player: Player) {
        val properties = ArrayList<PlayerInfoUpdatePacket.Property>()
        if (skinTexture != null && skinSignature != null) {
            properties.add(PlayerInfoUpdatePacket.Property("textures", skinTexture, skinSignature))
        }
        val entry = PlayerInfoUpdatePacket.Entry(
            uuid, username, properties, false,
            0, GameMode.SURVIVAL, null, null
        )
        player.sendPacket(PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry))

        // Spawn the player entity
        super.updateNewViewer(player)

        // Enable skin layers
        player.sendPackets(
            EntityMetaDataPacket(
                entityId,
                mapOf(17 to Metadata.Byte(127.toByte()))
            )
        )
    }

    override fun updateOldViewer(player: Player) {
        super.updateOldViewer(player)

        player.sendPacket(PlayerInfoRemovePacket(uuid))
    }
}