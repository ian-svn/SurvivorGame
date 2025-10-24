package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.algoritmos.ClickEffect;
import io.github.package_game_survival.entidades.*;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.standards.LabelStandard;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final MyGame game;
    private final FastMenuScreen fm;

    private Stage stage;
    private Jugador jugador;
    private ArrayList<Bloque> bloques;
    private ArrayList<Objeto> objetos;
    private ArrayList<Enemigo> enemigos;

    private final float WORLD_WIDTH = 1800;
    private final float WORLD_HEIGHT = 1200;

    private final Vector3 tempVec = new Vector3();

    private Animation<TextureRegion> clickAnimation;

    private LabelStandard labelFinJuego;
    private LabelStandard labelVolverMenu;
    private Image fondoOscuro;
    private boolean juegoTerminado = false;

    public GameScreen(MyGame game) {
        this.game = game;
        stage = new Stage(game.getViewport());

        Gdx.input.setInputProcessor(stage);

        jugador = new Jugador("ian", 100, 100);

        cargarEfectosVisuales();
        this.enemigos = new ArrayList<>();
        this.bloques = new ArrayList<>();
        this.objetos = new ArrayList<>();

        inicializarFondo();
        inicializarBloques();
        inicializarObjetos();
        inicializarEnemigos();

        agregarAlStageActores();

        fm = new FastMenuScreen(game, this);
        jugador.agregarAlStage(stage);

        inicializarLabelsYFondoFinJuego();
    }

    private void inicializarLabelsYFondoFinJuego() {
        fondoOscuro = new Image(Assets.get(PathManager.GAME_BACKGROUND_TEXTURE, Texture.class));
        fondoOscuro.setColor(0, 0, 0, 0.6f);
        fondoOscuro.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        fondoOscuro.setVisible(false);

        labelFinJuego = new LabelStandard("");
        labelFinJuego.setColor(Color.RED);
        labelFinJuego.setFontScale(3f);
        labelFinJuego.setVisible(false);
        labelFinJuego.setPosition(MyGame.ANCHO_PANTALLA / 2f - 150, MyGame.ALTO_PANTALLA / 2f);
        stage.addActor(labelFinJuego);
        stage.addActor(fondoOscuro);
    }

    private void agregarAlStageActores() {
        for (Enemigo enemigo : enemigos){
            stage.addActor(enemigo);
        }

        for (Bloque bloque : bloques){
            stage.addActor(bloque);
        }

        for (Objeto objeto : objetos){
            stage.addActor(objeto);
        }
    }

    private void inicializarFondo() {
        Image fondo = new Image(Assets.get(PathManager.GAME_BACKGROUND_TEXTURE, Texture.class));
        fondo.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        stage.addActor(fondo);
    }

    private void inicializarEnemigos() {
        enemigos.add(new Enemigo(Bloque.ALTO*8,Bloque.ALTO*5, jugador, bloques));
    }

    private void inicializarBloques() {
        for (int x = 0; x < WORLD_WIDTH; x += Bloque.ANCHO) {
            bloques.add(new BloqueDeBarro(x, 0));
            bloques.add(new BloqueDeBarro(x, (int)WORLD_HEIGHT - Bloque.ALTO));
        }
        for (int y = Bloque.ALTO; y < WORLD_HEIGHT - Bloque.ALTO; y += Bloque.ALTO) {
            bloques.add(new BloqueDeBarro(0, y));
            bloques.add(new BloqueDeBarro((int)WORLD_WIDTH - Bloque.ANCHO, y));
        }
        bloques.add(new BloqueDeBarro(Bloque.ANCHO*3, Bloque.ALTO*3));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO*4, Bloque.ALTO*3));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO*4, Bloque.ALTO*4));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO*10, Bloque.ALTO*3));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO*10, Bloque.ALTO*4));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO*11, Bloque.ALTO*3));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO*20, Bloque.ALTO*10));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO*20, Bloque.ALTO*11));
    }

    private void inicializarObjetos() {
        objetos.add(new PocionDeAmatista(300, 100));
        objetos.add(new PocionDeAmatista(900, 400));
        objetos.add(new PocionDeAmatista(400, 700));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            jugador.alterarVida(-200); // fuerza a morir
        }

        ScreenUtils.clear(0, 0, 0, 1);

        if (!juegoTerminado) {

            float camX = jugador.getX();
            float camY = jugador.getY();
            camX = Math.max(MyGame.ANCHO_PANTALLA / 2f, Math.min(camX, WORLD_WIDTH - MyGame.ANCHO_PANTALLA / 2f));
            camY = Math.max(MyGame.ALTO_PANTALLA / 2f, Math.min(camY, WORLD_HEIGHT - MyGame.ALTO_PANTALLA / 2f));
            stage.getCamera().position.set(camX, camY, 0);
            stage.getCamera().update();

            gestionarClickMovimiento();

            stage.act(delta);
            stage.draw();

            // Verificar condiciones de fin de juego
            if (jugador.getVida() <= 0) {
                terminarJuego("PERDISTE");
            } else if (jugador.getInventario().size() >= 3) {
                terminarJuego("GANASTE");
            }

        } else {
            // Solo dibujamos el fondo y el label
            stage.act(delta);
            stage.draw();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(fm);
        }

    }

    private void cargarEfectosVisuales() {
        try {
            TextureAtlas atlas = Assets.get(PathManager.CLICK_ANIMATION, TextureAtlas.class);

            Array<TextureRegion> frames = new Array<>();
            frames.add(atlas.findRegion("click1"));
            frames.add(atlas.findRegion("click2"));
            frames.add(atlas.findRegion("click3"));
            frames.add(atlas.findRegion("click4"));
            frames.add(atlas.findRegion("click5"));

            this.clickAnimation = new Animation<>(0.08f, frames, Animation.PlayMode.NORMAL);

        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Error al cargar la animación de clic. ¿Está el atlas en Assets y PathManager?", e);
            this.clickAnimation = null;
        }
    }

    private void gestionarClickMovimiento() {
        if (juegoTerminado) return; // no permitir clicks si terminó

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {

            tempVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            stage.getCamera().unproject(tempVec);
            Vector2 destino = new Vector2(tempVec.x, tempVec.y);

            jugador.setEstrategia(new EstrategiaMoverAPunto(destino));

            if (this.clickAnimation != null) {
                ClickEffect effect = new ClickEffect(this.clickAnimation, destino.x, destino.y);
                stage.addActor(effect);
            }
        }
    }

    private void terminarJuego(String mensaje) {
        juegoTerminado = true;

        fondoOscuro.setVisible(true);
        fondoOscuro.toFront();

        labelFinJuego.setText(mensaje);
        labelFinJuego.setVisible(true);
        labelFinJuego.toFront();


        labelFinJuego.setPosition(
            stage.getCamera().position.x - labelFinJuego.getWidth(),
            stage.getCamera().position.y - labelFinJuego.getHeight() / 2
        );


        labelVolverMenu = new LabelStandard("Precione [ESC] para volver\n a jugar o volver al menu");
        labelVolverMenu.setPosition(stage.getCamera().position.x - labelVolverMenu.getWidth() / 2,
            stage.getCamera().position.y - labelVolverMenu.getHeight() / 2 - 150
        );
        stage.addActor(labelVolverMenu);

        jugador.remove();
        for (Enemigo e: enemigos) { e.remove(); }
    }




    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        AudioManager.getControler().stopMusic();
    }
}
