package io.github.package_game_survival.desastres;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;

public class EfectoVentisca implements Disposable {

    private static class Copo {
        float x, y, velocidadX, velocidadY, tamano, alpha;
    }

    private final Array<Copo> copos;
    private final Texture texturaCopo;
    private final TextureRegion regionCopo;
    private final Texture texturaCeguera;
    private final Jugador jugador;

    private float intensidadCeguera = 0f;
    private final float MAX_CEGUERA = 0.55f;
    private final int CANTIDAD_PARTICULAS = 600;

    private float anchoMundo, altoMundo;
    private boolean activo = false;

    // Lógica de daño
    private float acumuladorDanoFrio = 0f;
    private float tiempoSinCalor = 0f;
    private final float TIEMPO_PARA_EMPEZAR_DANO = 2.0f;

    // CAMBIO: Daño fijo solicitado
    private final int DANIO_POR_HIT = 4;

    public EfectoVentisca(float anchoMundo, float altoMundo, Jugador jugador) {
        this.anchoMundo = anchoMundo;
        this.altoMundo = altoMundo;
        this.jugador = jugador;
        this.copos = new Array<>();

        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.texturaCopo = new Texture(pixmap);
        this.regionCopo = new TextureRegion(texturaCopo);

        Pixmap pixBlind = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixBlind.setColor(Color.WHITE);
        pixBlind.fill();
        this.texturaCeguera = new Texture(pixBlind);
        pixBlind.dispose();
        pixmap.dispose();

        inicializarCopos();
    }

    private void inicializarCopos() {
        for (int i = 0; i < CANTIDAD_PARTICULAS; i++) {
            Copo c = new Copo();
            resetCopo(c, true);
            copos.add(c);
        }
    }

    private void resetCopo(Copo c, boolean inicioAleatorio) {
        if (inicioAleatorio) {
            c.x = MathUtils.random(0, anchoMundo + 200);
            c.y = MathUtils.random(0, altoMundo);
        } else {
            if (MathUtils.randomBoolean()) {
                c.x = MathUtils.random(0, anchoMundo + 200);
                c.y = altoMundo + 10;
            } else {
                c.x = anchoMundo + 10;
                c.y = MathUtils.random(0, altoMundo + 200);
            }
        }
        float variacion = MathUtils.random(0.5f, 1.5f);
        c.velocidadX = -350f * variacion;
        c.velocidadY = -150f * variacion;
        c.tamano = MathUtils.random(4f, 10f);
        c.alpha = MathUtils.random(0.3f, 0.8f);
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        if(activo) {
            this.intensidadCeguera = 0.4f;
        } else {
            this.intensidadCeguera = 0f;
            this.tiempoSinCalor = 0;
            this.acumuladorDanoFrio = 0;
        }
    }

    public void setIntensidadCeguera(float intensidad) {
        this.intensidadCeguera = MathUtils.clamp(intensidad, 0f, MAX_CEGUERA);
    }

    public void update(float delta, float camX, float camY, float viewportWidth, float viewportHeight) {
        if (!activo) return;

        aplicarLogicaFrio(delta);

        float minX = camX - viewportWidth / 2 - 50;
        float maxX = camX + viewportWidth / 2 + 50;
        float minY = camY - viewportHeight / 2 - 50;
        float maxY = camY + viewportHeight / 2 + 50;

        for (Copo c : copos) {
            c.x += c.velocidadX * delta;
            c.y += c.velocidadY * delta;
            if (c.x < minX || c.y < minY) {
                resetCopo(c, false);
                if (MathUtils.randomBoolean()) {
                    c.x = MathUtils.random(minX, maxX + 200); c.y = maxY;
                } else {
                    c.x = maxX; c.y = MathUtils.random(minY, maxY + 200);
                }
            }
        }
    }

    private void aplicarLogicaFrio(float delta) {
        if (jugador == null) return;

        if (!jugador.isSintiendoCalor()) {
            tiempoSinCalor += delta;

            if (tiempoSinCalor >= TIEMPO_PARA_EMPEZAR_DANO) {
                // Acumulamos "frecuencia" de daño (5 veces por segundo aprox)
                acumuladorDanoFrio += delta * 5;

                if (acumuladorDanoFrio >= 1.0f) {
                    // CAMBIO: Daño fijo de 4 por cada "tick"
                    jugador.alterarVida(-DANIO_POR_HIT);

                    // Restamos 1 ciclo del acumulador para mantener el ritmo
                    acumuladorDanoFrio -= 1.0f;
                }
            }
        } else {
            tiempoSinCalor = 0;
            acumuladorDanoFrio = 0;
        }
    }

    public void draw(Batch batch, float camX, float camY, float vW, float vH) {
        if (!activo) return;
        for (Copo c : copos) {
            batch.setColor(1, 1, 1, c.alpha);
            batch.draw(regionCopo, c.x, c.y, c.tamano, c.tamano);
        }
        if (intensidadCeguera > 0) {
            batch.setColor(0.9f, 0.9f, 1f, intensidadCeguera);
            batch.draw(texturaCeguera, camX - vW/2, camY - vH/2, vW, vH);
        }
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        texturaCopo.dispose();
        texturaCeguera.dispose();
    }

    public boolean isActivo() { return activo; }
}
