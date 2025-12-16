package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private Skin skin;

    public FastMenuScreen(final MyGame game, final GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        // Cargar Skin
        this.skin = Assets.get(PathManager.BACKGROUND, Skin.class);

        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. BOTÓN REANUDAR
        TextButtonStandard reanudarButton = new TextButtonStandard("Reanudar");
        reanudarButton.setClickListener(() -> {
            // No detenemos el viento aquí para que la atmósfera siga al volver al juego
            game.setScreen(gameScreen);
            gameScreen.getCamara().zoom = 0.6f;
        });

        // 2. BOTÓN REINICIAR
        TextButtonStandard resetButton = new TextButtonStandard("Reiniciar");
        resetButton.setClickListener(() -> {
            // CORRECCIÓN: Detener sonido PRIMERO
            AudioManager.getControler().stopSound("viento");

            CharacterSelectionScreen.TipoClase tipo = gameScreen.getTipoClaseActual();
            game.setScreen(new GameScreen(game, tipo));
            dispose();
        });

        // 3. BOTÓN CAMBIAR PERSONAJE
        TextButtonStandard cambiarPjButton = new TextButtonStandard("Cambiar Personaje");
        cambiarPjButton.setClickListener(() -> {
            // CORRECCIÓN: Detener sonido PRIMERO
            AudioManager.getControler().stopSound("viento");

            game.setScreen(new CharacterSelectionScreen(game));
            gameScreen.dispose();
            dispose();
        });

        // 4. BOTÓN VOLVER AL MENU
        TextButtonStandard volverMenuButton = new TextButtonStandard("Volver al Menu");
        volverMenuButton.setClickListener(() -> {
            // CORRECCIÓN: Detener sonido PRIMERO
            AudioManager.getControler().stopSound("viento");

            game.setScreen(new MenuScreen(game));

            // Música del menú
            AudioManager.getControler().changeMusic("menuMusic", PathManager.MENU_MUSIC, true);
            AudioManager.getControler().setVolume(20);

            gameScreen.dispose();
            dispose();
        });

        Table table = new Table();
        // Fondo opcional si el skin lo tiene
        if (skin != null && skin.has("fondoMenu", com.badlogic.gdx.scenes.scene2d.utils.Drawable.class)) {
            table.setBackground(skin.getDrawable("fondoMenu"));
        }

        table.setFillParent(true);
        table.center();
        table.pad(50);

        table.add(reanudarButton).width(250).height(60).pad(10).row();
        table.add(resetButton).width(250).height(60).pad(10).row();
        table.add(cambiarPjButton).width(250).height(60).pad(10).row();
        table.add(volverMenuButton).width(250).height(60).pad(10).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            game.setScreen(gameScreen);
            gameScreen.getCamara().zoom = 0.6f;
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
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
