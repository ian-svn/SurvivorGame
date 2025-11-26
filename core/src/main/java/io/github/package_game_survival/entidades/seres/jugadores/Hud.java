package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.managers.GestorTiempo;
import io.github.package_game_survival.pantallas.MyGame;
import io.github.package_game_survival.standards.ProgressBarStandard;

public class Hud implements Disposable {

    public Stage stage;
    private Viewport viewport;

    private ProgressBarStandard barraDeVida;
    private Jugador jugador;
    private GestorTiempo gestorTiempo;
    private SpriteBatch batch;

    // Necesitamos un ShapeRenderer para dibujar el cuadro de selección
    private ShapeRenderer shapeRenderer;

    private static final float ANCHO_UI = MyGame.ANCHO_PANTALLA;
    private static final float ALTO_UI = MyGame.ALTO_PANTALLA;

    public Hud(SpriteBatch batch, Jugador jugador, GestorTiempo gestorTiempo) {
        this.jugador = jugador;
        this.gestorTiempo = gestorTiempo;
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer(); // Inicializamos

        viewport = new FitViewport(ANCHO_UI, ALTO_UI);
        stage = new Stage(viewport, batch);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        gestorTiempo.agregarAlStage(this.stage);

        float x = ANCHO_UI - 130 - 20;
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
        dibujarInventario();
    }

    private void dibujarInventario() {
        float slotSize = 40;
        float padding = 5;
        // Dibujamos siempre 9 slots fijos para que parezca hotbar
        float totalWidth = 9 * (slotSize + padding);
        float startX = (ANCHO_UI / 2f) - (totalWidth / 2f);
        float y = 20;

        // 1. Dibujar el Selector (Cuadro Amarillo) DETRÁS de los items
        // Usamos ShapeRenderer para dibujar líneas
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);

        // Calculamos posición del selector según el slot del jugador
        float selectorX = startX + (jugador.getSlotSeleccionado() * (slotSize + padding));

        // Dibujamos un cuadro un poco más grande que el ítem
        shapeRenderer.rect(selectorX - 2, y - 2, slotSize + 4, slotSize + 4);
        shapeRenderer.end();

        // 2. Dibujar los Ítems
        batch.begin();
        for (int i = 0; i < 9; i++) { // Recorremos hasta 9 slots fijos

            // Solo dibujamos si el jugador tiene un ítem en ese slot
            if (i < jugador.getInventario().size) {
                Objeto obj = jugador.getInventario().get(i);
                TextureRegion region = obj.getRegionVisual();

                if (region != null) {
                    batch.setColor(1, 1, 1, 1);
                    batch.draw(region, startX + (i * (slotSize + padding)), y, slotSize, slotSize);
                }
            }
        }
        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
