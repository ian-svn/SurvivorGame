package io.github.package_game_survival.entidades.mapas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer; // Importante
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import io.github.package_game_survival.entidades.bloques.*;
import io.github.package_game_survival.entidades.objetos.*;
import io.github.package_game_survival.entidades.seres.animales.*;
import io.github.package_game_survival.entidades.seres.enemigos.*;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.BrilloManager;
import io.github.package_game_survival.managers.GestorTiempo;
import io.github.package_game_survival.managers.GestorSpawneo;
import java.util.Comparator;

public class Escenario implements IMundoJuego, Disposable {

    private final Stage stageMundo;
    private Stage stageUI;
    private final Jugador jugador;

    private final GestorTiempo gestorTiempo;
    private final GestorSpawneo gestorSpawneo;

    private final Array<Bloque> bloques;
    private final Array<Enemigo> enemigos;
    private final Array<Animal> animales;
    private final Array<Objeto> objetos;
    private final Mapa mapa;
    private final Array<Rectangle> cacheRectangulosColision;

    private final Comparator<Actor> comparadorProfundidad;

    // Dimensiones reales del mundo
    private float anchoMundo;
    private float altoMundo;

    public Escenario(Stage stageMundo, Jugador jugador) {
        this.stageMundo = stageMundo;
        this.jugador = jugador;

        this.bloques = new Array<>();
        this.animales = new Array<>();
        this.objetos = new Array<>();
        this.enemigos = new Array<>();
        this.cacheRectangulosColision = new Array<>();

        this.mapa = new Mapa();

        // --- CALCULAR TAMAÑO REAL DEL MAPA ---
        calcularDimensionesMapa();

        this.gestorTiempo = new GestorTiempo();

        this.comparadorProfundidad = new Comparator<Actor>() {
            @Override
            public int compare(Actor a1, Actor a2) {
                return Float.compare(a2.getY(), a1.getY());
            }
        };

        inicializarObjetosFijos();
        inicializarBloquesDesdeMapa();

        agregarEntidadesAlStage();

        // Le pasamos el tamaño real al GestorSpawneo
        this.gestorSpawneo = new GestorSpawneo(this, anchoMundo, altoMundo);
    }

    private void calcularDimensionesMapa() {
        TiledMap tiledMap = mapa.getMapa();
        // Obtenemos la primera capa (suelo) para saber el tamaño
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        // Ancho = Cantidad de Tiles * Tamaño del Tile (ej: 50 * 32 = 1600)
        this.anchoMundo = layer.getWidth() * layer.getTileWidth();
        this.altoMundo = layer.getHeight() * layer.getTileHeight();

        //Gdx.app.log("ESCENARIO", "Mapa cargado: " + anchoMundo + "x" + altoMundo);
    }

    @Override
    public GestorTiempo getGestorTiempo() {
        return this.gestorTiempo;
    }

    public void renderConShader(OrthographicCamera camara, float delta) {
        gestorTiempo.update(delta);
        gestorSpawneo.update(delta);

        // Limpieza de objetos expirados
        for (int i = objetos.size - 1; i >= 0; i--) {
            Objeto obj = objetos.get(i);
            if (obj.isMarcadoParaBorrar()) {
                objetos.removeIndex(i);
            }
        }

        float brilloActual = gestorTiempo.getFactorBrillo();
        BrilloManager.setBrillo(brilloActual);

        FrameBuffer fbo = BrilloManager.getFBO();
        SpriteBatch batchShader = BrilloManager.getBatchShader();
        ShaderProgram shader = BrilloManager.getShader();

        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapa.render(camara);

        stageMundo.getViewport().apply();
        stageMundo.getBatch().setProjectionMatrix(camara.combined);
        stageMundo.getBatch().setColor(1, 1, 1, 1);

        stageMundo.act(delta);
        stageMundo.getActors().sort(comparadorProfundidad);
        stageMundo.draw();

        fbo.end();

        Texture tex = fbo.getColorBufferTexture();
        batchShader.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batchShader.setShader(shader);
        batchShader.begin();
        shader.setUniformf("u_brightness", BrilloManager.getBrillo());
        batchShader.setColor(1, 1, 1, 1);
        batchShader.draw(tex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
        batchShader.end();

        if (stageUI != null) {
            stageUI.getViewport().apply();
            stageUI.act(delta);
            stageUI.draw();
        }
    }

    @Override public Array<Bloque> getBloques() { return bloques; }
    @Override public Array<Enemigo> getEnemigos() { return enemigos; }
    @Override public Array<Objeto> getObjetos() { return objetos; }
    @Override public Jugador getJugador() { return jugador; }
    @Override public void agregarActor(Actor actor) { stageMundo.addActor(actor); }

    @Override
    public void agregarActorUI(Actor actor) {
        if (stageUI != null) stageUI.addActor(actor);
        else stageMundo.addActor(actor);
    }

    @Override
    public Array<Rectangle> getRectangulosNoTransitables() {
        cacheRectangulosColision.clear();
        for (Bloque bloque : bloques) {
            if (!bloque.isTransitable()) {
                cacheRectangulosColision.add(bloque.getRectColision());
            }
        }
        return cacheRectangulosColision;
    }

    private void inicializarObjetosFijos() {
        Cama cama = new Cama(400, 300);
        objetos.add(cama);
    }

    private void inicializarBloquesDesdeMapa() {
        TiledMap tiledMap = mapa.getMapa();
        for (MapLayer layer : tiledMap.getLayers()) {
            if (!(layer instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer tileLayer)) continue;
            for (int x = 0; x < tileLayer.getWidth(); x++) {
                for (int y = 0; y < tileLayer.getHeight(); y++) {
                    var cell = tileLayer.getCell(x, y);
                    if (cell == null || cell.getTile() == null) continue;
                    var tile = cell.getTile();
                    var props = tile.getProperties();
                    boolean transitable = props.get("transitable", true, Boolean.class);
                    if (!transitable) {
                        String tipo = props.get("tipo", "bloque", String.class);
                        boolean destructible = props.get("destructible", false, Boolean.class);
                        String objetoTirado = props.get("objetoTirado", null, String.class);
                        Bloque bloque = destructible
                            ? new BloqueDestructible(x * 32, y * 32, tipo, objetoTirado)
                            : new BloqueNoTransitable(x * 32, y * 32, tipo);
                        bloques.add(bloque);
                    }
                }
            }
        }
    }

    private void agregarEntidadesAlStage() {
        for (Bloque bloque : bloques) bloque.agregarAlMundo(this);
        for (Objeto objeto : objetos) objeto.agregarAlMundo(this);
        jugador.agregarAlMundo(this);
    }

    public OrthographicCamera getCamara() { return (OrthographicCamera) stageMundo.getCamera(); }

    public void setStageUI(Stage stageUI) {
        this.stageUI = stageUI;
        if (gestorTiempo != null) {
            gestorTiempo.agregarAlStage(stageUI);
        }
    }

    public Stage getStageUI() { return stageUI; }

    @Override
    public void dispose() {
        if (mapa != null) {}
        jugador.dispose();
        for(Enemigo e : enemigos) e.dispose();
        for(Objeto o : objetos) o.dispose();
        bloques.clear();
        enemigos.clear();
        animales.clear();
        objetos.clear();
        cacheRectangulosColision.clear();
    }

    public Array<Animal> getAnimales() {
        return this.animales;
    }
}
