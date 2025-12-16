package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.interfaces.IMundoJuego;

public class ObjetoColeccionable extends Objeto {

    public ObjetoColeccionable(String nombre, float x, float y, Texture texture) {
        super(nombre, x, y, 32, 32, texture);
        // setName ya lo hace super()
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        super.agregarAlMundo(mundo); // Activa Tooltip
        this.toBack();
    }
}
