package io.github.package_game_survival.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import javax.swing.*;


public class Jugador extends Personaje {
    private List<Objeto> inventario = new ArrayList<>();
    private float velocidad = 400;
    private TooltipManager tm;
    private Skin skinTooltip;


    public Jugador(String nombre, Texture texture, int x, int y, TooltipManager tm){
        super(nombre, texture, x, y);
        this.tm = tm;
        skinTooltip = new Skin(Gdx.files.internal("skins/toolTip.json"));
        TextTooltip toolTip = new TextTooltip(nombre, tm, skinTooltip);
        toolTip.getContainer().setBackground((Drawable) null);
        this.addListener(toolTip);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

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
