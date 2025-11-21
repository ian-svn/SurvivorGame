package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.ProgressBarStandard;
import io.github.package_game_survival.standards.TooltipStandard;

public class Jugador extends SerVivo {

    private final Array<Objeto> inventario = new Array<>();
    private final Rectangle hitbox;

    private int puntos = 0;
    private float tiempoUltimoDaño = 0f;
    private float tiempoTitileo = 0f;

    private ProgressBarStandard barraDeVida;

    private final Vector3 tempVecInput = new Vector3();
    private final Vector2 tempDirMovimiento = new Vector2();
    private final Vector2 tempDestino = new Vector2();

    private IMundoJuego mundo;

    private static final float COOLDOWN_DANO = 3.0f;
    private static final float VELOCIDAD_MOVIMIENTO = 120f;

    public Jugador(String nombre, float x, float y) {
        super(nombre, x, y, 24, 40, 100, 100, 80, 20,
            Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));

        this.hitbox = new Rectangle(x, y, 1, 1);
        inicializarBarraVida();
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        this.mundo = mundo;

        mundo.agregarActor(this);

        if (barraDeVida != null) {
            mundo.agregarActorUI(barraDeVida);
        }

        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName(), this, (Escenario) mundo));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Camera cam = getStage().getCamera();
        actualizarUI(cam);
        moverse(delta, cam);
        revisarRecoleccionObjetos();
        revisarChoqueEnemigo(delta);

        if (getTooltip() != null) getTooltip().actualizarPosicion();

        actualizarTitileo(delta);
        if (getStage() == null) return;

        getVisual().update(delta);
    }

    private void moverse(float delta, Camera cam) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            tempVecInput.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(tempVecInput);
            tempDestino.set(tempVecInput.x, tempVecInput.y);

            if (mundo != null) {
                // Usamos getBloques() de la interfaz IMundoJuego
                estrategia = new EstrategiaMoverAPunto(tempDestino, mundo.getBloques());
            }
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
            tempDirMovimiento.set(dx, dy).nor().scl(VELOCIDAD_MOVIMIENTO * delta);
            moveBy(tempDirMovimiento.x, tempDirMovimiento.y);
            estrategia = null;

            if (mundo != null && colisionaConBloqueNoTransitable()) {
                setPosition(oldX, oldY);
            }
        } else if (estrategia != null) {
            estrategia.actualizar(this, delta);

            if (mundo != null && colisionaConBloqueNoTransitable()) {
                setPosition(oldX, oldY);
                estrategia = null;
            }

            if (estrategia != null && estrategia.haTerminado(this)) {
                estrategia = null;
            }
        }

        actualizarAnimacion(oldX, oldY);
    }

    private boolean colisionaConBloqueNoTransitable() {
        if (mundo == null) return false;

        float centerX = getX() + (getWidth() / 2) - (hitbox.width / 2);
        hitbox.setPosition(centerX, getY());

        // Usamos getRectangulosNoTransitables() de la interfaz
        for (Rectangle bloque : mundo.getRectangulosNoTransitables()) {
            if (hitbox.overlaps(bloque)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        float centerX = getX() + (getWidth() / 2) - (hitbox.width / 2);
        hitbox.setPosition(centerX, getY());
    }

    @Override
    public void moveBy(float x, float y) {
        super.moveBy(x, y);
        hitbox.x += x;
        hitbox.y += y;
    }

    @Override
    public Rectangle getRectColision() {
        return hitbox;
    }

    private void inicializarBarraVida() {
        barraDeVida = new ProgressBarStandard(0, 100, 130, 10, getVida(), false, "HP");
    }

    private void actualizarUI(Camera cam) {
        if (cam == null) return;
        float zoom = 0.6f;
        float offsetX = (cam.viewportWidth * zoom) / 2f - 140;
        float offsetY = (cam.viewportHeight * zoom) / 2f - 30;
        barraDeVida.setPosicion(cam.position.x + offsetX, cam.position.y + offsetY);
        barraDeVida.actualizar(getVida());
    }

    public void adquirirObjeto(Objeto objeto) {
        inventario.add(objeto);
        objeto.adquirir(); // Esto llama a remove() visualmente en Objeto
        AudioManager.getControler().loadSound("agarrarObjeto", PathManager.GRAB_OBJECT_SOUND);
        AudioManager.getControler().playSound("agarrarObjeto");
        this.puntos += objeto.getPuntos();
    }

    private void revisarRecoleccionObjetos() {
        if (mundo == null) return;

        Array<Objeto> objetosMundo = mundo.getObjetos();

        // Iteramos hacia atrás y eliminamos de la lista lógica del mundo
        for (int i = objetosMundo.size - 1; i >= 0; i--) {
            Objeto objeto = objetosMundo.get(i);
            if (getRectColision().overlaps(objeto.getRectColision())) {
                adquirirObjeto(objeto);
                objetosMundo.removeIndex(i); // IMPORTANTE: Evita recolección infinita
            }
        }
    }

    private void revisarChoqueEnemigo(float delta) {
        tiempoUltimoDaño += delta;
        if (mundo == null) return;

        for (Enemigo enemigo : mundo.getEnemigos()) {
            if (enemigo.getRectColision().overlaps(getRectColision())) {
                if (tiempoUltimoDaño >= COOLDOWN_DANO) {
                    alterarVida(-enemigo.getDanio());
                    tiempoUltimoDaño = 0f;
                    setEstaTitilando(true);
                    tiempoTitileo = 0f;
                    AudioManager.getControler().playSound("dañoRecibido");
                }
            }
        }
    }

    private void actualizarTitileo(float delta) {
        if (!isEstaTitilando()) return;
        tiempoTitileo += delta;
        if ((int) (tiempoTitileo * 10) % 2 == 0) {
            setVisible(true);
            setColor(Color.RED);
        } else {
            setVisible(false);
        }
        if (tiempoTitileo >= COOLDOWN_DANO) {
            setEstaTitilando(false);
            setVisible(true);
            setColor(Color.WHITE);
        }
    }

    public Array<Objeto> getInventario() {
        return inventario;
    }
}
