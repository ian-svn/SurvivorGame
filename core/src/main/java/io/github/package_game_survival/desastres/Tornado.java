package io.github.package_game_survival.desastres;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;

public class Tornado extends Entidad {

    private final Jugador jugador;
    private Animation<TextureRegion> animacion;
    private float stateTime = 0f;

    // Configuración
    private final int DANIO_JUGADOR = 20;
    private final int DANIO_ENEMIGO = 40; // Daño aumentado para enemigos

    private float cooldownDano = 0f;
    private final float INTERVALO_DANO = 0.2f;

    // IA - Máquina de Estados
    private enum Estado { APUNTANDO, EMBISTIENDO, ESPERANDO }
    private Estado estadoActual = Estado.APUNTANDO;

    private final Vector2 direccionCarga = new Vector2();
    private final float VELOCIDAD_CARGA = 200f;
    private float timerEstado = 0f;
    private final float TIEMPO_APUNTADO = 1.0f;
    private final float TIEMPO_ESPERA = 5.0f;

    private final Rectangle hitboxReducida = new Rectangle();
    private IMundoJuego mundoLocal;

    public Tornado(float x, float y, TextureAtlas atlas, Jugador jugador) {
        super("Tornado", x, y, 60, 90);
        this.jugador = jugador;

        if (atlas != null) {
            var regiones = atlas.findRegions("tornado");
            if (regiones == null || regiones.isEmpty()) regiones = atlas.getRegions();
            this.animacion = new Animation<>(0.1f, regiones, Animation.PlayMode.LOOP);
        }
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        super.agregarAlMundo(mundo);
        this.mundoLocal = mundo;
        this.toFront();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        timerEstado += delta;

        switch (estadoActual) {
            case APUNTANDO: comportamientoApuntando(); break;
            case EMBISTIENDO: comportamientoEmbistiendo(delta); break;
            case ESPERANDO: comportamientoEsperando(); break;
        }

        verificarColisiones(delta);
    }

    private void comportamientoApuntando() {
        float vibracion = MathUtils.sin(stateTime * 50) * 2f;
        setX(getX() + vibracion);

        if (timerEstado >= TIEMPO_APUNTADO) {
            float centroX = getX() + getWidth()/2;
            float centroY = getY() + getHeight()/2;

            if (jugador != null) {
                direccionCarga.set(jugador.getCentroX() - centroX, jugador.getY() - centroY).nor();
            } else {
                direccionCarga.setToRandomDirection();
            }
            estadoActual = Estado.EMBISTIENDO;
            timerEstado = 0;
        }
    }

    private void comportamientoEmbistiendo(float delta) {
        float moveX = direccionCarga.x * VELOCIDAD_CARGA * delta;
        float moveY = direccionCarga.y * VELOCIDAD_CARGA * delta;

        moveBy(moveX, moveY);

        boolean choco = false;
        float mapW = (mundoLocal != null) ? mundoLocal.getAncho() : 3000;
        float mapH = (mundoLocal != null) ? mundoLocal.getAlto() : 3000;

        if (getX() <= 0) { setX(0); choco = true; }
        else if (getX() + getWidth() >= mapW) { setX(mapW - getWidth()); choco = true; }

        if (getY() <= 0) { setY(0); choco = true; }
        else if (getY() + getHeight() >= mapH) { setY(mapH - getHeight()); choco = true; }

        if (choco) {
            estadoActual = Estado.ESPERANDO;
            timerEstado = 0;
        }
    }

    private void comportamientoEsperando() {
        if (timerEstado >= TIEMPO_ESPERA) {
            estadoActual = Estado.APUNTANDO;
            timerEstado = 0;
        }
    }

    private void verificarColisiones(float delta) {
        if (cooldownDano > 0) cooldownDano -= delta;

        boolean golpeoAlguien = false;

        // 1. Verificar Jugador (Recibe daño normal y EMPUJE)
        if (jugador != null && this.getRectColision().overlaps(jugador.getRectColision())) {
            if (cooldownDano <= 0) {
                jugador.alterarVida(-DANIO_JUGADOR);
                jugador.recibirEmpuje(direccionCarga.x * 500f, direccionCarga.y * 500f);
                golpeoAlguien = true;
            }
        }

        // 2. Verificar Enemigos (Recibe MÁS daño y SIN EMPUJE)
        if (mundoLocal != null) {
            Array<Enemigo> enemigos = mundoLocal.getEnemigos();
            for (int i = enemigos.size - 1; i >= 0; i--) {
                Enemigo enemigo = enemigos.get(i);
                if (this.getRectColision().overlaps(enemigo.getRectColision())) {
                    if (cooldownDano <= 0) {
                        enemigo.alterarVida(-DANIO_ENEMIGO); // 40 de daño

                        // ELIMINADO EL EMPUJE PARA ENEMIGOS
                        // enemigo.recibirEmpuje(...);

                        golpeoAlguien = true;
                    }
                }
            }
        }

        if (golpeoAlguien) {
            cooldownDano = INTERVALO_DANO;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = (animacion != null) ? animacion.getKeyFrame(stateTime) : null;
        if (currentFrame != null) {
            if (estadoActual == Estado.APUNTANDO) batch.setColor(1f, 0.5f, 0.5f, 1f);
            else if (estadoActual == Estado.ESPERANDO) batch.setColor(1f, 1f, 1f, 0.5f);
            else batch.setColor(1, 1, 1, 1);

            batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
            batch.setColor(1, 1, 1, 1);
        }
    }

    @Override
    public Rectangle getRectColision() {
        float margen = 15f;
        hitboxReducida.set(getX() + margen, getY() + margen, getWidth() - 2*margen, getHeight() - 2*margen);
        return hitboxReducida;
    }
}
