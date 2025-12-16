package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.LabelStandard;
import io.github.package_game_survival.standards.TextButtonStandard;

public class CharacterSelectionScreen implements Screen {

    private final MyGame game;
    private final Stage stage;

    // Recursos gráficos
    private Texture texGuerrero;
    private Texture texCazador;
    private Skin skin; // Para el fondo

    public enum TipoClase { GUERRERO, CAZADOR }

    public CharacterSelectionScreen(MyGame game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(MyGame.ANCHO_PANTALLA, MyGame.ALTO_PANTALLA));
        Gdx.input.setInputProcessor(stage);
        crearUI();
    }

    private void crearUI() {
        Table tabla = new Table();
        tabla.setFillParent(true);

        this.skin = Assets.get(PathManager.BACKGROUND, Skin.class);
        tabla.setBackground(skin.getDrawable("fondoMenu"));

        LabelStandard titulo = new LabelStandard("SELECCIONA TU PERSONAJE");
        titulo.setFontScale(2f);

        texGuerrero = Assets.get(PathManager.GUERRERO_TEXTURE, Texture.class);
        Image imgGuerrero = new Image(texGuerrero);
        imgGuerrero.setScaling(Scaling.fit);

        texCazador = Assets.get(PathManager.CAZADOR_TEXTURE, Texture.class);
        Image imgCazador = new Image(texCazador);
        imgCazador.setScaling(Scaling.fit);

        // 3. Crear Botones
        TextButtonStandard btnGuerrero = new TextButtonStandard("GUERRERO");
        btnGuerrero.setClickListener(() -> {
            iniciarJuego(TipoClase.GUERRERO);
            AudioManager.getControler().loadMusic("menuMusic","sounds/MyCastleTown.mp3");
            AudioManager.getControler().playMusic("menuMusic",true);
            AudioManager.getControler().setVolume(20);
        });

        TextButtonStandard btnCazador = new TextButtonStandard("CAZADOR");
        btnCazador.setClickListener(() -> {
            iniciarJuego(TipoClase.CAZADOR);
            AudioManager.getControler().loadMusic("menuMusic","sounds/MyCastleTown.mp3");
            AudioManager.getControler().playMusic("menuMusic",true);
            AudioManager.getControler().setVolume(20);
        });

        // --- ARMADO DE LA TABLA ---

        // Fila 1: Título
        tabla.add(titulo).padBottom(30).colspan(2).row();

        // Fila 2: Imágenes (Arriba de los botones)
        tabla.add(imgGuerrero).size(128, 128).pad(10);
        tabla.add(imgCazador).size(128, 128).pad(10).row();

        // Fila 3: Botones (Debajo de las imágenes)
        tabla.add(btnGuerrero).pad(10).width(300).height(80);
        tabla.add(btnCazador).pad(10).width(300).height(80);

        stage.addActor(tabla);
    }

    private void iniciarJuego(TipoClase tipo) {
        game.setScreen(new GameScreen(game, tipo));
        dispose();
    }

    @Override
    public void render(float delta) {
        // Limpiamos pantalla (el color da igual porque el fondo de la tabla lo tapará)
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
