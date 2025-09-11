package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.package_game_survival.standards.TextButtonStandard;

public class OptionsScreen implements Screen {

    private final MyGame game;
    private Stage stage;
    private Skin menuSkin, checkBoxSkin;

    public OptionsScreen(final MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        menuSkin = new Skin(Gdx.files.internal("skins/background.json"));
        checkBoxSkin = new Skin(Gdx.files.internal("skins/checkBox.json"));

        CheckBox pantallaCompleta = new CheckBox("Pantalla Completa", checkBoxSkin);
        CheckBox modoVentana = new CheckBox("Modo Ventana", checkBoxSkin);

        TextButtonStandard pantallaCompletaButton = new TextButtonStandard("Pantalla completa");
        pantallaCompletaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                Gdx.graphics.setFullscreenMode(displayMode);
            }
        });

        TextButtonStandard ventanaButton = new TextButtonStandard("Modo ventana");
        ventanaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.graphics.setUndecorated(false);
                Gdx.graphics.setWindowedMode(1280, 720);
            }
        });

        TextButtonStandard volverButton = new TextButtonStandard("Volver al Menu");
        volverButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });

        Table tableOpciones = new Table(menuSkin);
        tableOpciones.setFillParent(true);
        tableOpciones.center();
        tableOpciones.pad(50);
        tableOpciones.add(pantallaCompletaButton).width(220).height(60).pad(10);
        tableOpciones.row();
        tableOpciones.add(ventanaButton).width(220).height(60).pad(10);
        tableOpciones.row();
        tableOpciones.add(volverButton).width(220).height(60).pad(10);

        stage.addActor(tableOpciones);
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
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        menuSkin.dispose();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }
}
