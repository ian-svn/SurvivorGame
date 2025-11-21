package io.github.package_game_survival.managers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.package_game_survival.interfaces.EstadoAnimacion;

public class GestorAnimacion {

    private final boolean isStatic;
    private TextureRegion staticRegion;

    // Usamos ObjectMap de LibGDX y el Enum como clave
    private ObjectMap<EstadoAnimacion, Animation<TextureRegion>> animations;
    private ObjectMap<EstadoAnimacion, TextureRegion> staticFrames;

    private EstadoAnimacion currentEstado = null;
    private float EstadoTime = 0f;
    private EstadoAnimacion defaultEstado = null;

    public GestorAnimacion(TextureRegion staticRegion) {
        this.isStatic = true;
        this.staticRegion = staticRegion;
    }

    public GestorAnimacion() {
        this.isStatic = false;
        this.animations = new ObjectMap<>();
        this.staticFrames = new ObjectMap<>();
    }

    public void agregarAnimacion(EstadoAnimacion estado, Animation<TextureRegion> animacion) {
        if (isStatic) return;
        animations.put(estado, animacion);
    }

    public void agregarFrameEstatico(EstadoAnimacion estado, TextureRegion frame) {
        if (isStatic) return;
        staticFrames.put(estado, frame);
    }

    public void setDefaultEstado(EstadoAnimacion estado) {
        if (isStatic) return;
        this.defaultEstado = estado;
        if (this.currentEstado == null) {
            setEstado(defaultEstado);
        }
    }

    public void setEstado(EstadoAnimacion estado) {
        if (isStatic || (estado == currentEstado)) {
            return;
        }

        if (estado == null) {
            this.currentEstado = defaultEstado;
        } else {
            this.currentEstado = estado;
        }

        if (currentEstado != null && animations.containsKey(currentEstado)) {
            EstadoTime = 0f;
        }
    }

    public void update(float delta) {
        if (isStatic) return;
        EstadoTime += delta;
    }

    public TextureRegion getFrame() {
        if (isStatic) {
            return staticRegion;
        }

        if (currentEstado == null) {
            return null;
        }

        Animation<TextureRegion> anim = animations.get(currentEstado);
        if (anim != null) {
            return anim.getKeyFrame(EstadoTime, true);
        }

        TextureRegion staticFrame = staticFrames.get(currentEstado);
        if (staticFrame != null) {
            return staticFrame;
        }

        return null;
    }

    public void inicializarAtlas(TextureAtlas atlas) {
        animations.clear();
        staticFrames.clear();

        // --- Animaciones ---
        Array<TextureRegion> rightFrames = new Array<>();
        rightFrames.add(atlas.findRegion("Der1"));
        rightFrames.add(atlas.findRegion("der2"));
        rightFrames.add(atlas.findRegion("der3"));
        agregarAnimacion(EstadoAnimacion.WALK_RIGHT, new Animation<>(0.2f, rightFrames, Animation.PlayMode.LOOP));

        Array<TextureRegion> leftFrames = new Array<>();
        for (TextureRegion r : rightFrames) {
            TextureRegion flip = new TextureRegion(r);
            flip.flip(true, false);
            leftFrames.add(flip);
        }
        agregarAnimacion(EstadoAnimacion.WALK_LEFT, new Animation<>(0.2f, leftFrames, Animation.PlayMode.LOOP));

        Array<TextureRegion> upFrames = new Array<>();
        upFrames.add(atlas.findRegion("arriba"));
        upFrames.add(atlas.findRegion("arriba1"));
        agregarAnimacion(EstadoAnimacion.WALK_UP, new Animation<>(0.2f, upFrames, Animation.PlayMode.LOOP));

        Array<TextureRegion> downFrames = new Array<>();
        downFrames.add(atlas.findRegion("abajo1"));
        downFrames.add(atlas.findRegion("abajo2"));
        agregarAnimacion(EstadoAnimacion.WALK_DOWN, new Animation<>(0.2f, downFrames, Animation.PlayMode.LOOP));

        Array<TextureRegion> diagRightFrames = new Array<>();
        diagRightFrames.add(atlas.findRegion("diagnalDer2"));
        diagRightFrames.add(atlas.findRegion("diagnalDer3"));
        agregarAnimacion(EstadoAnimacion.WALK_DIAG_UP_RIGHT, new Animation<>(0.2f, diagRightFrames, Animation.PlayMode.LOOP));

        Array<TextureRegion> diagLeftFrames = new Array<>();
        for (TextureRegion r : diagRightFrames) {
            TextureRegion flip = new TextureRegion(r);
            flip.flip(true, false);
            diagLeftFrames.add(flip);
        }
        agregarAnimacion(EstadoAnimacion.WALK_DIAG_UP_LEFT, new Animation<>(0.2f, diagLeftFrames, Animation.PlayMode.LOOP));

        // --- Frames Estáticos (Idle) ---
        agregarFrameEstatico(EstadoAnimacion.IDLE_DOWN, atlas.findRegion("abajoIdle"));
        agregarFrameEstatico(EstadoAnimacion.IDLE_RIGHT, atlas.findRegion("DerIdle"));
        agregarFrameEstatico(EstadoAnimacion.IDLE_DIAG_UP_RIGHT, atlas.findRegion("diagonalDerIdle"));
        agregarFrameEstatico(EstadoAnimacion.IDLE_UP, upFrames.get(0));

        TextureRegion idleLeft = new TextureRegion(atlas.findRegion("DerIdle"));
        idleLeft.flip(true, false);
        agregarFrameEstatico(EstadoAnimacion.IDLE_LEFT, idleLeft);

        TextureRegion idleDiagLeft = new TextureRegion(atlas.findRegion("diagonalDerIdle"));
        idleDiagLeft.flip(true, false);
        agregarFrameEstatico(EstadoAnimacion.IDLE_DIAG_UP_LEFT, idleDiagLeft);

        setDefaultEstado(EstadoAnimacion.IDLE_DOWN);
    }
}
