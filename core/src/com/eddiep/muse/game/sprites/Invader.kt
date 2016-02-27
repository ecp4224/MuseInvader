package com.eddiep.muse.game.sprites

import com.eddiep.muse.game.Entity
import com.eddiep.muse.utils.Global
import com.eddiep.muse.utils.Vector2f

class Invader(val texture: String) : Entity(texture, 0) {
    override fun onLoad() {
        super.onLoad()

        velocity = Vector2f(0f, -0.3f)
        minVelocity = Vector2f(0f, -0.4f)

        currentAnimation.currentFrame = Global.RANDOM.nextInt(currentAnimation.framecount)
    }

    override fun tick() {
        super.tick()

        if (y < -48) {
            parentScene.removeEntity(this)
        }
    }
}