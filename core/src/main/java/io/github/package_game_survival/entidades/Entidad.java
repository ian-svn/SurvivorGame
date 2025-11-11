package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.interfaces.Colisionable;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Entidad extends Actor implements Colisionable {

    private Rectangle hitbox;
    private TooltipStandard tooltip;

    public Entidad(String nombre, float x, float y, float ancho, float alto) {
        setName(nombre);
        setBounds(x,y,ancho,alto);
        this.hitbox = getRectColision();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public abstract void agregarAlEscenario(Escenario escenario);

    public String getNombre(){
       return getName();
    }

    public float getAncho(){
        return getWidth();
    }

    public float getAlto(){
        return getHeight();
    }

    public TooltipStandard getTooltip() {
        return tooltip;
    }

    public void instanciarTooltip(TooltipStandard tooltip) {
        if(this.tooltip == null){
            this.tooltip = tooltip;
        }
    }
}
