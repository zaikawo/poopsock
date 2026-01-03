package demo.player

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

fun Player.getSupportingBlock(): Pos {
    val down = 1.0
    val side = 0.4

    val below = this.instance.getBlock(this.position.add(0.0, -down, 0.0))
    val belowpos = this.position.add(0.0, -down, 0.0)

    if (below.isSolid) {
        return this.position
    }

    var directions = listOf(
        belowpos.add(0.0, 0.0, side),
        belowpos.add(side, 0.0, 0.0),
        belowpos.add(0.0, 0.0, -side),
        belowpos.add(-side, 0.0, 0.0),

        belowpos.add(side, 0.0, side),
        belowpos.add(-side, 0.0, side),
        belowpos.add(side, 0.0, -side),
        belowpos.add(-side, 0.0, -side)
    )

    directions = directions.filter {
        this.instance.getBlock(it).isSolid
    }

    if (directions.isEmpty()) {
        return this.position
    }

    val ret = directions.sortedBy {
        it.distanceSquared(this.position)
    }[0]

    return ret.add(0.0, down, 0.0)
}