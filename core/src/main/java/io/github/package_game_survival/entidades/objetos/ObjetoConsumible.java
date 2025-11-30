package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.Consumible;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class ObjetoConsumible extends Objeto implements Consumible {
    private int vidaCurada;
    private int hambreSaciada;
    private int sedSaciada;

    // Bonificadores
    private int bonoDanio;
    private int bonoVelocidad;
    private int bonoVidaMaxima; // NUEVO

    private boolean consumido;

    public ObjetoConsumible(String nombre, float x, float y, Texture texture,
                            int vidaCurada, int hambreSaciada, int sedSaciada,
                            int bonoDanio, int bonoVelocidad, int bonoVidaMaxima) { // NUEVO PARÁMETRO
        super(nombre, x, y, 32, 32, texture);
        this.vidaCurada = vidaCurada;
        this.hambreSaciada = hambreSaciada;
        this.sedSaciada = sedSaciada;
        this.bonoDanio = bonoDanio;
        this.bonoVelocidad = bonoVelocidad;
        this.bonoVidaMaxima = bonoVidaMaxima;
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
        super.draw(batch, parentAlpha);
    }

    @Override
    public void consumir(Jugador jugador) {
        if(!consumido) {
            jugador.alterarVida(vidaCurada);

            if (bonoDanio != 0) jugador.alterarDanio(bonoDanio);
            if (bonoVelocidad != 0) jugador.alterarVelocidad(bonoVelocidad);
            if (bonoVidaMaxima != 0) jugador.aumentarVidaMaxima(bonoVidaMaxima);

            consumido = true;
            adquirir();
        }
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
        this.toBack();
        if (mundo instanceof Escenario) {
            String info = getName() + "\nHP: " + vidaCurada;
            if (bonoVidaMaxima != 0) info += " | MaxHP: +" + bonoVidaMaxima;
            if (bonoDanio != 0) info += " | Dmg: +" + bonoDanio;
            if (bonoVelocidad != 0) info += " | Spd: +" + bonoVelocidad;

            instanciarTooltip(new TooltipStandard(info, this, (Escenario) mundo));
        }
    }
}
