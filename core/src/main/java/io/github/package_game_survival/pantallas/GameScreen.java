    package io.github.package_game_survival.pantallas;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Input;
    import com.badlogic.gdx.Screen;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.Animation;
    import com.badlogic.gdx.graphics.g2d.TextureAtlas;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.math.Vector3;
    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.badlogic.gdx.scenes.scene2d.ui.Image;
    import com.badlogic.gdx.utils.Array;
    import com.badlogic.gdx.utils.ScreenUtils;
    import io.github.package_game_survival.algoritmos.ClickEffect;
    import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
    import io.github.package_game_survival.entidades.mapas.Escenario;
    import io.github.package_game_survival.entidades.seres.Enemigo;
    import io.github.package_game_survival.entidades.seres.Jugador;
    import io.github.package_game_survival.managers.Assets;
    import io.github.package_game_survival.managers.Audio.AudioManager;
    import io.github.package_game_survival.managers.PathManager;
    import io.github.package_game_survival.standards.LabelStandard;

    public class GameScreen implements Screen {

        private final MyGame game;
        private final FastMenuScreen fm;
        private final Stage stage;
        private Jugador jugador;
        private Escenario escenario;

        private final float ANCHO_MUNDO = 1800;
        private final float ALTO_MUNDO = 1200;

        private final Vector3 tempVec = new Vector3();
        private Animation<TextureRegion> clickAnimation;
        private LabelStandard labelFinJuego;
        private LabelStandard labelVolverMenu;
        private Image fondoOscuro;
        private boolean juegoTerminado = false;

        public GameScreen(MyGame game) {
            this.game = game;
            this.stage = new Stage(game.getViewport());
            Gdx.input.setInputProcessor(stage);

            this.jugador = new Jugador("Ian", 100, 100);
            this.escenario = new Escenario(stage, jugador, ANCHO_MUNDO, ALTO_MUNDO);
            this.fm = new FastMenuScreen(game, this);

            inicializarUI();
            cargarEfectosVisuales();
        }


        private void inicializarUI() {
            fondoOscuro = new Image(Assets.get(PathManager.GAME_BACKGROUND_TEXTURE, Texture.class));
            fondoOscuro.setColor(0, 0, 0, 0.6f);
            fondoOscuro.setSize(ANCHO_MUNDO, ALTO_MUNDO);
            fondoOscuro.setVisible(false);

            labelFinJuego = new LabelStandard("");
            labelFinJuego.setColor(Color.RED);
            labelFinJuego.setFontScale(3f);
            labelFinJuego.setVisible(false);


            labelVolverMenu = new LabelStandard("Precione [ESC] para volver a intentarlo");
            labelVolverMenu.setColor(Color.WHITE);
            labelVolverMenu.setFontScale(0.5f);
            labelVolverMenu.setVisible(false);

            stage.addActor(fondoOscuro);
            stage.addActor(labelFinJuego);
            stage.addActor(labelVolverMenu);
        }

        @Override
        public void render(float delta) {
            ScreenUtils.clear(0, 0, 0, 1);

            if(juegoTerminado){
                jugador.setEstrategia(null);
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.K)){
                jugador.alterarVida(-100);
            }

            if (!juegoTerminado) {
                actualizarCamara();
                gestionarClickMovimiento();
                stage.act(delta);
                stage.draw();

                if (jugador.getVida() <= 0) terminarJuego("PERDISTE");
                else if (jugador.getInventario().size() >= 3) terminarJuego("GANASTE");
            } else {
                stage.act(delta);
                stage.draw();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.setScreen(fm);
            }
        }

        private void actualizarCamara() {
            float camX = jugador.getX();
            float camY = jugador.getY();
            camX = Math.max(MyGame.ANCHO_PANTALLA / 2f, Math.min(camX, ANCHO_MUNDO - MyGame.ANCHO_PANTALLA / 2f));
            camY = Math.max(MyGame.ALTO_PANTALLA / 2f, Math.min(camY, ALTO_MUNDO - MyGame.ALTO_PANTALLA / 2f));
            stage.getCamera().position.set(camX, camY, 0);
            stage.getCamera().update();
        }

        private void centrarUI() {
            float camX = stage.getCamera().position.x;
            float camY = stage.getCamera().position.y;
            float viewportW = stage.getViewport().getWorldWidth();

            fondoOscuro.setPosition(camX - fondoOscuro.getWidth() / 2f, camY - fondoOscuro.getHeight() / 2f);

            labelFinJuego.setPosition(
                camX - (viewportW / 2f) + (viewportW - labelFinJuego.getPrefWidth()) / 2f,
                camY - labelFinJuego.getPrefHeight() / 2f + 30
            );

            labelVolverMenu.setPosition(
                camX - (viewportW / 2f) + (viewportW - labelVolverMenu.getPrefWidth()) / 2f,
                camY - labelFinJuego.getPrefHeight() - 30
            );
        }




        private void gestionarClickMovimiento() {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                tempVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                stage.getCamera().unproject(tempVec);
                Vector2 destino = new Vector2(tempVec.x, tempVec.y);
                jugador.setEstrategia(new EstrategiaMoverAPunto(destino));

                if (clickAnimation != null)
                    stage.addActor(new ClickEffect(clickAnimation, destino.x, destino.y));
            }
        }

        private void cargarEfectosVisuales() {
            try {
                TextureAtlas atlas = Assets.get(PathManager.CLICK_ANIMATION, TextureAtlas.class);
                Array<TextureRegion> frames = new Array<>();
                for (int i = 1; i <= 5; i++)
                    frames.add(atlas.findRegion("click" + i));
                clickAnimation = new Animation<>(0.08f, frames, Animation.PlayMode.NORMAL);
            } catch (Exception e) {
                Gdx.app.log("GameScreen", "Error al cargar animacion de clic", e);
            }
        }

        private void terminarJuego(String mensaje) {

            for (Enemigo e : escenario.getEnemigos()){
                e.remove();
            }

            juegoTerminado = true;
            fondoOscuro.setVisible(true);
            fondoOscuro.toFront();

            labelFinJuego.setText(mensaje);
            labelFinJuego.setVisible(true);
            labelFinJuego.toFront();


            labelVolverMenu.setVisible(true);
            labelVolverMenu.toFront();

            centrarUI();
        }

        @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
        @Override public void dispose() { stage.dispose(); AudioManager.getControler().stopMusic(); }
        @Override public void show() { Gdx.input.setInputProcessor(stage); }
        @Override public void pause() {}
        @Override public void resume() {}
        @Override public void hide() {}
    }
