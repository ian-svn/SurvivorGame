package io.github.package_game_survival.desastres;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;

public class Charco extends Entidad {

    private final Texture textura;
    private Jugador jugador;

    private float cooldownDano = 0f;
    // BALANCE: Daño por pisar agua aumentado a 25
    private final int DANIO_AGUA = 15;
    private final Rectangle hitboxReducida = new Rectangle();

    public Charco(float x, float y, float ancho, float alto, Texture textura, Jugador jugador) {
        // "Charco" se pasa a Entidad para el Tooltip
        super("Charco", x, y, ancho, alto);
        this.textura = textura;
        this.jugador = jugador;
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        super.agregarAlMundo(mundo); // Crea el Tooltip
        this.toBack();
        if (mundo.getJugador() != null) {
            this.jugador = mundo.getJugador();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (cooldownDano > 0) cooldownDano -= delta;

        if (jugador != null && estaPisando(jugador)) {
            if (cooldownDano <= 0) {
                jugador.alterarVida(-DANIO_AGUA);
                cooldownDano = 0.5f;
            }
        }
    }

    @Override
    public Rectangle getRectColision() {
        hitboxReducida.set(getX() + 10, getY() + 10, getWidth() - 20, getHeight() - 20);
        return hitboxReducida;
    }

    public boolean estaPisando(Jugador j) {
        return this.getRectColision().overlaps(j.getRectColision());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1, 1, 1, 0.6f);
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());
        batch.setColor(1, 1, 1, 1);
    }
}
