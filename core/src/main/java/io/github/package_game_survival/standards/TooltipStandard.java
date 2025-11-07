package io.github.package_game_survival.standards;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class TooltipStandard {

    private static final Skin skinTooltip = Assets.get(PathManager.TOOLTIP, Skin.class);
    private static final TooltipManager tm = TooltipManager.getInstance();

    static {
        tm.animations = false;
        tm.initialTime = 0.5f;
        tm.subsequentTime = 0f;
        tm.resetTime = 0f;
        tm.offsetX = 10f;
        tm.offsetY = 10f;
    }

    private final TextTooltip tooltip;
    private final Actor actor;
    private final OrthographicCamera camera;

    public TooltipStandard(String text, Actor actor, Escenario escenario) {
        this.actor = actor;
        this.camera = escenario.getCamara();
        tooltip = new TextTooltip(text, tm, skinTooltip);
        tooltip.getContainer().setBackground((Drawable) null);

        actor.addListener(tooltip);
    }

    public void actualizarPosicion() {
        if (actor.getStage() == null) return;
        float x = actor.getX();
        float y = actor.getY() + actor.getHeight() + 15f;

        tooltip.getContainer().setPosition(x, y);
    }
}
