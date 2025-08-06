package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.entidades.Jugador;
import io.github.package_game_survival.entidades.Objeto;
import io.github.package_game_survival.entidades.PocionDeAmatista;

public class GameScreen implements Screen { // <-- CORREGIDO: Ya no extiende de Actor

    private final MyGame game;
    private Stage stage;
    private Jugador jugador;
    private Objeto objeto;

    public GameScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        jugador = new Jugador("Ian", new Texture(Gdx.files.internal("sprites/jugador.png")) ,50, 50);
        objeto = new PocionDeAmatista();
        objeto.spawnear(300, 100);

        Skin skin = new Skin(Gdx.files.internal("skins/background.json"));

        stage.addActor(jugador);
        stage.addActor(objeto);
    }

    @Override
    public void render(float delta) { // Usar 'delta' es una convención más clara
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
