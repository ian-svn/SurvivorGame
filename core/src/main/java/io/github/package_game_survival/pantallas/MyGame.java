package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MyGame extends Game {

    public static final float ANCHO_PANTALLA = 1280f;
    public static final float ALTO_PANTALLA = 720f;

    public SpriteBatch batch;
    private Viewport viewport;

    @Override
    public void create () {
        batch = new SpriteBatch();
        viewport = new FitViewport(ANCHO_PANTALLA, ALTO_PANTALLA);

        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }

    public Viewport getViewport(){
        return this.viewport;
    }
}
