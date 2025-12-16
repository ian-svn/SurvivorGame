package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TextButtonStandard;

public class MenuScreen implements Screen {

    private final MyGame game;
    private Stage stage;
    private Table tableMenu;

    public MenuScreen(final MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(game.getViewport());

            Image fondo = new Image(Assets.get(PathManager.MENU_BACKGROUND_TEXTURE, Texture.class));
            fondo.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
            stage.addActor(fondo);

        Gdx.input.setInputProcessor(stage);

        tableMenu = new Table();
        tableMenu.setFillParent(true);
        tableMenu.bottom().left().pad(50);

        TextButtonStandard jugarButton = new TextButtonStandard("Jugar");
        jugarButton.setClickListener(() -> {
            game.setScreen(new CharacterSelectionScreen(game));
        });

        TextButtonStandard opcionesButton = new TextButtonStandard("Opciones");
        opcionesButton.setClickListener(() -> {
            game.setScreen(new OptionsScreen(game));
        });

        TextButtonStandard salirButton = new TextButtonStandard("Salir");
        salirButton.setClickListener(() -> Gdx.app.exit());

        tableMenu.add(jugarButton).width(220).height(60).pad(10).row();
        tableMenu.add(opcionesButton).width(220).height(60).pad(10).row();
        tableMenu.add(salirButton).width(220).height(60).pad(10);

        stage.addActor(tableMenu);
    }

    @Override
    public void render(float delta) {
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
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        // PROTECCIÓN CONTRA EL CRASH:
        // Verificamos si stage no es null antes de borrarlo.
        if (stage != null) {
            stage.dispose();
            stage = null; // Lo marcamos como nulo para que no se pueda borrar dos veces
        }
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }
}
