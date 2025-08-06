package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class OptionsScreen implements Screen {

    private final MyGame game;
    private Stage stage;
    private Skin jugarSkin, opcionesSkin, salirSkin, menuSkin;

    public OptionsScreen(final MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        jugarSkin = new Skin(Gdx.files.internal("skins/JugarButton.json"));
        opcionesSkin = new Skin(Gdx.files.internal("skins/OpcionesButton.json"));
        salirSkin = new Skin(Gdx.files.internal("skins/SalirButton.json"));
        menuSkin = new Skin(Gdx.files.internal("skins/background.json"));

        Button pantallaCompletaButton = new Button(jugarSkin);
        pantallaCompletaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                Gdx.graphics.setFullscreenMode(displayMode);
            }
        });

        Button ventanaButton = new Button(opcionesSkin);
        ventanaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.graphics.setUndecorated(false);
                Gdx.graphics.setWindowedMode(1280, 720);
            }
        });

        Button volverButton = new Button(salirSkin);
        volverButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });

        // Configurar la tabla de opciones
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
        jugarSkin.dispose();
        opcionesSkin.dispose();
        salirSkin.dispose();
        menuSkin.dispose();
    }
}
