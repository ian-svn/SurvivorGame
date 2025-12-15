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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import io.github.package_game_survival.desastres.Charco;
import io.github.package_game_survival.entidades.bloques.*;
import io.github.package_game_survival.entidades.efectos.EfectoVisual;
import io.github.package_game_survival.entidades.objetos.*;
import io.github.package_game_survival.entidades.objetos.Hoguera;
import io.github.package_game_survival.entidades.seres.animales.*;
import io.github.package_game_survival.entidades.seres.enemigos.*;
import io.github.package_game_survival.entidades.seres.jugadores.Hud;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.BrilloManager;
import io.github.package_game_survival.managers.GestorDesastres;
import io.github.package_game_survival.managers.GestorSpawneo;
import io.github.package_game_survival.managers.GestorTiempo;

import java.util.Comparator;

public class Escenario implements IMundoJuego, Disposable {

    private final Stage stageMundo;
    private Stage stageUI;
    private final Jugador jugador;
    private final Hud hud;

    private final GestorTiempo gestorTiempo;
    private final GestorSpawneo gestorSpawneo;
    private final GestorDesastres gestorDesastres;

    private final Array<Bloque> bloques = new Array<>();
    private final Array<Enemigo> enemigos = new Array<>();
    private final Array<Animal> animales = new Array<>();
    private final Array<Objeto> objetos = new Array<>();
    private final Mapa mapa;
    private final Array<Rectangle> cacheRectangulosColision = new Array<>();
    private final Comparator<Actor> comparadorProfundidad;

    private float anchoMundo, altoMundo;

    public Escenario(Stage stageMundo, Jugador jugador, Hud hud) {
        this.stageMundo = stageMundo;
        this.jugador = jugador;
        this.hud = hud;

        this.mapa = new Mapa();
        calcularDimensionesMapa();

        this.gestorTiempo = new GestorTiempo();
        if(hud != null) hud.setGestorTiempo(gestorTiempo);

        this.gestorDesastres = new GestorDesastres(this, jugador, hud);

        this.comparadorProfundidad = (a1, a2) -> {
            boolean a1Top = (a1 instanceof EfectoVisual);
            boolean a2Top = (a2 instanceof EfectoVisual);
            if(a1Top && !a2Top) return 1;
            if(!a1Top && a2Top) return -1;

            boolean a1Bot = (a1 instanceof Charco);
            boolean a2Bot = (a2 instanceof Charco);
            if(a1Bot && !a2Bot) return -1;
            if(!a1Bot && a2Bot) return 1;

            return Float.compare(a2.getY(), a1.getY());
        };

        inicializarObjetosFijos(); // (Solo la cama)
        inicializarBloquesDesdeMapa(); // (Aquí se carga la hoguera)
        agregarEntidadesAlStage();

        this.gestorSpawneo = new GestorSpawneo(this, anchoMundo, altoMundo);
    }

    private void calcularDimensionesMapa() {
        TiledMap tiledMap = mapa.getMapa();
        if (tiledMap.getLayers().getCount() > 0) {
            TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
            this.anchoMundo = layer.getWidth() * layer.getTileWidth();
            this.altoMundo = layer.getHeight() * layer.getTileHeight();
        }
    }

    public void renderConShader(OrthographicCamera camara, float delta) {
        if(jugador != null) jugador.setSintiendoCalor(false);

        stageMundo.act(delta);
        stageMundo.getActors().sort(comparadorProfundidad);

        gestorTiempo.update(delta);
        gestorSpawneo.update(delta);
        gestorDesastres.update(delta);

        for (int i = objetos.size - 1; i >= 0; i--) {
            if (objetos.get(i).isMarcadoParaBorrar()) objetos.removeIndex(i);
        }

        BrilloManager.setBrillo(gestorTiempo.getFactorBrillo());
        FrameBuffer fbo = BrilloManager.getFBO();
        SpriteBatch batchShader = BrilloManager.getBatchShader();
        ShaderProgram shader = BrilloManager.getShader();

        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapa.render(camara);

        stageMundo.getBatch().setProjectionMatrix(camara.combined);
        stageMundo.getBatch().setColor(1, 1, 1, 1);
        stageMundo.draw();

        stageMundo.getBatch().begin();
        gestorDesastres.draw(stageMundo.getBatch());
        stageMundo.getBatch().end();

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

    private void inicializarObjetosFijos() {
        Cama cama = new Cama(400, 300);
        objetos.add(cama);
        // ELIMINADO: Hoguera hoguera = new Hoguera(500, 300); objetos.add(hoguera);
        // Ahora se carga desde el mapa (Tiled)
    }

    private void inicializarBloquesDesdeMapa() {
        TiledMap tiledMap = mapa.getMapa();
        for (MapLayer layer : tiledMap.getLayers()) {
            if (!(layer instanceof TiledMapTileLayer)) continue;
            TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;

            for (int x = 0; x < tileLayer.getWidth(); x++) {
                for (int y = 0; y < tileLayer.getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                    if (cell == null || cell.getTile() == null) continue;

                    var tile = cell.getTile();
                    var props = tile.getProperties();
                    float worldX = x * 32;
                    float worldY = y * 32;

                    // --- DETECCIÓN DE HOGUERA ---
                    // "cuando reconoce un 'bloqueAnimado' = true con el 'nombre' = 'hoguera'"
                    boolean esAnimado = props.get("bloqueAnimado", false, Boolean.class);
                    String nombre = props.get("nombre", String.class);

                    if (esAnimado && "hoguera".equals(nombre)) {
                        Hoguera h = new Hoguera(worldX, worldY);
                        // Al agregarla al mundo, la hoguera buscará al jugador para darle calor
                        h.agregarAlMundo(this);
                        objetos.add(h);

                        // Importante: Quitamos el tile estático para que se vea nuestro objeto animado
                        cell.setTile(null);
                        continue;
                    }

                    // --- OTROS BLOQUES ---
                    BloqueAnimado anim = BloqueAnimado.verificarCreacion(tile, worldX, worldY);
                    if (anim != null) {
                        bloques.add(anim);
                        cell.setTile(null);
                        continue;
                    }

                    boolean transitable = props.get("transitable", true, Boolean.class);
                    if (!transitable) {
                        String tipo = props.get("tipo", "bloque", String.class);
                        boolean destructible = props.get("destructible", false, Boolean.class);
                        String drop = props.get("objetoTirado", null, String.class);
                        Bloque b = destructible ? new BloqueDestructible(worldX, worldY, tipo, drop) : new BloqueNoTransitable(worldX, worldY, tipo);
                        bloques.add(b);
                    }
                }
            }
        }
    }

    private void agregarEntidadesAlStage() {
        for(Bloque b : bloques) b.agregarAlMundo(this);
        for(Objeto o : objetos) o.agregarAlMundo(this);
        jugador.agregarAlMundo(this);
    }

    @Override public float getAncho() { return anchoMundo; }
    @Override public float getAlto() { return altoMundo; }
    @Override public GestorTiempo getGestorTiempo() { return gestorTiempo; }
    @Override public Array<Bloque> getBloques() { return bloques; }
    @Override public Array<Enemigo> getEnemigos() { return enemigos; }
    @Override public Array<Objeto> getObjetos() { return objetos; }
    @Override public Jugador getJugador() { return jugador; }
    @Override public Array<Animal> getAnimales() { return animales; }
    @Override public void agregarActor(Actor actor) { stageMundo.addActor(actor); }
    @Override public void agregarActorUI(Actor actor) { if (stageUI != null) stageUI.addActor(actor); else stageMundo.addActor(actor); }
    public OrthographicCamera getCamara() { return (OrthographicCamera) stageMundo.getCamera(); }
    public Stage getStageMundo() { return stageMundo; }
    public Stage getStageUI() { return stageUI; }
    public void setStageUI(Stage s) { this.stageUI = s; }

    @Override
    public Array<Rectangle> getRectangulosNoTransitables() {
        cacheRectangulosColision.clear();
        for (Bloque b : bloques) if (!b.isTransitable()) cacheRectangulosColision.add(b.getRectColision());
        return cacheRectangulosColision;
    }

    @Override public void dispose() {
        if(jugador != null) jugador.dispose();
        for(Enemigo e : enemigos) e.dispose();
        for(Objeto o : objetos) o.dispose();
        bloques.clear(); enemigos.clear(); animales.clear(); objetos.clear(); cacheRectangulosColision.clear();
        if(mapa != null) mapa.dispose();
        if(gestorDesastres != null) gestorDesastres.dispose();
    }
}
