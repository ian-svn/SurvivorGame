package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.interfaces.Consumible;

public class Pocion extends Objeto implements Consumible {
    private int vidaCurada;

    public Pocion(Texture texture, int vidaCurada){
        super(texture);
        this.vidaCurada = vidaCurada;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(super.texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void consumir(Jugador jugador) {
        jugador.alterarVida(vidaCurada);
    }
}
