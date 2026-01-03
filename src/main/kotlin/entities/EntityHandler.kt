package demo.entities

import demo.game.GameInstance
import demo.game.GameUser
import demo.gameUserToPlayer
import demo.item.Slot
import demo.player.checkTag
import demo.player.getCustomAttribute
import demo.puppetToGameEntity
import demo.registries.damageKnockback
import net.kyori.adventure.text.Component
import net.minestom.server.collision.BoundingBox
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.*
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.attribute.AttributeInstance
import net.minestom.server.entity.damage.Damage
import net.minestom.server.instance.Instance
import java.lang.Math.pow
import java.lang.Thread.sleep
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

fun Entity.getGameEntity(): GameEntity {
    return puppetToGameEntity[this.uuid]!!
}

fun vecBetween(loc1: Pos, loc2: Pos): Vec {
    return loc1.sub(loc2).asVec().mul(-1.0)
}

fun EntityCreature.getTotalDamage(dmg: Double): Double {
    val armor = this.getCustomAttribute("armor").toDouble()
    val total = dmg / ( (armor+25)/25 )
    return total
}

fun EntityCreature.getCustomAttribute(attribute: String): Int {
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

    return totalstat
}

open class GameEntity (
    private val puppettype: EntityType,
    var health: Int,
    var attack: Int,
    var range: Double,
    var speed: Double,
    var startingstate: State,
    var attackFunc: (GameEntity) -> Unit = fun (entity: GameEntity) {
        entity.blockForMills(1000)
        entity.puppet.swingMainHand()
        entity.target!!.damageKnockback(2f, entity.puppet)
    },
    var dieFunc: (GameEntity) -> Unit = fun (entity: GameEntity) {
        entity.isdead = true
        entity.puppet.damage(Damage.fromEntity(entity.puppet, 0f))
        entity.puppet.kill()
    },
) {
    val puppet: EntityCreature = EntityCreature(puppettype)
    private var instance: Instance? = null
    private var game: GameInstance? = null
    var target: Player? = null
    var targetBlock: Pos? = null
    var walkBlock: Pos? = null
    private var statemachine: StateMachine = StateMachine(this, startingstate)
    private val timesource: TimeSource = TimeSource.Monotonic
    private var blocksince: TimeMark = timesource.markNow()
    var lasthit: TimeMark = timesource.markNow()
    var isdead: Boolean = false
    private var deadinc: Double = 0.0

    fun create(loc: Point, gam: GameInstance) {
        instance = gam.instance
        game = gam
        puppet.setInstance(gam.instance, loc)
        puppet.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = speed
        puppet.health = health.toFloat()
        puppetToGameEntity[puppet.uuid] = this
    }

    fun getSeenPlayer(): Player? {
        var seen = this.game!!.players.filter {
            (this.puppet.getDistanceSquared(it) <= range.pow(2.0)) && (it.gameMode == GameMode.ADVENTURE)
        }

        if (seen.isEmpty()) {
            return null
        }

        seen = seen.sortedBy {
            this.puppet.getDistanceSquared(it)
        }

        return seen[0]
    }

    fun blockForMills(mils: Int) {
        blocksince = timesource.markNow().plus(mils.milliseconds)
    }

    fun tick() {
        if (!isdead) {
            livetick()
        } else {
            deadtick()
        }
    }

    private fun livetick() {
        if (blocksince.hasPassedNow()) {
            this.statemachine.tick()
        }
        puppet.customName = Component.text("${this.puppet.health}")
        puppet.isCustomNameVisible = true
        val col = this.game!!.entities.filter {
            (this.puppet.getDistanceSquared(it.puppet) < 0.25) && (it != this) && (this.puppet.getDistanceSquared(it.puppet) != 0.0)
        }

        for (entity in col) {
            var vec = vecBetween(this.puppet.position, entity.puppet.position).normalize().mul(3.0).withY(0.0)
            entity.puppet.velocity = entity.puppet.velocity.add(vec)
            vec = vec.mul(-1.0)
            this.puppet.velocity = this.puppet.velocity.add(vec)
        }
    }

    private fun deadtick() {
        deadinc += 0.05
        if (deadinc > 2) {
            this.remove()
        }
    }

    fun die() {
        dieFunc(this)
    }

    fun remove() {
        puppet.remove()
        puppetToGameEntity -= puppet.uuid
    }
}