package com.eddiep.muse.handlers.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.eddiep.muse.MuseFun
import com.eddiep.muse.handlers.CoopMuseHandler
import com.eddiep.muse.handlers.DualMuseHandler
import com.eddiep.muse.handlers.OnePlayerMuseHandler
import com.eddiep.muse.render.Text
import com.eddiep.muse.render.scene.AbstractScene

class MenuScene : AbstractScene() {
    private lateinit var header: Text;
    private lateinit var stage: Stage;
    override fun onInit() {
        header = Text(72, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Bold.ttf"));
        header.x = 512f
        header.y = 520f
        header.text = "MUSE\nINVADER"
        header.load()

        stage = Stage(
                ScalingViewport(Scaling.stretch, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), OrthographicCamera()),
                MuseFun.getInstance().batch
        )
        Gdx.input.inputProcessor = stage

        val skin = Skin(Gdx.files.internal("sprites/ui/uiskin.json"))

        var table = Table()
        table.width = 800f
        table.height = 400f
        table.x = (1920f / 2f) - (table.width / 2f)
        table.y = 300f - (table.height / 2f)
        stage.addActor(table)

        val button = TextButton("SINGLE PLAYER", skin)
        val button2 = TextButton("CO-OP", skin)
        val button3 = TextButton("DUAL", skin)
        button.label.setFontScale(4f)
        button2.label.setFontScale(4f)
        button3.label.setFontScale(4f)
        table.add(button).width(450f).height(140f).padBottom(60f)
        table.row()
        table.add(button2).width(450f).height(140f).padBottom(60f)
        table.row()
        table.add(button3).width(450f).height(140f)

        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val handler = OnePlayerMuseHandler()
                MuseFun.getInstance().handler = handler
                MuseFun.getInstance().clearScreen()
                handler.start()
            }
        })

        button2.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val handler = CoopMuseHandler()
                MuseFun.getInstance().handler = handler
                MuseFun.getInstance().clearScreen()
                handler.start()
            }
        })

        button3.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val handler = DualMuseHandler()
                MuseFun.getInstance().handler = handler
                MuseFun.getInstance().clearScreen()
                handler.start()
            }
        })

        //table.debug = true
    }

    override fun render(camera: OrthographicCamera, batch: SpriteBatch) {
        batch.begin()
        header.draw(batch)
        batch.end()

        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

}