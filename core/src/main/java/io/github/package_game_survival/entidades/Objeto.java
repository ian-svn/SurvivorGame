package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.interfaces.Colisionable;

public abstract class Objeto extends Actor implements Colisionable {
    protected Texture texture;
    private int ANCHO = 100;
    private int ALTO = 100;
    private int puntos = 5;

    public Objeto(Texture texture){
        this.texture = texture;
        setWidth(ANCHO);
        setHeight(ALTO);
    }

    public void spawnear(int x, int y){
        setX(x);
        setY(y);
    }

    public int getPuntos() {
        return this.puntos;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY());
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public void adquirir() {
        remove();
    }
}
