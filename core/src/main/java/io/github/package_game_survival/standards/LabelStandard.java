package io.github.package_game_survival.standards;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class LabelStandard extends Label {

    public LabelStandard(CharSequence text) {
        super(text, Assets.get(PathManager.LABEL,Skin.class));
    }

}
