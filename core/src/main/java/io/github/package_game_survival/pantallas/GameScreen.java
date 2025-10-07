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
import io.github.package_game_survival.standards.LabelStandard;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final MyGame game;
    private final FastMenuScreen fm;

    private Stage stage;
    private Jugador jugador;
    private TooltipManager tm;
    private ArrayList<Bloque> bloques;
    private ArrayList<Objeto> objetos;
    private LabelStandard labelPantallCompleta;

    public GameScreen(MyGame game) {

        this.game = game;

        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        Image fondo = new Image(Assets.get(PathManager.GAME_BACKGROUND_TEXTURE, Texture.class));
        fondo.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(fondo);

        this.bloques = new ArrayList<Bloque>();
        inicializarBloques();

        this.objetos = new ArrayList<Objeto>();
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

        jugador = new Jugador("ian", tm, 100,100);

        stage.addActor(jugador);

        fm = new FastMenuScreen(game, this);

        labelPantallCompleta = new LabelStandard("Puntos: 0");
        labelPantallCompleta.setPosition(1000, 630);
        stage.addActor(labelPantallCompleta);

    }

    private void inicializarBloques() {
        bloques.add(new BloqueDeBarro(500,250));
        bloques.add(new BloqueDeBarro(500,250+ Bloque.ALTO));
        bloques.add(new BloqueDeBarro(500,250+ Bloque.ALTO*2));
    }

    private void inicializarObjetos() {
        objetos.add(new PocionDeAmatista(300,100));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();

        labelPantallCompleta.setText("Puntos: " + jugador.getPuntos());

        jugador.actualizar(bloques, objetos, delta);

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
    @Override public void hide() {
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        AudioManager.getControler().stopMusic();
    }
}
