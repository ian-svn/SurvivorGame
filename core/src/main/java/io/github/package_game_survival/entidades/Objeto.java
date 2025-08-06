package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class Objeto extends Actor {
    private Texture texture;
    private int ANCHO = 100;
    private int ALTO = 100;

    public Objeto(Texture texture){
        this.texture = texture;
        setWidth(ANCHO);
        setHeight(ALTO);
    }

    public void spawnear(int x, int y){
        setX(x);
        setY(y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY());
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public boolean overlaps(Actor otro) {
        if (otro instanceof Objeto) {
            return this.getBounds().overlaps(((Objeto) otro).getBounds());
        }
        return false;
    }
}
