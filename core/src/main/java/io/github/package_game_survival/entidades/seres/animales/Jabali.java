package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAleatorio;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public class Jabali extends Animal {
    public Jabali(float x, float y) {
        super("Jabali", x, y, 54, 42, 20, 20, 10, 0,
            Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class), null);
        this.estrategia = new EstrategiaMoverAleatorio();
    }

    @Override
    public void agregarAlEscenario(Escenario escenario) {
        escenario.agregar(this);
        instanciarTooltip(new TooltipStandard(getName(), this, escenario));
    }
}
