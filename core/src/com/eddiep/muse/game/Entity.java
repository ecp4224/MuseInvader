package com.eddiep.muse.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eddiep.muse.MuseFun;
import com.eddiep.muse.game.animations.Animation;
import com.eddiep.muse.game.animations.AnimationType;
import com.eddiep.muse.handlers.scenes.SpriteScene;
import com.eddiep.muse.logic.Logical;
import com.eddiep.muse.render.Blend;
import com.eddiep.muse.render.Drawable;
import com.eddiep.muse.utils.Direction;
import com.eddiep.muse.utils.Vector2f;
import com.eddiep.muse.utils.annotations.InternalOnly;

import java.util.ArrayList;
import java.util.List;

public class Entity extends Sprite implements Drawable, Logical, Attachable {
    private int z;
    private boolean hasLoaded = false;
    private short id;

    private Vector2f velocity = new Vector2f(0f, 0f);
    private Vector2f target;

    private Animation animation;
    private List<Animation> animations = new ArrayList<Animation>();

    private Vector2f acceleration = new Vector2f(0f, 0f);
    private Vector2f maxVelocity = new Vector2f(0f, 0f);
    private Vector2f minVelocity = new Vector2f(0f, 0f);

    private Vector2f inter_target, inter_start;
    private long inter_duration, inter_timeStart;
    private boolean interpolate = false;
    private Blend blend = new Blend(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    private ArrayList<Attachable> children = new ArrayList<Attachable>();
    private ArrayList<Attachable> parents = new ArrayList<Attachable>();

    private final Object child_lock = new Object();
    private boolean lightable = true;
    private SpriteScene scene;

    public static Entity fromImage(String path) {
        Texture texture = MuseFun.ASSETS.get(path, Texture.class);
        Sprite sprite = new Sprite(texture);
        return new Entity(sprite, (short)0);
    }

    public static Entity fromImage(String path, short id) {
        Texture texture = MuseFun.ASSETS.get(path, Texture.class);
        Sprite sprite = new Sprite(texture);
        return new Entity(sprite, id);
    }

    public Entity(Sprite sprite, short id) {
        super(sprite);

        setOriginCenter();

        this.id = id;
    }

    protected Entity(String path, short id) {
        super(MuseFun.ASSETS.get(path, Texture.class));

        setOriginCenter();

        this.id = id;
    }

    @Override
    public Blend blendMode() {
        return blend;
    }

    @Override
    public boolean hasLighting() {
        return lightable;
    }

    @Override
    public int getZIndex() {
        return z;
    }

    @Override
    public SpriteScene getParentScene() {
        return scene;
    }

    @Override
    public void setParentScene(SpriteScene scene) {
        this.scene = scene;
    }

    public void setZIndex(int z) {
        this.z = z;
    }

    @Deprecated
    public void setHasLighting(boolean val) {
        this.lightable = val;

        //We need to reload this sprite now
        scene.removeEntity(this);
        scene.addEntity(this);
    }

    public Vector2f getMinVelocity() {
        return minVelocity;
    }

    public void setMinVelocity(Vector2f minVelocity) {
        this.minVelocity = minVelocity;
    }

    public void setBlend(Blend blend) {
        this.blend = blend;
    }

    public short getID() {
        return id;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public void setTarget(Vector2f target) {
        this.target = target;
    }

    public Vector2f getTarget() {
        return target;
    }

    public float getCenterX() {
        return getX() + (getWidth() / 2f);
    }

    public float getCenterY() {
        return getY() + (getWidth() / 2f);
    }

    public float getAlpha() { return getColor().a; }

    @InternalOnly
    public final void load() {
        onLoad();
        if (!hasLoaded)
            throw new IllegalStateException("super.onLoad() was not invoked!");
    }

    protected void onLoad() {
        hasLoaded = true;
    }

    @InternalOnly
    public final void unload() {
        onUnload();
        if (hasLoaded)
            throw new IllegalStateException("super.onUnload() was not invoked!");
    }

    protected void onUnload() {
        hasLoaded = false;
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    @Override
    public void setX(float x) {
        float dif = getX() - x;
        super.setX(x);

        synchronized (child_lock) {
            for (Attachable c : children) {
                c.setX(c.getX() - dif);
            }
        }
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);

        synchronized (child_lock) {
            for (Attachable c : children) {
                c.setAlpha(alpha);
            }
        }
    }

    @Override
    public void setY(float y) {
        float dif = getY() - y;
        super.setY(y);

        synchronized (child_lock) {
            for (Attachable c : children) {
                c.setY(c.getY() - dif);
            }
        }
    }

    public void attach(Attachable e) {
        synchronized (child_lock) {
            children.add(e);
        }
        e.addParent(this);
    }

    public void deattach(Attachable e) {
        synchronized (child_lock) {
            children.remove(e);
        }
        e.removeParent(this);
    }

    @Override
    public void addParent(Attachable e) {
        parents.add(e);
    }

    @Override
    public void removeParent(Attachable e) {
        parents.remove(e);
    }

    @Override
    public void tick() {
        if (!interpolate) {
            if (target != null) {
                if (Math.abs(getX() - target.x) < 8 && Math.abs(getY() - target.y) < 8) {
                    velocity.x = velocity.y = 0;
                }
            }

            setX(getX() + velocity.x);
            setY(getY() + velocity.y);
        } else {
            float x = ease(inter_start.x, inter_target.x, System.currentTimeMillis() - inter_timeStart, inter_duration);
            float y = ease(inter_start.y, inter_target.y, System.currentTimeMillis() - inter_timeStart, inter_duration);

            setX(x);
            setY(y);

            if (x == inter_target.x && y == inter_target.y) {
                interpolate = false;
            }
        }

        if (isFadingOut) {
            float alpha = ease(1f, 0f, fadeDuration, System.currentTimeMillis() - fadeStart);

            setAlpha(alpha);

            if (alpha == 0f) {
                isFadingOut = false;
                if (isFadeOutDespawn) {
                    scene.removeEntity(this);
                }
            }
        }

        velocity.x += acceleration.x;
        velocity.y += acceleration.y;

        velocity.x = Math.min(velocity.x, maxVelocity.x);
        velocity.y = Math.min(velocity.y, maxVelocity.y);

        velocity.x = Math.max(velocity.x, minVelocity.x);
        velocity.y = Math.max(velocity.y, minVelocity.y);

        if (animation != null) {
            animation.tick();
            setRegion(animation.getTextureRegion());
        }
    }

    public Vector2f getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2f acceleration) {
        this.acceleration = acceleration;
    }

    public Vector2f getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(Vector2f maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    @Override
    public void dispose() { }

    public void interpolateTo(float x, float y, long duration) {
        inter_start = new Vector2f(getX(), getY());
        inter_target = new Vector2f(x, y);
        inter_timeStart = System.currentTimeMillis();
        inter_duration = duration;
        interpolate = true;
    }

    //Code taken from: https://code.google.com/p/replicaisland/source/browse/trunk/src/com/replica/replicaisland/Lerp.java?r=5
    //Because I'm a no good dirty scrub
    public static float ease(float start, float target, float duration, float timeSinceStart) {
        float value = start;
        if (timeSinceStart > 0.0f && timeSinceStart < duration) {
            final float range = target - start;
            final float percent = timeSinceStart / (duration / 2.0f);
            if (percent < 1.0f) {
                value = start + ((range / 2.0f) * percent * percent * percent);
            } else {
                final float shiftedPercent = percent - 2.0f;
                value = start + ((range / 2.0f) *
                        ((shiftedPercent * shiftedPercent * shiftedPercent) + 2.0f));
            }
        } else if (timeSinceStart >= duration) {
            value = target;
        }
        return value;
    }

    public Animation getAnimation(AnimationType type, Direction direction) {
        for (Animation animation : animations) {
            if (animation.getType() == type && animation.getDirection() == direction) {
                return animation;
            }
        }
        return null;
    }

    public Vector2f getPosition() {
        return new Vector2f(getCenterX(), getCenterY());
    }

    private boolean isFadingOut;
    private boolean isFadeOutDespawn;
    private long fadeDuration;
    private long fadeStart;
    public void fadeOutAndDespawn(long arg) {
        this.fadeDuration = arg;
        this.isFadingOut = true;
        this.isFadeOutDespawn = true;
        fadeStart = System.currentTimeMillis();
    }

    public void fadeOut(long arg) {
        this.fadeDuration = arg;
        this.isFadingOut = true;
        fadeStart = System.currentTimeMillis();
    }

    public void attachAnimations(Animation... animations) {
        for (Animation animation : animations) {
            animation.attach(this);
            this.animations.add(animation);
        }

        this.animation = this.animations.get(0);
    }

    public Animation getCurrentAnimation() {
        return animation;
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        this.animation = currentAnimation;
        setSize(this.animation.getWidth(), this.animation.getHeight());
    }
}
