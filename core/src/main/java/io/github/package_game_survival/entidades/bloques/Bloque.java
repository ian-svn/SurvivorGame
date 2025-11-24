package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.interfaces.Colisionable;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Bloque extends Entidad implements Colisionable {

    public static final int ANCHO = 32;
    public static final int ALTO = 32;
    public boolean transitable = false;

    public Bloque(float x, float y, String nombre) {
        super(nombre, x, y, ANCHO, ALTO);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        getTooltip().actualizarPosicion();
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        escenario.agregar(this);
        instanciarTooltip(new TooltipStandard(getName(), this, escenario));
    }

    public boolean isTransitable() {
        return transitable;
    }
}


