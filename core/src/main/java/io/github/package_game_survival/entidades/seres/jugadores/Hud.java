package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.interfaces.IAtaque;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.GestorTiempo;
import io.github.package_game_survival.managers.PathManager;
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

    private LabelStandard labelAviso;
    private Image iconoFuego;
    private Table tablaStats;

    // Labels de Stats
    private LabelStandard lblStatsVida;
    private LabelStandard lblStatsDanio;
    private LabelStandard lblStatsVelocidad;
    // NUEVOS LABELS
    private LabelStandard lblStatsRango;
    private LabelStandard lblStatsArea;

    private static final float ANCHO_UI = MyGame.ANCHO_PANTALLA;
    private static final float ALTO_UI = MyGame.ALTO_PANTALLA;
    private final float BARRA_X = ANCHO_UI - 130 - 20;
    private final float BARRA_Y = 730;

    public Hud(SpriteBatch batch, Jugador jugador, GestorTiempo gestorTiempo) {
        this.jugador = jugador;
        this.gestorTiempo = gestorTiempo;
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer();

        viewport = new FitViewport(ANCHO_UI, ALTO_UI);
        stage = new Stage(viewport, batch);

        if (gestorTiempo != null) {
            inicializarComponentes();
        }

        inicializarAlertas();
        inicializarTablaStats();
    }

    public void setGestorTiempo(GestorTiempo gt) {
        this.gestorTiempo = gt;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        gestorTiempo.agregarAlStage(this.stage);
        barraDeVida = new ProgressBarStandard(0, 100, 130, 10, jugador.getVida(), false, "HP");
        barraDeVida.setPosicion(BARRA_X, BARRA_Y);
        stage.addActor(barraDeVida);
    }

    private void inicializarAlertas() {
        labelAviso = new LabelStandard("", Color.RED, 1.0f);
        labelAviso.setCentrado();
        labelAviso.setPosition(ANCHO_UI / 2f, ALTO_UI - 40);
        labelAviso.setVisible(false);
        stage.addActor(labelAviso);

        Texture texFuego = Assets.get(PathManager.HOGUERA_TEXTURE, Texture.class);
        iconoFuego = new Image(texFuego);
        iconoFuego.setSize(32, 32);
        iconoFuego.setPosition(ANCHO_UI - 50, ALTO_UI - 150);
        iconoFuego.setVisible(false);
        stage.addActor(iconoFuego);
    }

    private void inicializarTablaStats() {
        tablaStats = new Table();

        LabelStandard titulo = new LabelStandard("ESTADISTICAS", Color.NAVY, 0.8f);

        lblStatsVida = new LabelStandard("HP: 0/0", Color.BLUE, 0.7f);
        lblStatsDanio = new LabelStandard("DMG: 0", Color.BLUE, 0.7f);
        lblStatsVelocidad = new LabelStandard("SPD: 0", Color.BLUE, 0.7f);

        // Inicializamos los nuevos labels
        lblStatsRango = new LabelStandard("RNG: 0", Color.BLUE, 0.7f);
        lblStatsArea = new LabelStandard("AREA: 0", Color.BLUE, 0.7f);

        tablaStats.add(titulo).align(Align.left).padBottom(5).row();
        tablaStats.add(lblStatsVida).align(Align.left).padBottom(2).row();
        tablaStats.add(lblStatsDanio).align(Align.left).padBottom(2).row();
        tablaStats.add(lblStatsVelocidad).align(Align.left).padBottom(2).row();
        // Agregamos al layout
        tablaStats.add(lblStatsRango).align(Align.left).padBottom(2).row();
        tablaStats.add(lblStatsArea).align(Align.left).padBottom(2).row();

        tablaStats.pack();

        float tablaX = ANCHO_UI - tablaStats.getWidth() - 20;
        float tablaY = BARRA_Y - 220; // Bajamos un poco más la tabla
        tablaStats.setPosition(tablaX, tablaY);
        tablaStats.setVisible(false);
        stage.addActor(tablaStats);
    }

    public void mostrarAvisoDesastre(String texto) {
        labelAviso.setText(texto);
        labelAviso.setVisible(true);
        labelAviso.clearActions();

        labelAviso.addAction(Actions.sequence(
            Actions.alpha(0),
            Actions.fadeIn(0.5f),
            Actions.delay(2.5f),
            Actions.fadeOut(0.5f),
            Actions.visible(false)
        ));
    }

    public void update(float delta) {
        if (barraDeVida != null) {
            barraDeVida.setRange(0, jugador.getVidaMaxima());
            barraDeVida.actualizar(jugador.getVida());
        }

        iconoFuego.setVisible(jugador.isSintiendoCalor());

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

        IAtaque ataque = jugador.getHabilidadPrincipal();
        if (ataque instanceof AtaqueAranazo) {
            AtaqueAranazo aranazo = (AtaqueAranazo) ataque;
            lblStatsRango.setText("RNG: " + (int)aranazo.getRango());
            lblStatsArea.setText("AREA: " + (int)aranazo.getAncho());
        } else {
            lblStatsRango.setText("RNG: -");
            lblStatsArea.setText("AREA: -");
        }

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

    // Método auxiliar para que otras clases agreguen actores al HUD (ej: Tooltips)
    public void agregarActor(com.badlogic.gdx.scenes.scene2d.Actor actor) {
        stage.addActor(actor);
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
