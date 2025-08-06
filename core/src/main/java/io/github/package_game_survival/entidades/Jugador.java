package io.github.package_game_survival.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.Intersector.overlaps;
import com.badlogic.gdx.math.Rectangle;


public class Jugador extends Personaje {
    private List<Objeto> inventario = new ArrayList<>();
    private float velocidad = 400;

    public Jugador(String nombre, Texture texture, int x, int y) {
        super(nombre, texture, x, y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Detección de colisión
        // Asegúrate de que el stage no sea null antes de usarlo
        if (getStage() != null) {
            for (Actor actor : getStage().getActors()) {
                if (actor instanceof Objeto && actor != this) {
                    if (this.overlaps(actor)) {
                        adquirirObjeto((Objeto) actor);
                    }
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.TAB)){

        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            setY(getY() + velocidad * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            setY(getY() - velocidad * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            setX(getX() - velocidad * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            setX(getX() + velocidad * delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.texture, getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public boolean overlaps(Actor otro) {
        if (otro instanceof Objeto) {
            return this.getBounds().overlaps(((Objeto) otro).getBounds());
        }
        return false;
    }

    public void adquirirObjeto(Objeto objeto){
        if(inventario.add(objeto)){
            objeto.remove();
        }
    }

    public List<Objeto> getInventario() {
        return inventario;
    }
}
