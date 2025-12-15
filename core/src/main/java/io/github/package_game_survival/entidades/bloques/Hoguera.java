package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;

public class Hoguera extends BloqueAnimado {

    private float tiempoAcumuladoDanio = 0f;
    private static final float INTERVALO_DANIO = 0.1f;
    private static final int CANTIDAD_DANIO = 1;

    // 4 bloques * 32 pixeles = 128f
    private static final float RADIO_CALOR = 128f;

    public Hoguera(float x, float y, TextureAtlas atlas) {
        super(x, y, "hoguera");
        TextureRegion[] frames = atlas.findRegions("hoguera").toArray(TextureRegion.class);
        this.animacion = new Animation<>(0.1f, frames);
        this.animacion.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getStage() == null) return;

        for (Actor actor : getStage().getActors()) {
            if (actor instanceof Jugador) {
                Jugador jugador = (Jugador) actor;

                // 1. LÓGICA DE DAÑO (Contacto)
                if (this.getRectColision().overlaps(jugador.getRectColision())) {
                    procesarDanio(jugador, delta);
                } else {
                    tiempoAcumuladoDanio = INTERVALO_DANIO;
                }

                // 2. LÓGICA DE CALOR (Proximidad)
                float centroX_Hoguera = getX() + getWidth() / 2;
                float centroY_Hoguera = getY() + getHeight() / 2;

                float dist = Vector2.dst(centroX_Hoguera, centroY_Hoguera, jugador.getCentroX(), jugador.getY() + jugador.getHeight()/2);

                if (dist <= RADIO_CALOR) {
                    jugador.setSintiendoCalor(true);
                }

                break;
            }
        }
    }

    private void procesarDanio(Jugador jugador, float delta) {
        tiempoAcumuladoDanio += delta;
        if (tiempoAcumuladoDanio >= INTERVALO_DANIO) {
            jugador.alterarVida(-CANTIDAD_DANIO);
            tiempoAcumuladoDanio -= INTERVALO_DANIO;
        }
    }
}
