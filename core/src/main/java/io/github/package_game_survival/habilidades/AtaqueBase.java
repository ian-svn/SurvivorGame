package io.github.package_game_survival.habilidades;

import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.interfaces.IAtaque;
import io.github.package_game_survival.interfaces.IMundoJuego;

public abstract class AtaqueBase implements IAtaque {

    protected float cooldownMaximo;
    protected float duracionCasteo; // Este es el tiempo de "preparación" antes del golpe
    protected int danioBase;
    protected float rango;
    protected Class<? extends SerVivo> claseObjetivo;

    protected float timerCooldown = 0f;
    protected float timerCasteo = 0f;
    protected boolean casteando = false;

    // Snapshot de datos
    protected Vector2 destinoGuardado = new Vector2();
    protected SerVivo atacanteGuardado;
    protected IMundoJuego mundoGuardado;

    public AtaqueBase(float cooldown, float duracionCasteo, int danio, float rango, Class<? extends SerVivo> claseObjetivo) {
        this.cooldownMaximo = cooldown;
        this.duracionCasteo = duracionCasteo;
        this.danioBase = danio;
        this.rango = rango;
        this.claseObjetivo = claseObjetivo;
    }

    @Override
    public void intentarAtacar(SerVivo atacante, Vector2 destino, IMundoJuego mundo) {
        if (timerCooldown > 0 || casteando) return;

        this.atacanteGuardado = atacante;
        this.mundoGuardado = mundo;
        this.destinoGuardado.set(destino);

        this.casteando = true;
        this.timerCasteo = duracionCasteo; // Empieza la cuenta regresiva

        onInicioCasteo(atacante, destino, mundo);
    }

    @Override
    public void update(float delta) {
        if (timerCooldown > 0) timerCooldown -= delta;

        if (casteando) {
            timerCasteo -= delta;

            // CUANDO LLEGA A 0 -> EJECUTA EL GOLPE
            if (timerCasteo <= 0) {
                ejecutarGolpe(atacanteGuardado, destinoGuardado, mundoGuardado);
                casteando = false;
                timerCooldown = cooldownMaximo;
            }
        }
    }

    protected abstract void onInicioCasteo(SerVivo atacante, Vector2 destino, IMundoJuego mundo);
    protected abstract void ejecutarGolpe(SerVivo atacante, Vector2 destino, IMundoJuego mundo);

    @Override public boolean estaCasteando() { return casteando; }
    @Override public boolean isEnCooldown() { return timerCooldown > 0; }
    public float getRango() { return rango; }
}
