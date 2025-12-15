package io.github.package_game_survival.desastres;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;

public class Charco extends Entidad {

    private final Texture textura;
    private final Jugador jugador;

    private float cooldownDano = 0f;
    private final int DANIO_AGUA = 10; // Daño solicitado
    private final Rectangle hitboxReducida = new Rectangle();

    public Charco(float x, float y, float ancho, float alto, Texture textura, Jugador jugador) {
        super("Charco", x, y, ancho, alto);
        this.textura = textura;
        this.jugador = jugador;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (cooldownDano > 0) cooldownDano -= delta;

        if (jugador != null && estaPisando(jugador)) {
            // Si el cooldown interno del charco terminó, intentamos dañar.
            if (cooldownDano <= 0) {
                // Al llamar alterarVida:
                // 1. Si el jugador es invulnerable -> No pasa nada.
                // 2. Si NO es invulnerable -> Recibe 10 daño y se activa su invulnerabilidad (ej. 1.5s).
                jugador.alterarVida(-DANIO_AGUA);

                // Ponemos un pequeño cooldown local para no spamear colisiones en cada frame
                cooldownDano = 0.5f;
            }
        }
    }

    @Override
    public Rectangle getRectColision() {
        // Hitbox un poco más chica para ser justos visualmente
        hitboxReducida.set(getX() + 10, getY() + 10, getWidth() - 20, getHeight() - 20);
        return hitboxReducida;
    }

    public boolean estaPisando(Jugador j) {
        return this.getRectColision().overlaps(j.getRectColision());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1, 1, 1, 0.6f); // Semi-transparente
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());
        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
    }
}
