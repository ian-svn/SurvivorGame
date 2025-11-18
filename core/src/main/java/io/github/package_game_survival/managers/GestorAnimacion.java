package io.github.package_game_survival.managers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class GestorAnimacion {

    private final boolean isStatic;
    private TextureRegion staticRegion;

    private Map<String, Animation<TextureRegion>> animations;
    private Map<String, TextureRegion> staticFrames; // Para frames de "idle"

    private String currentState = null;
    private float stateTime = 0f;
    private String defaultState = null;

    public GestorAnimacion(TextureRegion staticRegion) {
        this.isStatic = true;
        this.staticRegion = staticRegion;
    }

    public GestorAnimacion() {
        this.isStatic = false;
        this.animations = new HashMap<>();
        this.staticFrames = new HashMap<>();
    }

    public void agregarAnimacion(String nombreEstado, Animation<TextureRegion> animacion) {
        if (isStatic) return;
        animations.put(nombreEstado, animacion);
    }

    public void agregarFrameEstatico(String nombreEstado, TextureRegion frame) {
        if (isStatic) return;
        staticFrames.put(nombreEstado, frame);
    }

    public void setDefaultState(String nombreEstado) {
        if (isStatic) return;
        this.defaultState = nombreEstado;
        if (this.currentState == null) {
            setState(defaultState);
        }
    }

    public void setState(String nombreEstado) {
        if (isStatic || (nombreEstado != null && nombreEstado.equals(currentState))) {
            return;
        }

        if (nombreEstado == null) {
            this.currentState = defaultState;
        } else {
            this.currentState = nombreEstado;
        }

        if (currentState != null && animations.containsKey(currentState)) {
            stateTime = 0f;
        }
    }

    public void update(float delta) {
        if (isStatic) return;
        stateTime += delta;
    }

    public TextureRegion getFrame() {
        if (isStatic) {
            return staticRegion;
        }

        if (currentState == null) {
            return null;
        }

        Animation<TextureRegion> anim = animations.get(currentState);
        if (anim != null) {
            return anim.getKeyFrame(stateTime, true);
        }

        TextureRegion staticFrame = staticFrames.get(currentState);
        if (staticFrame != null) {
            return staticFrame;
        }

        return null;
    }

    public Map<String, Animation<TextureRegion>> getAnimations() {
        return animations;
    }

    public Map<String, TextureRegion> getStaticFrames() {
        return staticFrames;
    }

    public void inicializarAtlas(TextureAtlas atlas) {
            getAnimations().clear();
            getStaticFrames().clear();

            Array<TextureRegion> rightFrames = new Array<>();
            rightFrames.add(atlas.findRegion("Der1"));
            rightFrames.add(atlas.findRegion("der2"));
            rightFrames.add(atlas.findRegion("der3"));
            agregarAnimacion("WALK_RIGHT", new Animation<>(0.2f, rightFrames, Animation.PlayMode.LOOP));

            Array<TextureRegion> leftFrames = new Array<>();
            for (TextureRegion r : rightFrames) {
                TextureRegion flip = new TextureRegion(r);
                flip.flip(true, false);
                leftFrames.add(flip);
            }
            agregarAnimacion("WALK_LEFT", new Animation<>(0.2f, leftFrames, Animation.PlayMode.LOOP));

            Array<TextureRegion> upFrames = new Array<>();
            upFrames.add(atlas.findRegion("arriba"));
            upFrames.add(atlas.findRegion("arriba1"));
            agregarAnimacion("WALK_UP", new Animation<>(0.2f, upFrames, Animation.PlayMode.LOOP));

            Array<TextureRegion> downFrames = new Array<>();
            downFrames.add(atlas.findRegion("abajo1"));
            downFrames.add(atlas.findRegion("abajo2"));
            agregarAnimacion("WALK_DOWN", new Animation<>(0.2f, downFrames, Animation.PlayMode.LOOP));

            Array<TextureRegion> diagRightFrames = new Array<>();
            diagRightFrames.add(atlas.findRegion("diagnalDer2"));
            diagRightFrames.add(atlas.findRegion("diagnalDer3"));
            agregarAnimacion("WALK_DIAG_UP_RIGHT", new Animation<>(0.2f, diagRightFrames, Animation.PlayMode.LOOP));

            Array<TextureRegion> diagLeftFrames = new Array<>();
            for (TextureRegion r : diagRightFrames) {
                TextureRegion flip = new TextureRegion(r);
                flip.flip(true, false);
                diagLeftFrames.add(flip);
            }
            agregarAnimacion("WALK_DIAG_UP_LEFT", new Animation<>(0.2f, diagLeftFrames, Animation.PlayMode.LOOP));

            agregarFrameEstatico("IDLE_DOWN", atlas.findRegion("abajoIdle"));
            agregarFrameEstatico("IDLE_RIGHT", atlas.findRegion("DerIdle"));
            agregarFrameEstatico("IDLE_DIAG_UP_RIGHT", atlas.findRegion("diagonalDerIdle"));

            agregarFrameEstatico("IDLE_UP", upFrames.get(0));

            TextureRegion idleLeft = new TextureRegion(atlas.findRegion("DerIdle"));
            idleLeft.flip(true, false);
            agregarFrameEstatico("IDLE_LEFT", idleLeft);

            TextureRegion idleDiagLeft = new TextureRegion(atlas.findRegion("diagonalDerIdle"));
            idleDiagLeft.flip(true, false);
            agregarFrameEstatico("IDLE_DIAG_UP_LEFT", idleDiagLeft);

            setDefaultState("IDLE_DOWN");
    }


}
