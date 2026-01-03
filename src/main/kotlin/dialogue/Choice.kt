package demo.dialogue

import demo.player.mm
import net.minestom.server.entity.Player
import java.lang.Thread.sleep

class Choice (
    val dialogue: Dialogue,
    vararg val choices: String
) {
    val player = dialogue.player
    var kill = false

    fun read(): Int {
        if (dialogue.kill || dialogue.entity.isRemoved) {
            return -1
        }

        var msg = "<newline>  <white>Select an option:<newline><newline>"

        var idx = 0

        for (choice in choices) {
            idx += 1
            msg += "<gold>[ $idx ] <white><italic>$choice</italic><newline>"
        }
        msg += "<newline>"

        player.sendMessage(mm.deserialize(msg))

        player.setHeldItemSlot(8.toByte())

        while (player.heldSlot.toInt() > idx) {
            if (player.getDistanceSquared(dialogue.pos) > 25) {
                kill = true
                break
            }
            sleep(50L)
        }

        if (kill) {
            dialogue.display("kill youself")
            dialogue.destroy()
            return -1
        }

        return player.heldSlot.toInt()+1
    }
}