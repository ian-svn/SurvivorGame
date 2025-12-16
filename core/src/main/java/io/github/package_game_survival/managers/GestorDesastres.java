package io.github.package_game_survival.managers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.desastres.Charco;
import io.github.package_game_survival.desastres.EfectoVentisca;
import io.github.package_game_survival.desastres.Tornado;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Cama;
import io.github.package_game_survival.entidades.objetos.Hoguera;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.jugadores.Hud;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.managers.Audio.AudioManager;

import static io.github.package_game_survival.managers.PathManager.TORNADO_ATLAS;

public class GestorDesastres {

    public enum TipoDesastre { VENTISCA, TORNADO, INUNDACION, TERREMOTO }

    private final Escenario escenario;
    private final Jugador jugador;
    private final Hud hud;
    private final EfectoVentisca efectoVentisca;

    private boolean activaVentisca = false;
    private boolean activaTornado = false;
    private boolean activaInundacion = false;
    private boolean activaTerremoto = false;

    private float timerDanoVentisca = 0f;
    private final float INTERVALO_DANO_VENTISCA = 2.0f;
    private final int DANO_VENTISCA = 15;

    private Array<TipoDesastre> bolsaDesastres;
    private int contadorNoches = 0;
    private boolean esDeNochePrevio = false;
    private boolean eventoNocheActivado = false;

    private float fuerzaTemblor = 0;
    private boolean estabaEnAgua = false;
    private final Rectangle rectAux = new Rectangle();

    private int velocidadActualJugador; // CAMBIO: Usamos esto para recordar la velocidad real con buffs
    private final int VELOCIDAD_AGUA = 60;
    private int velocidadTerremoto;

    public GestorDesastres(Escenario escenario, Jugador jugador, Hud hud) {
        this.escenario = escenario;
        this.jugador = jugador;
        this.hud = hud;
        this.efectoVentisca = new EfectoVentisca(escenario.getAncho(), escenario.getAlto(), jugador);

        // Inicializamos con la velocidad base del personaje (ej: 160)
        this.velocidadActualJugador = jugador.getVelocidad();
        this.velocidadTerremoto = (int) (velocidadActualJugador * 0.6f);

        try {
            AudioManager.getControler().loadSound("viento", PathManager.VIENTO_SOUND);
        } catch (Exception e) {}

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
            if (escenario.getEnemigos() != null) {
                for (var enemigo : escenario.getEnemigos()) {
                    enemigo.escalarDificultad(contadorNoches);
                }
            }
        }
        esDeNochePrevio = esDeNoche;

        if (esDeNoche && !eventoNocheActivado && contadorNoches > 0) {
            int dia = escenario.getGestorTiempo().getDia();
            activarEventoAleatorio(dia == 4);
            eventoNocheActivado = true;
        }
    }

    private void actualizarEfectos(float delta) {
        if (activaVentisca) {
            var cam = escenario.getCamara();
            efectoVentisca.update(delta, cam.position.x, cam.position.y, cam.viewportWidth * cam.zoom, cam.viewportHeight * cam.zoom);

            timerDanoVentisca += delta;
            if (timerDanoVentisca >= INTERVALO_DANO_VENTISCA) {
                if (!jugador.isSintiendoCalor()) {
                    jugador.alterarVida(-DANO_VENTISCA);
                }
                timerDanoVentisca = 0f;
            }
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

    // --- CORRECCIÓN IMPORTANTE ---
    private void gestionarVelocidadGlobal() {
        boolean hayDesastreDeVelocidad = false;

        // 1. Prioridad: Terremoto
        if (activaTerremoto) {
            aplicarVelocidad(velocidadTerremoto);
            hayDesastreDeVelocidad = true;
        }
        // 2. Prioridad: Agua
        else if (activaInundacion && jugadorPisaAgua()) {
            if (!estabaEnAgua) {
                aplicarVelocidad(VELOCIDAD_AGUA);
                estabaEnAgua = true;
            }
            hayDesastreDeVelocidad = true;
        }

        // 3. Si NO hay desastre activo afectando la velocidad
        if (!hayDesastreDeVelocidad) {
            // Si acabamos de salir del agua o terremoto, restauramos la velocidad guardada
            if (estabaEnAgua || jugador.getVelocidad() < velocidadActualJugador) {
                aplicarVelocidad(velocidadActualJugador);
                estabaEnAgua = false;
            }
            // CLAVE: Actualizamos la velocidad base si el jugador recibió un buff (carne)
            // mientras no estaba en un desastre.
            else {
                velocidadActualJugador = jugador.getVelocidad();
            }
        }
    }

    private void aplicarVelocidad(int vel) {
        if (jugador.getVelocidad() != vel) jugador.setVelocidad(vel);
    }

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
                try { AudioManager.getControler().loopSound("viento"); } catch(Exception e){}
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
                aplicarEfectoTerremoto();
                break;
        }
    }

    private void aplicarEfectoTerremoto() {
        jugador.alterarVida(-20);
        Array<Objeto> objetos = escenario.getObjetos();

        for (int i = objetos.size - 1; i >= 0; i--) {
            Objeto obj = objetos.get(i);
            if (obj instanceof Cama || obj instanceof Hoguera) {
                continue;
            }
            obj.delete();
            objetos.removeIndex(i);
        }
        fuerzaTemblor = 5.0f;
    }

    private void spawnTornados(int cantidad) {
        TextureAtlas atlas = io.github.package_game_survival.managers.Assets.get(TORNADO_ATLAS, TextureAtlas.class);
        float anguloBase = MathUtils.random(0, 360);
        for (int i = 0; i < cantidad; i++) {
            float angulo = anguloBase + (360f / cantidad) * i;
            float dist = 500f;
            float x = jugador.getX() + MathUtils.cosDeg(angulo) * dist;
            float y = jugador.getY() + MathUtils.sinDeg(angulo) * dist;

            Tornado tornado = new Tornado(x, y, atlas, jugador);
            tornado.agregarAlMundo(escenario);
        }
    }

    private void spawnInundacionGrid() {
        com.badlogic.gdx.graphics.Texture tex = io.github.package_game_survival.managers.Assets.get(io.github.package_game_survival.managers.PathManager.AGUA_TEXTURE, com.badlogic.gdx.graphics.Texture.class);
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

    private void intentarPonerCharco(int c, int f, float cw, float ch, com.badlogic.gdx.graphics.Texture tex) {
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
                charco.agregarAlMundo(escenario);
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
        if (activaVentisca) {
            try { AudioManager.getControler().stopSound("viento"); } catch(Exception e){}
        }

        activaVentisca = false; activaTornado = false; activaInundacion = false; activaTerremoto = false;
        efectoVentisca.setActivo(false);

        // Restauramos a la última velocidad conocida del jugador
        aplicarVelocidad(velocidadActualJugador);

        estabaEnAgua = false;
        fuerzaTemblor = 0;
        Array<Actor> actores = escenario.getStageMundo().getActors();
        for(int i=actores.size-1; i>=0; i--) {
            Actor a = actores.get(i);
            if(a instanceof Tornado) {
                ((Tornado)a).delete();
            } else if(a instanceof Charco) {
                ((Charco)a).delete();
            }
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
