package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Hoguera extends Objeto {

    private Jugador jugador;
    private final float RADIO_CALOR = 180f;
    private final int DANIO_FUEGO = 10;
    private final Vector2 centroAux = new Vector2();

    private Animation<TextureRegion> animacionFuego;
    private float stateTime = 0f;
    private float tiempoParaQuemar = 0f;

    private final Rectangle hitboxBase = new Rectangle();

    public Hoguera(float x, float y) {
        super("Hoguera", x, y, 32, 44, null);

        this.setDesaparecible(false);

        // Carga de animación
        TextureAtlas atlas = Assets.get(PathManager.HOGUERA_ATLAS, TextureAtlas.class);
        TextureAtlas.AtlasRegion region = atlas.findRegion("hoguera");
        if (region != null) {
            animacionFuego = new Animation<>(0.1f, atlas.findRegions("hoguera"), Animation.PlayMode.LOOP);
        } else {
            animacionFuego = new Animation<>(0.1f, atlas.getRegions(), Animation.PlayMode.LOOP);
        }
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        // CRÍTICO: Llamamos al padre para que cree el tooltip
        super.agregarAlMundo(mundo);

        this.jugador = mundo.getJugador();
        this.toBack(); // Que se dibuje detrás del jugador si coinciden
    }

    @Override
    public void act(float delta) {
        super.act(delta); // Actualiza tooltip
        stateTime += delta;

        if (jugador != null) {
            // Lógica Calor
            centroAux.set(getX() + getWidth()/2, getY() + getHeight()/2);
            if (centroAux.dst(jugador.getCentroX(), jugador.getY()) < RADIO_CALOR) {
                jugador.setSintiendoCalor(true);
            }

            // Lógica Daño
            if (tiempoParaQuemar > 0) tiempoParaQuemar -= delta;
            if (tiempoParaQuemar <= 0 && this.getRectColision().overlaps(jugador.getRectColision())) {
                jugador.alterarVida(-DANIO_FUEGO);
                tiempoParaQuemar = 0.5f;
            }
        }
    }

    @Override
    public Rectangle getRectColision() {
        // Hitbox pequeña en la base
        float wHit = 24;
        float hHit = 20;
        hitboxBase.set(getX() + (getWidth() - wHit) / 2, getY(), wHit, hHit);
        return hitboxBase;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = null;
        if (animacionFuego != null) currentFrame = animacionFuego.getKeyFrame(stateTime);

        if (currentFrame != null) {
            Color c = getColor();
            batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
            batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
    }

    @Override public void adquirir() { } // No se puede recoger
}
