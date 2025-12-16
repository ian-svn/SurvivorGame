package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Enemigo extends SerVivo {

    protected Jugador objetivo;
    protected Array<Bloque> bloques;
    private Rectangle hitbox;

    // Tooltip propio para poder actualizarlo
    private TooltipStandard tooltipEnemigo;

    protected float rangoAtaque = 50f;

    private boolean quemandose = false;
    private float timerQuemar = 0f;
    private final float INTERVALO_DAÑO = 0.5f;
    private final int DAÑO_POR_TICK = 5;

    private boolean dropsHabilitados = true;

    private static class DropPosible {
        Class<? extends Objeto> claseObjeto;
        float probabilidad;
        public DropPosible(Class<? extends Objeto> claseObjeto, float probabilidad) {
            this.claseObjeto = claseObjeto;
            this.probabilidad = probabilidad;
        }
    }
    private Array<DropPosible> listaDrops;

    public Enemigo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio, atlas);
        this.listaDrops = new Array<>();
        setSize(ancho, alto);
    }

    public void escalarDificultad(int numeroNoche) {
        if (numeroNoche > 0) {
            this.setVelocidad(this.getVelocidad() + (numeroNoche * 10));
            this.aumentarVidaMaxima(numeroNoche * 20);
        }
    }

    public void setQuemandose(boolean quemandose) {
        this.quemandose = quemandose;
    }

    protected void agregarDrop(Class<? extends Objeto> clase, float probabilidad) {
        listaDrops.add(new DropPosible(clase, probabilidad));
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

        // Creamos nuestro tooltip personalizado
        this.tooltipEnemigo = new TooltipStandard(generarInfoTooltip(), this);
    }

    private String generarInfoTooltip() {
        return getName() + "\nHP: " + getVida() + "/" + getVidaMaxima();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isMuerto()) return;

        // ACTUALIZAR TOOLTIP CON VIDA EN TIEMPO REAL
        if (tooltipEnemigo != null) {
            tooltipEnemigo.setText(generarInfoTooltip());
        }

        if (quemandose) {
            timerQuemar += delta;
            if (timerQuemar >= INTERVALO_DAÑO) {
                timerQuemar = 0f;
                if (getVida() - DAÑO_POR_TICK <= 0) dropsHabilitados = false;
                alterarVida(-DAÑO_POR_TICK);
            }
        }

        comportamientoIA(delta);
    }

    @Override
    public void delete() {
        super.delete();
        if (dropsHabilitados && mundo != null) {
            if (listaDrops.notEmpty() && MathUtils.randomBoolean(0.5f)) {
                float dado = MathUtils.random();
                float acumulador = 0f;
                for (DropPosible drop : listaDrops) {
                    acumulador += drop.probabilidad;
                    if (dado <= acumulador) {
                        try {
                            Objeto loot = drop.claseObjeto.getConstructor(float.class, float.class)
                                .newInstance(getX(), getY());
                            loot.agregarAlMundo(mundo);
                            if (mundo instanceof Escenario) {
                                ((Escenario) mundo).getObjetos().add(loot);
                            }
                        } catch (Exception e) {}
                        break;
                    }
                }
            }
        }
        if (mundo != null) mundo.getEnemigos().removeValue(this, true);
    }

    private void comportamientoIA(float delta) {
        if (getVida() <= 0) return;
        if (objetivo == null || mundo == null) return;

        float distancia = Vector2.dst(getCentroX(), getY(), objetivo.getCentroX(), objetivo.getY());

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
        if (isMuerto()) return;
        float oldX = getX();
        float oldY = getY();

        if (estrategia == null) {
            estrategia = new EstrategiaMoverAPunto(new Vector2(objetivo.getX(), objetivo.getY()), bloques);
        }
        if (estrategia instanceof EstrategiaMoverAPunto) {
            ((EstrategiaMoverAPunto) estrategia).setDestino(new Vector2(objetivo.getX(), objetivo.getY()));
        }

        estrategia.actualizar(this, delta);
        actualizarAnimacion(oldX, oldY);
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
