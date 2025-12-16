package io.github.package_game_survival.entidades.efectos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class EfectoVisual extends Actor {

    private Animation<TextureRegion> animation;
    private float stateTime = 0f;
    private boolean loop = false;

    public EfectoVisual(TextureAtlas atlas, String regionName, float frameDuration, boolean loop) {
        // Busca la región, si no la encuentra usa todas las regiones del atlas por defecto
        var regiones = atlas.findRegions(regionName);
        if (regiones == null || regiones.isEmpty()) {
            regiones = atlas.getRegions();
        }

        this.animation = new Animation<>(frameDuration, regiones, loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        this.loop = loop;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        // Si la animación terminó y no es loop, eliminamos el actor
        if (!loop && animation.isAnimationFinished(stateTime)) {
            this.remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime);
        if (currentFrame != null) {
            Color c = getColor();
            batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);

            // Dibujamos respetando rotación y escala (importante para el arañazo)
            batch.draw(currentFrame,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation()
            );

            batch.setColor(Color.WHITE);
        }
    }
}
