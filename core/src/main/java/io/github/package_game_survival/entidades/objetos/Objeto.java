package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.GestorAnimacion;

public abstract class Objeto extends Entidad {

    private GestorAnimacion visual;
    private int puntos = 5;
    private Rectangle hitbox;

    private float tiempoVida = 0f;
    // --- CAMBIOS DE TIEMPO ---
    private final float TIEMPO_MAXIMO = 60f; // 1 Minuto
    private final float INICIO_PARPADEO = 50f; // Parpadea los últimos 10s
    private boolean marcadoParaBorrar = false;

    protected boolean desaparecible = true;

    public Objeto(String nombre, float x, float y, float ancho, float alto, Texture texture){
        super(nombre, x, y, ancho, alto);
        if (texture != null) {
            this.visual = new GestorAnimacion(new TextureRegion(texture));
        } else {
            this.visual = new GestorAnimacion();
        }
        setColor(Color.WHITE);
    }

    public void setDesaparecible(boolean desaparecible) {
        this.desaparecible = desaparecible;
    }

    public void reiniciarTiempoVida() {
        this.tiempoVida = 0f;
        this.marcadoParaBorrar = false;
        setVisible(true);
        setColor(getColor().r, getColor().g, getColor().b, 1f);
    }

    public boolean isMarcadoParaBorrar() { return marcadoParaBorrar; }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!desaparecible) return;

        tiempoVida += delta;

        if (tiempoVida >= TIEMPO_MAXIMO) {
            marcadoParaBorrar = true;
            this.remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame = visual.getFrame();
        if (frame != null) {
            Color c = getColor();
            float alpha = c.a * parentAlpha;

            if (desaparecible && tiempoVida > INICIO_PARPADEO) {
                float tiempoRestante = TIEMPO_MAXIMO - tiempoVida;
                // Velocidad parpadeo basada en los 10 segundos restantes
                float velocidadParpadeo = (10f - tiempoRestante) * 3f;

                float parpadeo = Math.abs(MathUtils.sin(tiempoVida * velocidadParpadeo));
                if (parpadeo < 0.5f) alpha = 0.4f;
                else alpha = 1f;
            }

            batch.setColor(c.r, c.g, c.b, alpha);
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
    }

    public int getPuntos() { return this.puntos; }
    public TextureRegion getRegionVisual() { return visual.getFrame(); }
    public Color getColorVisual() { return getColor(); }
    public void adquirir() { remove(); }

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
