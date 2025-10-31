package io.github.package_game_survival.entidades.seres;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.Entidad;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

import java.util.ArrayList;

public abstract class Enemigo extends SerVivo {

    protected Jugador objetivo;
    protected EstrategiaMoverAPunto estrategia;
    protected ArrayList<Bloque> bloques;

    public Enemigo(String nombre, float x, float y, float ancho, float alto,
                   int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas,
                   Jugador objetivo, ArrayList<Bloque> bloques) {

        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio, atlas);
        this.objetivo = objetivo;
        this.bloques = bloques;
        this.estrategia = null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        moverse(delta);
    }

    private void moverse(float delta) {
        if (objetivo != null && estrategia != null) {
            estrategia.setDestino(new Vector2(objetivo.getX(), objetivo.getY()));
            estrategia.actualizar(this, delta);
        }
    }

    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
        batch.draw(
            Assets.get(PathManager.ENEMIGO_TEXTURE, Texture.class),
            getX(),
            getY(),
            getWidth(),
            getHeight()
        );
    }

    public void setObjetivo(Jugador objetivo) {
        this.objetivo = objetivo;
    }

    public void setBloques(ArrayList<Bloque> bloques) {
        this.bloques = bloques;
    }

    @Override
    public Rectangle getRectColision() {
        return new Rectangle(getX(), getY(), getAncho(), getAlto()/2);
    }
}
