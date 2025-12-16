package io.github.package_game_survival.standards;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
        tm.initialTime = 0.1f;
        tm.subsequentTime = 0f;
        tm.resetTime = 0f;
        tm.offsetX = 10f;
        tm.offsetY = 10f;
    }

    private final TextTooltip tooltip;

    // Constructor para actores generales (Jugador, Items)
    public TooltipStandard(String text, Actor actor) {
        tooltip = new TextTooltip(text, tm, skinTooltip);
        tooltip.getContainer().setBackground((Drawable) null);
        actor.addListener(tooltip);
    }

    // Constructor de compatibilidad (por si lo usas en Escenario)
    public TooltipStandard(String text, Actor actor, Escenario escenario) {
        this(text, actor);
    }

    // --- NUEVO: PERMITE CAMBIAR EL TEXTO EN VIVO ---
    public void setText(String newText) {
        // Accedemos al Label interno del TextTooltip y le cambiamos el texto
        Label label = tooltip.getActor();
        if (label != null) {
            label.setText(newText);
            tooltip.getContainer().pack(); // Reajustar tamaño del fondo
        }
    }

    public void actualizarPosicion() { }
}
