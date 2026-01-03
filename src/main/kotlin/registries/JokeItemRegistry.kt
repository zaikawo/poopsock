package demo.registries

import demo.item.Item
import demo.item.ItemAttribute
import demo.item.Rarity
import demo.item.Slot
import net.minestom.server.item.Material


val JokeItemRegistry = mapOf<String, Item>(
"sacrificial_dagger" to Item()
    .material(Material.STONE_SWORD)
    .name("sacrificial dagger")
    .desc("The classic, returning again; a true staple.")
    .slot(Slot.MAINHAND)
    .rarity(Rarity.UNUSUAL)
    .attributes(
        ItemAttribute("damage", 3)
    ),
    "bicycle_fish" to Item()
        .material(Material.COOKED_COD)
        .name("bicycle fish")
        .desc("why is he riding it? is he dumb?")
        .rarity(Rarity.ANCESTRAL)
        .attributes(
            ItemAttribute("fish", 11),
            ItemAttribute("bicycle", 13)
        ),
    "tome_of_warding" to Item()
        .material(Material.ENCHANTED_BOOK)
        .name("tome of warding")
        .desc("It wards.")
        .slot(Slot.OFFHAND)
        .rarity(Rarity.ANCESTRAL)
        .attributes(
            ItemAttribute("armor", 13)
        ),
    "throngler" to Item()
        .material(Material.NETHERITE_SWORD)
        .name("the throngler")
        .desc("The less words a magic sword's name has, the more threatening it is.")
        .slot(Slot.MAINHAND)
        .rarity(Rarity.FILTH)
        .attributes(
            ItemAttribute("damage", 100)
        )
)