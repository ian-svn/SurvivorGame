package io.github.package_game_survival.entidades.seres;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.interfaces.EstadoAnimacion;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;
import io.github.package_game_survival.managers.GestorAnimacion;

public abstract class SerVivo extends Entidad {

    private int vida;
    private int vidaMinima = 0, vidaMaxima = 100;
    private int velocidad;
    private int danio;

    protected IEstrategiaMovimiento estrategia;
    private TextureAtlas atlas;
    private GestorAnimacion visual;

    private boolean estaTitilando = false;

    // Enum interno para recordar la última dirección y poner el IDLE correcto
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
        // Importante: Asumimos que GestorAnimacion ya usa los Enums internamente
        visual.inicializarAtlas(atlas);
    }

    public void alterarVida(int cantidad) {
        this.vida += cantidad;

        if (this.vida < vidaMinima) {
            this.vida = vidaMinima;
        } else if (this.vida > vidaMaxima) {
            this.vida = vidaMaxima;
        }

        if (vida <= vidaMinima) {
            remove(); // Muere y desaparece del mundo
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = this.visual.getFrame();

        if (currentFrame == null) return;

        Color color = getColor();
        // Aplicamos el color del actor (incluye el rojo de daño si está titilando)
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());

        // Restauramos color blanco para no afectar a los siguientes actores
        batch.setColor(Color.WHITE);
    }

    /**
     * Calcula la animación correcta basándose en cuánto se movió (delta X/Y).
     * Usa lógica de "Dominancia de Eje" para evitar cambios bruscos en diagonales.
     */
    protected void actualizarAnimacion(float oldX, float oldY) {
        float dx = getX() - oldX;
        float dy = getY() - oldY;
        final float UMBRAL = 0.01f;

        // 1. IDLE (Quieto)
        if (Math.abs(dx) < UMBRAL && Math.abs(dy) < UMBRAL) {
            switch (ultimaDireccion) {
                case UP:           visual.setEstado(EstadoAnimacion.IDLE_UP); break;
                case DOWN:         visual.setEstado(EstadoAnimacion.IDLE_DOWN); break;
                case LEFT:         visual.setEstado(EstadoAnimacion.IDLE_LEFT); break;
                case RIGHT:        visual.setEstado(EstadoAnimacion.IDLE_RIGHT); break;
                case UP_RIGHT:     visual.setEstado(EstadoAnimacion.IDLE_DIAG_UP_RIGHT); break;
                case UP_LEFT:      visual.setEstado(EstadoAnimacion.IDLE_DIAG_UP_LEFT); break;
                case DOWN_RIGHT:
                case DOWN_LEFT:    visual.setEstado(EstadoAnimacion.IDLE_DOWN); break;
            }
            return;
        }

        float absDx = Math.abs(dx);
        float absDy = Math.abs(dy);
        final float FACTOR_DOMINANCIA = 2.5f; // Preferencia por animaciones laterales/verticales puras

        // 2. MOVIMIENTO
        if (absDx > UMBRAL && absDx > absDy * FACTOR_DOMINANCIA) {
            // Movimiento Horizontal Dominante
            if (dx > 0) {
                visual.setEstado(EstadoAnimacion.WALK_RIGHT);
                ultimaDireccion = Direccion.RIGHT;
            } else {
                visual.setEstado(EstadoAnimacion.WALK_LEFT);
                ultimaDireccion = Direccion.LEFT;
            }
        }
        else if (absDy > UMBRAL && absDy > absDx * FACTOR_DOMINANCIA) {
            // Movimiento Vertical Dominante
            if (dy > 0) {
                visual.setEstado(EstadoAnimacion.WALK_UP);
                ultimaDireccion = Direccion.UP;
            } else {
                visual.setEstado(EstadoAnimacion.WALK_DOWN);
                ultimaDireccion = Direccion.DOWN;
            }
        }
        else {
            // Movimiento Diagonal
            if (dy > 0) { // Arriba
                if (dx > 0) {
                    visual.setEstado(EstadoAnimacion.WALK_DIAG_UP_RIGHT);
                    ultimaDireccion = Direccion.UP_RIGHT;
                } else {
                    visual.setEstado(EstadoAnimacion.WALK_DIAG_UP_LEFT);
                    ultimaDireccion = Direccion.UP_LEFT;
                }
            } else { // Abajo
                // Si no tienes sprites diagonales abajo, usas WALK_DOWN
                visual.setEstado(EstadoAnimacion.WALK_DOWN);
                ultimaDireccion = (dx > 0) ? Direccion.DOWN_RIGHT : Direccion.DOWN_LEFT;
            }
        }
    }

    // Getters y Setters
    public int getVida() { return vida; }
    public float getCentroX() { return getX() + getWidth() / 2f; }

    public int getVelocidad() { return velocidad; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }

    public int getDanio() { return danio; }

    public IEstrategiaMovimiento getEstrategia() { return estrategia; }
    public void setEstrategia(IEstrategiaMovimiento estrategia) { this.estrategia = estrategia; }

    public boolean isEstaTitilando() { return estaTitilando; }
    public void setEstaTitilando(boolean estaTitilando) { this.estaTitilando = estaTitilando; }

    public GestorAnimacion getVisual() { return visual; }
    public TextureAtlas getAtlas() { return atlas; }
}
