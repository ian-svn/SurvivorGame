package io.github.package_game_survival.standards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class LabelStandard extends Label {

    // Constructor básico
    public LabelStandard(CharSequence text) {
        super(text, Assets.get(PathManager.LABEL, Skin.class));
    }

    // Constructor completo (Texto, Color, Escala)
    public LabelStandard(CharSequence text, Color color, float scale) {
        super(text, Assets.get(PathManager.LABEL, Skin.class));
        this.setColor(color);
        this.setFontScale(scale);
    }

    // Métodos de utilidad para configuración fluida
    public void setCustomConfig(Color color, float scale) {
        this.setColor(color);
        this.setFontScale(scale);
    }

    public void setCentrado() {
        this.setAlignment(Align.center);
    }
}
