package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.interfaces.Colisionable;

public abstract class Personaje extends Actor implements Colisionable {

    private int vida;
    private int ancho = 100, alto = 140;
    private int vidaMinima = 1, vidaMaxima = 100;
    protected Texture texture;

    public Personaje(String nombre, Texture texture, int x, int y) {
        setName(nombre);
        setX(x);
        setY(y);
        setWidth(ancho);
        setHeight(alto);
        this.texture = texture;
        this.vida = 100;
    }

    public void alterarVida(int vida){
        this.vida+=vida;

        if(vida<=vidaMinima){
            this.vida=vidaMinima;
        } else if(vida>=vidaMaxima){
            this.vida=vidaMaxima;
        }
    }

}
