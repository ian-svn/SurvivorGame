package io.github.package_game_survival.entidades;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import io.github.package_game_survival.interfaces.Colisionable;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Entidad extends Actor implements Colisionable, Disposable {

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

    // --- NUEVO MÉTODO DELETE ---
    // Este es el método que usaremos para eliminar entidades de forma segura
    public void delete() {
        this.remove(); // Se quita del Stage (Actor)

        // Aquí podrías agregar lógica extra como soltar recursos, partículas de muerte, etc.
    }

    public abstract void agregarAlMundo(IMundoJuego mundo);

    @Override
    public Rectangle getRectColision() {
        if (hitbox == null) {
            hitbox = new Rectangle(getX(), getY(), getWidth(), getHeight());
        }
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }

    public String getNombre() { return getName(); }
    public float getAncho() { return getWidth(); }
    public float getAlto() { return getHeight(); }
    public TooltipStandard getTooltip() { return tooltip; }
    public void instanciarTooltip(TooltipStandard tooltip) { this.tooltip = tooltip; }

    @Override
    public void dispose() { }
}
