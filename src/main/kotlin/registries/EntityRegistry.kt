package demo.registries

import demo.death.youAreNowDead
import demo.entities.*
import demo.game.getProfile
import demo.player.getTotalDamage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.Damage
import net.minestom.server.entity.damage.EntityDamage
import java.lang.Thread.sleep
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

val timesource = TimeSource.Monotonic

fun Player.damageKnockback(amount: Float, attacker: Entity) {
    if (this.getProfile().lasthit.hasPassedNow()) {
        this.getProfile().lasthit = timesource.markNow().plus(500.milliseconds)
        val dmgamount = this.getTotalDamage(amount.toDouble()).toFloat()
        if (dmgamount >= this.health) {
            val pl = this
            CoroutineScope(EmptyCoroutineContext).launch {
                pl.youAreNowDead()
            }
        } else {
            val damage = Damage.fromEntity(attacker, dmgamount)
            this.damage(damage)
            val atk = attacker.position
            val x = sin(atk.yaw * (PI/180))
            val y = -cos(atk.yaw * (PI/180))
            this.takeKnockback(0.4f, x, y)
        }
    }
}

fun EntityCreature.damageKnockback(amount: Float, attacker: Entity) {
    if (this.getGameEntity().lasthit.hasPassedNow()) {
        this.getGameEntity().lasthit = timesource.markNow().plus(500.milliseconds)
        val dmgamount = this.getTotalDamage(amount.toDouble()).toFloat()
        val damage = Damage.fromEntity(attacker, dmgamount)
        this.damage(damage)
        val atk = attacker.position
        val x = sin(atk.yaw * (PI/180))
        val y = -cos(atk.yaw * (PI/180))
        this.takeKnockback(0.4f, x, y)
    }
}

class Infected : GameEntity(
    EntityType.BOGGED,
    16,
    2,
    20.0,
    0.1,
    IdleState()
)

class Rodent : GameEntity(
    EntityType.SILVERFISH,
    4,
    1,
    5.0,
    0.2,
    IdleState()
)