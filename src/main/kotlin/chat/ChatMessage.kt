package demo.chat

import demo.game.getProfile
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.entity.Player

val bannedWords = "anal,anus,arse,ass,ballsack,balls,bastard,bitch,biatch,bloody,blowjob,blow job,bollock,bollok,boner,boob,bugger,bum,butt,buttplug,clitoris,cock,coon,crap,cunt,damn,dick,dildo,dyke,fag,feck,fellate,fellatio,felching,fuck,f u c k,fudgepacker,fudge packer,flange,Goddamn,God damn,hell,homo,jerk,jizz,knobend,knob end,labia,lmao,lmfao,muff,nigger,nigga,omg,penis,piss,poop,prick,pube,pussy,queer,scrotum,sex,shit,s hit,sh1t,slut,smegma,spunk,tit,tosser,turd,twat,vagina,wank,whore,wtf".split(',')

fun Player.sendChatMessage(text: String) {
    val mm = MiniMessage.miniMessage()

    val tag = this.getProfile().tag

    val struct = tag.sym
    val color = tag.color
    val chatcolor = tag.chatColor

    val message = filterMessage(text)

    this.instance.sendMessage(
        mm.deserialize(
            "<font:tags:tags>$struct</font> $color${this.username}: $chatcolor$message"
        )
    )
}

fun filterMessage(omsg: String): String {
    var msg = " $omsg "
    val regexFilter = "[\\.!/\\\\?\\-_ ,']"

    for (word in bannedWords) {
        val pattern = Regex("$regexFilter$word$regexFilter")
        val repl = "*".repeat(word.length)
        val matches = pattern.findAll(msg, 0)
        for (match in matches) {
            val nrepl = "${match.value[0]}$repl${match.value[match.value.length-1]}"
            msg = msg.replace(match.value, nrepl)
        }
    }
    return msg.substring(1, msg.length-1)
}