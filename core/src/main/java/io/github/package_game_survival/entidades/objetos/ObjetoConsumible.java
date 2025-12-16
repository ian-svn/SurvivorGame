package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.Consumible;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class ObjetoConsumible extends Objeto implements Consumible {

    private int vidaCurada;

    private int bonoDanio;
    private int bonoVelocidad;
    private int bonoVidaMaxima;

    private float bonoRango;
    private float bonoArea;

    private boolean consumido;

    public ObjetoConsumible(String nombre, float x, float y, Texture texture,
                            int vidaCurada,
                            int bonoDanio, int bonoVelocidad, int bonoVidaMaxima,
                            float bonoRango, float bonoArea) {

        super(nombre, x, y, 32, 32, texture);

        this.vidaCurada = vidaCurada;
        this.bonoDanio = bonoDanio;
        this.bonoVelocidad = bonoVelocidad;
        this.bonoVidaMaxima = bonoVidaMaxima;
        this.bonoRango = bonoRango;
        this.bonoArea = bonoArea;

        this.consumido = false;
    }

    @Override
    public void consumir(Jugador jugador) {
        if(!consumido) {
            if (vidaCurada != 0) jugador.alterarVida(vidaCurada);
            if (bonoDanio != 0) jugador.alterarDanio(bonoDanio);
            if (bonoVelocidad != 0) jugador.alterarVelocidad(bonoVelocidad);
            if (bonoVidaMaxima != 0) jugador.aumentarVidaMaxima(bonoVidaMaxima);

            if (bonoRango != 0 || bonoArea != 0) {
                jugador.mejorarRangoAtaque(bonoRango, bonoArea);
            }

            consumido = true;
            adquirir();
            AudioManager.getControler().playSound("consumir");
        }
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        super.agregarAlMundo(mundo);
        this.toBack();

        if (mundo instanceof Escenario) {
            StringBuilder info = new StringBuilder(getName());

            if (vidaCurada != 0) info.append("\nHP: ").append(vidaCurada > 0 ? "+" : "").append(vidaCurada);
            if (bonoVidaMaxima != 0) info.append("\nMaxHP: +").append(bonoVidaMaxima);
            if (bonoDanio != 0) info.append("\nDmg: +").append(bonoDanio);
            if (bonoVelocidad != 0) info.append("\nSpd: +").append(bonoVelocidad);
            if (bonoRango != 0) info.append("\nRng: +").append((int)bonoRango);
            if (bonoArea != 0) info.append("\nArea: +").append((int)bonoArea);

            if (getTooltip() != null) {
                instanciarTooltip(new TooltipStandard(info.toString(), this, (Escenario) mundo));
            } else {
                instanciarTooltip(new TooltipStandard(info.toString(), this, (Escenario) mundo));
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
