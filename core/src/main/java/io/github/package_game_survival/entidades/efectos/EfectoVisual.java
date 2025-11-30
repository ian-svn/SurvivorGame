package io.github.package_game_survival.entidades.efectos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.interfaces.IMundoJuego;

public class EfectoVisual extends Entidad {

    private final Animation<TextureRegion> animacion;
    private float tiempoEstado = 0f;

    public EfectoVisual(TextureAtlas atlas, String regionName, float x, float y, float frameDuration, float angulo) {
        super("Efecto_" + regionName, x, y, 0, 0);

        Array<AtlasRegion> regions = atlas.findRegions(regionName);

        // Si no encuentra el grupo, buscamos manualmente "nombre1", "nombre2"...
        if (regions == null || regions.isEmpty()) {
            regions = new Array<>();
            int i = 1;
            AtlasRegion r;
            while ((r = atlas.findRegion(regionName + i)) != null) {
                regions.add(r);
                i++;
            }
            // Intento final: buscar el nombre exacto sin números
            if (regions.isEmpty()) {
                r = atlas.findRegion(regionName);
                if (r != null) regions.add(r);
            }
        }

        if (regions.isEmpty()) {
            //Gdx.app.error("EFECTO_VISUAL", "¡ERROR! No se encontró la textura: " + regionName);
            this.animacion = null;
        } else {
            this.animacion = new Animation<>(frameDuration, regions, Animation.PlayMode.NORMAL);

            TextureRegion primerFrame = regions.first();
            setSize(primerFrame.getRegionWidth(), primerFrame.getRegionHeight());
            setOrigin(getWidth() / 2, getHeight() / 2);

        }

        setRotation(angulo);
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
        this.toFront(); // ASEGURA QUE SE DIBUJE ENCIMA DE TODO
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (animacion == null) {
            remove();
            return;
        }
        tiempoEstado += delta;
        if (animacion.isAnimationFinished(tiempoEstado)) {
            remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (animacion == null) return;
        TextureRegion frame = animacion.getKeyFrame(tiempoEstado, false);

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        batch.draw(frame,
            getX(), getY(),
            getOriginX(), getOriginY(),
            getWidth(), getHeight(),
            getScaleX(), getScaleY(),
            getRotation());

        batch.setColor(Color.WHITE);
    }
}
