package io.github.package_game_survival.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class PocionDeAmatista extends Pocion{

    public PocionDeAmatista(int x, int y) {
        super(new Texture(Gdx.files.internal("sprites/objeto.png")),100);
        setX(x);
        setY(y);
    }
}
