package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.seres.Jugador;
import io.github.package_game_survival.interfaces.Consumible;
import io.github.package_game_survival.standards.TooltipStandard;

public class ObjetoConsumible extends Objeto implements Consumible {
    private int vidaCurada;
    private boolean consumido;

    public ObjetoConsumible(String nombre, float x, float y, float ancho, float alto,
                            Texture texture, int vidaCurada) {
        super(nombre, x, y, ancho, alto, texture);
        this.vidaCurada = vidaCurada;
        this.consumido = false;
        setName(nombre);
        TooltipStandard tooltip = new TooltipStandard(getName() + "\n" + "vida curada: " + this.vidaCurada, this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(super.texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void consumir(Jugador jugador) {
        if(!consumido) {
            jugador.alterarVida(vidaCurada);
            consumido = true;
        }
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        escenario.agregar(this);
    }
}
