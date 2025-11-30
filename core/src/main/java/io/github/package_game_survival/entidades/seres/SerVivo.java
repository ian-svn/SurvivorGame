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
import io.github.package_game_survival.managers.GestorAnimacion;

public abstract class SerVivo extends Entidad {

    private int vida;
    private int vidaMinima = 0;
    private int vidaMaxima = 100;
    private int velocidad;
    private int danio;

    // --- NUEVO: LÍMITES (CAPS) ---
    private final int LIMITE_DANIO = 60;
    private final int LIMITE_VELOCIDAD = 150;

    protected IEstrategiaMovimiento estrategia;
    protected IAtaque habilidadPrincipal;
    protected IMundoJuego mundo;

    private TextureAtlas atlas;
    private GestorAnimacion visual;

    private float tiempoHurt = 0f;
    private boolean isHurt = false;
    private final float DURACION_ROJO = 0.15f;

    private boolean muerto = false;

    private final Rectangle rectAux = new Rectangle();

    private enum Direccion {
        UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }
    private Direccion ultimaDireccion = Direccion.DOWN;

    public SerVivo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto);
        this.velocidad = velocidad;
        this.vida = vidaInicial;
        this.vidaMaxima = vidaMaxima;
        this.danio = danio;
        this.atlas = atlas;

        this.visual = new GestorAnimacion();
        if (atlas != null) {
            visual.inicializarAtlas(atlas);
        }
    }

    // --- MÉTODOS MODIFICADOS CON LÍMITES ---

    public void alterarDanio(int cantidad) {
        this.danio += cantidad;
        // Si baja de 0, queda en 0. Si supera 60, se queda en 60.
        if (this.danio < 0) this.danio = 0;
        if (this.danio > LIMITE_DANIO) this.danio = LIMITE_DANIO;
    }

    public void alterarVelocidad(int cantidad) {
        this.velocidad += cantidad;
        // Si baja de 0, queda en 0. Si supera 150, se queda en 150.
        if (this.velocidad < 0) this.velocidad = 0;
        if (this.velocidad > LIMITE_VELOCIDAD) this.velocidad = LIMITE_VELOCIDAD;
    }

    public void aumentarVidaMaxima(int cantidad) {
        this.vidaMaxima += cantidad;
        this.vida += cantidad;
    }
    // ----------------------------------------

    public void recibirEmpuje(float fuerzaX, float fuerzaY) {
        if (muerto) return;
        float oldX = getX();
        moveBy(fuerzaX, 0);
        if (colisionaConMundo()) setX(oldX);
        float oldY = getY();
        moveBy(0, fuerzaY);
        if (colisionaConMundo()) setY(oldY);
    }

    private boolean colisionaConMundo() {
        if (mundo == null) return false;
        float w = getWidth() * 0.6f;
        float h = getHeight() * 0.4f;
        float x = getX() + (getWidth() - w) / 2f;
        float y = getY();
        rectAux.set(x, y, w, h);
        for (Rectangle bloque : mundo.getRectangulosNoTransitables()) {
            if (rectAux.overlaps(bloque)) return true;
        }
        return false;
    }

    public void setMundo(IMundoJuego mundo) { this.mundo = mundo; }

    public void alterarVida(int cantidad) {
        if (muerto) return;
        this.vida += cantidad;
        if (cantidad < 0) {
            setColor(Color.RED);
            isHurt = true;
            tiempoHurt = DURACION_ROJO;
        }
        if (this.vida < vidaMinima) this.vida = vidaMinima;
        else if (this.vida > vidaMaxima) this.vida = vidaMaxima;
        if (vida <= vidaMinima) {
            muerto = true;
            delete();
        }
    }

    public boolean isMuerto() { return muerto; }

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

    public void atacar(Vector2 destino, IMundoJuego mundo) {
        if (!muerto && habilidadPrincipal != null) habilidadPrincipal.intentarAtacar(this, destino, mundo);
    }

    public boolean estaAtacando() { return habilidadPrincipal != null && habilidadPrincipal.estaCasteando(); }

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
            switch (ultimaDireccion) {
                case DOWN: visual.setEstado(EstadoAnimacion.IDLE_DOWN); break;
                default: visual.setEstado(EstadoAnimacion.IDLE_DOWN); break;
            }
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

    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public float getCentroX() { return getX() + getWidth() / 2f; }
    public int getVelocidad() { return velocidad; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }
    public int getDanio() { return danio; }
    public IEstrategiaMovimiento getEstrategia() { return estrategia; }
    public void setEstrategia(IEstrategiaMovimiento estrategia) { this.estrategia = estrategia; }
    public GestorAnimacion getVisual() { return visual; }
    public TextureAtlas getAtlas() { return atlas; }
    public IAtaque getHabilidadPrincipal() { return habilidadPrincipal; }
}
