package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.interfaces.IMundoJuego;
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
        // Por defecto el color visual es blanco (normal)
        setColor(Color.WHITE);
    }

    public int getPuntos() { return this.puntos; }

    public TextureRegion getRegionVisual() {
        return visual.getFrame();
    }

    // Nuevo método para que el HUD sepa de qué color dibujar el icono
    public Color getColorVisual() {
        return getColor();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame = visual.getFrame();
        if (frame != null) {
            Color c = getColor();
            // Aplicamos el color del objeto (importante para la carne podrida)
            batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
            batch.setColor(Color.WHITE); // Restauramos siempre
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
