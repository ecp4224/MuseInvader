package com.eddiep.muse.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eddiep.muse.handlers.scenes.SpriteScene;

public interface Drawable {
    void draw(SpriteBatch batch);

    void load();

    void unload();

    Blend blendMode();

    boolean hasLighting();

    int getZIndex();

    SpriteScene getParentScene();

    void setParentScene(SpriteScene scene);
}
