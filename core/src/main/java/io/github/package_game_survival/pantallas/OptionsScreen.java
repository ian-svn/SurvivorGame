package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.BrilloManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.CheckBoxStandard;
import io.github.package_game_survival.standards.LabelStandard;
import io.github.package_game_survival.standards.TextButtonStandard;

public class OptionsScreen implements Screen {

    private final MyGame game;
    private Stage stage;
    private Skin background;

    private CheckBoxStandard boxPantallaCompleta;
    private CheckBoxStandard boxModoVentana;

    public OptionsScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        LabelStandard labelPantallCompleta = new LabelStandard("Pantalla Completa");
        LabelStandard labelModoVentana = new LabelStandard("Modo Ventana");

        background = Assets.get(PathManager.BACKGROUND, Skin.class);

        Table table = new Table();
        table.setBackground(background.getDrawable("fondoMenu"));
        table.setFillParent(true);
        stage.addActor(table);

        TextButtonStandard volverButton = new TextButtonStandard("Volver al Menu");
        volverButton.setClickListener(() -> game.setScreen(new MenuScreen(game)));

        boxPantallaCompleta = new CheckBoxStandard();
        boxModoVentana = new CheckBoxStandard();

        ButtonGroup<CheckBox> grupoCheckBoxes = new ButtonGroup<>();
        grupoCheckBoxes.add(boxPantallaCompleta, boxModoVentana);
        grupoCheckBoxes.setMaxCheckCount(1);
        grupoCheckBoxes.setMinCheckCount(0);

        if (Gdx.graphics.isFullscreen()) {
            boxPantallaCompleta.setChecked(true);
            boxPantallaCompleta.setDisabled(true);
            boxModoVentana.setDisabled(false);
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        } else {
            boxModoVentana.setChecked(true);
            boxModoVentana.setDisabled(true);
            boxPantallaCompleta.setDisabled(false);
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }

        boxPantallaCompleta.setClickLister(() -> {
            if (boxPantallaCompleta.isChecked()) {
                Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                Gdx.graphics.setFullscreenMode(displayMode);
                boxPantallaCompleta.setDisabled(true);
                boxModoVentana.setDisabled(false);

                // ✅ Forzar actualización del viewport y FBO
                game.getViewport().update(displayMode.width, displayMode.height, true);
                BrilloManager.redimensionar(displayMode.width, displayMode.height);

                //Gdx.app.log("OptionsScreen", "Pantalla completa activada: " + displayMode.width + "x" + displayMode.height);
            }
        });

        boxModoVentana.setClickLister(() -> {
            if (boxModoVentana.isChecked()) {
                Gdx.graphics.setUndecorated(false);
                Gdx.graphics.setWindowedMode(1280, 720);
                boxModoVentana.setDisabled(true);
                boxPantallaCompleta.setDisabled(false);

                // ✅ Forzar actualización del viewport y FBO
                game.getViewport().update(1280, 720, true);
                BrilloManager.redimensionar(1280, 720);

                //Gdx.app.log("OptionsScreen", "Modo ventana activado: 1280x720");
            }
        });

        table.add(labelPantallCompleta).pad(10);
        table.add(boxPantallaCompleta).pad(10);
        table.row();
        table.add(labelModoVentana).pad(10);
        table.add(boxModoVentana).pad(10);
        table.row();
        table.add(volverButton).pad(20);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        // ✅ También redimensionar el FBO aquí por si acaso
        BrilloManager.redimensionar(width, height);
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
