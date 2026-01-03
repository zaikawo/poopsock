package demo.effects

import demo.game.getProfile
import net.kyori.adventure.text.Component
import net.minestom.server.collision.Aerodynamics
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import org.w3c.dom.Text
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

fun toQuaternion(pitch: Int, yaw: Int, roll: Int): FloatArray {
    val cr: Double = cos(roll * 0.5)
    val sr: Double = sin(roll * 0.5)
    val cp: Double = cos(pitch * 0.5)
    val sp: Double = sin(pitch * 0.5)
    val cy: Double = cos(yaw * 0.5)
    val sy: Double = sin(yaw * 0.5)

    val w = cr * cp * cy + sr * sp * sy;
    val x = sr * cp * cy - cr * sp * sy;
    val y = cr * sp * cy + sr * cp * sy;
    val z = cr * cp * sy - sr * sp * cy;

    return floatArrayOf(w.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
}

interface Effect {
    val name: String
    val icon: String
    val color: String
    val iconcolor: String
    var dura: Int

    fun activate(victim: Player) {
        victim.getProfile().activeeffects += this
    }

    fun tick(victim: Player) {
        dura -= 1
        if (dura == 0) {
            this.deactivate(victim)
        }
    }

    fun deactivate(victim: Player) {
        victim.getProfile().activeeffects -= this
    }

    fun getString(): String {
        return "<$iconcolor>$icon <$color>$name"
    }

    fun getTime(): String {
        val adj = (dura / 20) + 1

        val minutes = adj / 60
        val seconds = adj % 60

        val minutesStr = "$minutes".padStart(2, '0')
        val secondsStr = "$seconds".padStart(2, '0')

        return "$minutesStr:$secondsStr"
    }
}

class TestEffect (
    duration: Int
) : Effect {
    override val name = "testeffect"
    override val icon = "+"
    override val color = "#aaaaaa"
    override val iconcolor = "#888888"
    override var dura = duration*20

    override fun activate(victim: Player) {
        super.activate(victim)

        victim.sendMessage("You have activated.")
    }

    override fun tick(victim: Player) {
        super.tick(victim)

        victim.sendMessage("You have ticked. $dura")
    }

    override fun deactivate(victim: Player) {
        super.deactivate(victim)

        victim.sendMessage("You have deactivated.")
    }
}