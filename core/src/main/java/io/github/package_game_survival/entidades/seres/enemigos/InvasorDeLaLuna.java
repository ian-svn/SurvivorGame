package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public class InvasorDeLaLuna extends Enemigo {

//    private float ANCHO = 40f, ALTO = 60f;
//    private int vidaMaxima = 100, vidaInicial = vidaMaxima, velocidad = 100, danio = 20;
//    private TextureAtlas atlas = Assets.get(PathManager.PLAYER_ATLAS);
//    private Jugador objetivo;
//    private ArrayList <Bloque> bloques;

    public InvasorDeLaLuna(float x, float y) {
        super("Invasor De La Luna", x, y, 30, 40, 100,
            100, 50, 20, Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class),
            null, null);
    }
}
