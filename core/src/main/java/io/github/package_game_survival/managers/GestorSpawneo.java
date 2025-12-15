package io.github.package_game_survival.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Carne;
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

    // Configuración
    private final float DURACION_NOCHE = 120f;
    private final int CANTIDAD_OLEADAS = 5;
    private final float INTERVALO_OLEADA = DURACION_NOCHE / CANTIDAD_OLEADAS;

    private final int BASE_ENEMIGOS = 20;
    private final int ENEMIGOS_EXTRA_POR_DIA = 5;
    private final int VIDA_EXTRA_POR_DIA = 20;

    private final int BASE_ITEMS = 4;
    private final int ITEMS_EXTRA_POR_DIA = 2;
    private final int CANTIDAD_ANIMALES_DIA = 3;

    private int enemigosPorOleadaActual;
    private float timerNoche = 0f;
    private int oleadaActual = 0;

    private final float mundoAncho;
    private final float mundoAlto;
    private final Rectangle rectTest = new Rectangle();

    public GestorSpawneo(Escenario escenario, float ancho, float alto) {
        this.escenario = escenario;
        this.mundoAncho = ancho;
        this.mundoAlto = alto;
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
        int totalEnemigosHoy = BASE_ENEMIGOS + (ENEMIGOS_EXTRA_POR_DIA * (diaCalc - 1));
        this.enemigosPorOleadaActual = Math.max(1, totalEnemigosHoy / CANTIDAD_OLEADAS);

        int bonoVidaHoy = (diaCalc - 1) * VIDA_EXTRA_POR_DIA;

        Gdx.app.log("DIFICULTAD", "========================================");
        Gdx.app.log("DIFICULTAD", " INICIO DEL DÍA " + diaActual);
        Gdx.app.log("DIFICULTAD", " Enemigos Totales: " + totalEnemigosHoy);
        Gdx.app.log("DIFICULTAD", " BUFF DE VIDA ENEMIGOS: +" + bonoVidaHoy + " HP");
        Gdx.app.log("DIFICULTAD", "========================================");
    }

    public void update(float delta) {
        boolean esDeNocheAhora = escenario.getGestorTiempo().esDeNoche();
        int diaActual = escenario.getGestorTiempo().getDia();

        float velocidad = 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.T)) velocidad = 50.0f;
        float deltaAjustado = delta * velocidad;

        if (esDeNocheAhora && !eraDeNoche) {
            recalcularDificultad(diaActual);
            timerNoche = 0f;
            oleadaActual = 0;
            spawnearGrupoEnemigos(enemigosPorOleadaActual);
            oleadaActual++;
        }

        if (esDeNocheAhora) {
            gestionarOleadas(deltaAjustado);
        }

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

    // --- CORRECCIÓN AQUÍ: Búsqueda precisa por tamaño ---
    private void spawnearAnimales(int cantidad) {
        for (int i = 0; i < cantidad; i++) {

            // 1. Decidimos QUÉ animal es antes de buscar sitio
            boolean esVaca = MathUtils.randomBoolean();

            // 2. Definimos el tamaño exacto según la clase (Valores sacados de Jabali.java y Vaca.java)
            float anchoRequerido = esVaca ? 32 : 54;
            float altoRequerido = esVaca ? 32 : 42;

            // 3. Buscamos posición válida para ESE tamaño específico
            Vector2 pos = getPosicionValida(anchoRequerido, altoRequerido);

            if (pos != null) {
                Animal animal;
                if (esVaca) {
                    animal = new Vaca(pos.x, pos.y);
                } else {
                    animal = new Jabali(pos.x, pos.y);
                }

                animal.agregarAlMundo(escenario);
                escenario.getAnimales().add(animal);
            }
        }
    }

    private void spawnearGrupoEnemigos(int cantidad) {
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

                if (r <= 30) {
                    objeto = new Carne(pos.x, pos.y);
                } else {
                    objeto = new PocionDeAmatista(pos.x, pos.y);
                }

                objeto.agregarAlMundo(escenario);
                escenario.getObjetos().add(objeto);
            }
        }
    }

    private Vector2 getPosicionValida(float ancho, float alto) {
        rectTest.setWidth(ancho);
        rectTest.setHeight(alto);

        // Aumentamos intentos a 50 para asegurar éxito
        for (int i = 0; i < 50; i++) {
            float x = MathUtils.random(50, mundoAncho - 50);
            float y = MathUtils.random(50, mundoAlto - 50);
            rectTest.setPosition(x, y);

            boolean colisiona = false;

            // 1. Chequeamos contra bloques sólidos
            for (Rectangle bloque : escenario.getRectangulosNoTransitables()) {
                if (rectTest.overlaps(bloque)) {
                    colisiona = true;
                    break;
                }
            }

            // 2. Chequeamos contra el jugador
            if (!colisiona && escenario.getJugador() != null) {
                float distJugador = Vector2.dst(x, y, escenario.getJugador().getX(), escenario.getJugador().getY());
                if (distJugador < 200) colisiona = true;
            }

            if (!colisiona) return new Vector2(x, y);
        }
        return null;
    }
}
