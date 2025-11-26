package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class LoadingScreen implements Screen {

    private final MyGame game;
private SpriteBatch batch;
    private Stage stage;
    private Label label;
    private ShapeRenderer shapeRenderer;
    private ProgressBar progressBar;

    private float minTime = 2f;
    private float timer = 0f;

    public LoadingScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        inicializacionBarraCarga();

        stage = new Stage();
        shapeRenderer = new ShapeRenderer();

        stage.addActor(label);
        stage.addActor(progressBar);

        //Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        //Gdx.graphics.setFullscreenMode(displayMode);

        Assets.load();
    }

    @Override
    public void render(float delta) {
        timer += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float progress = Assets.getProgress();
        if((((int)(progress * 100))<100)){
            label.setText("Cargando... " + (int)(progress * 100) + "%");
        } else {
            label.setText("Abriendo juego...");
        }

        progressBar.setValue((int)(progress * 100));

        stage.act(delta);
        stage.draw();

        if (Assets.update() && timer >= minTime) {
            game.setScreen(new MenuScreen(game));
        }
    }

    private void inicializacionBarraCarga() {
        progressBar = new ProgressBar(0f, 100f, 1f, false,
            new Skin(Gdx.files.internal(PathManager.PROGRESS_BAR_SKIN), new TextureAtlas(PathManager.PROGRESS_BAR_ATLAS)));
        progressBar.setValue(0f);
        progressBar.setWidth(1000);
        progressBar.setHeight(100);
        progressBar.setPosition(Gdx.graphics.getWidth() / 2f - progressBar.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2 - progressBar.getHeight());
        Skin skinLabel = new Skin(Gdx.files.internal(PathManager.LABEL)); //no se puede hacer con assets porque justamente esto no se cargó aún
        label = new Label("Cargando... 0%", skinLabel);
        label.setPosition(Gdx.graphics.getWidth() / 2f - label.getWidth() / 2f,
            Gdx.graphics.getHeight() * 0.55f);
    }

    @Override
    public void resize(int i, int i1) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
