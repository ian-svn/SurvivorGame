package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAleatorio;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Animal extends SerVivo {

    protected Jugador objetivo;
    // Asumo que EstrategiaMoverAleatorio implementa IEstrategiaMovimiento
    // Pero si lo tienes definido como tipo concreto, lo dejamos así:
    protected EstrategiaMoverAleatorio estrategia;

    private Rectangle hitbox;

    // Constructor simplificado (quitamos objetivo del constructor, se asigna al agregar al mundo)
    public Animal(String nombre, float x, float y, float ancho, float alto,
                  int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {

        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio, atlas);
        this.estrategia = null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        moverse(delta);
    }

    private void moverse(float delta) {
        if (estrategia != null) {
            estrategia.actualizar(this, delta);
        }
    }

    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
        // Aquí usa tu lógica visual, o la de SerVivo.
        // Si quieres forzar la textura de Vaca como tenías:
        batch.draw(
            Assets.get(PathManager.VACA_TEXTURE, Texture.class),
            getX(), getY(), getWidth(), getHeight()
        );
    }

    @Override
    public Rectangle getRectColision() {
        if (hitbox == null) {
            hitbox = new Rectangle(getX(), getY(), getAncho(), getAlto()/2);
        }
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
        this.objetivo = mundo.getJugador();

        this.estrategia = new EstrategiaMoverAleatorio();

        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName(), this, (Escenario) mundo));
        }
    }
}
