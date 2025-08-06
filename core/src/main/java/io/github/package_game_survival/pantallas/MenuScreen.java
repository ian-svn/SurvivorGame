package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen implements Screen {

    private final MyGame game;
    private Stage stage;
    private Skin jugarSkin, opcionesSkin, salirSkin;

    public MenuScreen(final MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        jugarSkin = new Skin(Gdx.files.internal("skins/JugarButton.json"));
        opcionesSkin = new Skin(Gdx.files.internal("skins/OpcionesButton.json"));
        salirSkin = new Skin(Gdx.files.internal("skins/SalirButton.json"));

        Button jugarButton = new Button(jugarSkin);
        jugarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new GameScreen(game));
            }
        });

        Button opcionesButton = new Button(opcionesSkin);
        opcionesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new OptionsScreen(game));
            }
        });

        Button salirButton = new Button(salirSkin);
        salirButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });

        Table tableMenu = new Table();
        tableMenu.setFillParent(true);
        tableMenu.bottom().left();
        tableMenu.pad(50);
        tableMenu.add(jugarButton).width(220).height(60).pad(10);
        tableMenu.row();
        tableMenu.add(opcionesButton).prefSize(300,60).pad(10);
        tableMenu.row();
        tableMenu.add(salirButton).width(200).height(60).pad(10);

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
        jugarSkin.dispose();
        opcionesSkin.dispose();
        salirSkin.dispose();
    }
}
