package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.algoritmos.ClickEffect;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.animales.Animal;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.LabelStandard;

public class GameScreen implements Screen {

    private final MyGame game;
    private final FastMenuScreen fm;

    private final Stage stageMundo; // 🌍 Mundo (con shader)
    private final Stage stageUI;    // 🎛️ Interfaz (sin shader)
    private final Jugador jugador;
    private final Escenario escenario;
    private final OrthographicCamera camara;

    private static final float ANCHO_MUNDO = 960;
    private static final float ALTO_MUNDO = 640;

    private final Vector3 tempVec = new Vector3();
    private Animation<TextureRegion> clickAnimation;
    private LabelStandard labelFinJuego;
    private LabelStandard labelVolverMenu;
    private Image fondoOscuro;
    private boolean juegoTerminado = false;
    private float brillo = 0.3f;

    public GameScreen(MyGame game) {
        this.game = game;

        this.stageMundo = new Stage(game.getViewport());
        this.stageUI = new Stage(game.getViewport());

        // Entradas: solo UI
        Gdx.input.setInputProcessor(stageUI);

        this.jugador = new Jugador("Ian", ANCHO_MUNDO/2, ALTO_MUNDO/2);
        this.escenario = new Escenario(stageMundo, jugador);
        this.escenario.setStageUI(stageUI);

        this.fm = new FastMenuScreen(game, this);

        this.camara = (OrthographicCamera) stageMundo.getCamera();
        this.camara.zoom = 0.6f;
        this.camara.update();

        inicializarUI();
        cargarEfectosVisuales();
    }

    private void inicializarUI() {
        fondoOscuro = new Image(Assets.get(PathManager.GAME_BACKGROUND_TEXTURE, Texture.class));
        fondoOscuro.setColor(0, 0, 0, 0.6f);
        fondoOscuro.setSize(ANCHO_MUNDO, ALTO_MUNDO);
        fondoOscuro.setVisible(false);

        labelFinJuego = new LabelStandard("");
        labelFinJuego.setColor(Color.RED);
        labelFinJuego.setFontScale(3f);
        labelFinJuego.setVisible(false);

        labelVolverMenu = new LabelStandard("Presione [ESC] para volver al menu");
        labelVolverMenu.setColor(Color.WHITE);
        labelVolverMenu.setFontScale(0.6f);
        labelVolverMenu.setVisible(false);

        stageUI.addActor(fondoOscuro);
        stageUI.addActor(labelFinJuego);
        stageUI.addActor(labelVolverMenu);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        actualizarCamara();

        // 🌍 Mundo con shader
        escenario.setBrillo(brillo);
        escenario.renderConShader(camara, delta);

        // 🎛️ UI sin shader
        stageUI.act(delta);
        stageUI.draw();

        // 🧠 Lógica
        if (juegoTerminado) {
            jugador.setEstrategia(null);
        } else {
            gestionarClickMovimiento();
            verificarCondicionesFinJuego();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(fm);
            this.camara.zoom = 1f;
        }
    }

    private void actualizarCamara() {
        float zoom = camara.zoom;
        float viewportAncho = camara.viewportWidth * zoom;
        float viewportAlto = camara.viewportHeight * zoom;

        float camX = jugador.getX();
        float camY = jugador.getY();

        float minX = viewportAncho / 2f;
        float maxX = ANCHO_MUNDO - viewportAncho / 2f;
        float minY = viewportAlto / 2f;
        float maxY = ALTO_MUNDO - viewportAlto / 2f;

        camX = Math.max(minX, Math.min(camX, maxX));
        camY = Math.max(minY, Math.min(camY, maxY));

        camara.position.set(camX, camY, 0);
        camara.update();
    }

    private void verificarCondicionesFinJuego() {
        if (jugador.getVida() <= 0) {
            terminarJuego("PERDISTE");
        } else if (jugador.getInventario().size() >= 3) {
            terminarJuego("GANASTE");
        }
    }

    private void gestionarClickMovimiento() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            tempVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camara.unproject(tempVec);
            Vector2 destino = new Vector2(tempVec.x, tempVec.y);
            jugador.setEstrategia(new EstrategiaMoverAPunto(destino));

            if (clickAnimation != null)
                stageMundo.addActor(new ClickEffect(clickAnimation, destino.x, destino.y));
        }
    }

    private void cargarEfectosVisuales() {
        try {
            TextureAtlas atlas = Assets.get(PathManager.CLICK_ANIMATION, TextureAtlas.class);
            Array<TextureRegion> frames = new Array<>();
            for (int i = 1; i <= 5; i++) {
                TextureRegion frame = atlas.findRegion("click" + i);
                if (frame != null)
                    frames.add(frame);
            }
            if (frames.size > 0)
                clickAnimation = new Animation<>(0.08f, frames, Animation.PlayMode.NORMAL);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error al cargar animacion de clic", e);
        }
    }

    private void terminarJuego(String mensaje) {
        for (Enemigo enemigo : escenario.getEnemigos()) enemigo.remove();
        for (Objeto objeto : escenario.getObjetos()) objeto.remove();
        for (Animal animal : escenario.getAnimales()) animal.remove();

        juegoTerminado = true;

        fondoOscuro.setVisible(true);
        fondoOscuro.toFront();

        labelFinJuego.setText(mensaje);
        labelFinJuego.setVisible(true);
        labelFinJuego.toFront();

        labelVolverMenu.setVisible(true);
        labelVolverMenu.toFront();

        centrarUI();
    }

    private void centrarUI() {
        float camX = camara.position.x;
        float camY = camara.position.y;

        fondoOscuro.setPosition(camX - fondoOscuro.getWidth() / 2f,
            camY - fondoOscuro.getHeight() / 2f);

        labelFinJuego.setPosition(camX - labelFinJuego.getPrefWidth() / 2f,
            camY + 30);

        labelVolverMenu.setPosition(camX - labelVolverMenu.getPrefWidth() / 2f,
            camY - labelFinJuego.getPrefHeight() - 40);
    }

    @Override
    public void resize(int width, int height) {
        stageMundo.getViewport().update(width, height, true);
        stageUI.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stageMundo.dispose();
        stageUI.dispose();
        escenario.dispose();
        AudioManager.getControler().stopMusic();
    }

    public OrthographicCamera getCamara() { return camara; }
    @Override public void show() { Gdx.input.setInputProcessor(stageUI); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
