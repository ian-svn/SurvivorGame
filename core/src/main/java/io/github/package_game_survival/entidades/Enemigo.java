package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Enemigo extends Personaje {
    public Enemigo(String nombre, Texture texture, int x, int y) {
        super(nombre, texture, 100, x, y);
    }
    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}
