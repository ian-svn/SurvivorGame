package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.entidades.objetos.Carne; // Importante
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Vaca extends Animal {

    public Vaca(float x, float y) {
        super("Vaca", x, y, 32, 32, 125, 125, 10, 0,
            Assets.get(PathManager.VACA_TEXTURE_ATLAS, Texture.class));

        this.agregarDrop(Carne.class);
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        super.agregarAlMundo(mundo);
    }
}
