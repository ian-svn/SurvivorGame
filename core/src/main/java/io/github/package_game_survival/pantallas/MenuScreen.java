package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.standards.TextButtonStandard;

public class MenuScreen implements Screen {

    private final MyGame game;
    private Stage stage;
    private Skin menuSkin;
    private Table tableMenu;

    public MenuScreen(final MyGame game) {
        this.game = game;

        Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setFullscreenMode(displayMode);
    }

    @Override
    public void show() {

        AudioManager.getControler().playMusic("menuMusic",true);

        menuSkin = new Skin(Gdx.files.internal("skins/background.json"));
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        tableMenu = new Table();
        tableMenu.setFillParent(true);
        tableMenu.bottom().left().pad(50);

        tableMenu.setBackground(menuSkin.getDrawable("fondoMenu"));

        TextButtonStandard jugarButton = new TextButtonStandard("Jugar");
        jugarButton.setClickListener(() -> game.setScreen(new GameScreen(game)));

        TextButtonStandard opcionesButton = new TextButtonStandard("Opciones");
        opcionesButton.setClickListener(() -> game.setScreen(new OptionsScreen(game)));

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
        stage.dispose();
        menuSkin.dispose();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }
}
