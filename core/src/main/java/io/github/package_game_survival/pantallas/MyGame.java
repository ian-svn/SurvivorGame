package io.github.package_game_survival.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.BrilloManager;

public class MyGame extends Game {

    public static final float ANCHO_PANTALLA = 1280f;
    public static final float ALTO_PANTALLA = 720f;
    public static TooltipManager tm;

    public SpriteBatch batch;
    private Viewport viewport;

    @Override
    public void create () {
        BrilloManager.inicializar();
        AudioManager.getControler().loadMusic("menuMusic","sounds/MenuTheme.mp3");
        AudioManager.getControler().playMusic("menuMusic",true);
        AudioManager.getControler().setVolume(20);

        batch = new SpriteBatch();
        viewport = new FitViewport(ANCHO_PANTALLA, ALTO_PANTALLA);
        tm = new TooltipManager();
        this.setScreen(new LoadingScreen(this));

            Assets.load();
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose () {
        batch.dispose();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    public Viewport getViewport(){
        return this.viewport;
    }
}
