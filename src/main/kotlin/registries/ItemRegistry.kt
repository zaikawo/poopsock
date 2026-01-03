package demo.item

import net.minestom.server.item.Material

val noAttr = arrayOf<ItemAttribute>()
val noEvents = arrayOf<ItemEvent>()

const val LevelChar = "◆"
val LevelColors = listOf<String>(
    "<gray>",
    "<green>",
    "<yellow>",
    "<red>",
    "<light_purple>",
    "<aqua>",
    "<rainbow>"
)

val AttributeColors = mapOf<String, String>(
    "damage" to "blue",
    "health" to "red",
    "armor" to "yellow"
)

enum class Rarity(val title: String, val color: String) {
    FILTH("filth", "gray"),
    ABUNDANT("abundant", "white"),
    UNUSUAL("unusual", "yellow"),
    ABNORMAL("abnormal", "gold"),
    DECREPIT("decrepit", "red"),
    ANCESTRAL("ancestral", "#8330b0"),
    DIVINE("divine", "#f5d993")
}

enum class Event(val title: String, val event: String) {
    USE("when used", "consume"),
    CONSUME("when consumed", "consume"),
    CLICK("when clicked", "click"),
    DAMAGE_SELF("when damaged", "damage_self"),
    DAMAGE_MOB("when damaging mob", "damage_mob"),
    HEAL("when healed", "heal"),
    DIE("when dead", "death")
}

enum class Slot(var slot: String) {
    MAINHAND("main"), //held in main hand (weapons)
    OFFHAND("off"), //held in offhand (tomes)
    EQUIPMENT("armor"), //held in equipment slots (armor)
    PASSIVE("inv"), //hend in inventory (i cant think of a good example but better to be safe than sorry amirite lads)
    INTERNAL("internal") //for use in like shits and giggles probably unused lmao
}

enum class Type(var type: String, val twohanded: Boolean) {
    SWORD("sword", false),
    AXE("axe", false),
    LONGSWORD("longsword", true),
    BATTLEAXE("battle axe", true),
    BOW("bow", false),
    CROSSBOW("crossbow", true),
    GEM_OF_MASS_DESTRUCTION_AND_IMMESURABLE_POWER("gary", true)
}

const val ItemWidthPx = 200

val ItemRegistry = mapOf<String, Item>(
    "failed" to Item()
        .material(Material.BLACK_DYE)
        .name("failed operation")
        .desc("this operation failed, cry about it"),

    "leather_shoes" to Item()
        .material(Material.LEATHER_BOOTS)
        .name("leather shoes")
        .desc("Ragged and torn, the left one is missing shoestrings.")
        .slot(Slot.EQUIPMENT)
        .rarity(Rarity.ABUNDANT)
        .attributes(
            ItemAttribute("armor", 1)
        ),

    "iron_breastplate" to Item()
        .material(Material.IRON_CHESTPLATE)
        .name("iron breastplate")
        .desc("Heavy piece of equipment, protecting the wearer from most close combat hits.")
        .slot(Slot.EQUIPMENT)
        .rarity(Rarity.ABNORMAL)
        .attributes(
            ItemAttribute("armor", 4)
        ),

    "steak" to Item()
        .material(Material.COOKED_BEEF)
        .name("steak")
        .desc("A juicy yet tender piece of meat, roasted over a fire.")
        .rarity(Rarity.ABUNDANT)
        .attributes(
            ItemAttribute("health", 5)
        )
        .events(
            ItemEvent(Event.CONSUME, "heal", "Heal.")
        ),

    "magic_mirror" to Item()
        .material(Material.DIAMOND)
        .name("magic mirror")
        .desc("If you look closely, you can see yourself. If you look closer, you can see right through. If you look behind it, you can see the price tag.")
        .rarity(Rarity.UNUSUAL)
        .events(
            ItemEvent(Event.CLICK, "magicmirror", "Activate.")
        ),

    "wooden_bat" to Item()
        .material(Material.WOODEN_SWORD)
        .name("wooden bat")
        .desc("Sturdy but old, worn on the handle, slightly rotten.")
        .slot(Slot.MAINHAND)
        .type(Type.SWORD)
        .rarity(Rarity.ABUNDANT)
        .attributes(
            ItemAttribute("damage", 2)
        ),

    "paper" to Item()
        .material(Material.MAP)
        .name("paper")
        .desc("Tiny and torn up pieces of paper."),

    "wooden_hatchet" to Item()
        .material(Material.WOODEN_AXE)
        .name("wooden hatchet")
        .desc("Somehow, it still stands unbroken although heavily rotten.")
        .slot(Slot.MAINHAND)
        .type(Type.AXE)
        .rarity(Rarity.UNUSUAL)
        .attributes(
            ItemAttribute("damage", 3)
        ),

    "stone_dagger" to Item()
        .material(Material.STONE_SWORD)
        .name("stone dagger")
        .desc("Sometimes used for ritualistic practices, although it's been replaced by more occult counterparts.")
        .slot(Slot.MAINHAND)
        .type(Type.SWORD)
        .rarity(Rarity.UNUSUAL)
        .attributes(
            ItemAttribute("damage", 3)
        ),

    "crown_of_nothing" to Item()
        .material(Material.GOLDEN_HELMET)
        .name("crown of nothing")
        .desc("Lined with overflowing circuits, eternally in a shortage.")
        .slot(Slot.EQUIPMENT)
        .rarity(Rarity.DECREPIT)
        .attributes(
            ItemAttribute("armor", 2)
        )
        .events(
            ItemEvent(Event.HEAL, "crownofnothing", "Cancel healing. Amount of health you would've restored is added to your next hit as damage.")
        ),

    "watcher_eye" to Item()
        .material(Material.FERMENTED_SPIDER_EYE)
        .name("elder watcher's sinful eye")
        .desc("\"But, how was it possible?\" his followers shrieked. The machine had taken over the body and destroyed anything resembling a limb; before them, lie the dead watcher's rotting body.")
        .rarity(Rarity.DIVINE)
        .events(
            ItemEvent(Event.CLICK, "watchereye", "Reveal the unseen.")
        ),

    "deity_omen" to Item()
        .material(Material.OMINOUS_BOTTLE)
        .name("dead deity's omen")
        .desc("Schaskarov looks at the machine with malicious intent, for it claims that it has become the very concept of death, something Schaskarov couldn't allow.")
        .rarity(Rarity.DIVINE)
        .events(
            ItemEvent(Event.DIE, "deityomen", "Resurrect")
        ),

    "crown_of_everything" to Item()
        .material(Material.NETHERITE_HELMET)
        .name("crown of everything")
        .desc("\"And when you see your reflection in my oil-blood, you will weep. And when you see my code in your dreams, you will weep. And when you see me facing you, grabbing on to your life, you WILL weep.\"")
        .rarity(Rarity.DIVINE)
        .events(
            ItemEvent(Event.DIE, "crownofeverything", "Do something.")
        ),

    "elder_sword" to Item()
        .material(Material.WOODEN_SWORD)
        .name("scimitar of the elder gods")
        .desc("The very sword he used throughout his dominion, now used against him in a fight for his honor.")
        .slot(Slot.MAINHAND)
        .type(Type.SWORD)
        .rarity(Rarity.DIVINE)
        .attributes(
            ItemAttribute("damage", 6)
        )
        .events(
            ItemEvent(Event.DAMAGE_MOB, "eldersword", "On the first hit, strike the enemy down with lightning. On every subsequent hit, deal +8% damage, capping at 40%.")
        ),

    "reverse_bear_trap" to Item()
        .material(Material.FIREWORK_STAR)
        .name("reverse bear trap")
        .desc("Deploys a reverse bear trap, healing whichever living are on it.")
        .rarity(Rarity.ABUNDANT)
        .events(
            ItemEvent(Event.CLICK, "reversebeartrap", "Deploy below you.")
        ),

    "portable_stormcloud" to Item()
        .material(Material.WIND_CHARGE)
        .name("portable stormcloud")
        .desc("30,000 amps in a small cubic space. Terrifying.")
        .rarity(Rarity.ABNORMAL)
        .events(
            ItemEvent(Event.DAMAGE_MOB, "stormcloud", "Chance to strike enemy with lightning. Has a 50% chance to chain to other nearby enemies.")
        ),

    "paper_harpoon" to Item()
        .material(Material.SPECTRAL_ARROW)
        .name("paper harpoon")
        .desc("Glides smoothly through the air and showers victims in innumerable tiny paper cuts.")
        .rarity(Rarity.UNUSUAL)
        .attributes(
            ItemAttribute("damage", 4)
        )
        .events(
            ItemEvent(Event.CLICK, "throwpaperharpoon", "Throw.")
        ),

    "paper_harpoon_used" to Item()
        .material(Material.STRING)
        .name("paper harpoon chain")
        .desc("There it goes. Woah.")
        .rarity(Rarity.UNUSUAL)
        .attributes(
            ItemAttribute("damage", 2)
        )
        .events(
            ItemEvent(Event.CLICK, "recallpaperharpoon", "Unthrow.")
        ),

    "hold_of_bagging" to Item()
        .material(Material.CHEST_MINECART)
        .name("hold of bagging")
        .desc("A bag with an impossibly small space inside; holds one item.")
        .rarity(Rarity.ABNORMAL)
        .events(
            ItemEvent(Event.CLICK, "holdofbagging", "Open the comically small bag.")
        ),

    "massive_tuning_fork" to Item()
        .material(Material.STONE_PICKAXE)
        .name("massive tuning fork")
        .desc("How the fuck is it that big, you may ask? Well, we don't know either.")
        .rarity(Rarity.UNUSUAL)
        .events(
            ItemEvent(Event.CLICK, "tuningfork", "Stun nearby enemies.")
        ),

    "schaskarovs_coin" to Item()
        .material(Material.SUNFLOWER)
        .name("schaskarov's coin")
        .desc("Etched into pure gold with only a needle.")
        .rarity(Rarity.DECREPIT)
        .events(
            ItemEvent(Event.CLICK, "cointoss", "Toss the coin; If it lands on heads, your next hit will deal twice as much damage. If it lands on tails, your next hit will deal the same damage to you.")
        ),

    "mobile_salmon" to Item()
        .material(Material.SALMON)
        .name("mobile salmon")
        .desc("A noble steed.")
        .rarity(Rarity.ABNORMAL)
        .events(
            ItemEvent(Event.CLICK, "mobilesalmon", "Summon your mighty steed.")
        ),

    "lily_of_the_valley" to Item()
        .material(Material.LILY_OF_THE_VALLEY)
        .name("lily of the valley")
        .desc("A sweet-smelling plant with white, bell-shaped flowers. Native to the colder regions of the northern hemisphere.")
        .rarity(Rarity.ABUNDANT),

    "cornflower" to Item()
        .material(Material.CORNFLOWER)
        .name("cornflower")
        .desc("A blue flower native to the central northern hemisphere. Used to make medicine and tonics.")
        .rarity(Rarity.ABUNDANT),

    "oxeye_daisy" to Item()
        .material(Material.OXEYE_DAISY)
        .name("oxeye daisy")
        .desc("A white-flowered plant with a yellow button in the middle. Highly invasive species.")
        .rarity(Rarity.ABUNDANT),

    "allium" to Item()
        .material(Material.ALLIUM)
        .name("allium")
        .desc("A plant with large purple ball-shaped flowers on the top. If the flowers had been harvested pre-blossom, they could've been used to grow giant onions.")
        .rarity(Rarity.ABUNDANT),

    "sunflower" to Item()
        .material(Material.SUNFLOWER)
        .name("sunflower")
        .desc("A tall flower with yellow petals and an orange center. Well, calling it a singular flower is a lie, they're actually made up of thousands of smaller flowers.")
        .rarity(Rarity.ABUNDANT),

    "white_tulip" to Item()
        .material(Material.WHITE_TULIP)
        .name("white tulip")
        .desc("A white flower that looks like it's always about to bloom more. They're simple, modest, and perfect in some people's eyes.")
        .rarity(Rarity.ABUNDANT),

    "wither_rose" to Item()
        .material(Material.WITHER_ROSE)
        .name("wither rose")
        .desc("A black rose that makes you cough. You feel like if you touched it for too long, your skin would fall off.")
        .rarity(Rarity.ABUNDANT) //i dont actually know a good rarity but probably higher than abundant cause you have to find it in dungeon vs just buying it
)