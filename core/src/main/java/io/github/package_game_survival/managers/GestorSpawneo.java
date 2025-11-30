package io.github.package_game_survival.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Carne;
import io.github.package_game_survival.entidades.objetos.CarnePodrida;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.objetos.PocionDeAmatista;
import io.github.package_game_survival.entidades.seres.animales.Animal;
import io.github.package_game_survival.entidades.seres.animales.Jabali;
import io.github.package_game_survival.entidades.seres.animales.Vaca;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.enemigos.InvasorArquero;
import io.github.package_game_survival.entidades.seres.enemigos.InvasorDeLaLuna;
import io.github.package_game_survival.entidades.seres.enemigos.InvasorMago;

public class GestorSpawneo {

    private final Escenario escenario;
    private boolean eraDeNoche;

    // --- CONFIGURACIÓN ---
    private final float DURACION_NOCHE = 120f;
    private final int CANTIDAD_OLEADAS = 5;
    private final float INTERVALO_OLEADA = DURACION_NOCHE / CANTIDAD_OLEADAS;

    private final int BASE_ENEMIGOS = 20;
    private final int ENEMIGOS_EXTRA_POR_DIA = 5;

    // Cantidad de vida extra que ganan por día
    private final int VIDA_EXTRA_POR_DIA = 20;

    private final int BASE_ITEMS = 4;
    private final int ITEMS_EXTRA_POR_DIA = 2;

    private final int CANTIDAD_ANIMALES_DIA = 3;

    private int enemigosPorOleadaActual;
    private float timerNoche = 0f;
    private int oleadaActual = 0;

    private final float MUNDO_ANCHO;
    private final float MUNDO_ALTO;
    private final Rectangle rectTest = new Rectangle();

    public GestorSpawneo(Escenario escenario, float ancho, float alto) {
        this.escenario = escenario;
        this.MUNDO_ANCHO = ancho;
        this.MUNDO_ALTO = alto;
        this.eraDeNoche = escenario.getGestorTiempo().esDeNoche();

        recalcularDificultad(escenario.getGestorTiempo().getDia());

        // Inicio seguro de Día
        if (!eraDeNoche) {
            spawnearItemsRandom(BASE_ITEMS);
            spawnearAnimales(CANTIDAD_ANIMALES_DIA);
            Gdx.app.log("SPAWNER", "¡Inicio Día! Items y Animales generados.");
        } else {
            spawnearGrupoEnemigos(enemigosPorOleadaActual);
        }
    }

    private void recalcularDificultad(int diaActual) {
        int diaCalc = Math.max(1, diaActual);

        // 1. Calculamos enemigos totales
        int totalEnemigosHoy = BASE_ENEMIGOS + (ENEMIGOS_EXTRA_POR_DIA * (diaCalc - 1));
        this.enemigosPorOleadaActual = Math.max(1, totalEnemigosHoy / CANTIDAD_OLEADAS);

        // 2. Calculamos el buff de vida actual
        int bonoVidaHoy = (diaCalc - 1) * VIDA_EXTRA_POR_DIA;

        // --- MENSAJE DE REPORTE DE DIFICULTAD ---
        Gdx.app.log("DIFICULTAD", "========================================");
        Gdx.app.log("DIFICULTAD", " INICIO DEL DÍA " + diaActual);
        Gdx.app.log("DIFICULTAD", " Enemigos Totales: " + totalEnemigosHoy);
        Gdx.app.log("DIFICULTAD", " Enemigos por Oleada: " + enemigosPorOleadaActual);
        Gdx.app.log("DIFICULTAD", " BUFF DE VIDA ENEMIGOS: +" + bonoVidaHoy + " HP");
        Gdx.app.log("DIFICULTAD", "========================================");
    }

    public void update(float delta) {
        boolean esDeNocheAhora = escenario.getGestorTiempo().esDeNoche();
        int diaActual = escenario.getGestorTiempo().getDia();

        float velocidad = 1.0f;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.T)) velocidad = 50.0f;
        float deltaAjustado = delta * velocidad;

        // 1. ATARDECER
        if (esDeNocheAhora && !eraDeNoche) {
            // Recalculamos justo antes de empezar la noche para mostrar el mensaje
            recalcularDificultad(diaActual);

            timerNoche = 0f;
            oleadaActual = 0;
            spawnearGrupoEnemigos(enemigosPorOleadaActual);
            oleadaActual++;
        }

        if (esDeNocheAhora) {
            gestionarOleadas(deltaAjustado);
        }

        // 2. AMANECER
        if (!esDeNocheAhora && eraDeNoche) {
            limpiarEnemigos();
            int itemsHoy = BASE_ITEMS + (ITEMS_EXTRA_POR_DIA * (diaActual - 1));
            spawnearItemsRandom(itemsHoy);
            spawnearAnimales(CANTIDAD_ANIMALES_DIA);
        }

        this.eraDeNoche = esDeNocheAhora;
    }

    private void gestionarOleadas(float delta) {
        if (oleadaActual < CANTIDAD_OLEADAS) {
            timerNoche += delta;
            if (timerNoche >= (oleadaActual * INTERVALO_OLEADA)) {
                spawnearGrupoEnemigos(enemigosPorOleadaActual);
                oleadaActual++;
            }
        }
    }

    private void spawnearAnimales(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            Vector2 pos = getPosicionValida(32, 32);
            if (pos != null) {
                Animal animal;
                if (MathUtils.randomBoolean()) animal = new Vaca(pos.x, pos.y);
                else animal = new Jabali(pos.x, pos.y);

                animal.agregarAlMundo(escenario);
                escenario.getAnimales().add(animal);
            }
        }
    }

    private void spawnearGrupoEnemigos(int cantidad) {
        // Obtenemos el bono para aplicarlo al spawnear
        int diaActual = Math.max(1, escenario.getGestorTiempo().getDia());
        int bonoVida = (diaActual - 1) * VIDA_EXTRA_POR_DIA;

        for (int i = 0; i < cantidad; i++) {
            Vector2 pos = getPosicionValida(32, 40);
            if (pos != null) {
                Enemigo enemigo;
                int r = MathUtils.random(0, 100);
                if (r < 40) enemigo = new InvasorDeLaLuna(pos.x, pos.y);
                else if (r < 70) enemigo = new InvasorArquero(pos.x, pos.y);
                else enemigo = new InvasorMago(pos.x, pos.y);

                // Aplicar Buff
                if (bonoVida > 0) enemigo.aumentarVidaMaxima(bonoVida);

                enemigo.agregarAlMundo(escenario);
                escenario.getEnemigos().add(enemigo);
            }
        }
    }

    private void limpiarEnemigos() {
        Array<Enemigo> lista = escenario.getEnemigos();
        for (Enemigo e : lista) {
            e.setQuemandose(true);
        }
    }

    private void spawnearItemsRandom(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            Vector2 pos = getPosicionValida(32, 32);
            if (pos != null) {
                Objeto objeto;
                int r = MathUtils.random(0, 100);
                if (r <= 30) objeto = new Carne(pos.x, pos.y);
                else objeto = new PocionDeAmatista(pos.x, pos.y);

                objeto.agregarAlMundo(escenario);
                escenario.getObjetos().add(objeto);
            }
        }
    }

    private Vector2 getPosicionValida(float ancho, float alto) {
        rectTest.setWidth(ancho);
        rectTest.setHeight(alto);

        for (int i = 0; i < 20; i++) {
            float x = MathUtils.random(50, MUNDO_ANCHO - 50);
            float y = MathUtils.random(50, MUNDO_ALTO - 50);
            rectTest.setPosition(x, y);

            boolean colisiona = false;
            for (Rectangle bloque : escenario.getRectangulosNoTransitables()) {
                if (rectTest.overlaps(bloque)) {
                    colisiona = true;
                    break;
                }
            }
            if (!colisiona && escenario.getJugador() != null) {
                float distJugador = Vector2.dst(x, y, escenario.getJugador().getX(), escenario.getJugador().getY());
                if (distJugador < 200) colisiona = true;
            }

            if (!colisiona) return new Vector2(x, y);
        }
        return null;
    }
}
