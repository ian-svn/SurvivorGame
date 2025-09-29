package io.github.package_game_survival.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class PocionDeAmatista extends Pocion{

    public PocionDeAmatista() {
        super(new Texture(Gdx.files.internal("sprites/objeto.png")),100);
    }
}
