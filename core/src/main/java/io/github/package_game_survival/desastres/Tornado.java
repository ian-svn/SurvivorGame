package io.github.package_game_survival.entidades.desastres;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;

public class Tornado extends Entidad {

    private enum EstadoTornado {
        APUNTANDO,    // Gira y busca al jugador
        EMBISTIENDO,  // Se mueve recto rápido
        REPOSO        // Espera en el borde
    }

    private Animation<TextureRegion> animacion;
    private float stateTime;
    private Vector2 direccion;

    // Configuración de movimiento
    private float velocidadCarga = 400f;
    private EstadoTornado estado = EstadoTornado.APUNTANDO;

    private float timerEstado = 0f;
    private final float TIEMPO_APUNTADO = 1.5f; // Tiempo que tarda en fijar objetivo
    private final float TIEMPO_REPOSO = 3.0f;   // Tiempo que descansa en la pared

    private SerVivo objetivo;
    private Array<SerVivo> victimasRecientes = new Array<>();
    private float tiempoResetGolpes = 0f;

    public Tornado(float x, float y, TextureAtlas atlas, SerVivo objetivo) {
        super("Tornado", x, y, 64, 128);

        TextureRegion[] frames = atlas.findRegions("tornado").toArray(TextureRegion.class);
        this.animacion = new Animation<>(0.1f, frames);
        this.animacion.setPlayMode(Animation.PlayMode.LOOP);

        this.objetivo = objetivo;
        this.direccion = new Vector2();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        timerEstado += delta;

        switch (estado) {
            case APUNTANDO:
                comportamientoApuntar();
                break;
            case EMBISTIENDO:
                comportamientoEmbestir(delta);
                break;
            case REPOSO:
                comportamientoReposo();
                break;
        }

        // Lógica de daño (siempre activa por si te chocas con él en reposo)
        gestionarDaño(delta);
    }

    private void comportamientoApuntar() {
        if (objetivo != null && !objetivo.isMuerto()) {
            // Calcular vector hacia el jugador
            float targetX = objetivo.getX() + objetivo.getWidth()/2;
            float targetY = objetivo.getY() + objetivo.getHeight()/2;
            float miX = getX() + getWidth()/2;
            float miY = getY() + getHeight()/2;
            direccion.set(targetX - miX, targetY - miY).nor();
        }

        // Si pasaron 1.5s apuntando, ¡A LA CARGA!
        if (timerEstado >= TIEMPO_APUNTADO) {
            estado = EstadoTornado.EMBISTIENDO;
            victimasRecientes.clear(); // Nueva carga, nuevos golpes
        }
    }

    private void comportamientoEmbestir(float delta) {
        // Moverse rápido en la dirección fijada
        moveBy(direccion.x * velocidadCarga * delta, direccion.y * velocidadCarga * delta);

        // Verificar si chocó con los bordes del mapa (asumiendo stage limits)
        if (getStage() != null) {
            float mapW = getStage().getWidth();
            float mapH = getStage().getHeight();
            boolean choco = false;

            if (getX() < 0) { setX(0); choco = true; }
            if (getY() < 0) { setY(0); choco = true; }
            if (getX() > mapW - getWidth()) { setX(mapW - getWidth()); choco = true; }
            if (getY() > mapH - getHeight()) { setY(mapH - getHeight()); choco = true; }

            if (choco) {
                estado = EstadoTornado.REPOSO;
                timerEstado = 0f; // Reiniciar timer para contar el reposo
            }
        }
    }

    private void comportamientoReposo() {
        // Esperar unos segundos en la pared
        if (timerEstado >= TIEMPO_REPOSO) {
            estado = EstadoTornado.APUNTANDO;
            timerEstado = 0f;
        }
    }

    private void gestionarDaño(float delta) {
        tiempoResetGolpes += delta;
        if (tiempoResetGolpes > 0.5f) { // Golpe cada 0.5s si te quedas dentro
            victimasRecientes.clear();
            tiempoResetGolpes = 0f;
        }
        verificarColisiones();
    }

    private void verificarColisiones() {
        if (getStage() == null) return;
        for (Actor actor : getStage().getActors()) {
            if (actor instanceof SerVivo && actor != this) {
                SerVivo ser = (SerVivo) actor;
                if (victimasRecientes.contains(ser, true)) continue;

                if (this.getRectColision().overlaps(ser.getRectColision())) {
                    aplicarDaño(ser);
                    victimasRecientes.add(ser);
                }
            }
        }
    }

    private void aplicarDaño(SerVivo ser) {
        if (ser instanceof Enemigo) {
            ser.alterarVida(-50);
            ser.moveBy(direccion.x * 20, direccion.y * 20); // Empuje pequeño
        } else if (ser instanceof Jugador) {
            ser.alterarVida(-30);
            // Empuje fuerte en la dirección del tornado
            ser.recibirEmpuje(direccion.x * 100, direccion.y * 100);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animacion.getKeyFrame(stateTime, true);
        // Efecto visual: Si está apuntando, vibra un poco o cambia color?
        // Dejémoslo simple por ahora.
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void agregarAlMundo(io.github.package_game_survival.interfaces.IMundoJuego mundo) {
        mundo.agregarActor(this);
    }
}
