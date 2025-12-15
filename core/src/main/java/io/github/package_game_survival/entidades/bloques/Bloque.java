package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Bloque extends Entidad {

    public static final int ANCHO = 32;
    public static final int ALTO = 32;
    public boolean transitable = false;

    protected Rectangle hitbox;

    public Bloque(float x, float y, String nombre) {
        // Asumiendo que Entidad tiene este constructor
        super(nombre, x, y, ANCHO, ALTO);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Bloque base no dibuja nada por sí mismo si es abstracto/lógico,
        // pero si tienes bloques estáticos, aquí iría el draw.
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Actualizamos la posición del tooltip si existe
        if(getTooltip() != null) getTooltip().actualizarPosicion();
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);

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
        } else {
            // Importante: Actualizar la posición por si el bloque se mueve (raro, pero posible)
            hitbox.setPosition(getX(), getY());
        }
        return hitbox;
    }
}
