package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.Consumible;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class ObjetoConsumible extends Objeto implements Consumible {
    private int vidaCurada;
    private int hambreSaciada;
    private int sedSaciada;
    private boolean consumido;

    public ObjetoConsumible(String nombre, float x, float y, Texture texture,
                            int vidaCurada, int hambreSaciada, int sedSaciada) {
        super(nombre, x, y, texture);
        this.vidaCurada = vidaCurada;
        this.hambreSaciada = hambreSaciada;
        this.sedSaciada = sedSaciada;
        this.consumido = false;
        setName(nombre);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getTooltip() != null) getTooltip().actualizarPosicion();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Assets.get(PathManager.POCION_TEXTURE, Texture.class), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void consumir(Jugador jugador) {
        if(!consumido) {
            jugador.alterarVida(vidaCurada);
            consumido = true;
            adquirir(); // Llama a remove() del padre
        }
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);

        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName() + "\n" +
                "Vida Curada: " + this.vidaCurada + "\n" +
                "Hambre Saciada: " + this.hambreSaciada+ "\n" +
                "Sed Saciada: " + this.sedSaciada,
                this, (Escenario) mundo));
        }
    }
}
