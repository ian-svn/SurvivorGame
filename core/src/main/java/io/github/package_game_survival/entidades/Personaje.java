package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.interfaces.Colisionable;

public abstract class Personaje extends Actor implements Colisionable {

    private int vida;
    private int ancho = 100, alto = 140;
    private int vidaMinima = 0, vidaMaxima = 100;
    protected Texture texture;
    private int velocidad;

    public Personaje(String nombre, Texture texture, int vidaInicial, int velocidad, float x, float y) {
        setName(nombre);
        setX(x);
        setY(y);
        setWidth(ancho);
        setHeight(alto);
        this.velocidad = velocidad;
        this.texture = texture;
        this.vida = vidaInicial;
    }

    public void alterarVida(int cantidad) {
        this.vida += cantidad;

        if (this.vida < vidaMinima) {
            this.vida = vidaMinima;
        } else if (this.vida > vidaMaxima) {
            this.vida = vidaMaxima;
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

    public Vector2 getPosition(Vector2 out) {
        return out.set(getX(), getY());
    }
}
