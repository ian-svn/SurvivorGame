package io.github.package_game_survival;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {

    private Stage stage;
    private Table tableMenu;
    private Table tableOpciones;
    private Skin jugarSkin;
    private Skin opcionesSkin;
    private Skin salirSkin;
    private Skin menuSkin;
    private Button jugarButton;
    private Button opcionesButton;
    private Button salirButton;
    private Viewport viewport; // Renombrado a "viewport" para mayor claridad
    private final float ANCHO_PANTALLA = 1280f;
    private final float ALTO_PANTALLA = 720f;

    @Override
    public void create () {
        // --- 1. Inicializar el Viewport ---
        viewport = new FitViewport(ANCHO_PANTALLA, ALTO_PANTALLA);

        jugarSkin = new Skin(Gdx.files.internal("skins/JugarButton.json"));
        opcionesSkin = new Skin(Gdx.files.internal("skins/OpcionesButton.json"));
        salirSkin = new Skin(Gdx.files.internal("skins/SalirButton.json"));
        menuSkin = new Skin(Gdx.files.internal("skins/background.json"));

        jugarButton = new Button(jugarSkin);
        jugarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                tableMenu.setVisible(false);
            }
        });

        opcionesButton = new Button(opcionesSkin);
        opcionesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                tableMenu.setVisible(false);
                tableOpciones.setVisible(true);
            }
        });

        salirButton = new Button(salirSkin);
        salirButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        tableMenu = new Table();
        tableMenu.setFillParent(true);
        tableMenu.bottom().left();

        tableOpciones = new Table(menuSkin);
        tableOpciones.setFillParent(true);
        tableOpciones.center();
        tableOpciones.setVisible(false);

        tableMenu.pad(50);
        tableMenu.add(jugarButton).width(220).height(60).pad(10);
        tableMenu.row();
        tableMenu.add(opcionesButton).prefSize(300,60).pad(10);
        tableMenu.row();
        tableMenu.add(salirButton).width(200).height(60).pad(10);

        stage.addActor(tableMenu);
    }

    @Override
    public void resize (int width, int height) {
        // --- 3. Actualizar el viewport cuando la ventana cambie de tamaño ---
        viewport.update(width, height, true);
    }

    @Override
    public void render () {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
           

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            Gdx.graphics.setUndecorated(false);
            Gdx.graphics.setWindowedMode(1280, 720);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        jugarSkin.dispose();
        opcionesSkin.dispose();
        salirSkin.dispose();
        menuSkin.dispose();
    }
}
