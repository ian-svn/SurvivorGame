package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAleatorio;
import io.github.package_game_survival.interfaces.IMundoJuego; // Importante
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Vaca extends Animal {

    public Vaca(float x, float y) {
        // 1. Corregido: Quitamos el 'null' del final.
        super("Vaca", x, y, 32, 32, 20, 20, 10, 0,
            Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));
    }

    // 2. Corregido: Usamos la interfaz IMundoJuego
    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        // La clase Animal ya se encarga de agregar el Tooltip y la IA por defecto
        super.agregarAlMundo(mundo);
    }
}
