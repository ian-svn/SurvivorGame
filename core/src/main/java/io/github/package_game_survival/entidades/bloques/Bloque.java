package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.interfaces.Colisionable;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Bloque extends Entidad implements Colisionable {

    public static final int ANCHO = 60;
    public static final int ALTO = 60;
    public boolean atravesable = false;
    public Texture texture;

    public Bloque(int x, int y, String nombre){
        super(nombre, x, y, ANCHO, ALTO);
        TooltipStandard tooltipStandard = new TooltipStandard(getName(),this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        escenario.agregar(this);
    }
}
