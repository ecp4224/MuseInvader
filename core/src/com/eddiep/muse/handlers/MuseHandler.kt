package com.eddiep.muse.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.eddiep.muse.MuseFun
import com.eddiep.muse.game.sprites.InvaderCreator
import com.eddiep.muse.game.sprites.Player
import com.eddiep.muse.handlers.scenes.LoadingScene
import com.eddiep.muse.handlers.scenes.SpriteScene
import com.eddiep.muse.handlers.scenes.TextOverlayScene
import com.eddiep.muse.logic.Handler
import com.eddiep.muse.render.Text
import com.eddiep.muse.utils.Global
import com.eddiep.muse.utils.Vector2f
import com.interaxon.libmuse.*

class MuseHandler : Handler, MuseDataListener() {
    private lateinit var headsetOnText : TextOverlayScene;
    private lateinit var statsText : Text;
    private lateinit var score : Text;
    private lateinit var player : Player
    private var lastUpdate = System.currentTimeMillis()
    private var mellow = 0.0
    private var concentration = 0.0
    private var beta = 0.0
    private var start = System.currentTimeMillis()
    private var lastSpawn = System.currentTimeMillis()
    private var nextSpawn = 0L

    override fun receiveMuseDataPacket(p0: MuseDataPacket?) {
        if (p0 == null)
            return

        if (p0.packetType == MuseDataPacketType.MELLOW) {
            val data = p0.values

            mellow = data[0]

            lastUpdate = System.currentTimeMillis()
        }
        if (p0.packetType == MuseDataPacketType.CONCENTRATION) {
            val data = p0.values

            concentration = data[0]

            lastUpdate = System.currentTimeMillis()
        }
        if (p0.packetType == MuseDataPacketType.BETA_SCORE) {
            val data = p0.values

            var sum = 0.0
            for (i in data) {
                sum += i
            }
            sum /= data.size.toDouble()

            beta = sum

            lastUpdate = System.currentTimeMillis()

            if (beta > 0 && !begin) {
                begin = true
                System.out.println("HELLO WORLD")
            }

            if (beta == 0.0 && System.currentTimeMillis() - start >= 25000 && !begin) {
                statsText.text = "I can't get a good signal :("
            }
        }
    }

    override fun receiveMuseArtifactPacket(p0: MuseArtifactPacket?) {
        if (p0 == null)
            return

        if (p0.headbandOn && p0.blink && begin) {
            player.fire()
        }

        if (p0.headbandOn && headsetOnText.isVisible) {
            start = System.currentTimeMillis()
        }

        headsetOnText.isVisible = !p0.headbandOn
        world.isVisible = p0.headbandOn
    }

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
        score.text = "Score: 0"
        score.x = 70f
        score.y = 900f

        world.addEntity(score)
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
            loading.setText("MUSE Found!")
            Thread(Runnable {
                loading.setText("Connecting to MUSE..")
                val muse = muses[0]
                try {
                    muse.runAsynchronously();
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                muse.registerDataListener(this,
                        MuseDataPacketType.ACCELEROMETER);
                muse.registerDataListener(this, MuseDataPacketType.MELLOW);
                muse.registerDataListener(this, MuseDataPacketType.CONCENTRATION);
                muse.registerDataListener(this, MuseDataPacketType.BETA_SCORE);
                muse.registerDataListener(this, MuseDataPacketType.ALPHA_SCORE);
                muse.registerDataListener(this,
                        MuseDataPacketType.ARTIFACTS);
                muse.registerDataListener(this,
                        MuseDataPacketType.BATTERY);
                muse.setPreset(MusePreset.PRESET_14);
                muse.enableDataTransmission(true);

                player = Player()
                player.y = 64f
                player.x = 360f

                start = System.currentTimeMillis()

                world.addEntity(player)

                world.isVisible = true
                loading.isVisible = false
            }).start()
        })
    }

    private var firstInvader = true
    private var tutorial = 0
    private var begin = false
    private var displayedTip = false
    override fun tick() {
        if (!world.isVisible || !begin)
            return

        if (tutorial == 0) {
            statsText.text = "Start concentrating on the ship\nFill your mind with thought"
        } else if (tutorial == 1) {
            statsText.text = "..now relax..\nEmpty your mind"
        } else if (tutorial == 2) {
            statsText.text = "Got it? Good!\nPractice for a little bit"
            nextSpawn = 8000L
            lastSpawn = System.currentTimeMillis()
            tutorial++
        }

        if (beta >= 0.3) {
            val pos = beta - 0.3
            player.acceleration = Vector2f(pos.toFloat(), 0f)
            player.maxVelocity = Vector2f(4f, 0f)

            if (beta >= 0.7 && tutorial == 0) {
                tutorial++;
            }
        } else {
            player.acceleration = Vector2f(-beta.toFloat(), 0f)
            player.minVelocity = Vector2f(-4f, 0f)

            if (tutorial == 1) {
                tutorial++;
            }
        }

        if (player.x <= 32)
            player.x = 32f
        if (player.x >= 1024f - 32f) {
            player.x = 1024f - 32f

            if (tutorial == 0)
                tutorial++
        }

        if (tutorial >= 3) {
            if (System.currentTimeMillis() - lastSpawn >= nextSpawn) {
                val count = if (firstInvader) 1 else Global.rand(1, 3)

                for (i in 0..count) {
                    val invader = InvaderCreator.createInvader()
                    invader.y = Global.rand(720 - 32, 900).toFloat()
                    invader.x = Global.rand(32, 1024 - 32).toFloat()
                    world.addEntity(invader)
                }

                if (firstInvader) {
                    statsText.text = "Look out!\nBlink to shoot!"
                    firstInvader = false
                }

                nextSpawn = Global.rand(11000, 20000).toLong()
                lastSpawn = System.currentTimeMillis()
                tutorial++

                if (tutorial == 6) {
                    statsText.text = ""
                }
            }
        }

        score.text = "Score: ${player.score}"
    }
}