package io.github.package_game_survival;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {

    private Stage stage;
    private Table table;
    private Skin jugarSkin;
    private Skin opcionesSkin;
    private Skin salirSkin;
    private Button jugarButton;
    private Button opcionesButton;
    private Button salirButton;
    private Viewport vierport;
    private final float ANCHO_PANTALLA = 1280f;
    private final float ALTO_PANTALLA = 720f;

    @Override
    public void create () {
        vierport = new FitViewport(ANCHO_PANTALLA, ALTO_PANTALLA);

        jugarSkin = new Skin(Gdx.files.internal("skins/JugarButton.json"));
        opcionesSkin = new Skin(Gdx.files.internal("skins/OpcionesButton.json"));
        salirSkin = new Skin(Gdx.files.internal("skins/SalirButton.json"));

        jugarButton = new Button(jugarSkin);
        opcionesButton = new Button(opcionesSkin);
        salirButton = new Button(salirSkin);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.bottom().left();

        table.pad(50);
        table.add(jugarButton).width(220).height(60).pad(10);
        table.row();
        table.add(opcionesButton).prefSize(300,60).pad(10);
        table.row();
        table.add(salirButton).width(200).height(60).pad(10);

        stage.addActor(table);
    }

    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render () {
        ScreenUtils.clear(0, 0, 0, 1); // fondo negro
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        opcionesSkin.dispose();
    }
}
