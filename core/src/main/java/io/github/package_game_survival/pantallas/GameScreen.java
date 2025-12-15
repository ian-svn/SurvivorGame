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
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.package_game_survival.algoritmos.ClickEffect;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.animales.Animal;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.jugadores.Hud;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.BrilloManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.LabelStandard;

public class GameScreen implements Screen {

    private final MyGame game;
    private final FastMenuScreen fm;
    private final Stage stageMundo;
    private final Stage stageUI;
    private final FitViewport viewportUI;
    private final Hud hud;
    private final Jugador jugador;
    private final Escenario escenario;
    private final OrthographicCamera camara;
    private final Vector3 tempVec = new Vector3();
    private Animation<TextureRegion> clickAnimation;
    private LabelStandard labelFinJuego, labelVolverMenu;
    private Image fondoOscuro;
    private boolean juegoTerminado = false;
    private static float ANCHO_MUNDO;
    private static float ALTO_MUNDO;

    public GameScreen(MyGame game) {
        this.game = game;
        ANCHO_MUNDO = game.ANCHO_PANTALLA;
        ALTO_MUNDO = game.ALTO_PANTALLA;

        this.stageMundo = new Stage(game.getViewport());
        this.viewportUI = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO);
        this.stageUI = new Stage(viewportUI);
        Gdx.input.setInputProcessor(stageUI);

        this.jugador = new Jugador("Ian", ANCHO_MUNDO/2, ALTO_MUNDO/2);

        // 1. Crear HUD sin tiempo
        this.hud = new Hud(game.batch, jugador, null);

        // 2. Crear Escenario con HUD
        this.escenario = new Escenario(stageMundo, jugador, hud);
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

    private void cargarEfectosVisuales() {
        try {
            TextureAtlas atlas = Assets.get(PathManager.CLICK_ANIMATION, TextureAtlas.class);
            Array<TextureRegion> frames = new Array<>();
            for (int i = 1; i <= 5; i++) frames.add(atlas.findRegion("click" + i));
            clickAnimation = new Animation<>(0.08f, frames, Animation.PlayMode.NORMAL);
        } catch (Exception e) {}
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        actualizarCamara();
        escenario.renderConShader(camara, delta);

        if (!juegoTerminado) {
            hud.update(delta);
            hud.draw();
        }

        stageUI.act(delta);
        stageUI.draw();

        if (escenario.getGestorTiempo().isJuegoGanado()) {
            if (!juegoTerminado) terminarJuego("GANASTE");
        } else if (juegoTerminado) {
            jugador.setEstrategia(null);
        } else {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                tempVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camara.unproject(tempVec);
                Vector2 dest = new Vector2(tempVec.x, tempVec.y);
                jugador.setEstrategia(new EstrategiaMoverAPunto(dest, escenario.getBloques()));
                if(clickAnimation!=null) stageMundo.addActor(new ClickEffect(clickAnimation, dest.x, dest.y));
            }
            if(jugador.getVida() <= 0) terminarJuego("PERDISTE");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(fm);
            this.camara.zoom = 1f;
        }
    }

    private void actualizarCamara() {
        float zoom = camara.zoom;
        float vw = camara.viewportWidth * zoom;
        float vh = camara.viewportHeight * zoom;
        float x = Math.max(vw/2, Math.min(jugador.getX(), ANCHO_MUNDO - vw/2));
        float y = Math.max(vh/2, Math.min(jugador.getY(), ALTO_MUNDO - vh/2));
        camara.position.set(x, y, 0);
        camara.update();
    }

    private void terminarJuego(String msg) {
        for(Enemigo e : escenario.getEnemigos()) e.remove();
        for(Objeto o : escenario.getObjetos()) o.remove();
        for(Animal a : escenario.getAnimales()) a.remove();
        juegoTerminado = true;
        fondoOscuro.setVisible(true); fondoOscuro.toFront();
        labelFinJuego.setText(msg); labelFinJuego.setVisible(true); labelFinJuego.toFront();
        labelVolverMenu.setVisible(true); labelVolverMenu.toFront();
        labelFinJuego.setPosition(ANCHO_MUNDO/2f - labelFinJuego.getPrefWidth()/2f, ALTO_MUNDO/2f + 50);
        labelVolverMenu.setPosition(ANCHO_MUNDO/2f - labelVolverMenu.getPrefWidth()/2f, ALTO_MUNDO/2f - 100);
    }

    @Override public void resize(int w, int h) {
        stageMundo.getViewport().update(w, h, true);
        viewportUI.update(w, h, true);
        hud.resize(w, h);
        BrilloManager.redimensionar(w, h);
        camara.update();
    }

    @Override public void dispose() {
        stageMundo.dispose(); stageUI.dispose();
        if(hud!=null) hud.dispose();
        if(escenario!=null) escenario.dispose();
        AudioManager.getControler().stopMusic();
    }

    // MÉTODO QUE FALTABA Y CAUSABA ERROR EN FASTMENUSCREEN
    public OrthographicCamera getCamara() { return camara; }

    @Override public void show() { Gdx.input.setInputProcessor(stageUI); BrilloManager.redimensionar(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
