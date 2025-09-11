package io.github.package_game_survival.pantallas;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.package_game_survival.managers.Assets;

public class LoadingScreen implements Screen {

        private final MyGame game;
        private SpriteBatch batch;
        private BitmapFont font;

        public LoadingScreen(MyGame game) {
            this.game = game;
        }

        @Override
        public void show() {
            batch = new SpriteBatch();
            font = new BitmapFont();
            Assets.load();
        }

        @Override
        public void render(float delta) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            font.draw(batch, "Cargando... " + (int)(Assets.getProgress() * 100) + "%", 100, 200);
            batch.end();

            if (Assets.update()) {
                game.setScreen(new MenuScreen(game));
            }
        }

        @Override
        public void resize(int width, int height) {}
        @Override
        public void pause() {}
        @Override
        public void resume() {}
        @Override
        public void hide() {}
        @Override
        public void dispose() {
            batch.dispose();
            font.dispose();
        }
    }
