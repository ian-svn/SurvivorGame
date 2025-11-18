package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
// Importa tu VisualComponent
import io.github.package_game_survival.standards.ProgressBarStandard;
import io.github.package_game_survival.standards.TooltipStandard;

import java.util.*;
import java.util.List;

public class Jugador extends SerVivo {

    private final List<Objeto> inventario = new ArrayList<>();
    private final Rectangle hitbox;

    private int puntos = 0;
    private float tiempoUltimoDaño = 0f;
    private float tiempoTitileo = 0f;

    private ProgressBarStandard barraDeVida;
    private final Vector3 tempVec = new Vector3();

    private Escenario escenarioActual;

    private static final float COOLDOWN_DANO = 3.0f;
    private static final float VELOCIDAD_MOVIMIENTO = 120f;



    public Jugador(String nombre, float x, float y) {
        super(nombre, x, y, 32, 54, 100, 100, 80, 20,
                Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));

        this.hitbox = new Rectangle(x, y, 16, 16);
        inicializarBarraVida();
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
            tempVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(tempVec);
            Vector2 destino = new Vector2(tempVec.x, tempVec.y);
            estrategia = new EstrategiaMoverAPunto(destino);
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
            Vector2 dir = new Vector2(dx, dy).nor().scl(VELOCIDAD_MOVIMIENTO * delta);
            moveBy(dir.x, dir.y);
            estrategia = null;

            if (escenarioActual != null && colisionaConBloqueNoTransitable()) {
                setPosition(oldX, oldY);
            }
        } else if (estrategia != null) {
            estrategia.actualizar(this, delta);

            if (escenarioActual != null && colisionaConBloqueNoTransitable()) {
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
        if (escenarioActual == null) return false;

        float centerX = getX() + (getWidth() / 2) - (hitbox.width / 2);
        hitbox.setPosition(centerX, getY());

        for (Rectangle bloque : escenarioActual.getRectangulosBloquesNoTransitables()) {
            if (hitbox.overlaps(bloque)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        // ... (Tu código aquí no cambia)
        this.escenarioActual = escenario;
        escenario.agregar(this);
        if (escenario.getStageUI() != null) {
            escenario.getStageUI().addActor(barraDeVida);
        } else {
            escenario.agregar(barraDeVida);
        }
        instanciarTooltip(new TooltipStandard(getName(), this, escenario));
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
        // ... (Tu código aquí no cambia)
        barraDeVida = new ProgressBarStandard(0, 100, 130, 10, getVida(), false, "HP");
    }


    private void actualizarUI(Camera cam) {
        // ... (Tu código aquí no cambia)
        if (cam == null) return;
        float zoom = 0.6f;
        float offsetX = (cam.viewportWidth * zoom) / 2f - 140;
        float offsetY = (cam.viewportHeight * zoom) / 2f - 30;
        barraDeVida.setPosicion(cam.position.x + offsetX, cam.position.y + offsetY);
        barraDeVida.actualizar(getVida());
    }

    public void adquirirObjeto(Objeto objeto) {
        // ... (Tu código aquí no cambia)
        if (inventario.add(objeto)) {
            objeto.adquirir();
            AudioManager.getControler().loadSound("agarrarObjeto", PathManager.GRAB_OBJECT_SOUND);
            AudioManager.getControler().playSound("agarrarObjeto");
            this.puntos += objeto.getPuntos();
        }
    }

    private void revisarRecoleccionObjetos() {
        // ... (Tu código aquí no cambia)
        if (getStage() == null) return;
        Array<Actor> actores = getStage().getActors();
        for (int i = actores.size - 1; i >= 0; i--) {
            Actor actor = actores.get(i);
            // Usamos getRectColision() (nuestra hitbox)
            if (actor instanceof Objeto objeto && getRectColision().overlaps(objeto.getRectColision())) {
                adquirirObjeto(objeto);
            }
        }
    }

    private void revisarChoqueEnemigo(float delta) {
        // ... (Tu código aquí no cambia)
        tiempoUltimoDaño += delta;
        if (getStage() == null) return;

        Array<Actor> actores = getStage().getActors();
        for (Actor actor : actores) {
            if (actor instanceof Enemigo enemigo &&
                    enemigo.getRectColision().overlaps(getRectColision())) {
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

    public List<Objeto> getInventario() {
        return inventario;
    }
}
