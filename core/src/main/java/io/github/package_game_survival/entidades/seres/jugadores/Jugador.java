package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Cama;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.objetos.ObjetoConsumible;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.GestorTiempo;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public class Jugador extends SerVivo {

    private final Array<Objeto> inventario = new Array<>();
    private final Rectangle hitbox;

    private int slotSeleccionado = 0;
    private float tiempoUltimoDañoContacto = 0f;
    private static final float COOLDOWN_DANO_CONTACTO = 1.0f;

    private boolean invulnerable = false;
    private float tiempoInvulnerable = 0f;
    private final float DURACION_INVULNERABILIDAD = 1.5f;

    private boolean sintiendoCalor = false;

    private final Vector3 tempVecInput = new Vector3();
    private final Vector2 tempDirMovimiento = new Vector2();
    private final Vector2 tempDestino = new Vector2();
    private final Rectangle rectDrop = new Rectangle();

    private float timerClickDerecho = 0f;
    private final float INTERVALO_CLICK_DERECHO = 0.2f;

    public Jugador(String nombre, float x, float y) {
        super(nombre, x, y, 24, 40, 120, 120, 120, 20,
            Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));

        this.hitbox = new Rectangle(x, y, 1, 1);
        this.habilidadPrincipal = new AtaqueAranazo(0.5f, 0.1f, 25, 60f,
            40f, SerVivo.class, Color.BLUE);
    }

    // --- Nuevo método para la mejora de Lana ---
    public void mejorarRangoAtaque(float rangoExtra, float areaExtra) {
        if (habilidadPrincipal instanceof AtaqueAranazo) {
            AtaqueAranazo ataque = (AtaqueAranazo) habilidadPrincipal;
            ataque.aumentarRango(rangoExtra);
            ataque.aumentarArea(areaExtra);
        }
    }

    @Override
    public void alterarVida(int cantidad) {
        if (cantidad < 0 && invulnerable) return;
        super.alterarVida(cantidad);
        if (cantidad < 0) activarInvulnerabilidad();
    }

    private void activarInvulnerabilidad() {
        this.invulnerable = true;
        this.tiempoInvulnerable = DURACION_INVULNERABILIDAD;
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        setMundo(mundo);
        mundo.agregarActor(this);
        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName(), this, (Escenario) mundo));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (invulnerable) {
            tiempoInvulnerable -= delta;
            if (tiempoInvulnerable <= 0) {
                invulnerable = false;
                tiempoInvulnerable = 0;
                getColor().a = 1f;
            }
        }

        if (getStage() == null) return;

        Camera cam = getStage().getCamera();
        gestionarCombate(cam);

        boolean interactuoConMundo = revisarRecoleccionObjetos();
        if (!interactuoConMundo) {
            gestionarInventario(cam);
        }

        moverse(delta, cam);
        revisarChoqueEnemigo(delta);

        if (getTooltip() != null) getTooltip().actualizarPosicion();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (invulnerable) {
            boolean visible = (tiempoInvulnerable % 0.15f) > 0.07f;
            if (!visible) return;
        }
        super.draw(batch, parentAlpha);
    }

    public void setSintiendoCalor(boolean calor) { this.sintiendoCalor = calor; }
    public boolean isSintiendoCalor() { return sintiendoCalor; }

    private void gestionarCombate(Camera cam) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            tempVecInput.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(tempVecInput);
            Vector2 destinoMouse = new Vector2(tempVecInput.x, tempVecInput.y);
            if (mundo != null) atacar(destinoMouse, mundo);
        }
    }

    private void gestionarInventario(Camera cam) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) slotSeleccionado = 0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) slotSeleccionado = 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) slotSeleccionado = 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) slotSeleccionado = 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) slotSeleccionado = 4;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) slotSeleccionado = 5;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) slotSeleccionado = 6;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) slotSeleccionado = 7;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) slotSeleccionado = 8;
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) usarObjetoSeleccionado();
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) tirarObjetoSeleccionado(cam);
    }

    private void tirarObjetoSeleccionado(Camera cam) {
        if (slotSeleccionado < inventario.size) {
            Objeto objeto = inventario.get(slotSeleccionado);
            tempVecInput.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(tempVecInput);
            Vector2 mousePos = new Vector2(tempVecInput.x, tempVecInput.y);
            Vector2 miPos = new Vector2(getX() + getWidth()/2, getY() + getHeight()/2);
            Vector2 direccion = mousePos.sub(miPos).nor();
            Vector2 destinoDrop = new Vector2(miPos).mulAdd(direccion, 50f);

            if (esPosicionValidaParaDrop(destinoDrop.x, destinoDrop.y, objeto.getWidth(), objeto.getHeight())) {
                objeto.setPosition(destinoDrop.x, destinoDrop.y);
            } else {
                objeto.setPosition(getX(), getY());
            }

            objeto.reiniciarTiempoVida();
            objeto.agregarAlMundo(mundo);
            mundo.getObjetos().add(objeto);
            inventario.removeIndex(slotSeleccionado);
        }
    }

    private boolean esPosicionValidaParaDrop(float x, float y, float w, float h) {
        rectDrop.set(x, y, w, h);
        for (Rectangle bloque : mundo.getRectangulosNoTransitables()) {
            if (rectDrop.overlaps(bloque)) return false;
        }
        return true;
    }

    private void usarObjetoSeleccionado() {
        if (slotSeleccionado < inventario.size) {
            Objeto objeto = inventario.get(slotSeleccionado);
            if (objeto instanceof ObjetoConsumible) {
                ((ObjetoConsumible) objeto).consumir(this);
                inventario.removeIndex(slotSeleccionado);
            }
        }
    }

    private void moverse(float delta, Camera cam) {
        // --- 1. SOLUCIÓN AL BUG DE QUEDAR TRABADO ---
        // Verificamos si estamos atrapados en una pared ANTES de intentar movernos.
        verificarDesatasco();

        // --- Lógica de movimiento normal ---
        boolean clickDerechoPresionado = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
        boolean clickDerechoJustoAhora = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
        if (clickDerechoPresionado) {
            timerClickDerecho += delta;
            if (clickDerechoJustoAhora || timerClickDerecho >= INTERVALO_CLICK_DERECHO) {
                timerClickDerecho = 0f;
                tempVecInput.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                cam.unproject(tempVecInput);
                tempDestino.set(tempVecInput.x, tempVecInput.y);
                if (mundo != null) {
                    if (estrategia instanceof EstrategiaMoverAPunto) ((EstrategiaMoverAPunto) estrategia).setDestino(tempDestino);
                    else estrategia = new EstrategiaMoverAPunto(tempDestino, mundo.getBloques());
                }
            }
        } else {
            timerClickDerecho = 0f;
        }

        float oldX = getX();
        float oldY = getY();
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        if (dx != 0 || dy != 0) {
            estrategia = null;
            tempDirMovimiento.set(dx, dy).nor().scl(getVelocidad() * delta);

            // Movimiento en X
            float nextX = getX() + tempDirMovimiento.x;
            setPosition(nextX, getY());
            if (mundo != null && colisionaConBloqueNoTransitable()) {
                setX(oldX);
            }

            // Movimiento en Y
            float nextY = getY() + tempDirMovimiento.y;
            setPosition(getX(), nextY);
            if (mundo != null && colisionaConBloqueNoTransitable()) {
                setY(oldY);
            }

        } else if (estrategia != null) {
            estrategia.actualizar(this, delta);
            if (mundo != null && colisionaConBloqueNoTransitable()) {
                setPosition(oldX, oldY);
                estrategia = null;
            }
            if (estrategia != null && estrategia.haTerminado(this)) estrategia = null;
        }
        actualizarAnimacion(oldX, oldY);
    }

    // --- NUEVO MÉTODO DE DESATASCO AUTOMÁTICO ---
    private void verificarDesatasco() {
        if (mundo != null && colisionaConBloqueNoTransitable()) {
            // ¡Estamos atrapados! Intentamos empujar hacia las 4 direcciones cercanas
            // para ver si alguna nos libera.
            float oldX = getX();
            float oldY = getY();

            // Probamos saltos pequeños (4px) en cruz
            float[] offsetsX = {4, -4, 0, 0, 8, -8, 0, 0};
            float[] offsetsY = {0, 0, 4, -4, 0, 0, 8, -8};

            for (int i = 0; i < offsetsX.length; i++) {
                setPosition(oldX + offsetsX[i], oldY + offsetsY[i]);
                if (!colisionaConBloqueNoTransitable()) {
                    // Encontramos un lugar libre, nos quedamos aquí
                    // Gdx.app.log("JUGADOR", "Desatascado automáticamente!");
                    return;
                }
            }

            // Si nada funcionó, volvemos a donde estábamos (ya estábamos trabados igual)
            setPosition(oldX, oldY);
        }
    }

    private boolean colisionaConBloqueNoTransitable() {
        if (mundo == null) return false;
        if (getX() < 0) return true;
        if (getY() < 0) return true;
        if (getX() + getWidth() > mundo.getAncho()) return true;
        if (getY() + getHeight() > mundo.getAlto()) return true;

        Rectangle r = getRectColision();
        for (Rectangle bloque : mundo.getRectangulosNoTransitables()) {
            if (r.overlaps(bloque)) return true;
        }
        return false;
    }

    @Override public void setPosition(float x, float y) { super.setPosition(x, y); }
    @Override public void moveBy(float x, float y) { super.moveBy(x, y); }

    @Override public Rectangle getRectColision() {
        float w = getWidth() * 0.4f;
        float h = getHeight() * 0.2f;
        float x = getX() + (getWidth() - w) / 2f;
        float y = getY();
        hitbox.set(x, y, w, h);
        return hitbox;
    }

    public void adquirirObjeto(Objeto objeto) {
        inventario.add(objeto);
        objeto.adquirir();
        AudioManager.getControler().playSound("agarrarObjeto");
    }

    private boolean revisarRecoleccionObjetos() {
        if (mundo == null) return false;
        Array<Objeto> objetosMundo = mundo.getObjetos();
        boolean accionRealizada = false;
        for (int i = objetosMundo.size - 1; i >= 0; i--) {
            Objeto objeto = objetosMundo.get(i);
            if (getRectColision().overlaps(objeto.getRectColision())) {
                if (objeto instanceof Cama) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        GestorTiempo tiempo = mundo.getGestorTiempo();
                        if (tiempo.esDeNoche()) { } else { tiempo.hacerDeNoche(); accionRealizada = true; }
                    }
                    continue;
                }
                adquirirObjeto(objeto);
                objetosMundo.removeIndex(i);
            }
        }
        return accionRealizada;
    }

    private void revisarChoqueEnemigo(float delta) {
        tiempoUltimoDañoContacto += delta;
        if (invulnerable) return;

        if (mundo == null) return;
        for (Enemigo enemigo : mundo.getEnemigos()) {
            if (enemigo.getVida() <= 0) continue;
            if (enemigo.getRectColision().overlaps(getRectColision())) {
                if (tiempoUltimoDañoContacto >= COOLDOWN_DANO_CONTACTO) {
                    alterarVida(-enemigo.getDanio());
                    tiempoUltimoDañoContacto = 0f;
                    AudioManager.getControler().playSound("dañoRecibido");
                }
            }
        }
    }

    public Array<Objeto> getInventario() { return inventario; }
    public int getSlotSeleccionado() { return slotSeleccionado; }
}
