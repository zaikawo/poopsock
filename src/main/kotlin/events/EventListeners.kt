package demo.events

import demo.chat.sendChatMessage
import demo.chests.Chest
import demo.chests.lootTableMap
import demo.dialogue.Choice
import demo.dialogue.Dialogue
import demo.entities.getGameEntity
import demo.game.getProfile
import demo.game.registerProfile
import demo.gui.craftingInventory
import demo.item.getItemFromId
import demo.lobbyInstance
import demo.npcs.NPCs
import demo.player.getCustomAttribute
import demo.player.updateTablist
import demo.puppetToGameEntity
import demo.registries.damageKnockback
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.text.Component
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.entity.EntityTickEvent
import net.minestom.server.event.item.ItemUsageCompleteEvent
import net.minestom.server.event.item.PickupItemEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.event.player.PlayerStartDiggingEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.tag.Tag
import kotlinx.coroutines.launch
import net.kyori.adventure.title.Title
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.PlayerTickEvent
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.sin

fun chatEvent(event: PlayerChatEvent) {
    event.isCancelled = true
    event.player.sendChatMessage(event.message)
}

fun breakBlockEvent(event: PlayerBlockBreakEvent) {
    event.isCancelled = true
}

fun startDiggingEvent(event: PlayerStartDiggingEvent) {
    event.isCancelled = true
}

fun blockInteractEvent(event: PlayerBlockInteractEvent) {
    val block = event.block
    val player = event.player
    val blockname = block.name()

    if (blockname == "minecraft:crafting_table") {
        val craftingInv = craftingInventory()
        player.openInventory(craftingInv)
    }

    if (blockname == "minecraft:chest") {
        val chest = Chest(event.blockPosition.asVec().asPosition() ,player, lootTableMap["chest_normal"]!!)

        chest.openInv()
    }
}

fun useItemEvent(event: PlayerUseItemEvent) {
    val player = event.player
    val item = event.itemStack

    val evc = item.getTag(Tag.String("onclick"))

    if (evc != null) {
        CoroutineScope(EmptyCoroutineContext).launch {
            itemEvents[evc]!!(item, player)
        }
    }
}

fun consumeEvent(event: ItemUsageCompleteEvent) {
    val player = event.player
    val item = event.itemStack

    val evc = item.getTag(Tag.String("onconsume"))

    if (evc != null) {
        CoroutineScope(EmptyCoroutineContext).launch {
            itemEvents[evc]!!(item, player)
        }
    }
}

fun spawnEvent(event: PlayerSpawnEvent) {
    val player = event.player

    player.sendMessage("you joined bruv innit bruv")
    player.registerProfile()
    player.gameMode = GameMode.ADVENTURE
    player.instance.sendMessage(Component.text("${player.username} hath jointh!!"))
    player.showTitle(
        Title.title(
            mm.deserialize("<font:spacing:spacing>f</font>"),
            mm.deserialize(""),
            Title.Times.times(0.javaMilliseconds(), 500.javaMilliseconds(), 1000.javaMilliseconds()
        )
    ))
}

fun shortestAngleDifference(currentAngle: Float, targetAngle: Float): Float {
    var delta = (targetAngle - currentAngle) % 360.0
    if (delta > 180.0) {
        delta -= 360.0
    } else if (delta < -180.0) {
        delta += 360.0
    }
    return delta.toFloat()
}

fun updateYaw(currentYaw: Float, targetYaw: Float, shiftFraction: Double = 1.0 / 8.0): Float {
    val angleDiff = shortestAngleDifference(currentYaw, targetYaw)
    var newYaw = currentYaw + angleDiff * shiftFraction

    // Normalize the new yaw to ensure it's within [-180, 180] range
    newYaw = (newYaw + 360.0) % 360.0
    if (newYaw > 180.0) {
        newYaw -= 360.0
    }
    return newYaw.toFloat()
}

fun entityTickEvent(event: EntityTickEvent) {
    if (event.entity.uuid in puppetToGameEntity) {
        val en = event.entity.getGameEntity()
        en.tick()
    }

    if (event.entity.getTag(Tag.String("isnpc")) == "true") {
        var targetYaw = 0f
        var targetPitch = 0f

        val near = lobbyInstance.players.filter {
            it.getDistanceSquared(event.entity.position.add(0.0, event.entity.eyeHeight, 0.0)) < 25 &&
                    it.getProfile().isindialogue == event.entity.getTag(Tag.String("npc"))
        }

        if (near.size == 0) {
            targetYaw = event.entity.getTag(Tag.String("restyaw")).toFloat()
            targetPitch = event.entity.getTag(Tag.String("restpitch")).toFloat()
        } else {
            val nearest = near.sortedBy {
                it.getDistanceSquared(event.entity.position.add(0.0, event.entity.eyeHeight, 0.0))
            }[0]

            val face = event.entity.position.withLookAt(nearest.position)

            targetYaw = face.yaw
            targetPitch = face.pitch
        }

        val deltaPitch = (targetPitch - event.entity.position.pitch) / 8

        val newPitch = event.entity.position.pitch + deltaPitch
        val newYaw = updateYaw(event.entity.position.yaw, targetYaw)

        if (
            newPitch != event.entity.position.pitch &&
            newYaw != event.entity.position.yaw
        ) {

            event.entity.teleport(event.entity.position.withYaw(newYaw).withPitch(newPitch))
        }
    }

    if (event.entity.getTag(Tag.String("isdialogue")) == "yas queen") {
        if (event.entity.aliveTicks < 31) {
            val up = (30 - event.entity.aliveTicks).toDouble() / 35

            val opacity = ((event.entity.aliveTicks.toDouble() / 30) * 255).toInt()

            event.entity.velocity = Vec(0.0, up, 0.0)
            (event.entity.entityMeta as TextDisplayMeta).textOpacity = opacity.toByte()
        } else {
            val t = (event.entity.aliveTicks - 16).toDouble() / 5

            event.entity.velocity = Vec(0.0, sin(t)/10, 0.0)
        }
    }
}

fun entityAttackEvent(event: EntityAttackEvent) {
    println(event.target)
    println(event.entity)

    if (event.target.getTag(Tag.String("isdoor")) == "yeah") {
        event.target.remove()
        (event.entity as Player).getProfile().game!!.advance()
    }

    if (event.target is EntityCreature && event.entity is Player && !(event.target.getGameEntity().isdead)) {
        val strength = (event.entity as Player).getCustomAttribute("damage").toFloat()
        if ((event.target as EntityCreature).health <= strength) {
            event.target.getGameEntity().die()
        } else {
            (event.target as EntityCreature).damageKnockback(strength, event.entity)
        }
    }
}

fun pickupItemEvent(event: PickupItemEvent) {
    if (event.entity is Player) {
        (event.entity as Player).inventory.addItemStack(event.itemStack)
    }
}

fun tickEvent(event: PlayerTickEvent) {
    val active = event.player.getProfile().activeeffects.toSet()
    for (effect in active) {
        effect.tick(event.player)
    }

    val activecd = event.player.getProfile().activecooldowns.toSet()
    for (cd in activecd) {
        cd.tick()
    }

    event.player.updateTablist()
}

fun playerInteractEvent(event: PlayerEntityInteractEvent) {
    CoroutineScope(EmptyCoroutineContext).launch {
        if (event.target.hasTag(Tag.Boolean("isdoor"))) {
            event.player.getProfile().game!!.dungeon.build()
        }

        if (event.player.getProfile().isindialogue == null) {
            if (event.target.hasTag(Tag.String("npc"))) {
                val npcid = event.target.getTag(Tag.String("npc"))

                when (npcid) {
                    "cleric" -> {
                        val dialogue = Dialogue(event.target.position.add(0.0, 2.0, 0.0), event.player, NPCs.CLERIC)
                        dialogue.display("Hi! I'm Willow, the town's cleric!")
                        dialogue.display("I also work at a sex shop on the side. Money is hard to come by, you know.")
                        dialogue.display("So, hit me up if you want purification or a lap dance!")
                        dialogue.destroy()
                    }
                    "blacksmith" -> {
                        val dialogue = Dialogue(event.target.position.add(0.0, 2.0, 0.0), event.player, NPCs.BLACKSMITH)
                        dialogue.display("hello everybody my name is compaseer AKA synsao")
                        dialogue.display("back to you with another tutorial")
                        dialogue.display("this time im gonna teach you how to do a proper noose")
                        dialogue.display("you just wanna pick up a gun and shoot yourself")
                        dialogue.display("nooses are bad because then theres no blood")
                        dialogue.display("and whats the fun if there isnt a bit of blood")
                        dialogue.destroy()
                    }
                    "florist" -> {
                        val dialogue = Dialogue(event.target.position.add(0.0, 2.0, 0.0), event.player, NPCs.FLORIST)
                        dialogue.display("Hi!! You must be the new.. 'candidate', yes?")
                        dialogue.display("I've heard a lot of things about you!")
                        dialogue.display("Oh, sorry. My name's Eddie, I'm the town's Florist!")
                        dialogue.display("And Crimson has also tasked me with keeping track of your logbook!")
                        dialogue.display("I also sell flowers, if you want to check them out.")
                        when (dialogue.choice(
                            "Flowers?",
                            "Logbook?"
                        ).read()) {
                            1 -> {
                                dialogue.display("Here are some of my flowers!")
                                dialogue.display("*imagine a shop menu popped up i dont have that budget yet*")
                            }

                            2 -> {
                                dialogue.display("Oh, yeah. I keep track of every piece of equipment, material, weapon,")
                                dialogue.display("commodity, power adapter, unreasonably big anchor.. that you find in the dungeon!")
                                dialogue.display("If you come back in a bit I'll have it ready for you!")
                            }
                        }
                        dialogue.destroy()
                    }
                    "gardener" -> {
                        val dialogue = Dialogue(event.target.position.add(0.0, 2.0, 0.0), event.player, NPCs.GARDENER)
                        dialogue.display("i am going to kill myself")
                        dialogue.display("but i gotta make two idiots fall in love so")
                        dialogue.display("sucks to suck")
                        dialogue.destroy()
                    }
                    "cryptkeeper" -> {
                        val dialogue = Dialogue(event.target.position.add(0.0, 2.0, 0.0), event.player, NPCs.CRYPTKEEPER)
                        dialogue.display(setOf(
                            "What now?",
                            "Get in there, you don't have infinite time.",
                            "Quit wasting time, god dammit."
                        ).random())
                        when (dialogue.choice(
                            "Who am i?",
                            "What am i doing?",
                            "Who are you?"
                        ).read()) {
                            1 -> {
                                dialogue.display("Tell me yourself.")
                                dialogue.display("*imagine the character selection screen*")
                            }

                            2 -> {
                                dialogue.display("You're gonna go into the dungeon.")
                                dialogue.display("...")
                                dialogue.display("What? The 'coming back out alive' part is entirely optional and not done by most participants.")
                                dialogue.display("I bet a lot of money on you. You better do well.")
                            }

                            3 -> {
                                dialogue.display("A certain someone who makes sure people like YOU.")
                                dialogue.display("Go into the big hole behind that door at the end of the hallway.")
                            }
                        }
                        dialogue.destroy()
                    }
                    "bartender" -> {
                        val dialogue = Dialogue(event.target.position.add(0.0, 2.0, 0.0), event.player, NPCs.BARTENDER)
                        dialogue.display("Please show me your arm, you know. No skin, no service.")
                        dialogue.display("...")
                        dialogue.display("And your ID, don't be a smartass.")
                        val idChoice = dialogue.choice(
                            "(show your id)",
                            "suck my big fat dick you stupid piece of shit mhmmhmmhmhhmhmhm"
                        )
                        when (idChoice.read()) {
                            1 -> {
                                dialogue.display("Looks good. I'll give you a pass.")
                            }

                            2 -> {
                                dialogue.display("what :desolate:")
                            }
                        }
                        dialogue.destroy()
                    }
                }
            }
        }
    }
}