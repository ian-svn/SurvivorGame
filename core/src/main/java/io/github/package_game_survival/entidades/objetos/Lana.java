package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Lana extends ObjetoConsumible {

    public Lana(float x, float y) {
        // Configuramos valores base (puedes ajustar curación si quieres)
        super("Lana", x, y, Assets.get(PathManager.LANA_TEXTURE, Texture.class),
            0, 0, 0, 0, 0, 0);
    }

    @Override
    public void consumir(Jugador jugador) {
        super.consumir(jugador);
        jugador.mejorarRangoAtaque(15f, 8f);

        System.out.println("¡Lana consumida! El ataque del jugador ha mejorado.");
    }
}
