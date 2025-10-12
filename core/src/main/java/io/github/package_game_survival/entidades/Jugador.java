package io.github.package_game_survival.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.interfaces.Consumible;
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
    private float velocidad = 50;
    private Rectangle hitbox;

    private Animation<TextureRegion> walkDown, walkUpRight, walkRight, walkLeft, walkUp;
    private TextureRegion idleDown;

    private float stateTime = 0f;
    private TextureRegion currentFrame;
    private int puntos = 0;
    private ProgressBarStandard barraDeVida;
    private LabelStandard labelPuntos;

    public Jugador(String nombre, TooltipManager tm, int x, int y) {
        super(nombre, null, 50, x, y);

        barraDeVida = new ProgressBarStandard(0,100, 200, 30, this.getVida(), false, "Hp");
        barraDeVida.setPosicion(MyGame.ANCHO_PANTALLA - barraDeVida.getWidth() - 20, MyGame.ALTO_PANTALLA - barraDeVida.getHeight() - 20);

        labelPuntos = new LabelStandard("Puntos: 0");
        labelPuntos.setPosition(20, MyGame.ALTO_PANTALLA - labelPuntos.getHeight());

        hitbox = new Rectangle(x, y, getWidth(), getHeight());

        TooltipStandard tooltipStandard = new TooltipStandard(this.getName());
        tooltipStandard.attach(this);

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
        TextureRegion idleUp = atlas.findRegion("arribaIdle");
        TextureRegion idleRight = atlas.findRegion("DerIdle");
        TextureRegion idleUpRight = atlas.findRegion("diagonalDerIdle");
        TextureRegion idleLeft = new TextureRegion(idleRight);
        idleLeft.flip(true, false);

        if (idleDown == null) idleDown = rightFrames.first();
        if (walkDown.getKeyFrame(0) == null) walkDown = walkRight;

    }

    public int getPuntos(){
        int puntos = 0;
        for(Objeto objeto : this.inventario) {
            puntos += objeto.getPuntos();
        }
        return puntos;
    }

    public void actualizar(ArrayList<Bloque> bloques, ArrayList<Objeto> objetos, float delta){
        int dx = 0, dy = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy = -1;

        float moveX = dx * velocidad * delta;
        float moveY = dy * velocidad * delta;
        hitbox.x += moveX;

        for (Bloque bloque : bloques) {
            if (hitbox.overlaps(bloque.getBounds())) {
                hitbox.x -= moveX; break;
            }
        }
        setX(hitbox.x);
        hitbox.y += moveY;
        for (Bloque bloque : bloques) {
            if (hitbox.overlaps(bloque.getBounds())) {
                hitbox.y -= moveY; break;
            }
        } setY(hitbox.y);

        Iterator<Objeto> it = objetos.iterator();
        while(it.hasNext()) {
            Objeto objeto = it.next();
            if(objeto.getBounds().overlaps(this.getBounds())) {
                it.remove();
                if(objeto.getStage() != null) objeto.remove();
                adquirirObjeto(objeto);
            }
        }

        boolean seguir = true;
        int x = 0;
        while(x < inventario.size() && seguir){
            if(inventario.get(x) instanceof Consumible){
                ((Consumible) inventario.get(x)).consumir(this); //por ahora
                inventario.remove(x);
                seguir = false;
            }
            x++;
        }

        labelPuntos.setText("Puntos: " + this.puntos);
        barraDeVida.actualizar(getVida());
    }

    public void agregarAlStage(Stage stage){
        stage.addActor(this);
        stage.addActor(this.barraDeVida);
        stage.addActor(this.barraDeVida.getLabel());
        stage.addActor(this.labelPuntos);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        Camera cam = getStage().getCamera();
        barraDeVida.setPosicion(cam.position.x + cam.viewportWidth/2 - barraDeVida.getWidth() - 20,
            cam.position.y + cam.viewportHeight/2 - barraDeVida.getHeight() - 20);

        labelPuntos.setPosition(cam.position.x - cam.viewportWidth/2 + 20,
            cam.position.y + cam.viewportHeight/2 - labelPuntos.getHeight() - 20);

        int dx = 0, dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy = -1;

        float moveX = dx * velocidad * delta;
        float moveY = dy * velocidad * delta;

        hitbox.x += moveX;
        boolean colisionX = false;
        for (Actor actor : getStage().getActors()) {
            if (actor instanceof Bloque bloque && hitbox.overlaps(bloque.getBounds()) && !bloque.atravesable) {
                colisionX = true;
                break;
            }
        }
        if (colisionX) hitbox.x -= moveX;
        setX(hitbox.x);

        hitbox.y += moveY;
        boolean colisionY = false;
        for (Actor actor : getStage().getActors()) {
            if (actor instanceof Bloque bloque && hitbox.overlaps(bloque.getBounds()) && !bloque.atravesable) {
                colisionY = true;
                break;
            }
        }
        if (colisionY) hitbox.y -= moveY;
        setY(hitbox.y);

        if (dx == 0 && dy == 0) currentFrame = idleDown;
        else if (dx > 0 && dy == 0) currentFrame = walkRight.getKeyFrame(stateTime);
        else if (dx < 0 && dy == 0) currentFrame = walkLeft.getKeyFrame(stateTime);
        else if (dx == 0 && dy > 0) currentFrame = walkUp.getKeyFrame(stateTime);
        else if (dx == 0 && dy < 0) currentFrame = walkDown.getKeyFrame(stateTime);
        else if (dx > 0 && dy > 0) currentFrame = walkUpRight.getKeyFrame(stateTime);
        else if (dx < 0 && dy > 0) {
            TextureRegion frame = walkUpRight.getKeyFrame(stateTime);
            if (frame != null) {
                frame = new TextureRegion(frame);
                frame.flip(true, false);
            }
            currentFrame = frame;
        }
        else if (dx > 0 && dy < 0) {
            currentFrame = walkDown.getKeyFrame(stateTime);
        }
        else if (dx < 0 && dy < 0) {
            TextureRegion frame = walkDown.getKeyFrame(stateTime);
            if (frame != null) {
                frame = new TextureRegion(frame);
                frame.flip(true, false);
            }
            currentFrame = frame;
        }
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        return hitbox;
    }

    public void adquirirObjeto(Objeto objeto) {
        if (inventario.add(objeto)) {
            objeto.remove();
            objeto.adquirir();
            AudioManager.getControler().loadSound("agarrarObjeto",PathManager.GRAB_OBJECT);
            AudioManager.getControler().playSound("agarrarObjeto");
            this.puntos+=5;
        }
    }

    public List<Objeto> getInventario() {
        return inventario;
    }

    public ProgressBarStandard getBarraDeVida() {
        return barraDeVida;
    }
}
