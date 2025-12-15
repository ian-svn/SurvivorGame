package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Hoguera extends Objeto {

    private Jugador jugador;
    private final float RADIO_CALOR = 180f; // Distancia para quitar el frío
    private final int DANIO_FUEGO = 10;     // Daño al tocarla
    private final Vector2 centroAux = new Vector2();

    // Animación
    private Animation<TextureRegion> animacionFuego;
    private float stateTime = 0f;

    // Cooldown interno para no spamear llamadas innecesarias (aunque el jugador tenga invulnerabilidad)
    private float tiempoParaQuemar = 0f;

    public Hoguera(float x, float y) {
        super("Hoguera", x, y, 32, 40, null);
        this.setDesaparecible(false);

        TextureAtlas atlas = Assets.get(PathManager.HOGUERA_ATLAS, TextureAtlas.class);
        // Buscamos "hoguera" o usamos todas las regiones si no hay nombre específico
        TextureAtlas.AtlasRegion region = atlas.findRegion("hoguera");
        if (region != null) {
            animacionFuego = new Animation<>(0.1f, atlas.findRegions("hoguera"), Animation.PlayMode.LOOP);
        } else {
            animacionFuego = new Animation<>(0.1f, atlas.getRegions(), Animation.PlayMode.LOOP);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (jugador != null) {
            // 1. Lógica de CALOR (Radio)
            centroAux.set(getX() + getWidth()/2, getY() + getHeight()/2);
            float distancia = centroAux.dst(jugador.getCentroX(), jugador.getY());

            if (distancia < RADIO_CALOR) {
                jugador.setSintiendoCalor(true);
            }

            // 2. Lógica de DAÑO (Contacto)
            // Usamos un pequeño timer (0.1s) para no saturar, pero el daño real
            // lo limita la invulnerabilidad del jugador.
            if (tiempoParaQuemar > 0) tiempoParaQuemar -= delta;

            if (tiempoParaQuemar <= 0 && this.getRectColision().overlaps(jugador.getRectColision())) {
                // Intentamos hacer daño. Si el jugador es invulnerable, el método alterarVida lo ignorará.
                // Si ya se le pasó la invulnerabilidad, recibirá el daño y se volverá invulnerable de nuevo.
                jugador.alterarVida(-DANIO_FUEGO);
                tiempoParaQuemar = 0.5f; // Reintentar en 0.5s
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = null;
        if (animacionFuego != null) {
            currentFrame = animacionFuego.getKeyFrame(stateTime);
        }

        if (currentFrame != null) {
            Color c = getColor();
            batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
            batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
        this.toBack();
        this.jugador = mundo.getJugador();
    }

    @Override
    public void adquirir() { }
}
