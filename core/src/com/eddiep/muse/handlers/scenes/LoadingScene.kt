package com.eddiep.muse.handlers.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.eddiep.muse.MuseFun
import com.eddiep.muse.render.Text
import com.eddiep.muse.render.scene.AbstractScene

public class LoadingScene : AbstractScene() {
    private lateinit var progressBarBack : Sprite;
    private lateinit var progressBarFront : Sprite;
    private lateinit var progressText : Text
    private var didCall = false
    private var onFinished = Runnable {  }

    override fun onInit() {
        var back = Texture("sprites/progress_back.png")
        var front = Texture("sprites/progress_front.png");

        progressBarBack = Sprite(back)
        progressBarFront = Sprite(front)

        progressBarFront.setCenter(512f, 32f)
        progressBarBack.setCenter(512f, 32f)

        progressBarFront.setOriginCenter()
        progressBarBack.setOriginCenter()

        progressText = Text(36, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Light.ttf"));
        progressText.x = 512f
        progressText.y = 360f
        progressText.text = "Loading"
        progressText.load()

        requestOrder(1)
        MuseFun.loadGameAssets(MuseFun.ASSETS)
    }

    override fun render(camera: OrthographicCamera, batch: SpriteBatch) {
        var temp = MuseFun.ASSETS.progress * 720f

        progressBarFront.setSize(temp, 16f)

        batch.begin()

        progressText.draw(batch)
        progressBarBack.draw(batch)
        progressBarFront.draw(batch)

        batch.end()
        if (MuseFun.ASSETS.update() && !didCall) {
            onFinished.run()
            didCall = true
        }
    }

    override fun dispose() {

    }

    public fun setText(text: String) {
        progressText.text = text
    }

    public fun setLoadedCallback(callback: Runnable) {
        this.onFinished = callback
    }
}
