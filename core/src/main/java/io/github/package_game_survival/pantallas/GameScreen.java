package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.entidades.Jugador;
import io.github.package_game_survival.entidades.Objeto;
import io.github.package_game_survival.entidades.PocionDeAmatista;
import io.github.package_game_survival.managers.Audio.AudioControler;
import io.github.package_game_survival.managers.Audio.AudioManager;

public class GameScreen implements Screen {
    private final MyGame game;
    private final FastMenuScreen fm;

    private Stage stage;
    private Jugador jugador;
    private Objeto objeto;
    private TooltipManager tm;
    private AudioControler audioManager = AudioManager.getControler();

    public GameScreen(MyGame game) {

        audioManager.stopMusic();
        audioManager.loadMusic("gameMusic","sounds/MyCastleTown.mp3");
        audioManager.playMusic("gameMusic",true);

        this.game = game;

        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        tm = new TooltipManager();
        tm.initialTime = 1f;
        tm.subsequentTime = 0.2f;
        tm.resetTime = 0f;
        tm.animations = false;
        tm.hideAll();
        tm.maxWidth = 200f;

        jugador = new Jugador("ian", new Texture(Gdx.files.internal("sprites/jugador.png")), 100,100, tm);
        objeto = new PocionDeAmatista();
        objeto.spawnear(300, 100);

        stage.addActor(jugador);
        stage.addActor(objeto);

        fm = new FastMenuScreen(game, this);
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
        audioManager.stopMusic();
    }
}
