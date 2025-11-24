package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAleatorio;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;


public abstract class Animal extends SerVivo {

    protected Jugador objetivo;
    protected EstrategiaMoverAleatorio estrategia;

    public Animal(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas,
                   Jugador objetivo) {

        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio, atlas);
        this.objetivo = objetivo;
        this.estrategia = null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        moverse(delta);
    }

    private void moverse(float delta) {
        estrategia.actualizar(this, delta);
    }

    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
        batch.draw(
            Assets.get(PathManager.VACA_TEXTURE, Texture.class),
            getX(),
            getY(),
            getWidth(),
            getHeight()
        );
    }

    public void setObjetivo(Jugador objetivo) {
        this.objetivo = objetivo;
    }

    @Override
    public Rectangle getRectColision() {
        return new Rectangle(getX(), getY(), getAncho(), getAlto()/2);
    }
}
