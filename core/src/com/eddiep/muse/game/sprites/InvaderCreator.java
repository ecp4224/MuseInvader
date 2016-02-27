package com.eddiep.muse.game.sprites;

import com.badlogic.gdx.Gdx;
import com.eddiep.muse.MuseFun;
import com.eddiep.muse.game.animations.Animation;
import com.eddiep.muse.game.animations.Skin;
import com.eddiep.muse.utils.Global;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvaderCreator {
    private static final Gson GSON = new Gson();
    private static final String[] invaderTypes = new String[] {
            "green_invader"
    };
    private static final List<Invader> invaders = new ArrayList<Invader>();

    private String name;
    private byte id;
    private Animation[] animations;
    private Skin[] skins;
    private Skin currentSkin;

    public static Invader createInvader() {
        String json = Gdx.files.internal("sprites/invader.json").readString();

        InvaderCreator creator = GSON.fromJson(json, InvaderCreator.class);

        for (Animation animation : creator.animations) {
            animation.init();
        }

        Skin skin = creator.skins[Global.RANDOM.nextInt(creator.skins.length)];

        if (skin != null) {
            skin.applyTo(creator);
            creator.currentSkin = skin;
        }

        Invader invader = new Invader(skin.getTexturFile());
        invader.attachAnimations(creator.animations);
        invaders.add(invader);

        return invader;
    }

    public static List<Invader> getInvaders() {
        return Collections.unmodifiableList(invaders);
    }

    public static void killInvader(Invader invader) {
        invader.getParentScene().removeEntity(invader);
        invaders.remove(invader);
    }

    public Animation[] getAnimations() {
        return animations;
    }
}
