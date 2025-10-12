package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.interfaces.Consumible;
import io.github.package_game_survival.standards.TooltipStandard;

public class Pocion extends Objeto implements Consumible {
    private int vidaCurada;

    public Pocion(Texture texture, String nombre, int vidaCurada){
        super(texture);
        this.vidaCurada = vidaCurada;
        setName(nombre);
        TooltipStandard tooltip = new TooltipStandard(getName() + "\n" + "vida curada: " + this.vidaCurada);
        tooltip.attach(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(super.texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void consumir(Jugador jugador) {
        jugador.alterarVida(vidaCurada);
    }
}
