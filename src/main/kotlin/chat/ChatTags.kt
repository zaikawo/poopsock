package demo.chat

enum class ChatTags (
    val sym: String,
    val order: Int,
    val color: String,
    val chatColor: String
) {
    DEFAULT(
        "p",
        0,
        "<#D2E9E9>",
        "<#E3F4F4>"
    ),
    ADMIN(
        "a",
        100,
        "<#CA8787>",
        "<#F8E8EE>"
    ),
    MOD(
        "m",
        50,
        "<#C1D0B5>",
        "<#D6E8DB>"
    ),
    HELPER(
        "h",
        50,
        "<#7FA1C3>",
        "<#F5EDED>"
    ),
    NAME_PICKER(
        "n",
        0,
        "<#D2E9E9>",
        "<#E3F4F4>"
    ),
    EMOTIONAL_SUPPORT(
        "e",
        0,
        "<#D2E9E9>",
        "<#E3F4F4>"
    )
}