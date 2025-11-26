package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.entidades.objetos.Carne; // Importante
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Jabali extends Animal {

    public Jabali(float x, float y) {
        super("Jabali", x, y, 54, 42, 150, 150, 10, 0,
            Assets.get(PathManager.JABALI_TEXTURE_ATLAS, Texture.class));

        this.agregarDrop(Carne.class);
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        super.agregarAlMundo(mundo);
    }
}
