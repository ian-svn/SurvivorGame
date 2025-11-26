package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.interfaces.IMundoJuego; // Interfaz
import io.github.package_game_survival.managers.GestorAnimacion;

public abstract class Objeto extends Entidad {

    private GestorAnimacion visual;
    private int puntos = 5;
    private Rectangle hitbox;

    public Objeto(String nombre, float x, float y, Texture texture){
        super(nombre, x, y, 32, 32);
        if (texture != null) {
            this.visual = new GestorAnimacion(new TextureRegion(texture));
        } else {
            this.visual = new GestorAnimacion();
        }
    }

    public int getPuntos() {
        return this.puntos;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame = visual.getFrame();
        if (frame != null) {
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
        }
    }

    public void adquirir() {
        remove();
    }

    @Override
    public Rectangle getRectColision() {
        if (hitbox == null) {
            hitbox = new Rectangle(getX(), getY(), getAncho(), getAlto());
        }
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }

    @Override
    public abstract void agregarAlMundo(IMundoJuego mundo);
}
