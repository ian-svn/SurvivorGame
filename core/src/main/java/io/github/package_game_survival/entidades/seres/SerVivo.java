package io.github.package_game_survival.entidades.seres;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.interfaces.Colisionable;

public abstract class SerVivo extends Entidad implements Colisionable {

    private int vida;
    private int vidaMinima = 0, vidaMaxima = 100;
    private int velocidad;
    private int danio;
    private TextureAtlas atlas;

    public SerVivo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto);
        this.velocidad = velocidad;
        this.vida = vidaInicial;
        this.vidaMaxima = vidaMaxima;
        this.danio = danio;
        this.atlas = atlas;
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

    @Override
    public Rectangle getRectColision() {
        return new Rectangle(getX(), getY(), getAncho(), getAlto()/2);
    }
}
