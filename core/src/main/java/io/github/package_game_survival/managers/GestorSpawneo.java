package io.github.package_game_survival.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.*;
import io.github.package_game_survival.entidades.seres.animales.*;
import io.github.package_game_survival.entidades.seres.enemigos.*;

public class GestorSpawneo {

    private final Escenario escenario;
    private final float anchoMundo, altoMundo;
    private final Rectangle rectTest = new Rectangle();

    // Configuración
    private final int BASE_ENEMIGOS = 20;
    private final int EXTRA_ENEMIGOS_DIA = 5;
    private final int EXTRA_VIDA_DIA = 20;
    private final int BASE_ITEMS = 4;
    private final int EXTRA_ITEMS_DIA = 2;
    private final int ANIMALES_DIA = 3;

    // Oleadas
    private final float DURACION_NOCHE = 120f;
    private final int OLEADAS = 5;
    private final float INTERVALO = DURACION_NOCHE / OLEADAS;

    private int enemigosPorOleada;
    private float timerNoche = 0f;
    private int oleadaActual = 0;
    private boolean eraDeNoche;

    public GestorSpawneo(Escenario escenario, float ancho, float alto) {
        this.escenario = escenario;
        this.anchoMundo = ancho;
        this.altoMundo = alto;
        this.eraDeNoche = escenario.getGestorTiempo().esDeNoche();
        recalcularDificultad(escenario.getGestorTiempo().getDia());

        if(!eraDeNoche) iniciarDia(escenario.getGestorTiempo().getDia());
    }

    public void update(float delta) {
        boolean esDeNoche = escenario.getGestorTiempo().esDeNoche();
        int dia = escenario.getGestorTiempo().getDia();
        float dt = delta * (Gdx.input.isKeyPressed(Input.Keys.T) ? 50f : 1f);

        if(esDeNoche && !eraDeNoche) {
            recalcularDificultad(dia);
            timerNoche = 0f;
            oleadaActual = 0;
            spawnEnemigos(enemigosPorOleada);
            oleadaActual++;
        }

        if(esDeNoche) {
            if(oleadaActual < OLEADAS) {
                timerNoche += dt;
                if(timerNoche >= (oleadaActual * INTERVALO)) {
                    spawnEnemigos(enemigosPorOleada);
                    oleadaActual++;
                }
            }
        }

        if(!esDeNoche && eraDeNoche) {
            limpiarEnemigos();
            iniciarDia(dia);
        }
        eraDeNoche = esDeNoche;
    }

    private void iniciarDia(int dia) {
        int nItems = BASE_ITEMS + (EXTRA_ITEMS_DIA * (dia-1));
        spawnItems(nItems);
        spawnAnimales(ANIMALES_DIA);
    }

    private void recalcularDificultad(int dia) {
        int total = BASE_ENEMIGOS + (EXTRA_ENEMIGOS_DIA * Math.max(0, dia-1));
        this.enemigosPorOleada = Math.max(1, total / OLEADAS);
    }

    private void spawnEnemigos(int n) {
        int dia = Math.max(1, escenario.getGestorTiempo().getDia());
        int bonoHp = (dia-1) * EXTRA_VIDA_DIA;
        for(int i=0; i<n; i++) {
            Vector2 pos = buscarPos(32, 40);
            if(pos != null) {
                Enemigo e = factoryEnemigo(pos.x, pos.y);
                if(bonoHp > 0) e.aumentarVidaMaxima(bonoHp);
                e.agregarAlMundo(escenario);
                escenario.getEnemigos().add(e);
            }
        }
    }

    private Enemigo factoryEnemigo(float x, float y) {
        int r = MathUtils.random(0, 100);
        if(r < 40) return new InvasorDeLaLuna(x, y);
        if(r < 70) return new InvasorArquero(x, y);
        return new InvasorMago(x, y);
    }

    private void spawnAnimales(int n) {
        for(int i=0; i<n; i++) {
            int tipo = MathUtils.random(0, 2);
            float w = (tipo==1)? 54 : (tipo==2? 42 : 32);
            float h = (tipo==1)? 42 : 32;
            Vector2 pos = buscarPos(w, h);
            if(pos != null) {
                Animal a = factoryAnimal(tipo, pos.x, pos.y);
                a.agregarAlMundo(escenario);
                escenario.getAnimales().add(a);
            }
        }
    }

    private Animal factoryAnimal(int tipo, float x, float y) {
        switch(tipo) {
            case 0: return new Vaca(x, y);
            case 1: return new Jabali(x, y);
            case 2: return new Oveja(x, y);
            default: return null;
        }
    }

    // --- AQUÍ ESTABA TU ERROR PROBABLEMENTE ---
    private void spawnItems(int n) {
        for(int i=0; i<n; i++) {
            Vector2 pos = buscarPos(32, 32);
            if(pos != null) {
                // SOLO Carne y Pocion. NADA MÁS.
                Objeto o = (MathUtils.randomBoolean(0.3f))
                    ? new Carne(pos.x, pos.y)
                    : new PocionDeAmatista(pos.x, pos.y);

                o.agregarAlMundo(escenario);
                escenario.getObjetos().add(o);
            }
        }
    }

    private void limpiarEnemigos() {
        for(Enemigo e : escenario.getEnemigos()) e.setQuemandose(true);
    }

    private Vector2 buscarPos(float w, float h) {
        float margen = 10f;
        rectTest.setWidth(w + margen);
        rectTest.setHeight(h + margen);
        int intentos = 0;
        while(intentos < 50) {
            intentos++;
            float x = MathUtils.random(50, anchoMundo-50);
            float y = MathUtils.random(50, altoMundo-50);
            rectTest.setPosition(x-margen/2, y-margen/2);

            boolean choca = false;
            for(Rectangle b : escenario.getRectangulosNoTransitables()) if(rectTest.overlaps(b)) { choca=true; break; }
            if(!choca && escenario.getJugador() != null) {
                if(Vector2.dst(x, y, escenario.getJugador().getX(), escenario.getJugador().getY()) < 200) choca=true;
            }
            if(!choca) return new Vector2(x, y);
        }
        return null;
    }
}
