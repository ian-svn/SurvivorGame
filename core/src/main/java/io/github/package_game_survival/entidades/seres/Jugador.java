package io.github.package_game_survival.entidades.seres;

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
import io.github.package_game_survival.interfaces.Consumible;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.pantallas.MyGame;
import io.github.package_game_survival.standards.LabelStandard;
import io.github.package_game_survival.standards.ProgressBarStandard;
import io.github.package_game_survival.standards.TooltipStandard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Jugador extends SerVivo {

    private List<Objeto> inventario = new ArrayList<>();
    private Rectangle hitbox;

    private Animation<TextureRegion> walkDown, walkUpRight, walkRight, walkLeft, walkUp;
    private TextureRegion idleDown;

    private float stateTime = 0f;
    private TextureRegion currentFrame;
    private int puntos = 0;
    private ProgressBarStandard barraDeVida;
    private LabelStandard labelPuntos;
    private float tiempoUltimoDaño = 0f;
    private final float COOLDOWN_DAÑO = 3.0f;
    private boolean estaTitilando = false;
    private float tiempoTitileo = 0f;
    private final float INTERVALO_TITILEO = 0.1f;
    private float tiempoDesdeUltimoTitileo = 0f;
    private boolean visible = true;

    private IEstrategiaMovimiento estrategiaActual;
    private final Vector3 tempVec = new Vector3();

    public Jugador(String nombre, int x, int y) {
        super(nombre, x, y, 50, 80, 100, 100, 150, 20, Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));
        hitbox = new Rectangle(x, y, 32, 16);
        inicializarAtlas();
        inicializarBarraVida();
        inicializarLabel();
        TooltipStandard tooltipStandard = new TooltipStandard(this.getName(), this);
    }

    public List<Objeto> getInventario() {
        return inventario;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        Camera cam = getStage().getCamera();
        actualizarUI(cam);
        moverse(delta, cam);
        revisarRecoleccionObjetos();
        revisarChoqueEnemigo();

        if (estaTitilando) {
            tiempoTitileo += delta;
            tiempoDesdeUltimoTitileo += delta;
            if (tiempoDesdeUltimoTitileo >= INTERVALO_TITILEO) {
                visible = !visible;
                tiempoDesdeUltimoTitileo = 0f;
            }
            if (tiempoTitileo >= COOLDOWN_DAÑO) {
                estaTitilando = false;
                visible = true;
                tiempoTitileo = 0f;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            consumirPrimerConsumible();
        }
    }

    private void moverse(float delta, Camera cam) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            tempVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(tempVec);
            Vector2 destino = new Vector2(tempVec.x, tempVec.y);
            estrategiaActual = new EstrategiaMoverAPunto(destino);
        }

        float oldX = getX();
        float oldY = getY();

        if (estrategiaActual != null) {
            estrategiaActual.actualizar(this, delta);
            if (estrategiaActual.haTerminado(this)) {
                estrategiaActual = null;
            }
        }

        actualizarAnimacion(oldX, oldY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!visible) return;
        if (currentFrame != null) {
            if (estaTitilando) {
                batch.setColor(Color.RED);
                batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
                batch.setColor(Color.WHITE);
            } else {
                batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
            }
        }
    }

    public Vector2 getPosition(Vector2 out) {
        return out.set(getX(), getY());
    }

    private void revisarChoqueEnemigo() {
        tiempoUltimoDaño += Gdx.graphics.getDeltaTime();
        Array<Actor> actores = getParent().getChildren();
        for (Actor actor : actores) {
            if (actor instanceof Enemigo enemigo) {
                if (enemigo.getRectColision().overlaps(this.getRectColision())) {
                    if (tiempoUltimoDaño >= COOLDOWN_DAÑO) {
                        this.alterarVida(-enemigo.getDanio());
                        tiempoUltimoDaño = 0f;
                        estaTitilando = true;
                        visible = true;
                        tiempoTitileo = 0f;
                        tiempoDesdeUltimoTitileo = 0f;
                        AudioManager.getControler().loadSound("dañoRecibido", PathManager.HIT_SOUND);
                        AudioManager.getControler().playSound("dañoRecibido");
                    }
                }
            }
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (hitbox != null) hitbox.setPosition(x, y);
    }

    @Override
    public void moveBy(float x, float y) {
        super.moveBy(x, y);
        if (hitbox != null) hitbox.setPosition(getX(), getY());
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        escenario.getStage().addActor(this);
        escenario.getStage().addActor(this.barraDeVida);
        escenario.getStage().addActor(this.barraDeVida.getLabel());
        escenario.getStage().addActor(this.labelPuntos);
    }

    private void inicializarLabel() {
        labelPuntos = new LabelStandard("Puntos: 0");
        labelPuntos.setPosition(20, MyGame.ALTO_PANTALLA - labelPuntos.getHeight());
    }

    private void inicializarBarraVida() {
        barraDeVida = new ProgressBarStandard(0, 100, 200, 30, this.getVida(), false, "Hp");
        barraDeVida.setPosicion(MyGame.ANCHO_PANTALLA - barraDeVida.getWidth() - 20,
            MyGame.ALTO_PANTALLA - barraDeVida.getHeight() - 20);
    }

    private void inicializarAtlas() {
        TextureAtlas atlas = Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class);
        Array<TextureRegion> rightFrames = new Array<>();
        rightFrames.add(atlas.findRegion("Der1"));
        rightFrames.add(atlas.findRegion("der2"));
        rightFrames.add(atlas.findRegion("der3"));
        walkRight = new Animation<>(0.2f, rightFrames, Animation.PlayMode.LOOP);
        Array<TextureRegion> leftFrames = new Array<>();
        for (TextureRegion r : rightFrames) {
            TextureRegion copy = new TextureRegion(r);
            copy.flip(true, false);
            leftFrames.add(copy);
        }
        walkLeft = new Animation<>(0.2f, leftFrames, Animation.PlayMode.LOOP);
        Array<TextureRegion> upFrames = new Array<>();
        upFrames.add(atlas.findRegion("arriba"));
        upFrames.add(atlas.findRegion("arriba1"));
        walkUp = new Animation<>(0.2f, upFrames, Animation.PlayMode.LOOP);
        Array<TextureRegion> upRightFrames = new Array<>();
        upRightFrames.add(atlas.findRegion("diagnalDer2"));
        upRightFrames.add(atlas.findRegion("diagnalDer3"));
        walkUpRight = new Animation<>(0.2f, upRightFrames, Animation.PlayMode.LOOP);
        Array<TextureRegion> downFrames = new Array<>();
        downFrames.add(atlas.findRegion("abajo1png"));
        downFrames.add(atlas.findRegion("abajo2"));
        walkDown = new Animation<>(0.2f, downFrames, Animation.PlayMode.LOOP);
        idleDown = atlas.findRegion("abajoIdle");
        currentFrame = idleDown;
    }

    private void actualizarAnimacion(float oldX, float oldY) {
        float dx = getX() - oldX;
        float dy = getY() - oldY;
        if (Math.abs(dx) < 0.1f && Math.abs(dy) < 0.1f) {
            currentFrame = idleDown;
            stateTime = 0;
        } else if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) currentFrame = walkRight.getKeyFrame(stateTime);
            else currentFrame = walkLeft.getKeyFrame(stateTime);
        } else {
            if (dy > 0) currentFrame = walkUp.getKeyFrame(stateTime);
            else currentFrame = walkDown.getKeyFrame(stateTime);
        }
    }

    private void actualizarUI(Camera cam) {
        barraDeVida.setPosicion(cam.position.x + cam.viewportWidth / 2 - barraDeVida.getWidth() - 20,
            cam.position.y + cam.viewportHeight / 2 - barraDeVida.getHeight() - 20);
        barraDeVida.actualizar(getVida());
        labelPuntos.setPosition(cam.position.x - cam.viewportWidth / 2 + 20,
            cam.position.y + cam.viewportHeight / 2 - labelPuntos.getHeight() - 20);
        labelPuntos.setText("Puntos: " + puntos);
    }

    private void revisarRecoleccionObjetos() {
        if (getParent() == null) return;
        Array<Actor> actores = getParent().getChildren();
        for (int i = 0; i < actores.size; i++) {
            Actor actor = actores.get(i);
            if (actor instanceof Objeto objeto) {
                if (getRectColision().overlaps(objeto.getBounds())) {
                    adquirirObjeto(objeto);
                }
            }
        }
    }

    public void adquirirObjeto(Objeto objeto) {
        if (inventario.add(objeto)) {
            objeto.adquirir();
            AudioManager.getControler().loadSound("agarrarObjeto", PathManager.GRAB_OBJECT_SOUND);
            AudioManager.getControler().playSound("agarrarObjeto");
            this.puntos += objeto.getPuntos();
        }
    }

    public void consumirPrimerConsumible() {
        Iterator<Objeto> it = inventario.iterator();
        while (it.hasNext()) {
            Objeto o = it.next();
            if (o instanceof Consumible consumible) {
                consumible.consumir(this);
                it.remove();
                break;
            }
        }
    }

    public void setEstrategia(IEstrategiaMovimiento estrategiaActual) {
        this.estrategiaActual = estrategiaActual;
    }
}
