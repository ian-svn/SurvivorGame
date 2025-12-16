package io.github.package_game_survival.entidades;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import io.github.package_game_survival.entidades.mapas.Escenario;
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

    public void delete() {
        this.remove();
    }

    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
        if (mundo instanceof Escenario) {
            if (this.tooltip == null) {
                this.tooltip = new TooltipStandard(getName(), this, (Escenario) mundo);
            }
        }
    }

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

    // --- MÉTODOS QUE FALTABAN ---
    public float getCentroX() { return getX() + getWidth() / 2; }
    public float getCentroY() { return getY() + getHeight() / 2; }

    public TooltipStandard getTooltip() { return tooltip; }
    public void instanciarTooltip(TooltipStandard tooltip) { this.tooltip = tooltip; }

    @Override
    public void dispose() { }
}
