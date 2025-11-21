package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAleatorio;
import io.github.package_game_survival.interfaces.IMundoJuego; // Importante
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Jabali extends Animal {

    public Jabali(float x, float y) {
        // 1. Corregido: Quitamos el 'null' del final.
        super("Jabali", x, y, 54, 42, 20, 20, 10, 0,
            Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));
    }

    // 2. Corregido: Usamos la interfaz IMundoJuego
    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        // Llamamos al padre (Animal) para que configure la IA básica y agregue el actor
        super.agregarAlMundo(mundo);

        // Si quisieras una estrategia específica solo para el Jabalí, la cambiarías aquí:
        // this.estrategia = new EstrategiaMoverAleatorio();
    }
}
