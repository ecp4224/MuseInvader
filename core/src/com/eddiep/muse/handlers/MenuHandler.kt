package com.eddiep.muse.handlers

import com.eddiep.muse.MuseFun
import com.eddiep.muse.handlers.scenes.MenuScene
import com.eddiep.muse.logic.Handler

class MenuHandler : Handler {
    override fun start() {
        val menu = MenuScene()
        MuseFun.getInstance().addScene(menu)
    }

    override fun tick() {

    }

}