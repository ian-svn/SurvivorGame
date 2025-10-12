package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.entidades.*;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final MyGame game;
    private final FastMenuScreen fm;

    private Stage stage;
    private Jugador jugador;
    private TooltipManager tm;
    private ArrayList<Bloque> bloques;
    private ArrayList<Objeto> objetos;

    private final float WORLD_WIDTH = 1800; // ancho del mundo
    private final float WORLD_HEIGHT = 1000; // alto del mundo

    public GameScreen(MyGame game) {
        this.game = game;

        stage = new Stage(game.getViewport());

        Image fondo = new Image(Assets.get(PathManager.GAME_BACKGROUND_TEXTURE, Texture.class));
        fondo.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        stage.addActor(fondo);

        Gdx.input.setInputProcessor(stage);

        this.bloques = new ArrayList<>();
        inicializarBloques();

        this.objetos = new ArrayList<>();
        inicializarObjetos();

        for (Bloque bloque : bloques){
            stage.addActor(bloque);
        }

        for (Objeto objeto : objetos){
            stage.addActor(objeto);
        }

        tm = new TooltipManager();
        tm.initialTime = 1f;
        tm.subsequentTime = 0.2f;
        tm.resetTime = 0f;
        tm.animations = false;
        tm.hideAll();
        tm.maxWidth = 200f;

        fm = new FastMenuScreen(game, this);

        jugador = new Jugador("ian", tm, 100, 100);
        jugador.agregarAlStage(stage);
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

        bloques.add(new BloqueDeBarro(300, 300));
        bloques.add(new BloqueDeBarro(400, 300));
        bloques.add(new BloqueDeBarro(500, 300));

        bloques.add(new BloqueDeBarro(700, 500));
        bloques.add(new BloqueDeBarro(700, 560));
        bloques.add(new BloqueDeBarro(760, 500));

        bloques.add(new BloqueDeBarro(1200, 200));
        bloques.add(new BloqueDeBarro(1260, 200));
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
        ScreenUtils.clear(0, 0, 0, 1);

        jugador.actualizar(bloques, objetos, delta);

        // --- CAMARA SIGUE AL JUGADOR ---
        float camX = jugador.getX();
        float camY = jugador.getY();

        // Limitar para no mostrar fuera del mundo
        camX = Math.max(MyGame.ANCHO_PANTALLA / 2f, Math.min(camX, WORLD_WIDTH - MyGame.ANCHO_PANTALLA / 2f));
        camY = Math.max(MyGame.ALTO_PANTALLA / 2f, Math.min(camY, WORLD_HEIGHT - MyGame.ALTO_PANTALLA / 2f));

        stage.getCamera().position.set(camX, camY, 0);
        stage.getCamera().update();
        // --------------------------------

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(fm);
        }
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
