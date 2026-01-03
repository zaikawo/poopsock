package demo.command

import demo.death.youAreNowDead
import demo.dungeons.NDungeonGenerator
import demo.effects.TestEffect
import demo.registries.Infected
import demo.game.createGameInstance
import demo.game.getProfile
import demo.game.registerProfile
import demo.item.ItemRegistry
import demo.player.getCustomAttribute
import demo.item.getItemFromId
import demo.npcs.NPC
import demo.npcs.NPCSkins
import demo.npcs.NPCs
import demo.npcs.PlaybackPlayer
import demo.player.getSupportingBlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import kotlin.coroutines.EmptyCoroutineContext


class getCommand : Command("get") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            sender.sendMessage(":nerd: erm ackschualy you uesd this wrong: correct usage: /get <item id>")
        }

        val itemIdArg = ArgumentType.String("id")

        addSyntax({ sender: CommandSender, context: CommandContext ->
            val id = context.get(itemIdArg)
            if (sender is Player) {
                if (id == "*") {
                    for (iid in ItemRegistry.keys) {
                        val item = getItemFromId(iid)
                        sender.inventory.addItemStack(item.getItem())
                        sender.sendMessage("gave $iid")
                    }
                    return@addSyntax
                }

                val item = getItemFromId(id)

                sender.inventory.addItemStack(item.getItem())
                sender.sendMessage("ok done")
            } else {
                sender.sendMessage("you arent a player shut the fuck up")
            }
        }, itemIdArg)
    }
}

class gamemodeCommand : Command("gm") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            sender.sendMessage(":nerd: erm ackschualy you uesd this wrong: correct usage: /gm <gamemode shorthand>")
        }

        val gamemodeArg = ArgumentType.String("gamemode")

        addSyntax({ sender: CommandSender, context: CommandContext ->
            val gamemode = context.get(gamemodeArg)
            if (sender is Player) {
                when (gamemode) {
                    "s" -> sender.setGameMode(GameMode.SURVIVAL)
                    "c" -> sender.setGameMode(GameMode.CREATIVE)
                    "a" -> sender.setGameMode(GameMode.ADVENTURE)
                    "sp" -> sender.setGameMode(GameMode.SPECTATOR)
                    else -> sender.sendMessage("invalid gamemode you fucking tard")
                }
            } else {
                sender.sendMessage("you arent a player shut the fuck up")
            }
        }, gamemodeArg)
    }
}

class getStatCommand : Command("getstat") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            sender.sendMessage(":nerd: erm ackschualy you uesd this wrong: correct usage: KILL YOURSELF")
        }

        val attributeArg = ArgumentType.String("gamemode")

        addSyntax({ sender: CommandSender, context: CommandContext ->
            val attribute = context.get(attributeArg)
            if (sender is Player) {
                sender.sendMessage(sender.getCustomAttribute(attribute).toString())
            } else {
                sender.sendMessage("you arent a player shut the fuck up")
            }
        }, attributeArg)
    }
}

class registerCommand : Command("register") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                sender.registerProfile()
            }
        }
    }
}

class startGameCommand : Command("start") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                sender.createGameInstance()
            }
        }
    }
}

class disbandGameCommand : Command("stop") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                sender.getProfile().game!!.disband()
            }
        }
    }
}

class spawnEntityCommand : Command("spawnentity") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                sender.getProfile().game!!.createEntity(Infected(), Pos(20.0, 0.0, 20.0))
            }
        }
    }
}

class dungeonTestCommand : Command("dungeontest") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                sender.teleport(sender.getSupportingBlock())
            }
        }
    }
}

class schemPlaceTest : Command("schemtest") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                NDungeonGenerator(sender.position, sender.getProfile().game!!)
            }
        }
    }
}

class effectTest : Command("effecttest") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                TestEffect(10).activate(sender)
            }
        }
    }
}

class soundTestCommand : Command("soundtest") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            sender.sendMessage(":nerd: erm ackschualy you uesd this wrong: correct usage: /get <item id>")
        }

        val soundIdArg = ArgumentType.String("id")

        val soundPitchArg = ArgumentType.Float("pitch")
        val soundVolumeArg = ArgumentType.Float("volume")

        addSyntax({ sender: CommandSender, context: CommandContext ->
            val id = context.get(soundIdArg)
            val pitch = context.get(soundPitchArg)
            val volume = context.get(soundVolumeArg)
            if (sender is Player) {
                val sound = Sound.sound(Key.key(id), Sound.Source.BLOCK, volume, pitch)

                sender.playSound(sound)

                sender.sendMessage("$id at $pitch $volume")
            } else {
                sender.sendMessage("you arent a player shut the fuck up")
            }
        }, soundIdArg, soundPitchArg, soundVolumeArg)
    }
}

class npcTest : Command("npc") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                val npc = NPC(NPCs.CLERIC,sender.position)
            }
        }
    }
}

class killYourself : Command("kys") {
    init {
        // Executed if no other executor can be used
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            if (sender is Player) {
                CoroutineScope(EmptyCoroutineContext).launch {
                    sender.youAreNowDead()
                }
            }
        }
    }
}