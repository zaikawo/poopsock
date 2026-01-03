package demo.entities

import demo.player.getSupportingBlock
import net.kyori.adventure.text.Component
import java.lang.Thread.sleep

interface State {
    fun processState(entity: GameEntity): State {
        return this
    }
}

class IdleState : State {
    override fun processState(entity: GameEntity): State {
        val seen = entity.getSeenPlayer()
        if (seen == null) {
            entity.target = null
            entity.puppet.navigator.setPathTo(null)
            return this
        } else {
            entity.target = seen
            return ChaseState()
        }
    }
}

class ChaseState : State {
    override fun processState(entity: GameEntity): State {
        val seen = entity.getSeenPlayer()
        if (seen == null) {
            entity.target = null
            return IdleState()
        } else if (entity.puppet.getDistanceSquared(seen) < 1) {
            return AttackState()
        } else {
            entity.target = seen
            entity.puppet.navigator.setPathTo(seen.getSupportingBlock(), 0.3, 100.0, 1.0, null)
            return this
        }
    }
}

class AttackState : State {
    override fun processState(entity: GameEntity): State {
        entity.attackFunc(entity)
        return ChaseState()
    }
}

class StateMachine (
    private var entity: GameEntity,
    var currentState: State
) {
    fun tick() {
        currentState = currentState.processState(entity)
        //entity.puppet.instance.sendMessage(Component.text("current state: $currentState"))
    }
}