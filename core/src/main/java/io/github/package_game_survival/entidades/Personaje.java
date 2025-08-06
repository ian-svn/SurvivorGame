package io.github.package_game_survival.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.interfaces.Seleccionable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class Personaje extends Actor {

    private String nombre;
    private int vida;
    protected int x, y;
    private final int ANCHO = 100, ALTO = 100;
    private int vidaMinima, vidaMaxima;
    protected Texture texture;

    public Personaje(String nombre, Texture texture, int x, int y) {
        this.nombre = nombre;
        this.texture = texture;
        this.vida = 100;
        setX(x);
        setY(y);
        setWidth(ANCHO);
        setHeight(ALTO);
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
