package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.entidades.Entidad;

public abstract class Objeto extends Entidad {
    protected Texture texture;
    private int puntos = 5;

    public Objeto(String nombre, float x, float y, Texture texture){
        super(nombre, x, y, 32, 32);
        this.texture = texture;
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

    @Override
    public Rectangle getRectColision() {
        return new Rectangle(getX(), getY(), getAncho(), getAlto());
    }

}
