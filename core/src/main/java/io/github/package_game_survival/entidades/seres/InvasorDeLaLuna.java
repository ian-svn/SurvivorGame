package io.github.package_game_survival.entidades.seres;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorDeLaLuna extends Enemigo{

//    private float ANCHO = 40f, ALTO = 60f;
//    private int vidaMaxima = 100, vidaInicial = vidaMaxima, velocidad = 100, danio = 20;
//    private TextureAtlas atlas = Assets.get(PathManager.PLAYER_ATLAS);
//    private Jugador objetivo;
//    private ArrayList <Bloque> bloques;

    public InvasorDeLaLuna(float x, float y) {
        super("InvasorDeLaLuna", x, y, 50, 60, 100,
            100, 60, 20, Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class),
            null, null);
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        escenario.getStage().addActor(this);
        super.objetivo = escenario.getJugador();
        super.bloques = escenario.getBloques();
        super.estrategia = new EstrategiaMoverAPunto(new Vector2(objetivo.getX(), objetivo.getY()));
    }
}
