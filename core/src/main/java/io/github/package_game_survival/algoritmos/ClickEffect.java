package io.github.package_game_survival.algoritmos;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ClickEffect extends Actor {

    private Animation<TextureRegion> animation;
    private float stateTime = 0f;

    public ClickEffect(Animation<TextureRegion> animation, float x, float y) {
        this.animation = animation;

        TextureRegion frame = animation.getKeyFrame(0);
        if (frame != null) {
            setSize(frame.getRegionWidth(), frame.getRegionHeight());
        }

        setPosition(x - (getWidth() / 2), y - (getHeight() / 2));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (animation.isAnimationFinished(stateTime)) {
            this.remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(animation.getKeyFrame(stateTime), getX(), getY(), getWidth(), getHeight());
    }
}
