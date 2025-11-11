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
import io.github.package_game_survival.standards.ProgressBarStandard;
import io.github.package_game_survival.standards.TooltipStandard;

import java.util.*;

public class Jugador extends SerVivo {

    private final List<Objeto> inventario = new ArrayList<>();
    private final Rectangle hitbox;

    private Animation<TextureRegion> walkDown, walkRight, walkLeft, walkUp;
    private TextureRegion idleDown;
    private TextureRegion currentFrame;

    private float stateTime = 0f;
    private int puntos = 0;
    private boolean estaTitilando = false;
    private boolean visible = true;
    private float tiempoUltimoDaño = 0f;
    private float tiempoTitileo = 0f;

    private ProgressBarStandard barraDeVida;
    private final Vector3 tempVec = new Vector3();

    private Escenario escenarioActual;

    private static final float COOLDOWN_DANO = 3.0f;
    private static final float VELOCIDAD_MOVIMIENTO = 120f;

    // Dirección de la última animación (para idle)
    private enum Direccion {ARRIBA, ABAJO, IZQUIERDA, DERECHA}
    private Direccion ultimaDireccion = Direccion.ABAJO;

    public Jugador(String nombre, float x, float y) {
        super(nombre, x, y, 30, 50, 100, 100, 80, 20,
            Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));
        this.hitbox = new Rectangle(x, y, 16, 16);
        inicializarAtlas();
        inicializarBarraVida();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        stateTime += delta;

        Camera cam = getStage().getCamera();
        actualizarUI(cam);
        moverse(delta, cam);
        revisarRecoleccionObjetos();
        revisarChoqueEnemigo(delta);

        if (getTooltip() != null) getTooltip().actualizarPosicion();

        actualizarTitileo(delta);
        if (getStage() == null) return;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (currentFrame == null) return;

        Color color = getColor();
        if (estaTitilando && !visible) {
            return;
        }

        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    /**
     * Movimiento con clic derecho y teclas WASD.
     * Si el jugador se mueve en diagonal, se prioriza el sprite vertical (arriba/abajo).
     */
    private void moverse(float delta, Camera cam) {
        // Movimiento con clic
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            tempVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(tempVec);
            Vector2 destino = new Vector2(tempVec.x, tempVec.y);
            estrategia = new EstrategiaMoverAPunto(destino);
        }

        float oldX = getX();
        float oldY = getY();

        // Movimiento con WASD
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
        for (Rectangle bloque : escenarioActual.getRectangulosBloquesNoTransitables()) {
            if (hitbox.overlaps(bloque)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        this.escenarioActual = escenario;

        // El jugador va al stage del mundo (shader)
        escenario.agregar(this);

        // 🟢 La barra de vida va al stage de la UI (sin shader)
        if (escenario.getStageUI() != null) {
            escenario.getStageUI().addActor(barraDeVida);
        } else {
            // Fallback por si alguien usa el Jugador sin UI separada
            escenario.agregar(barraDeVida);
        }

        instanciarTooltip(new TooltipStandard(getName(), this, escenario));
    }


    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        hitbox.setPosition(x, y);
    }

    @Override
    public void moveBy(float x, float y) {
        super.moveBy(x, y);
        hitbox.setPosition(getX(), getY());
    }

    @Override
    public Rectangle getRectColision() {
        return hitbox;
    }

    private void inicializarBarraVida() {
        barraDeVida = new ProgressBarStandard(0, 100, 130, 10, getVida(), false, "HP");
    }

    private void inicializarAtlas() {
        TextureAtlas atlas = Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class);

        Array<TextureRegion> rightFrames = new Array<>();
        rightFrames.add(atlas.findRegion("Der1"));
        rightFrames.add(atlas.findRegion("der2"));
        walkRight = new Animation<>(0.2f, rightFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> leftFrames = new Array<>();
        for (TextureRegion r : rightFrames) {
            TextureRegion flip = new TextureRegion(r);
            flip.flip(true, false);
            leftFrames.add(flip);
        }
        walkLeft = new Animation<>(0.2f, leftFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> upFrames = new Array<>();
        upFrames.add(atlas.findRegion("arriba"));
        upFrames.add(atlas.findRegion("arriba1"));
        walkUp = new Animation<>(0.2f, upFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> downFrames = new Array<>();
        downFrames.add(atlas.findRegion("abajo1png"));
        downFrames.add(atlas.findRegion("abajo2"));
        walkDown = new Animation<>(0.2f, downFrames, Animation.PlayMode.LOOP);

        idleDown = atlas.findRegion("abajoIdle");
        currentFrame = idleDown;
    }

    /**
     * Prioriza animaciones verticales en movimiento diagonal.
     */
    private void actualizarAnimacion(float oldX, float oldY) {
        float dx = getX() - oldX;
        float dy = getY() - oldY;

        boolean seMueve = Math.abs(dx) > 0.1f || Math.abs(dy) > 0.1f;

        if (!seMueve) {
            // Cuando se detiene, se queda mirando a la última dirección
            switch (ultimaDireccion) {
                case ARRIBA -> currentFrame = walkUp.getKeyFrame(0);
                case ABAJO -> currentFrame = walkDown.getKeyFrame(0);
                case IZQUIERDA -> currentFrame = walkLeft.getKeyFrame(0);
                case DERECHA -> currentFrame = walkRight.getKeyFrame(0);
            }
            return;
        }

        // Prioridad vertical
        if (Math.abs(dy) >= Math.abs(dx)) {
            if (dy > 0) {
                currentFrame = walkUp.getKeyFrame(stateTime);
                ultimaDireccion = Direccion.ARRIBA;
            } else {
                currentFrame = walkDown.getKeyFrame(stateTime);
                ultimaDireccion = Direccion.ABAJO;
            }
        } else {
            if (dx > 0) {
                currentFrame = walkRight.getKeyFrame(stateTime);
                ultimaDireccion = Direccion.DERECHA;
            } else {
                currentFrame = walkLeft.getKeyFrame(stateTime);
                ultimaDireccion = Direccion.IZQUIERDA;
            }
        }
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
        if (inventario.add(objeto)) {
            objeto.adquirir();
            AudioManager.getControler().loadSound("agarrarObjeto", PathManager.GRAB_OBJECT_SOUND);
            AudioManager.getControler().playSound("agarrarObjeto");
            this.puntos += objeto.getPuntos();
        }
    }

    private void revisarRecoleccionObjetos() {
        if (getStage() == null) return;
        Array<Actor> actores = getStage().getActors();
        for (int i = actores.size - 1; i >= 0; i--) {
            Actor actor = actores.get(i);
            if (actor instanceof Objeto objeto && getRectColision().overlaps(objeto.getBounds())) {
                adquirirObjeto(objeto);
            }
        }
    }

    private void revisarChoqueEnemigo(float delta) {
        tiempoUltimoDaño += delta;
        if (getStage() == null) return;

        Array<Actor> actores = getStage().getActors();
        for (Actor actor : actores) {
            if (actor instanceof Enemigo enemigo &&
                enemigo.getRectColision().overlaps(getRectColision())) {
                if (tiempoUltimoDaño >= COOLDOWN_DANO) {
                    alterarVida(-enemigo.getDanio());
                    tiempoUltimoDaño = 0f;
                    estaTitilando = true;
                    tiempoTitileo = 0f;
                    AudioManager.getControler().playSound("dañoRecibido");
                }
            }
        }
    }

    private void actualizarTitileo(float delta) {
        if (!estaTitilando) return;

        tiempoTitileo += delta;

        if ((int) (tiempoTitileo * 10) % 2 == 0) {
            visible = true;
            setColor(Color.RED);
        } else {
            visible = false;
        }

        if (tiempoTitileo >= COOLDOWN_DANO) {
            estaTitilando = false;
            visible = true;
            setColor(Color.WHITE);
        }
    }

    public List<Objeto> getInventario() {
        return inventario;
    }
}
