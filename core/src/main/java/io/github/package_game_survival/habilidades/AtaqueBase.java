package io.github.package_game_survival.habilidades;

import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.interfaces.IAtaque;
import io.github.package_game_survival.interfaces.IMundoJuego;

public abstract class AtaqueBase implements IAtaque {

    public enum EstadoHabilidad {
        LISTO,
        CASTEANDO,    // Cargando el ataque
        ENFRIAMIENTO  // Cooldown
    }

    protected float tiempoEnfriamiento;
    protected float tiempoCasteo;
    protected float temporizador;
    protected EstadoHabilidad estado;

    protected int danio;
    protected float rango;
    protected Class<? extends SerVivo> claseObjetivo;

    protected SerVivo atacanteRef;
    protected Vector2 destinoRef = new Vector2();
    protected IMundoJuego mundoRef;

    public AtaqueBase(float tiempoEnfriamiento, float tiempoCasteo, int danio, float rango, Class<? extends SerVivo> claseObjetivo) {
        this.tiempoEnfriamiento = tiempoEnfriamiento;
        this.tiempoCasteo = tiempoCasteo;
        this.danio = danio;
        this.rango = rango;
        this.claseObjetivo = claseObjetivo;
        this.estado = EstadoHabilidad.LISTO;
        this.temporizador = 0;
    }

    @Override
    public void update(float delta) {
        if (estado == EstadoHabilidad.LISTO) return;

        temporizador -= delta;

        if (estado == EstadoHabilidad.CASTEANDO) {
            // Cancelar si el dueño murió
            if (atacanteRef != null && atacanteRef.getVida() <= 0) {
                estado = EstadoHabilidad.LISTO;
                return;
            }

            // Si termina el tiempo y sigue vivo, dispara
            if (temporizador <= 0) {
                ejecutarEfecto(atacanteRef, destinoRef, mundoRef);
                estado = EstadoHabilidad.ENFRIAMIENTO;
                temporizador = tiempoEnfriamiento;
            }
        }
        else if (estado == EstadoHabilidad.ENFRIAMIENTO) {
            if (temporizador <= 0) {
                estado = EstadoHabilidad.LISTO;
            }
        }
    }

    @Override
    public boolean intentarAtacar(SerVivo atacante, Vector2 destino, IMundoJuego mundo) {
        if (estado != EstadoHabilidad.LISTO) return false;

        this.atacanteRef = atacante;
        this.destinoRef.set(destino);
        this.mundoRef = mundo;

        if (tiempoCasteo > 0) {
            estado = EstadoHabilidad.CASTEANDO;
            temporizador = tiempoCasteo;
        } else {
            ejecutarEfecto(atacante, destino, mundo);
            estado = EstadoHabilidad.ENFRIAMIENTO;
            temporizador = tiempoEnfriamiento;
        }
        return true;
    }

    @Override
    public boolean estaCasteando() {
        return estado == EstadoHabilidad.CASTEANDO;
    }

    protected abstract void ejecutarEfecto(SerVivo atacante, Vector2 destino, IMundoJuego mundo);
}
