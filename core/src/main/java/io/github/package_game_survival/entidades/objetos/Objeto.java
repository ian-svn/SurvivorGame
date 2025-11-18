package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion; // Importante
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
// Importa tu VisualComponent desde donde lo hayas guardado
import io.github.package_game_survival.managers.GestorAnimacion;

public abstract class Objeto extends Entidad {

    private GestorAnimacion visual;
    private int puntos = 5;

    public Objeto(String nombre, float x, float y, Texture texture){
        super(nombre, x, y, 32, 32);
        this.visual = new GestorAnimacion(new TextureRegion(texture));
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
