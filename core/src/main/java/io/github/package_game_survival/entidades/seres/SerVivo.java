package io.github.package_game_survival.entidades.seres;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.interfaces.EstadoAnimacion;
import io.github.package_game_survival.interfaces.IAtaque;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.GestorAnimacion;
import io.github.package_game_survival.managers.PathManager;

public abstract class SerVivo extends Entidad {

    private int vida;
    private int vidaMinima = 0;
    protected int vidaMaxima;

    protected int topeVidaPermitida = 300;
    protected int limiteDanio = 150;

    private int velocidad;
    private int danio;

    protected IEstrategiaMovimiento estrategia;
    protected IAtaque habilidadPrincipal;
    protected IMundoJuego mundo;
    private TextureAtlas atlas;
    private GestorAnimacion visual;
    private float tiempoHurt = 0f;
    private boolean isHurt = false;
    private final float DURACION_ROJO = 0.15f;
    private boolean muerto = false;
    private enum Direccion { UP, DOWN, LEFT, RIGHT }
    private Direccion ultimaDireccion = Direccion.DOWN;

    public SerVivo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto);
        this.velocidad = velocidad;
        this.vida = vidaInicial;
        this.vidaMaxima = vidaMaxima;
        this.danio = danio;
        this.atlas = atlas;

        try {
            AudioManager.getControler().loadSound("hit", PathManager.HIT_SOUND);
        } catch (Exception e) {}

        this.visual = new GestorAnimacion();
        if (atlas != null) visual.inicializarAtlas(atlas);
    }

    public void recibirEmpuje(float fuerzaX, float fuerzaY) {
        if (muerto) return;
        float step = 1f;
        float remainingX = Math.abs(fuerzaX);
        float signX = Math.signum(fuerzaX);
        while (remainingX > 0) {
            float move = Math.min(remainingX, step);
            if (!intentarMover(move * signX, 0)) break;
            remainingX -= move;
        }
        float remainingY = Math.abs(fuerzaY);
        float signY = Math.signum(fuerzaY);
        while (remainingY > 0) {
            float move = Math.min(remainingY, step);
            if (!intentarMover(0, move * signY)) break;
            remainingY -= move;
        }
    }

    private boolean intentarMover(float dx, float dy) {
        float oldX = getX();
        float oldY = getY();
        moveBy(dx, dy);
        if (colisionaConMundo()) {
            setX(oldX);
            setY(oldY);
            return false;
        }
        return true;
    }

    private boolean colisionaConMundo() {
        if (mundo == null) return false;
        if (getX() < 0) return true;
        if (getY() < 0) return true;
        if (getX() + getWidth() > mundo.getAncho()) return true;
        if (getY() + getHeight() > mundo.getAlto()) return true;

        Rectangle miRect = getRectColision();
        for (Rectangle bloque : mundo.getRectangulosNoTransitables()) {
            if (miRect.overlaps(bloque)) return true;
        }
        return false;
    }

    public void alterarVelocidad(int cantidad) {
        this.velocidad += cantidad;
    }

    public void alterarDanio(int cantidad) {
        this.danio += cantidad;
        if (this.danio > limiteDanio) this.danio = limiteDanio;
        if (this.danio < 0) this.danio = 0;
    }

    public void aumentarVidaMaxima(int cantidad) {
        if (this.vidaMaxima + cantidad <= topeVidaPermitida) {
            this.vidaMaxima += cantidad;
            this.vida += cantidad;
        } else {
            int diferencia = topeVidaPermitida - this.vidaMaxima;
            if (diferencia > 0) {
                this.vidaMaxima = topeVidaPermitida;
                this.vida += diferencia;
            }
        }
    }

    public void alterarVida(int cantidad) {
        if (muerto) return;
        this.vida += cantidad;
        if (cantidad < 0) {
            setColor(Color.RED);
            isHurt = true;
            tiempoHurt = DURACION_ROJO;
            try { AudioManager.getControler().playSound("hit"); } catch(Exception e){}
        }
        this.vida = Math.max(vidaMinima, Math.min(vida, vidaMaxima));
        if (vida <= vidaMinima) {
            muerto = true;
            delete();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (muerto) return;

        if (habilidadPrincipal != null) habilidadPrincipal.update(delta);
        if (visual != null) visual.update(delta);

        if (isHurt) {
            tiempoHurt -= delta;
            if (tiempoHurt <= 0) {
                isHurt = false;
                setColor(Color.WHITE);
            }
        }
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        this.mundo = mundo;
        super.agregarAlMundo(mundo);
    }

    public void atacar(Vector2 destino, IMundoJuego mundo) {
        if (!muerto && habilidadPrincipal != null) {
            habilidadPrincipal.intentarAtacar(this, destino, mundo);
        }
    }

    public boolean estaAtacando() {
        return habilidadPrincipal != null && habilidadPrincipal.estaCasteando();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = this.visual.getFrame();
        if (currentFrame == null) return;
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    protected void actualizarAnimacion(float oldX, float oldY) {
        if (atlas == null) return;
        float dx = getX() - oldX;
        float dy = getY() - oldY;
        if (Math.abs(dx) < 0.01f && Math.abs(dy) < 0.01f) {
            visual.setEstado(EstadoAnimacion.IDLE_DOWN);
        } else {
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) { visual.setEstado(EstadoAnimacion.WALK_RIGHT); ultimaDireccion = Direccion.RIGHT; }
                else { visual.setEstado(EstadoAnimacion.WALK_LEFT); ultimaDireccion = Direccion.LEFT; }
            } else {
                if (dy > 0) { visual.setEstado(EstadoAnimacion.WALK_UP); ultimaDireccion = Direccion.UP; }
                else { visual.setEstado(EstadoAnimacion.WALK_DOWN); ultimaDireccion = Direccion.DOWN; }
            }
        }
    }

    public boolean isMuerto() { return muerto; }

    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public float getCentroX() { return getX() + getWidth() / 2f; }
    public int getVelocidad() { return velocidad; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }
    public int getDanio() { return danio; }
    public void setMundo(IMundoJuego mundo) { this.mundo = mundo; }
    public IEstrategiaMovimiento getEstrategia() { return estrategia; }
    public void setEstrategia(IEstrategiaMovimiento estrategia) { this.estrategia = estrategia; }
    public GestorAnimacion getVisual() { return visual; }
    public TextureAtlas getAtlas() { return atlas; }
    public IAtaque getHabilidadPrincipal() { return habilidadPrincipal; }
}
