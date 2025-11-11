package io.github.package_game_survival.entidades.mapas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.package_game_survival.entidades.bloques.*;
import io.github.package_game_survival.entidades.objetos.*;
import io.github.package_game_survival.entidades.seres.animales.*;
import io.github.package_game_survival.entidades.seres.enemigos.*;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;

import java.util.ArrayList;
import java.util.List;

public class Escenario {

    private final Stage stageMundo;
    private Stage stageUI;
    private final Jugador jugador;
    private final ArrayList<Bloque> bloques;
    private final ArrayList<Enemigo> enemigos;
    private final ArrayList<Animal> animales;
    private final ArrayList<Objeto> objetos;
    private final Mapa mapa;

    // 🔹 Shader
    private FrameBuffer fbo;
    private SpriteBatch batchShader;
    private ShaderProgram shaderBrillo;
    private float brillo = 1.0f; // 1.0 = normal

    public Escenario(Stage stageMundo, Jugador jugador) {
        this.stageMundo = stageMundo;
        this.jugador = jugador;
        this.bloques = new ArrayList<>();
        this.animales = new ArrayList<>();
        this.objetos = new ArrayList<>();
        this.enemigos = new ArrayList<>();
        this.mapa = new Mapa();

        inicializarEnemigos();
        inicializarAnimales();
        inicializarObjetos();
        inicializarBloquesDesdeMapa();
        agregarEntidadesAlStage();

        inicializarShader();
    }

    // 🔧 Inicializar shader y framebuffer
    private void inicializarShader() {
        ShaderProgram.pedantic = false;

        shaderBrillo = new ShaderProgram(
            Gdx.files.internal("shaders/brillo.vert"),
            Gdx.files.internal("shaders/brillo.frag")
        );

        if (!shaderBrillo.isCompiled()) {
            System.err.println("❌ Error compilando shader: " + shaderBrillo.getLog());
        }

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            false);
        batchShader = new SpriteBatch();
    }

    // 🎨 Render del mundo completo (tilemap + entidades) con shader de brillo
    public void renderConShader(OrthographicCamera camara, float delta) {
        // 1️⃣ Render al FBO
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapa.render(camara);

        stageMundo.getViewport().apply();
        stageMundo.getBatch().setProjectionMatrix(camara.combined);

        stageMundo.act(delta);
        stageMundo.draw();

        fbo.end();

        // 2️⃣ Dibujar el FBO aplicando el shader
        Texture textura = fbo.getColorBufferTexture();

        batchShader.setShader(shaderBrillo);
        batchShader.begin();

        shaderBrillo.setUniformf("u_brightness", brillo);

        // Invertir eje Y del FBO
        batchShader.draw(textura,
            0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
            0, 0, 1, 1);

        batchShader.end();
    }

    // 🔦 Control de brillo
    public void setBrillo(float valor) {
        this.brillo = Math.max(0f, valor);
    }

    public float getBrillo() {
        return brillo;
    }

    // 🧹 Limpieza
    public void dispose() {
        mapa.dispose();
        if (fbo != null) fbo.dispose();
        if (shaderBrillo != null) shaderBrillo.dispose();
        if (batchShader != null) batchShader.dispose();
    }

    // --- Inicialización de entidades ---
    private void inicializarObjetos() {
        objetos.add(new PocionDeAmatista(200, 600));
        objetos.add(new PocionDeAmatista(400, 400));
        objetos.add(new PocionDeAmatista(600, 200));
    }

    private void inicializarAnimales() {
        animales.add(new Vaca(400, 300));
        animales.add(new Jabali(20, 100));
        animales.add(new Vaca(350, 500));
    }

    private void inicializarEnemigos() {
        enemigos.add(new InvasorArquero(200, 500));
        enemigos.add(new InvasorMago(50, 100));
        enemigos.add(new InvasorDeLaLuna(280, 100));
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

    public List<com.badlogic.gdx.math.Rectangle> getRectangulosBloquesNoTransitables() {
        List<com.badlogic.gdx.math.Rectangle> rectangulos = new ArrayList<>();
        for (Bloque bloque : bloques) {
            if (!bloque.isTransitable()) {
                rectangulos.add(bloque.getRectColision());
            }
        }
        return rectangulos;
    }

    private void agregarEntidadesAlStage() {
        for (Bloque bloque : bloques) bloque.agregarAlEscenario(this);
        for (Enemigo enemigo : enemigos) enemigo.agregarAlEscenario(this);
        for (Animal animal : animales) animal.agregarAlEscenario(this);
        for (Objeto objeto : objetos) objeto.agregarAlEscenario(this);
        jugador.agregarAlEscenario(this);
    }

    public void agregar(Actor actor) {
        stageMundo.addActor(actor);
    }

    public ArrayList<Bloque> getBloques() { return bloques; }
    public OrthographicCamera getCamara() { return (OrthographicCamera) this.stageMundo.getCamera(); }
    public Jugador getJugador() { return jugador; }
    public ArrayList<Enemigo> getEnemigos() { return enemigos; }
    public ArrayList<Objeto> getObjetos() { return objetos; }
    public ArrayList<Animal> getAnimales() { return animales; }

    public void setStageUI(Stage stageUI) {
        this.stageUI = stageUI;
    }

    public Stage getStageUI() {
        return stageUI;
    }

}
