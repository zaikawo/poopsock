package demo.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.item.ItemComponent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.AttributeList
import net.minestom.server.tag.Tag
import kotlin.math.ceil
import kotlin.math.floor

const val NormalSpace = "<green> <white>"
const val BoldSpace = "<bold> <!bold>"

val SmallCaps = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢ".split("")
val Alphabet = "abcdefghijklmnopqrstuvwxyz".split("")

val LenText =
    ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#\$%^&*(),.?<>-+=' /\\|:;\"[]{}" +
            "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡʏᴢ" + "⁰¹²³⁴⁵⁶⁷⁸⁹₀₁₂₃₄₅₆₇₈₉⁻" + "Ꞌ›‹»«○◆" + "☽❌✔⛨⏯〒⌚★" + "☐☑☒⚠☯Ⓞ").split("")

val LenNum1 =
    ("66666566265366666664666666666666664666666666666666666666666666276666644422655666246622243333" +
            "6666666646666666666666666" + "545555555554555555554" + "1447766" + "97787688").split("")
val LenNum2 = "10,10,10,10,10,10".split(",")

val LenNum = LenNum1 + LenNum2

val LenDict = LenText.zip(LenNum).toMap()

fun getTextPixelLen(text: String): Int {
    var len = 0

    val chars = text.split("")

    for (ch in chars) {
        if (ch in LenDict) {
            len += LenDict[ch]!!.toInt()
        }
    }

    println("$text has pixel lenght of $len")

    return len
}

fun getTextPillows(text: String, len: Int): Array<String> {
    val tl = getTextPixelLen(text)
    val pillowlen = (len - tl).toDouble() / 2

    val leftlen = ceil(pillowlen)
    val rightlen = floor(pillowlen)

    var leftpillow = ".".repeat(leftlen.toInt())
    var rightpillow = ".".repeat(rightlen.toInt())

    leftpillow = "<font:spacing:spacing>" + leftpillow + "</font>"
    rightpillow = "<font:spacing:spacing>" + rightpillow + "</font>"

    return arrayOf<String>(
        leftpillow,
        rightpillow
    )
}

fun getTextPillowsOld(text: String, len: Int): Array<String> {
    val tl = getTextPixelLen(text)
    val pillowlen = (len - tl).toDouble() / 2

    val leftlen = ceil(pillowlen)
    val rightlen = floor(pillowlen)

    var leftpillow = ".".repeat(leftlen.toInt())
    var rightpillow = ".".repeat(rightlen.toInt())

    leftpillow = leftpillow.replace("....", NormalSpace)

    while (NormalSpace in leftpillow && "." in leftpillow) {
        leftpillow = leftpillow.replaceFirst(NormalSpace, "")
        leftpillow = leftpillow.replaceFirst(".", "")
        leftpillow = "$BoldSpace$leftpillow"
    }

    rightpillow = rightpillow.replace("....", NormalSpace)

    while (NormalSpace in rightpillow && "." in rightpillow) {
        rightpillow = rightpillow.replaceFirst(NormalSpace, "")
        rightpillow = rightpillow.replaceFirst(".", "")
        rightpillow = "$BoldSpace$rightpillow"
    }

    return arrayOf<String>(
        leftpillow,
        rightpillow
    )
}

fun toSmallCaps(text: String): String {
    var ret = text

    for (idx in SmallCaps.indices) {
        val small = SmallCaps[idx]
        val norma = Alphabet[idx]
        ret = ret.replace(norma, small)
    }

    return ret
}

fun splitIntoLines(text: String, len: Int): List<String> {
    val lines = mutableListOf<String>()
    var current = mutableListOf<String>()
    val words = text.split(" ")

    for (word in words) {
        val past = current.toList() + word
        val pasttest = past.joinToString(separator=" ")
        println(pasttest)
        if (getTextPixelLen(pasttest) > len) {
            lines += current.joinToString(separator=" ")
            current = mutableListOf<String>(word)
        } else {
            current += word
        }
        println("current is currently $current")
    }

    if (current.size > 0) {
        lines += current.joinToString(separator=" ")
    }

    return lines.toList()
}

fun getTextBar(amt: Int): String {
    var amount = amt
    val levels = mutableListOf(0, 0, 0, 0, 0)
    var level = 1
    var idx = -1

    while (amount > 0) {
        idx += 1
        if (idx == 5) {
            idx = 0
            level += 1
        }
        if (levels[idx] < level) {
            amount -= 1
            levels[idx] = level
        }
    }

    var ret = ""

    for (i in levels) {
        ret += "${LevelColors[i]}$LevelChar"
    }

    return ret
}

data class ItemAttribute(
    val attribute: String,
    val value: Int,
)

data class ItemEvent(
    val event: Event,
    val value: String,
    val text: String,
)

data class ItemData(
    val material: Material,
    val name: String,
    val desc: String,
    val rarity: Rarity,
    val type: Type?,
    val slot: Slot,
    val attributes: Array<ItemAttribute>,
    val events: Array<ItemEvent>,
)

class Item {
    private var material: Material = Material.STONE
    private var name: String = "change the name you dumb fuck"
    private var desc: String = "change the desc you dumb fuck"
    private var rarity: Rarity = Rarity.FILTH
    private var type: Type? = null
    private var slot: Slot = Slot.INTERNAL
    private var attributes: Array<ItemAttribute> = arrayOf()
    private var events: Array<ItemEvent> = arrayOf()

    fun material(mt: Material): Item {
        this.material = mt
        return this
    }

    fun name(nm: String): Item {
        this.name = nm
        return this
    }

    fun desc(dc: String): Item {
        this.desc = dc
        return this
    }

    fun rarity(rt: Rarity): Item {
        this.rarity = rt
        return this
    }

    fun type(tp: Type): Item {
        this.type = tp
        return this
    }

    fun slot(st: Slot): Item {
        this.slot = st
        return this
    }

    fun attributes(vararg attr: ItemAttribute): Item {
        this.attributes = attr as Array<ItemAttribute>
        return this
    }

    fun events(vararg ev: ItemEvent): Item {
        this.events = ev as Array<ItemEvent>
        return this
    }

    fun build(): ItemData {
        return ItemData(
            this.material,
            this.name,
            this.desc,
            this.rarity,
            this.type,
            this.slot,
            this.attributes,
            this.events
        )
    }
}

fun getItemFromId(id: String): ItemBuilder {
    return if (id in ItemRegistry) {
        val item = ItemRegistry[id]!!
        ItemBuilder(id, item.build())
    } else {
        val item = ItemRegistry["failed"]!!
        ItemBuilder("failed", item.build())
    }
}

class ItemBuilder(
    private val id: String,
    val data: ItemData
) {
    private var name: Component
    private var lore: MutableList<Component>
    private var tags: MutableMap<String, Any> = mutableMapOf<String, Any>(
        "id" to id
    )
    init {
        val mm = MiniMessage.miniMessage()

        this.lore = mutableListOf<Component>()

        //handle name

        var small = toSmallCaps(data.name)
        var pillows = getTextPillows(small, ItemWidthPx)
        val rcolor = data.rarity.color
        val rarity = data.rarity.title
        val templatename = "${pillows[0]}<${rcolor}>$small${pillows[1]}"

        this.name = mm.deserialize(templatename)

        //set rarity tags

        tags["rarity"] = rarity

        //set slot tags

        tags["slot"] = data.slot.slot

        //handle description

        val desclines = splitIntoLines(data.desc, ItemWidthPx -32)

        for (line in desclines) {
            val descpillows = getTextPillows(line, ItemWidthPx)
            lore += mm.deserialize("${descpillows[0]}<gray><italic>$line<!italic>${descpillows[1]}")
        }

        //handle attributes

        val attributes = data.attributes

        for (attr in attributes) {
            lore += mm.deserialize("")

            small = toSmallCaps(attr.attribute)
            pillows = getTextPillows("‹ $small ›", ItemWidthPx)

            var color = "blue"

            if (attr.attribute in AttributeColors) {
                color = AttributeColors[attr.attribute]!!
            }

            lore += mm.deserialize("${pillows[0]}<dark_gray><!italic>‹ <$color><italic>$small <dark_gray><!italic>›${pillows[1]}")

            pillows = getTextPillows(LevelChar.repeat(5), ItemWidthPx)
            lore += mm.deserialize("${pillows[0]}<!italic>${getTextBar(attr.value)}${pillows[1]}")

            tags[attr.attribute] = attr.value
        }

        val events = data.events

        for (event in events) {
            lore += mm.deserialize("")

            small = toSmallCaps(event.event.title)
            pillows = getTextPillows("‹‹ $small ››", ItemWidthPx)

            val color = "red"

            lore += mm.deserialize("${pillows[0]}<dark_gray><!italic>‹‹ <$color><italic>$small <dark_gray><!italic>››${pillows[1]}")

            val eventlines = splitIntoLines(event.text, ItemWidthPx -32)
            for (line in eventlines) {
                val eventpillows = getTextPillows(line, ItemWidthPx)
                lore += mm.deserialize("${eventpillows[0]}<gray><italic>$line<!italic>${eventpillows[1]}")
            }

            tags["on${event.event.event}"] = event.value
        }

        lore += mm.deserialize("")

        if (data.type != null) {
            small = toSmallCaps(data.type.type)
            pillows = getTextPillows(small, ItemWidthPx)
            lore += mm.deserialize("${pillows[0]}<dark_gray>${small}${pillows[1]}")

            tags["type"] = data.type.type
            tags["twohanded"] = data.type.twohanded.toString()
        }

        small = toSmallCaps(rarity)
        pillows = getTextPillows(small, ItemWidthPx)
        lore += mm.deserialize("${pillows[0]}<$rcolor>$small${pillows[1]}")
    }

    fun build(): ItemStack.Builder {
         val item = ItemStack.builder(data.material)
             .set(ItemComponent.ITEM_NAME, name)
             .set(ItemComponent.LORE, lore)
             .set(ItemComponent.HIDE_ADDITIONAL_TOOLTIP)
             .set(ItemComponent.ATTRIBUTE_MODIFIERS, AttributeList.EMPTY)

        for (entry in tags.entries) {
            item.setTag(Tag.String(entry.key), entry.value.toString())
            println("${entry.key} -> ${entry.value.toString()}")
        }

        return item
    }

    fun getItem(): ItemStack {
        return build().build()
    }

}