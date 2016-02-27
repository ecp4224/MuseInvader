package com.eddiep.muse;

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.eddiep.muse.logic.Handler
import com.eddiep.muse.logic.LogicHandler
import com.eddiep.muse.logic.Logical
import com.eddiep.muse.render.scene.Scene
import java.util.*

class MuseClient(var handler : Handler) : ApplicationAdapter() {
    public lateinit var batch : SpriteBatch; //We need to delay this
    private var loaded : Boolean = false;
    lateinit var camera : OrthographicCamera; //We need to delay this
    private val logicalHandler = LogicHandler()
    private val scenes = ArrayList<Scene>()
    public lateinit var world : World;

    override fun create() {
        batch = SpriteBatch()
        camera = OrthographicCamera(1024f, 720f)
        camera.setToOrtho(false, 1024f, 720f)

        world = World(Vector2(0f, 0f), true)


        logicalHandler.init()

        handler.start()
    }

    override fun render() {
        try {
            _render()
        } catch (t: Throwable) {
        }
    }

    fun _render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        logicalHandler.tick(handler, world)

        camera.update()

        batch.projectionMatrix = camera.combined;

        for (scene in scenes) {
            if (scene.isVisible) {
                scene.render(camera, batch)
            }
        }
    }

    public fun addScene(scene: Scene) {
        if (!scenes.contains(scene)) {
            Gdx.app.postRunnable {
                scene.init()
                scenes.add(scene)

                Collections.sort(scenes, { o1, o2 -> o2.requestedOrder() - o1.requestedOrder() })
            }
        }
    }

    public fun removeScene(scene: Scene) {
        if (scenes.contains(scene)) {
            scene.dispose()
            Gdx.app.postRunnable {
                scenes.remove(scene)
            }
        }
    }

    public fun addLogical(logic: Logical) {
        logicalHandler.addLogical(logic)
    }

    public fun removeLogical(logic: Logical) {
        logicalHandler.removeLogical(logic)
    }

    fun clearScreen() {
        Gdx.app.postRunnable {
            for (scene in scenes) {
                removeScene(scene)
            }
            logicalHandler.clear()
        }
    }

    override fun dispose() {
        world.dispose()

        for (scene in scenes) {
            removeScene(scene)
        }
    }
}
