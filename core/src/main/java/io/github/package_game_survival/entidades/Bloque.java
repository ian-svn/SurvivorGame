package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.interfaces.Colisionable;

public abstract class Bloque extends Actor implements Colisionable {

    public static final int ANCHO = 60;
    public static final int ALTO = 60;
    public boolean atravesable = false;
    public Texture texture;

    public Bloque(int x, int y){
        setX(x);
        setY(y);
        setWidth(ANCHO);
        setHeight(ALTO);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}
