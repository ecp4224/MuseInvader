package com.eddiep.muse.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.eddiep.muse.MuseFun
import com.eddiep.muse.game.MuseHandler
import com.eddiep.muse.game.sprites.InvaderCreator
import com.eddiep.muse.game.sprites.Player
import com.eddiep.muse.handlers.scenes.LoadingScene
import com.eddiep.muse.handlers.scenes.SpriteScene
import com.eddiep.muse.handlers.scenes.TextOverlayScene
import com.eddiep.muse.logic.Handler
import com.eddiep.muse.render.Text
import com.eddiep.muse.utils.Global
import com.eddiep.muse.utils.Vector2f
import com.interaxon.libmuse.MuseManager

class DualMuseHandler : Handler {
    val player1Color : Color = Color(0f, 0.341176471f, 0.7725490196f, 1f)
    val player2Color : Color = Color(0.7725490196f, 0f, 0f, 1f)

    private lateinit var headsetOnText : TextOverlayScene;
    private lateinit var statsText : Text;
    private lateinit var score : Text;
    private lateinit var score2 : Text;
    private lateinit var player : Player
    private lateinit var player2 : Player
    private var beta = 0.0
    private var beta2 = 0.0
    private var start = System.currentTimeMillis()
    private lateinit var museHandler : MuseHandler
    private lateinit var museHandler2 : MuseHandler
    lateinit var world : SpriteScene;

    override fun start() {
        val loading = LoadingScene()
        MuseFun.getInstance().addScene(loading)

        world = SpriteScene()
        world.isVisible = false
        MuseFun.getInstance().addScene(world)

        statsText = Text(48, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Bold.ttf"));
        statsText.text = "Waiting for data.."
        statsText.x = 960f
        statsText.y = 720f - 60f

        score = Text(32, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Bold.ttf"));
        score.text = "Player 1: 0"
        score.x = 70f
        score.y = 900f

        score2 = Text(32, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Bold.ttf"));
        score2.text = "Player 2: 0"
        score2.x = 70f
        score2.y = 800f

        world.addEntity(score)
        world.addEntity(score2)
        world.addEntity(statsText)

        headsetOnText = TextOverlayScene("Headset 404!", "Please put on the headset", false)
        headsetOnText.isVisible = false
        MuseFun.getInstance().addScene(headsetOnText)

        loading.setLoadedCallback(Runnable {
            loading.setText("Looking for MUSE...")
            MuseManager.refreshPairedMuses();
            val muses = MuseManager.getPairedMuses()
            if (muses.size == 0) {
                loading.setText("No MUSES found :(")
                return@Runnable
            }
            if (muses.size < 2) {
                loading.setText("Not enough MUSES :(")
                return@Runnable
            }
            loading.setText("MUSE Found!")
            Thread(Runnable {
                loading.setText("Connecting to MUSE..")
                val muse = muses[0]
                val muse2 = muses[1]


                museHandler = MuseHandler(muse)
                museHandler.setOnBlink {
                    if (begin)
                        player.fire()
                }
                museHandler.start()
                museHandler2 = MuseHandler(muse2)
                museHandler2.setOnBlink {
                    if (begin)
                        player2.fire()
                }
                museHandler2.start()
                ready = true

                player = Player()
                player.y = 64f
                player.x = 360f
                player.color = player1Color

                player2 = Player()
                player2.y = 720f - 64f
                player2.x = 1024f - 360f
                player2.rotation = 180f
                player2.color = player2Color

                start = System.currentTimeMillis()


                player2.enemy = player
                player.enemy = player2

                world.addEntity(player)
                world.addEntity(player2)

                world.isVisible = true
                loading.isVisible = false
            }).start()
        })
    }

    private var firstInvader = true
    private var tutorial = 0
    private var begin = false
    private var ready = false
    private var displayedTip = false
    override fun tick() {
        if (!ready)
            return

        headsetOnText.isVisible = !museHandler.isConnected && !museHandler2.isConnected
        world.isVisible = museHandler.isConnected && museHandler2.isConnected

        beta = museHandler.beta
        beta2 = museHandler2.beta

        if (beta > 0 && !begin) {
            begin = true
            System.out.println("HELLO WORLD")
            statsText.text = ""
        }

        if (!world.isVisible || !begin)
            return

        if (beta >= 0.3) {
            val pos = beta - 0.3
            player.acceleration = Vector2f(pos.toFloat(), 0f)
            player.maxVelocity = Vector2f(4f, 0f)
        } else {
            player.acceleration = Vector2f(-beta.toFloat(), 0f)
            player.minVelocity = Vector2f(-4f, 0f)
        }

        if (beta2 >= 0.3) {
            val pos = beta2 - 0.3
            player2.acceleration = Vector2f(pos.toFloat(), 0f)
            player2.maxVelocity = Vector2f(4f, 0f)
        } else {
            player2.acceleration = Vector2f(-beta2.toFloat(), 0f)
            player2.minVelocity = Vector2f(-4f, 0f)
        }

        if (player.x <= 32)
            player.x = 32f
        if (player.x >= 1024f - 32f) {
            player.x = 1024f - 32f
        }

        if (player2.x <= 32)
            player2.x = 32f
        if (player2.x >= 1024f - 32f) {
            player2.x = 1024f - 32f
        }

        score.text = "Player 1: ${player.score}"
        score2.text = "Player 2: ${player2.score}"
    }
}