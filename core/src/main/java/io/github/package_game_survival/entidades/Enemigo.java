package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

import java.util.ArrayList;

public class Enemigo extends Personaje {

    private Personaje objetivo;
    private EstrategiaMoverAPunto estrategia;
    private ArrayList<Bloque> bloques;
    private int dano;

    public Enemigo(float x, float y, Personaje objetivo, ArrayList<Bloque> bloques) {
        this(x, y, objetivo, bloques, 20);
    }

    public Enemigo(float x, float y, Personaje objetivo, ArrayList<Bloque> bloques, int dano) {
        super("Enemigo", Assets.get(PathManager.ENEMIGO_TEXTURE, Texture.class), 100, 100, x, y);
        setPosition(x, y);
        setSize(50, 50);
        this.objetivo = objetivo;
        this.bloques = bloques;
        this.dano = dano;
        this.estrategia = new EstrategiaMoverAPunto(new Vector2(objetivo.getX(), objetivo.getY()));
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

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public int getDano() {
        return this.dano;
    }

    public void setDano(int dano) {
        this.dano = dano;
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
}
