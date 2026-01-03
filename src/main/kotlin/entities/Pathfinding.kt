package demo.entities

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import kotlin.math.round
import kotlin.math.roundToInt

/*
*
*   " Now I rejoice in what I am suffering for you,
*   and I fill up in my flesh what is still lacking
*   in regard to Christ’s afflictions, for the sake
*         of his body, which is the church. "
*
*                - Colossians 1:24
*
*/

fun getValueDist(pos1: Pos, pos2: Pos): Int {
    return (pos1.distance(pos2) * 10).roundToInt()
}

fun Pos.center(): Pos {
    return Pos(this.x.roundToInt().toDouble()+0.5, this.y(), this.z().roundToInt().toDouble()+0.5)
}

class StarBlock (
    val loc: Pos,
    val parent: StarBlock?,
    private val start: Pos,
    private val end: Pos
) {
    var h = getValueDist(loc, start)
    var g = getValueDist(loc, end)
    var t = h + g

    fun changeH(nh: Int) {
        h = nh
        t = h + g
    }
}

class Pathfinder (
    startA: Pos,
    endA: Pos,
    private val instance: Instance
) {
    private val start: Pos = startA.center()
    private val end: Pos = endA.center()
    private var checkedBlocks: MutableSet<StarBlock> = mutableSetOf()
    private var checkedLocs: MutableSet<Pos> = mutableSetOf()
    private var candidates: MutableSet<StarBlock> = mutableSetOf(StarBlock(start, null, start, end))
    private var candidateLocs: MutableSet<Pos> = mutableSetOf()
    private val maxIterations: Int = 20000
    private var iterations: Int = 0

    private val vectorIterations = setOf<Vec>(
        Vec(1.0, 0.0, 0.0),
        Vec(-1.0, 0.0, 0.0),
        Vec(0.0, 0.0, 1.0),
        Vec(0.0, 0.0, -1.0),
        Vec(1.0, 1.0, 0.0),
        Vec(-1.0, 1.0, 0.0),
        Vec(0.0, 1.0, 1.0),
        Vec(0.0, 1.0, -1.0),
        Vec(1.0, -1.0, 0.0),
        Vec(-1.0, -1.0, 0.0),
        Vec(0.0, -1.0, 1.0),
        Vec(0.0, -1.0, -1.0),
    )


    private fun getBestCandidate(): StarBlock {
        val sort = candidates.sortedBy { it.t }

        return sort[0]
    }

    private fun getBlockFromLoc(loc: Pos): StarBlock? {
        val sort = candidates.sortedBy { it.loc == loc }

        val last = sort[sort.size-1]

        return if (last.loc == loc) {
            last
        } else {
            null
        }
    }

    fun pathfind(): List<Pos> {
        //instance.setBlock(end, Block.DIAMOND_BLOCK)

        var block = getBestCandidate()

        while (getValueDist(block.loc, end) > 20) {
            check(block)
            block = getBestCandidate()
            iterations += 1
            if (iterations > maxIterations) {
                instance.sendMessage(Component.text("reached max iteration number"))
                return emptyList()
            }
        }
        val ret: MutableList<Pos> = mutableListOf(block.loc)

        var curr = block

        while (curr.parent != null) {
            ret += curr.loc
            curr = curr.parent!!
        }

        return ret.toList()
    }

    private fun check(block: StarBlock) {
        candidates -= block
        candidateLocs -= block.loc
        checkedBlocks += block
        checkedLocs += block.loc
        //instance.setBlock(block.loc, Block.RED_STAINED_GLASS)

        for (vec in vectorIterations) {
            val newloc = block.loc.add(vec)

            if (newloc in checkedLocs) {
                continue
            }

            if (newloc in candidateLocs) {
                val checked = getBlockFromLoc(newloc)!!

                val newh = block.t + getValueDist(block.loc, newloc)

                if (newh < checked.h) {
                    checked.changeH(newh)
                }

                continue
            }

            if (valid(newloc)) {
                candidates += StarBlock(newloc, block, start, end)
                candidateLocs += newloc
                //instance.setBlock(newloc, Block.YELLOW_STAINED_GLASS)
            }
        }
    }

    private fun valid(loc: Pos): Boolean {
        if (instance.getBlock(loc).isSolid || instance.getBlock(loc.add(0.0, 1.0, 0.0)).isSolid) {
            return false
        }
        if (!instance.getBlock(loc.add(0.0, -1.0, 0.0)).isSolid) {
            return false
        }
        return true
    }
}