package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.standards.TooltipStandard;

public class ObjetoColeccionable extends Objeto {

    public ObjetoColeccionable(String nombre, float x, float y, Texture texture) {
        super(nombre, x, y, texture);
        setName(nombre);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        getTooltip().actualizarPosicion();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(super.texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        escenario.agregar(this);
    }
}
