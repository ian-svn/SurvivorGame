package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.entidades.objetos.Lana;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Oveja extends Animal {

    public Oveja(float x, float y) {
        super("Oveja", x, y, 42, 32, 100, 100, 10, 0,
            Assets.get(PathManager.OVEJA_TEXTURE_ATLAS, Texture.class));

        this.agregarDrop(Lana.class);
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        super.agregarAlMundo(mundo);
    }
}
