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
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Enemigo extends SerVivo {

    protected Jugador objetivo;
    protected Array<Bloque> bloques;

    private Rectangle hitbox;

    // CAMBIO: Ahora es 'protected' y no es 'final' para que cada enemigo defina su distancia
    protected float rangoAtaque = 50f;

    public Enemigo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio,
            Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class));

        // Ataque por defecto (Melee básico)
        this.habilidadPrincipal = new AtaqueAranazo(5.0f, 0.5f, 10, 40f, 30f, Jugador.class);
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        setMundo(mundo);
        mundo.agregarActor(this);
        this.objetivo = mundo.getJugador();
        this.bloques = mundo.getBloques();

        this.estrategia = new EstrategiaMoverAPunto(
            new Vector2(objetivo.getX(), objetivo.getY()), bloques
        );

        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName(), this, (Escenario) mundo));
        }
    }

    @Override
    public void delete() {
        super.delete();
        if (mundo != null) {
            mundo.getEnemigos().removeValue(this, true);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        comportamientoIA(delta);
        if(getTooltip() != null) getTooltip().actualizarPosicion();
    }

    private void comportamientoIA(float delta) {
        if (objetivo == null || mundo == null) return;

        float distancia = Vector2.dst(getCentroX(), getY(), objetivo.getCentroX(), objetivo.getY());

        // Usamos la variable rangoAtaque que modifica cada hijo
        if (distancia <= rangoAtaque) {
            estrategia = null;
            Vector2 posJugador = new Vector2(objetivo.getCentroX(), objetivo.getY() + objetivo.getAlto()/2);
            atacar(posJugador, this.mundo);
        } else {
            if (!estaAtacando()) {
                moverseHaciaObjetivo(delta);
            }
        }
    }

    private void moverseHaciaObjetivo(float delta) {
        if (estrategia == null) {
            estrategia = new EstrategiaMoverAPunto(new Vector2(objetivo.getX(), objetivo.getY()), bloques);
        }

        if (estrategia instanceof EstrategiaMoverAPunto) {
            ((EstrategiaMoverAPunto) estrategia).setDestino(objetivo.getX(), objetivo.getY());
        }

        estrategia.actualizar(this, delta);
        actualizarAnimacion(getX(), getY());
    }

    @Override
    public Rectangle getRectColision() {
        if (hitbox == null) hitbox = new Rectangle(getX(), getY(), getAncho(), getAlto() / 2);
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }

    public void setObjetivo(Jugador objetivo) { this.objetivo = objetivo; }
    public void setBloques(Array<Bloque> bloques) { this.bloques = bloques; }
}
