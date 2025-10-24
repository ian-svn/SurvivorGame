package io.github.package_game_survival.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
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

public class Jugador extends Personaje {

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

    private IEstrategiaMovimiento estrategiaActual;
    private final Vector3 tempVec = new Vector3();

    public Jugador(String nombre, int x, int y) {
        super(nombre, null, 100,100, x, y);
        hitbox = new Rectangle(x, y, 32, 16);

        inicializarAtlas();
        inicializarBarraVida();
        inicializarLabel();

        setWidth(50);
        setHeight(80);
        setVelocidad(150);

        TooltipStandard tooltipStandard = new TooltipStandard(this.getName());
        tooltipStandard.attach(this);
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
        revisarConsumibles();
        revisarChoqueEnemigo();
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

        revisarColisiones();
        actualizarAnimacion(oldX, oldY);
    }

//
//    private void moverConTeclado(float delta) {
//        float moveX = 0, moveY = 0;
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX = velocidad * delta;
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX = -velocidad * delta;
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY = velocidad * delta;
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY = -velocidad * delta;
//        moveBy(moveX, moveY);
//    }

    private void revisarColisiones() {
        float originalX = getX();
        float originalY = getY();
        boolean colision = false;

        for (Actor actor : getStage().getActors()) {
            if (actor instanceof Bloque bloque && !bloque.atravesable) {
                if (getBounds().overlaps(bloque.getBounds())) {
                    colision = true;
                    break;
                }
            }
        }

        if (colision) {
            setPosition(originalX, getY());
            boolean sigueColisionando = false;
            for (Actor actor : getStage().getActors()) {
                if (actor instanceof Bloque bloque && !bloque.atravesable && getBounds().overlaps(bloque.getBounds())) {
                    sigueColisionando = true;
                    break;
                }
            }
            if(sigueColisionando){
                setPosition(originalX, originalY);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(currentFrame != null) {
            batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
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
                if (enemigo.getBounds().overlaps(this.getBounds())) {
                    if (tiempoUltimoDaño >= COOLDOWN_DAÑO) {
                        this.alterarVida(-enemigo.getDano());
                        tiempoUltimoDaño = 0f;

                        AudioManager.getControler().loadSound("dañoRecibido", PathManager.HIT_SOUND);
                        AudioManager.getControler().playSound("dañoRecibido");
                    }
                }
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        hitbox.setPosition(getX(), getY());
        return hitbox;
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

    public void agregarAlStage(Stage stage){
        stage.addActor(this);
        stage.addActor(this.barraDeVida);
        stage.addActor(this.barraDeVida.getLabel());
        stage.addActor(this.labelPuntos);
    }



    private void inicializarLabel() {
        labelPuntos = new LabelStandard("Puntos: 0");
        labelPuntos.setPosition(20, MyGame.ALTO_PANTALLA - labelPuntos.getHeight());
    }

    private void inicializarBarraVida() {
        barraDeVida = new ProgressBarStandard(0,100, 200, 30, this.getVida(), false, "Hp");
        barraDeVida.setPosicion(MyGame.ANCHO_PANTALLA - barraDeVida.getWidth() - 20, MyGame.ALTO_PANTALLA - barraDeVida.getHeight() - 20);
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
                if (getBounds().overlaps(objeto.getBounds())) {
                    adquirirObjeto(objeto);
                }
            }
        }
    }

    private void revisarConsumibles() {
        Iterator<Objeto> it = inventario.iterator();
        while(it.hasNext()){
            Objeto o = it.next();
            if (o instanceof Consumible consumible) {
                consumible.consumir(this); // por ahora
                it.remove();
                break;
            }
        }
    }

    public void adquirirObjeto(Objeto objeto) {
        if (inventario.add(objeto)) {
            objeto.adquirir();
            AudioManager.getControler().loadSound("agarrarObjeto",PathManager.GRAB_OBJECT_SOUND);
            AudioManager.getControler().playSound("agarrarObjeto");
            this.puntos += objeto.getPuntos();
        }
    }

    public void setEstrategia(IEstrategiaMovimiento estrategiaActual) {
        this.estrategiaActual = estrategiaActual;
    }
}
