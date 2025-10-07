package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TextButtonStandard;

public class FastMenuScreen implements Screen {

    private final MyGame game;
    private final GameScreen gameScreen;

    private Stage stage;
    private Skin background;

    public FastMenuScreen(final MyGame game, final GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        this.background = Assets.get(PathManager.BACKGROUND, Skin.class);
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        TextButtonStandard reanudarButton = new TextButtonStandard("Reanudar");
        reanudarButton.setClickListener(() -> {
                game.setScreen(gameScreen);
                dispose();
        });

        TextButtonStandard resetButton = new TextButtonStandard("Jugar otra vez");
        resetButton.setClickListener(() -> {
                game.setScreen(new GameScreen(game));
                dispose();
        });

        TextButtonStandard volverMenuButton = new TextButtonStandard("Volver al Menu");
        volverMenuButton.setClickListener(() -> {
            game.setScreen(new MenuScreen(game));
            AudioManager.getControler().changeMusic("menuMusic", PathManager.MENU_MUSIC, true);
            AudioManager.getControler().setVolume(20);
            dispose();
        });

        Table table = new Table();
        table.setBackground(background.getDrawable("fondoMenu"));
        table.setFillParent(true);
        table.center();
        table.pad(50);
        table.add(reanudarButton).width(220).height(60).pad(10);
        table.row();
        table.add(resetButton).width(220).height(60).pad(10);
        table.row();
        table.add(volverMenuButton).width(220).height(60).pad(10);

        stage.addActor(table);
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

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }
}
