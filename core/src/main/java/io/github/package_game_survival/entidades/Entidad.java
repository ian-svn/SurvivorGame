package io.github.package_game_survival.entidades;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import io.github.package_game_survival.interfaces.Colisionable;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Entidad extends Actor implements Colisionable, Disposable {

    // No es final para permitir Lazy Init (evita crash en constructores)
    private Rectangle hitbox;
    private TooltipStandard tooltip;

    public Entidad(String nombre, float x, float y, float ancho, float alto) {
        setName(nombre);
        setBounds(x, y, ancho, alto);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (tooltip != null) {
            tooltip.actualizarPosicion();
        }
    }

    /**
     * Método abstracto obligado para que cada entidad sepa cómo conectarse al mundo
     * (buscar al jugador, obtener bloques, agregarse a la UI, etc.)
     */
    public abstract void agregarAlMundo(IMundoJuego mundo);

    @Override
    public Rectangle getRectColision() {
        // LAZY INITIALIZATION:
        // Si la hitbox no existe (porque nos llamó el constructor del padre), la creamos.
        if (hitbox == null) {
            hitbox = new Rectangle(getX(), getY(), getWidth(), getHeight());
        }

        // Siempre actualizamos la posición antes de devolverla
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }

    public String getNombre() {
        return getName();
    }

    public float getAncho() {
        return getWidth();
    }

    public float getAlto() {
        return getHeight();
    }

    public TooltipStandard getTooltip() {
        return tooltip;
    }

    public void instanciarTooltip(TooltipStandard tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void dispose() {
        // Implementación por defecto vacía.
        // Las clases hijas sobrescriben esto si tienen texturas propias que liberar.
    }
}
