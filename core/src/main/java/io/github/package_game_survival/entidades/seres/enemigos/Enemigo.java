package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.Gdx;
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
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Enemigo extends SerVivo {

    protected Jugador objetivo;
    protected Array<Bloque> bloques;
    private Rectangle hitbox;
    protected float rangoAtaque = 50f;

    // Sistema de Drops
    private static class DropPosible {
        Class<? extends Objeto> claseObjeto;
        float probabilidad; // Ej: 0.1 para 10%

        public DropPosible(Class<? extends Objeto> claseObjeto, float probabilidad) {
            this.claseObjeto = claseObjeto;
            this.probabilidad = probabilidad;
        }
    }
    private Array<DropPosible> listaDrops;

    public Enemigo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio,
            Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class));

        this.habilidadPrincipal = new AtaqueAranazo(5.0f, 0.5f, 10, 40f, 30f, Jugador.class);
        this.listaDrops = new Array<>();
    }

    protected void agregarDrop(Class<? extends Objeto> clase, float probabilidad) {
        listaDrops.add(new DropPosible(clase, probabilidad));
    }

    @Override
    public void delete() {
        super.delete(); // Quitar visualmente

        if (mundo != null && listaDrops.notEmpty()) {
            // --- NUEVA LÓGICA DE DROP ÚNICO (RULETA) ---

            float dado = MathUtils.random(); // Genera numero entre 0.0 y 1.0
            float acumulador = 0f;

            for (DropPosible drop : listaDrops) {
                acumulador += drop.probabilidad;

                // Si el dado cae en este rango, este es el objeto ganador
                if (dado <= acumulador) {
                    try {
                        Objeto loot = drop.claseObjeto.getConstructor(float.class, float.class)
                            .newInstance(getX(), getY());
                        loot.agregarAlMundo(mundo);
                        if (mundo instanceof Escenario) {
                            ((Escenario) mundo).getObjetos().add(loot);
                        }
                        Gdx.app.log("DROP", getName() + " soltó: " + loot.getName());

                    } catch (Exception e) {
                        Gdx.app.error("DROP", "Error: " + e.getMessage());
                    }

                    // ¡IMPORTANTE! Rompemos el bucle para que no suelte nada más
                    break;
                }
            }

            mundo.getEnemigos().removeValue(this, true);
        }
    }

    // ... (Resto de la clase igual: agregarAlMundo, act, comportamientoIA, moverseHaciaObjetivo, getRectColision, setters) ...
    // (Copia los métodos que ya tenías funcionando de la respuesta anterior)

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        setMundo(mundo);
        mundo.agregarActor(this);
        this.objetivo = mundo.getJugador();
        this.bloques = mundo.getBloques();
        this.estrategia = new EstrategiaMoverAPunto(new Vector2(objetivo.getX(), objetivo.getY()), bloques);
        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName(), this, (Escenario) mundo));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isMuerto()) return;
        comportamientoIA(delta);
        if(getTooltip() != null) getTooltip().actualizarPosicion();
    }

    private void comportamientoIA(float delta) {
        if (objetivo == null || mundo == null) return;
        float distancia = Vector2.dst(getCentroX(), getY(), objetivo.getCentroX(), objetivo.getY());
        if (distancia <= rangoAtaque) {
            estrategia = null;
            Vector2 posJugador = new Vector2(objetivo.getCentroX(), objetivo.getY() + objetivo.getAlto()/2);
            atacar(posJugador, this.mundo);
        } else {
            if (!estaAtacando()) moverseHaciaObjetivo(delta);
        }
    }

    private void moverseHaciaObjetivo(float delta) {
        if (isMuerto()) return;
        float oldX = getX();
        float oldY = getY();
        if (estrategia == null) estrategia = new EstrategiaMoverAPunto(new Vector2(objetivo.getX(), objetivo.getY()), bloques);
        if (estrategia instanceof EstrategiaMoverAPunto) ((EstrategiaMoverAPunto) estrategia).setDestino(objetivo.getX(), objetivo.getY());
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
