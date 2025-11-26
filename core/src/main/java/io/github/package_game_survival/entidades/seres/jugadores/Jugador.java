package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.objetos.ObjetoConsumible;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public class Jugador extends SerVivo {

    private final Array<Objeto> inventario = new Array<>();
    private final Rectangle hitbox;

    // --- NUEVO: Slot seleccionado del inventario (0 al 8) ---
    private int slotSeleccionado = 0;

    private float tiempoUltimoDañoContacto = 0f;
    private static final float COOLDOWN_DANO_CONTACTO = 1.0f;

    private final Vector3 tempVecInput = new Vector3();
    private final Vector2 tempDirMovimiento = new Vector2();
    private final Vector2 tempDestino = new Vector2();

    private static final float VELOCIDAD_MOVIMIENTO = 120f;

    public Jugador(String nombre, float x, float y) {
        super(nombre, x, y, 24, 40, 100, 100, 80, 20,
            Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));

        this.hitbox = new Rectangle(x, y, 1, 1);
        this.habilidadPrincipal = new AtaqueAranazo(0.5f, 0.1f, 25, 60f, 40f, SerVivo.class);
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
        if (getStage() == null) return;

        Camera cam = getStage().getCamera();

        gestionarCombate(cam);
        gestionarInventario(); // NUEVO: Control de teclas 1-9 y E
        moverse(delta, cam);

        revisarRecoleccionObjetos();
        revisarChoqueEnemigo(delta);

        if (getTooltip() != null) getTooltip().actualizarPosicion();
    }

    private void gestionarCombate(Camera cam) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            tempVecInput.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(tempVecInput);
            Vector2 destinoMouse = new Vector2(tempVecInput.x, tempVecInput.y);
            if (mundo != null) atacar(destinoMouse, mundo);
        }
    }

    // --- NUEVO MÉTODO: GESTIÓN TIPO MINECRAFT ---
    private void gestionarInventario() {
        // Selección de Slots (1 al 9)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) slotSeleccionado = 0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) slotSeleccionado = 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) slotSeleccionado = 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) slotSeleccionado = 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) slotSeleccionado = 4;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) slotSeleccionado = 5;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) slotSeleccionado = 6;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) slotSeleccionado = 7;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) slotSeleccionado = 8;

        // Usar objeto con 'E'
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            usarObjetoSeleccionado();
        }
    }

    private void usarObjetoSeleccionado() {
        // Verificamos si el slot es válido (si tenemos un objeto ahí)
        if (slotSeleccionado < inventario.size) {
            Objeto objeto = inventario.get(slotSeleccionado);

            if (objeto instanceof ObjetoConsumible) {
                // 1. Consumir efecto (curar)
                ((ObjetoConsumible) objeto).consumir(this);

                // 2. Eliminar del inventario
                inventario.removeIndex(slotSeleccionado);

                Gdx.app.log("INVENTARIO", "Consumido: " + objeto.getName());
            } else {
                Gdx.app.log("INVENTARIO", "Este objeto no se puede comer: " + objeto.getName());
            }
        }
    }

    private void moverse(float delta, Camera cam) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            tempVecInput.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(tempVecInput);
            tempDestino.set(tempVecInput.x, tempVecInput.y);
            if (mundo != null) estrategia = new EstrategiaMoverAPunto(tempDestino, mundo.getBloques());
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
            tempDirMovimiento.set(dx, dy).nor().scl(VELOCIDAD_MOVIMIENTO * delta);
            moveBy(tempDirMovimiento.x, tempDirMovimiento.y);
            if (mundo != null && colisionaConBloqueNoTransitable()) setPosition(oldX, oldY);
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

    private boolean colisionaConBloqueNoTransitable() {
        if (mundo == null) return false;
        float centerX = getX() + (getWidth() / 2) - (hitbox.width / 2);
        hitbox.setPosition(centerX, getY());
        for (Rectangle bloque : mundo.getRectangulosNoTransitables()) {
            if (hitbox.overlaps(bloque)) return true;
        }
        return false;
    }

    @Override public void setPosition(float x, float y) {
        super.setPosition(x, y);
        float centerX = getX() + (getWidth() / 2) - (hitbox.width / 2);
        hitbox.setPosition(centerX, getY());
    }
    @Override public void moveBy(float x, float y) {
        super.moveBy(x, y);
        hitbox.x += x; hitbox.y += y;
    }
    @Override public Rectangle getRectColision() { return hitbox; }

    // --- CORRECCIÓN: RECOLECCIÓN ESTILO MINECRAFT ---
    public void adquirirObjeto(Objeto objeto) {
        // Ahora TODO va al inventario primero.
        // Ya no consumimos automáticamente.
        inventario.add(objeto);

        // Lo quitamos del suelo (del Stage)
        objeto.adquirir();

        AudioManager.getControler().playSound("agarrarObjeto");
    }

    private void revisarRecoleccionObjetos() {
        if (mundo == null) return;
        Array<Objeto> objetosMundo = mundo.getObjetos();
        for (int i = objetosMundo.size - 1; i >= 0; i--) {
            Objeto objeto = objetosMundo.get(i);
            if (getRectColision().overlaps(objeto.getRectColision())) {
                adquirirObjeto(objeto);
                objetosMundo.removeIndex(i);
            }
        }
    }

    private void revisarChoqueEnemigo(float delta) {
        tiempoUltimoDañoContacto += delta;
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
