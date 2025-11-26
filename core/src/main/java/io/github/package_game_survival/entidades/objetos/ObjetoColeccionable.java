package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.interfaces.IMundoJuego;

public class ObjetoColeccionable extends Objeto {

    public ObjetoColeccionable(String nombre, float x, float y, Texture texture) {
        super(nombre, x, y, texture);
        setName(nombre);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getTooltip() != null) getTooltip().actualizarPosicion();
    }

    // CORRECCIÓN: Usamos el draw del padre para que se vea la textura correcta (Palo, Piedra).
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
    }
}
