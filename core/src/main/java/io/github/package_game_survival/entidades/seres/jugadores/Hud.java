package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.managers.GestorTiempo;
import io.github.package_game_survival.pantallas.MyGame;
import io.github.package_game_survival.standards.ProgressBarStandard;

public class Hud implements Disposable {

    public Stage stage;
    private Viewport viewport;

    private ProgressBarStandard barraDeVida;
    private Jugador jugador;
    private GestorTiempo gestorTiempo;

    private static final float ANCHO_UI = MyGame.ANCHO_PANTALLA;
    private static final float ALTO_UI = MyGame.ALTO_PANTALLA;

    public Hud(SpriteBatch batch, Jugador jugador, GestorTiempo gestorTiempo) {
        this.jugador = jugador;
        this.gestorTiempo = gestorTiempo;

        viewport = new FitViewport(ANCHO_UI, ALTO_UI);
        stage = new Stage(viewport, batch);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        gestorTiempo.agregarAlStage(this.stage);

        float x = ANCHO_UI - 130 - 20;
        float y = ALTO_UI - 10 - 30;

        barraDeVida = new ProgressBarStandard(0, 100, 130, 10, jugador.getVida(), false, "HP");

        barraDeVida.setPosicion(x, 730);

        stage.addActor(barraDeVida);
    }

    public void update(float delta) {
        barraDeVida.actualizar(jugador.getVida());
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
