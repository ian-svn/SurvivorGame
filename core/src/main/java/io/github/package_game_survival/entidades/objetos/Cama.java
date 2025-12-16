package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Cama extends Objeto {

    public Cama(float x, float y) {
        super("Cama\nDormir [E]", x, y, 32, 64, Assets.get(PathManager.CAMA_TEXTURE, Texture.class));
        this.setDesaparecible(false);
    }

    @Override
    public void adquirir() {

    }
}
