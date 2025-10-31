package io.github.package_game_survival.standards;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class TooltipStandard extends TextTooltip {

    private static final Skin skinTooltip = Assets.get(PathManager.TOOLTIP, Skin.class);
    private static final TooltipManager tm = TooltipManager.getInstance();

    static {
        tm.initialTime = 0.4f;
        tm.subsequentTime = 0.1f;
        tm.resetTime = 1f;
    }

    public TooltipStandard(String text, Actor actor) {
        super(text, tm, skinTooltip);
        this.getContainer().setBackground((Drawable) null);
    }

    public void attach(Actor actor) {
        actor.addListener(this);
    }
}
