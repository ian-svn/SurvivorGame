package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.interfaces.IMundoJuego; // Interfaz
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Bloque extends Entidad {

    public static final int ANCHO = 32;
    public static final int ALTO = 32;
    public boolean transitable = false;

    // Agrego esto para optimizar getRectColision en hijos
    protected Rectangle hitbox;

    public Bloque(float x, float y, String nombre) {
        super(nombre, x, y, ANCHO, ALTO);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Nota: Aquí deberías dibujar tu textura si la tienes.
        // getTooltip().actualizarPosicion(); -> Esto mejor moverlo al act()
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(getTooltip() != null) getTooltip().actualizarPosicion();
    }

    // --- NUEVA IMPLEMENTACIÓN DE INTERFAZ ---
    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);

        // Casteo seguro para Tooltip si requiere Escenario concreto
        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName(), this, (Escenario) mundo));
        }
    }

    public boolean isTransitable() {
        return transitable;
    }

    @Override
    public Rectangle getRectColision() {
        if (hitbox == null) {
            hitbox = new Rectangle(getX(), getY(), ANCHO, ALTO);
        }
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }
}
