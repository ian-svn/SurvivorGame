package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.entidades.Jugador;
import io.github.package_game_survival.entidades.Objeto;
import io.github.package_game_survival.entidades.PocionDeAmatista;
import io.github.package_game_survival.managers.PathManager;

public class GameScreen implements Screen{
    private final MyGame game;
    private Stage stage;
    private Jugador jugador;
    private Objeto objeto;
    private TooltipManager tm;

    public GameScreen(MyGame game) {
        this.game = game;

        tm = new TooltipManager();
        tm.initialTime = 1f;
        tm.subsequentTime = 0.2f;
        tm.resetTime = 0f;
        tm.animations = false;
        tm.hideAll();
        tm.maxWidth = 200f;
    }

    @Override
    public void show() {
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        jugador = new Jugador("ian", new Texture(Gdx.files.internal(PathManager.JUGADOR)), 100,100, tm);

        objeto = new PocionDeAmatista();
        objeto.spawnear(300, 100);

        stage.addActor(jugador);
        stage.addActor(objeto);
    }

    @Override
    public void render(float delta){
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
    }
}
