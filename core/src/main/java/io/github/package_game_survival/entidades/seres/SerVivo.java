package io.github.package_game_survival.entidades.seres;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.interfaces.Colisionable;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;
import io.github.package_game_survival.managers.GestorAnimacion;

public abstract class SerVivo extends Entidad implements Colisionable {

    private int vida;
    private int vidaMinima = 0, vidaMaxima = 100;
    private int velocidad;
    private int danio;
    protected IEstrategiaMovimiento estrategia;
    private TextureAtlas atlas;
    private GestorAnimacion visual;
    private boolean estaTitilando = false;
    private boolean visible = true;

    private enum Direccion {
        UP, DOWN, LEFT, RIGHT,
        UP_LEFT, UP_RIGHT,
        DOWN_LEFT, DOWN_RIGHT
    }

    private Direccion ultimaDireccion = Direccion.DOWN;

    public SerVivo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto);
        this.velocidad = velocidad;
        this.vida = vidaInicial;
        this.vidaMaxima = vidaMaxima;
        this.danio = danio;
        this.atlas = atlas;

        this.visual = new GestorAnimacion();
        visual.inicializarAtlas(atlas);
    }

    public void alterarVida(int cantidad) {
        this.vida += cantidad;

        if (this.vida < vidaMinima) {
            this.vida = vidaMinima;
        } else if (this.vida > vidaMaxima) {
            this.vida = vidaMaxima;
        }

        if(vida<=vidaMinima){
            remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = this.visual.getFrame();
        if (currentFrame == null) return;

        Color color = getColor();
        if (estaTitilando && !visible) {
            return;
        }

        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    protected void actualizarAnimacion(float oldX, float oldY) {
        float dx = getX() - oldX;
        float dy = getY() - oldY;

        final float UMBRAL = 0.01f;

        // 1. Si NO hay movimiento significativo, poner estado IDLE
        if (Math.abs(dx) < UMBRAL && Math.abs(dy) < UMBRAL) {
            // (La lógica IDLE anterior se mantiene sin cambios)
            switch (ultimaDireccion) {
                case UP:           getVisual().setState("IDLE_UP"); break;
                case DOWN:         getVisual().setState("IDLE_DOWN"); break;
                case LEFT:         getVisual().setState("IDLE_LEFT"); break;
                case RIGHT:        getVisual().setState("IDLE_RIGHT"); break;
                case UP_RIGHT:     getVisual().setState("IDLE_DIAG_UP_RIGHT"); break;
                case UP_LEFT:      getVisual().setState("IDLE_DIAG_UP_LEFT"); break;
                case DOWN_RIGHT:
                case DOWN_LEFT:
                    getVisual().setState("IDLE_DOWN");
                    break;
            }
            return;
        }

        // --- NUEVA LÓGICA DE DOMINANCIA DEL EJE ---
        float absDx = Math.abs(dx);
        float absDy = Math.abs(dy);

        // Si la diferencia de movimiento en X es mucho mayor que en Y, lo consideramos horizontal.
        // Usamos un factor de 2.5: si X es 2.5 veces más rápido que Y, priorizamos X.
        final float FACTOR_DOMINANCIA = 2.5f;

        // 2. Si hay movimiento, determinar dirección

        // --- MOVIMIENTO HORIZONTAL DOMINANTE ---
        if (absDx > UMBRAL && absDx > absDy * FACTOR_DOMINANCIA) {
            if (dx > 0) {
                getVisual().setState("WALK_RIGHT");
                ultimaDireccion = Direccion.RIGHT;
            } else { // dx < 0
                getVisual().setState("WALK_LEFT");
                ultimaDireccion = Direccion.LEFT;
            }
        }
        // --- MOVIMIENTO VERTICAL PURO DOMINANTE ---
        else if (absDy > UMBRAL && absDy > absDx * FACTOR_DOMINANCIA) {
            if (dy > 0) {
                getVisual().setState("WALK_UP");
                ultimaDireccion = Direccion.UP;
            } else { // dy < 0
                getVisual().setState("WALK_DOWN");
                ultimaDireccion = Direccion.DOWN;
            }
        }
        // --- MOVIMIENTO DIAGONAL (El movimiento X y Y son similares) ---
        else if (absDx > UMBRAL || absDy > UMBRAL) {
            // Esto solo se ejecuta si el movimiento no es puramente horizontal/vertical

            if (dy > 0) { // Hacia ARRIBA
                if (dx > 0) {
                    getVisual().setState("WALK_DIAG_UP_RIGHT");
                    ultimaDireccion = Direccion.UP_RIGHT;
                } else { // dx < 0
                    getVisual().setState("WALK_DIAG_UP_LEFT");
                    ultimaDireccion = Direccion.UP_LEFT;
                }
            } else { // dy < 0 (Hacia ABAJO)
                // Forzamos WALK_DOWN, pero guardamos la dirección lógica correcta para el IDLE
                getVisual().setState("WALK_DOWN");
                if (dx > 0) {
                    ultimaDireccion = Direccion.DOWN_RIGHT;
                } else { // dx < 0
                    ultimaDireccion = Direccion.DOWN_LEFT;
                }
            }
        }
    }

    public int getVida() {
        return this.vida;
    }

    public float getCentroX() {
        return getX() + getWidth() / 2f;
    }

    public int getVelocidad() {
        return this.velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getDanio() {
        return danio;
    }

    public IEstrategiaMovimiento getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(IEstrategiaMovimiento estrategiaMovimiento) {
        this.estrategia = estrategiaMovimiento;
    }

    @Override
    public Rectangle getRectColision() {
        return new Rectangle(getX(), getY(), getAncho(), getAlto()/2);
    }

    public boolean isEstaTitilando() {
        return estaTitilando;
    }

    public void setEstaTitilando(boolean estaTitilando) {
        this.estaTitilando = estaTitilando;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public GestorAnimacion getVisual() {
        return visual;
    }
}
