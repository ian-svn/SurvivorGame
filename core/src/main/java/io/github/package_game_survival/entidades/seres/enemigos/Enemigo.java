package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Enemigo extends SerVivo {

    protected Jugador objetivo;
    protected Array<Bloque> bloques;

    // No es final para Lazy Initialization
    private Rectangle hitbox;

    public Enemigo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {

        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio,
            Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class));

        // Inicializamos en null, se llenan en agregarAlMundo
        this.objetivo = null;
        this.bloques = null;
        this.estrategia = null;

        setDebug(true);
    }

    // --- IMPLEMENTACIÓN DE LA INTERFAZ ---
    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        // 1. Agregarse visualmente
        mundo.agregarActor(this);

        // 2. Obtener dependencias del mundo
        this.objetivo = mundo.getJugador();
        this.bloques = mundo.getBloques();

        // 3. Inicializar IA con datos del mundo real
        // Usamos el vector de posición actual del jugador
        this.estrategia = new EstrategiaMoverAPunto(
            new Vector2(objetivo.getX(), objetivo.getY()),
            bloques // Pasamos los obstáculos
        );

        // 4. Tooltip
        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName(), this, (Escenario) mundo));
        }
    }
    // -------------------------------------

    @Override
    public void act(float delta) {
        super.act(delta);
        moverse(delta);
        if(getTooltip() != null) getTooltip().actualizarPosicion();
        getVisual().update(delta); // Actualizar animación
    }

    private void moverse(float delta) {
        if (objetivo == null || estrategia == null) return;

        float oldX = getX();
        float oldY = getY();

        // Optimización: Actualizar destino sin crear basura (new Vector2)
        if (estrategia instanceof EstrategiaMoverAPunto) {
            ((EstrategiaMoverAPunto) estrategia).setDestino(objetivo.getX(), objetivo.getY());
        }

        estrategia.actualizar(this, delta);
        actualizarAnimacion(oldX, oldY);
    }

    @Override
    public Rectangle getRectColision() {
        // Lazy Init: Si es null (llamada desde constructor padre), lo creamos.
        if (hitbox == null) {
            hitbox = new Rectangle(getX(), getY(), getAncho(), getAlto() / 2);
        }
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }

    // Setters por si necesitas inyección manual en tests
    public void setObjetivo(Jugador objetivo) { this.objetivo = objetivo; }
    public void setBloques(Array<Bloque> bloques) { this.bloques = bloques; }
}
