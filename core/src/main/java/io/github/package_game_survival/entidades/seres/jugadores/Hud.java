package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.managers.GestorTiempo;
import io.github.package_game_survival.pantallas.MyGame;
import io.github.package_game_survival.standards.LabelStandard;
import io.github.package_game_survival.standards.ProgressBarStandard;

public class Hud implements Disposable {

    public Stage stage;
    private Viewport viewport;

    private ProgressBarStandard barraDeVida;
    private Jugador jugador;
    private GestorTiempo gestorTiempo;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Table tablaStats;
    private LabelStandard lblStatsVida;
    private LabelStandard lblStatsDanio;
    private LabelStandard lblStatsVelocidad;

    private static final float ANCHO_UI = MyGame.ANCHO_PANTALLA;
    private static final float ALTO_UI = MyGame.ALTO_PANTALLA;

    // Referencia para la barra de vida (Arriba a la derecha)
    private final float BARRA_X = ANCHO_UI - 130 - 20;
    private final float BARRA_Y = 730;

    public Hud(SpriteBatch batch, Jugador jugador, GestorTiempo gestorTiempo) {
        this.jugador = jugador;
        this.gestorTiempo = gestorTiempo;
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer();

        viewport = new FitViewport(ANCHO_UI, ALTO_UI);
        stage = new Stage(viewport, batch);

        inicializarComponentes();
        inicializarTablaStats();
    }

    private void inicializarComponentes() {
        gestorTiempo.agregarAlStage(this.stage);

        barraDeVida = new ProgressBarStandard(0, 100, 130, 10, jugador.getVida(), false, "HP");
        barraDeVida.setPosicion(BARRA_X, BARRA_Y);

        stage.addActor(barraDeVida);
    }

    private void inicializarTablaStats() {
        tablaStats = new Table();

        LabelStandard titulo = new LabelStandard("ESTADISTICAS");
        titulo.setColor(Color.GOLD);
        titulo.setFontScale(0.8f);

        lblStatsVida = new LabelStandard("HP: 0/0");
        lblStatsDanio = new LabelStandard("DMG: 0");
        lblStatsVelocidad = new LabelStandard("SPD: 0");

        lblStatsVida.setFontScale(0.7f);
        lblStatsDanio.setFontScale(0.7f);
        lblStatsVelocidad.setFontScale(0.7f);

        // Alineamos el contenido de la tabla a la izquierda
        tablaStats.add(titulo).align(Align.left).padBottom(5).row();
        tablaStats.add(lblStatsVida).align(Align.left).padBottom(2).row();
        tablaStats.add(lblStatsDanio).align(Align.left).padBottom(2).row();
        tablaStats.add(lblStatsVelocidad).align(Align.left).padBottom(2).row();

        // 1. Calculamos el tamaño real de la tabla con el texto
        tablaStats.pack();

        // 2. CORRECCIÓN DE POSICIÓN:
        // Alineamos la tabla a la DERECHA de la pantalla (con un margen de 20px)
        // Esto asegura que el texto nunca se corte, sin importar cuán largo sea.
        float tablaX = ANCHO_UI - tablaStats.getWidth() - 20;
        float tablaY = BARRA_Y - 120; // Debajo de la barra

        tablaStats.setPosition(tablaX, tablaY);

        tablaStats.setVisible(false);
        stage.addActor(tablaStats);
    }

    public void update(float delta) {
        barraDeVida.setRange(0, jugador.getVidaMaxima());
        barraDeVida.actualizar(jugador.getVida());

        if (Gdx.input.isKeyPressed(Input.Keys.TAB)) {
            actualizarDatosStats();
            tablaStats.setVisible(true);
        } else {
            tablaStats.setVisible(false);
        }

        stage.act(delta);
    }

    private void actualizarDatosStats() {
        lblStatsVida.setText("HP: " + jugador.getVida() + "/" + jugador.getVidaMaxima());
        lblStatsDanio.setText("DMG: " + jugador.getDanio());
        lblStatsVelocidad.setText("SPD: " + jugador.getVelocidad());

        // Recalculamos posición por si el texto cambia de tamaño (ej: de 1 a 100 de daño)
        tablaStats.pack();
        float tablaX = ANCHO_UI - tablaStats.getWidth() - 20;
        tablaStats.setX(tablaX);
    }

    public void draw() {
        stage.draw();
        dibujarInventario();
    }

    private void dibujarInventario() {
        float slotSize = 40;
        float padding = 5;
        float startX = (ANCHO_UI / 2f) - ((9 * (slotSize + padding)) / 2f);
        float y = 20;

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        float selectorX = startX + (jugador.getSlotSeleccionado() * (slotSize + padding));
        shapeRenderer.rect(selectorX - 2, y - 2, slotSize + 4, slotSize + 4);
        shapeRenderer.end();

        batch.begin();
        for (int i = 0; i < 9; i++) {
            if (i < jugador.getInventario().size) {
                Objeto obj = jugador.getInventario().get(i);
                TextureRegion region = obj.getRegionVisual();
                if (region != null) {
                    Color c = obj.getColorVisual();
                    batch.setColor(c.r, c.g, c.b, 1);
                    batch.draw(region, startX + (i * (slotSize + padding)), y, slotSize, slotSize);
                }
            }
        }
        batch.setColor(Color.WHITE);
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
