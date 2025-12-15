package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import io.github.package_game_survival.managers.Assets;

public abstract class BloqueAnimado extends Bloque {

    protected Animation<TextureRegion> animacion;
    protected float stateTime;

    public BloqueAnimado(float x, float y, String nombre) {
        super(x, y, nombre);
        this.stateTime = 0f;
        this.transitable = true; // Generalmente las hogueras se pueden pisar (para quemarse)

        // Aseguramos que el tamaño lógico sea 32x32
        setSize(ANCHO, ALTO);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (animacion == null) return;

        TextureRegion currentFrame = animacion.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Factoría estática para convertir Tiles de Tiled en Actores animados.
     */
    public static BloqueAnimado verificarCreacion(TiledMapTile tile, float worldX, float worldY) {
        if (tile.getProperties().containsKey("bloqueAnimado") &&
            tile.getProperties().get("bloqueAnimado", Boolean.class)) {

            String nombre = tile.getProperties().get("nombre", String.class);
            if (nombre == null) return null;

            switch (nombre) {
                case "hoguera":
                        TextureAtlas atlasHoguera = Assets.get("atlas/hoguera.atlas", TextureAtlas.class);
                        return new Hoguera(worldX, worldY, atlasHoguera);

                default:
                    return null;
            }
        }
        return null;
    }
}
