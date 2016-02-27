package com.eddiep.muse.game.sprites

import com.badlogic.gdx.graphics.Color
import com.eddiep.muse.game.Entity
import com.eddiep.muse.utils.Vector2f

class Bullet(val player: Player) : Entity("sprites/ball.png", 0) {

    override fun onLoad() {
        super.onLoad()

        setScale(0.125f)
        color = Color(216 / 255f, 250 / 255f, 22 / 255f, 1f)
        velocity = Vector2f(0f, 6f)
        maxVelocity = Vector2f(0f, 6f)
    }

    override fun tick() {
        super.tick()

        if (y >= 740) {
            parentScene.removeEntity(this)
        }

        for (invader in InvaderCreator.getInvaders()) {
            if (Math.abs(x - invader.x) < 32f && Math.abs(y - invader.y) < 32f) {
                InvaderCreator.killInvader(invader)
                player.score += (invader.y / 4).toInt()
                parentScene.removeEntity(this)
                break
            }
        }
    }
}