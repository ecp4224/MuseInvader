package com.eddiep.muse.game.sprites

import com.badlogic.gdx.graphics.Color
import com.eddiep.muse.game.Entity

class Player : Entity("sprites/player.png", 0) {
    var score = 0;
    var enemy : Player? = null
    override fun onLoad() {
        super.onLoad()

        setScale(2f)
    }

    fun fire() {
        val bullet1 = Bullet(this)
        val bullet2 = Bullet(this)
        bullet1.enemy = enemy
        bullet2.enemy = enemy

        bullet1.y = y + (height / 2f)
        bullet2.y = y + (height / 2f)
        bullet1.x = x + (width / 2f)
        bullet2.x = x - (width / 2f)

        parentScene.addEntity(bullet1)
        parentScene.addEntity(bullet2)
    }
}