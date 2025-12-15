package io.github.package_game_survival.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.desastres.Charco;
import io.github.package_game_survival.desastres.EfectoVentisca;
import io.github.package_game_survival.entidades.desastres.Tornado;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.seres.jugadores.Hud;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.entidades.objetos.Objeto;

// [NUEVOS IMPORTS] Para identificar qué salvar
import io.github.package_game_survival.entidades.objetos.Cama;
import io.github.package_game_survival.entidades.objetos.Hoguera;

public class GestorDesastres {

    public enum TipoDesastre { VENTISCA, TORNADO, INUNDACION, TERREMOTO }

    private final Escenario escenario;
    private final Jugador jugador;
    private final Hud hud;
    private final EfectoVentisca efectoVentisca;

    // Estados
    private boolean activaVentisca = false;
    private boolean activaTornado = false;
    private boolean activaInundacion = false;
    private boolean activaTerremoto = false;

    private Array<TipoDesastre> bolsaDesastres;

    // Variables control
    private int contadorNoches = 0;
    private boolean esDeNochePrevio = false;
    private boolean eventoNocheActivado = false;

    private float fuerzaTemblor = 0;
    private boolean estabaEnAgua = false;
    private final Rectangle rectAux = new Rectangle();

    // Constantes Velocidad
    private final int VELOCIDAD_NORMAL = 120;
    private final int VELOCIDAD_AGUA = 60;
    private final int VELOCIDAD_TERREMOTO = (int) (VELOCIDAD_NORMAL * 0.7f);

    public GestorDesastres(Escenario escenario, Jugador jugador, Hud hud) {
        this.escenario = escenario;
        this.jugador = jugador;
        this.hud = hud;
        this.efectoVentisca = new EfectoVentisca(escenario.getAncho(), escenario.getAlto(), jugador);

        llenarBolsaDesastres();
    }

    private void llenarBolsaDesastres() {
        bolsaDesastres = new Array<>();
        for (TipoDesastre tipo : TipoDesastre.values()) {
            bolsaDesastres.add(tipo);
        }
        bolsaDesastres.shuffle();
    }

    public void update(float delta) {
        boolean esDeNoche = escenario.getGestorTiempo().esDeNoche();
        gestionarCicloNoche(esDeNoche);

        if (!esDeNoche) {
            if (activaVentisca || activaTornado || activaInundacion || activaTerremoto) {
                limpiarTodosLosDesastres();
            }
        }

        actualizarEfectos(delta);
    }

    private void gestionarCicloNoche(boolean esDeNoche) {
        if (esDeNoche && !esDeNochePrevio) {
            contadorNoches++;
            eventoNocheActivado = false;
        }
        esDeNochePrevio = esDeNoche;

        if (esDeNoche && !eventoNocheActivado && contadorNoches > 0) {
            int dia = escenario.getGestorTiempo().getDia();
            activarEventoAleatorio(dia == 5);
            eventoNocheActivado = true;
        }
    }

    private void actualizarEfectos(float delta) {
        if (activaVentisca) {
            var cam = escenario.getCamara();
            efectoVentisca.update(delta, cam.position.x, cam.position.y, cam.viewportWidth * cam.zoom, cam.viewportHeight * cam.zoom);
        }

        if (fuerzaTemblor > 0) {
            if (activaTerremoto && fuerzaTemblor < 1.5f) fuerzaTemblor = 1.5f;
            var cam = escenario.getCamara();
            cam.translate(MathUtils.random(-fuerzaTemblor, fuerzaTemblor), MathUtils.random(-fuerzaTemblor, fuerzaTemblor));
            cam.update();
            fuerzaTemblor = MathUtils.lerp(fuerzaTemblor, 0, delta * 3);
        }

        gestionarVelocidadGlobal();
    }

    private void gestionarVelocidadGlobal() {
        if (activaTerremoto) {
            aplicarVelocidad(VELOCIDAD_TERREMOTO);
            return;
        }
        if (activaInundacion && jugadorPisaAgua()) {
            if (!estabaEnAgua) {
                aplicarVelocidad(VELOCIDAD_AGUA);
                estabaEnAgua = true;
            }
        } else {
            if (estabaEnAgua) {
                aplicarVelocidad(VELOCIDAD_NORMAL);
                estabaEnAgua = false;
            } else if (!activaTerremoto && jugador.getVelocidad() != VELOCIDAD_NORMAL) {
                aplicarVelocidad(VELOCIDAD_NORMAL);
            }
        }
    }

    private void aplicarVelocidad(int vel) { if (jugador.getVelocidad() != vel) jugador.setVelocidad(vel); }

    private boolean jugadorPisaAgua() {
        for (Actor actor : escenario.getStageMundo().getActors()) {
            if (actor instanceof Charco) {
                if (((Charco) actor).estaPisando(jugador)) return true;
            }
        }
        return false;
    }

    private void activarEventoAleatorio(boolean doble) {
        int cantidad = doble ? 2 : 1;
        StringBuilder aviso = new StringBuilder("¡PELIGRO! ");

        for (int i = 0; i < cantidad; i++) {
            if (bolsaDesastres.isEmpty()) llenarBolsaDesastres();
            TipoDesastre tipo = bolsaDesastres.pop();
            ejecutarDesastre(tipo);
            aviso.append(tipo.name()).append(i < cantidad - 1 ? " + " : "");
        }

        if (hud != null) hud.mostrarAvisoDesastre(aviso.toString());
    }

    private void ejecutarDesastre(TipoDesastre tipo) {
        switch (tipo) {
            case VENTISCA:
                activaVentisca = true;
                efectoVentisca.setActivo(true);
                efectoVentisca.setIntensidadCeguera(0.5f);
                break;
            case TORNADO:
                activaTornado = true;
                spawnTornados(2);
                break;
            case INUNDACION:
                activaInundacion = true;
                spawnInundacionGrid();
                break;
            case TERREMOTO:
                activaTerremoto = true;
                aplicarEfectoTerremoto(); // <--- Aquí es donde limpiamos objetos
                break;
        }
    }

    // --- MODIFICADO: NO ROMPE CAMAS NI HOGUERAS ---
    private void aplicarEfectoTerremoto() {
        jugador.alterarVida(-20);

        Array<Objeto> objetos = escenario.getObjetos();

        // Iteramos hacia atrás para poder borrar elementos sin romper el índice
        for (int i = objetos.size - 1; i >= 0; i--) {
            Objeto obj = objetos.get(i);

            // FILTRO DE SEGURIDAD:
            // Si el objeto es una Cama o una Hoguera, lo ignoramos (no se borra)
            if (obj instanceof Cama || obj instanceof Hoguera) {
                continue;
            }

            // Si es cualquier otra cosa (carne, poción, etc.), se borra
            obj.remove();
            objetos.removeIndex(i);
        }

        fuerzaTemblor = 5.0f;
    }

    private void spawnTornados(int cantidad) {
        TextureAtlas atlas = Assets.get(PathManager.TORNADO_ATLAS, TextureAtlas.class);
        float anguloBase = MathUtils.random(0, 360);
        for (int i = 0; i < cantidad; i++) {
            float angulo = anguloBase + (360f / cantidad) * i;
            float dist = 500f;
            float x = jugador.getX() + MathUtils.cosDeg(angulo) * dist;
            float y = jugador.getY() + MathUtils.sinDeg(angulo) * dist;
            escenario.agregarActor(new Tornado(x, y, atlas, jugador));
        }
    }

    private void spawnInundacionGrid() {
        Texture tex = Assets.get(PathManager.AGUA_TEXTURE, Texture.class);
        int cols = 12, filas = 12;
        float cw = escenario.getAncho()/cols;
        float ch = escenario.getAlto()/filas;

        for(int c=0; c<cols; c++) {
            for(int f=0; f<filas; f++) {
                if(MathUtils.randomBoolean(0.65f)) {
                    intentarPonerCharco(c, f, cw, ch, tex);
                }
            }
        }
    }

    private void intentarPonerCharco(int c, int f, float cw, float ch, Texture tex) {
        boolean ok = false;
        int intentos = 0;
        while(!ok && intentos < 5) {
            intentos++;
            float w = MathUtils.random(50, 110);
            float h = MathUtils.random(50, 110);
            float x = (c*cw) + MathUtils.random(0, cw-w);
            float y = (f*ch) + MathUtils.random(0, ch-h);
            rectAux.set(x,y,w,h);
            if(esSitioLibre(rectAux)) {
                Charco charco = new Charco(x, y, w, h, tex, jugador);
                escenario.agregarActor(charco);
                charco.toBack();
                ok = true;
            }
        }
    }

    private boolean esSitioLibre(Rectangle r) {
        for(Rectangle b : escenario.getRectangulosNoTransitables()) if(r.overlaps(b)) return false;
        for(Actor a : escenario.getStageMundo().getActors())
            if(a instanceof Charco && r.overlaps(((Charco)a).getRectColision())) return false;
        return true;
    }

    private void limpiarTodosLosDesastres() {
        activaVentisca = false; activaTornado = false; activaInundacion = false; activaTerremoto = false;
        efectoVentisca.setActivo(false);
        aplicarVelocidad(VELOCIDAD_NORMAL);
        estabaEnAgua = false;
        fuerzaTemblor = 0;
        Array<Actor> actores = escenario.getStageMundo().getActors();
        for(int i=actores.size-1; i>=0; i--) {
            Actor a = actores.get(i);
            if(a instanceof Tornado || a instanceof Charco) a.remove();
        }
    }

    public void draw(Batch batch) {
        if (efectoVentisca.isActivo()) {
            var cam = escenario.getCamara();
            efectoVentisca.draw(batch, cam.position.x, cam.position.y, cam.viewportWidth * cam.zoom, cam.viewportHeight * cam.zoom);
        }
    }

    public void dispose() {
        efectoVentisca.dispose();
    }
}
