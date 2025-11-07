package io.github.package_game_survival.standards;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class ProgressBarStandard extends ProgressBar {
    private boolean ubicado = false;

    private LabelStandard label;

    public ProgressBarStandard(float min, float max, int ancho, int alto, int valorInical, boolean formal, String text) {
        super(min, max, 1f, false, (formal) ? Assets.get(PathManager.PROGRESS_BAR_SKIN, Skin.class) : Assets.get(PathManager.PROGRESS_BAR_SKIN_VIDA, Skin.class));
        this.setValue(valorInical);
        this.setWidth(ancho);
        this.setHeight(alto);
        this.label = new LabelStandard(text);
        label.setFontScale(1f);

        ProgressBar.ProgressBarStyle style = this.getStyle();
        style.background.setMinHeight(20);
        style.knobBefore.setMinHeight(20);

        this.setStyle(style);
    }

    public void agregarAlStage(Stage stage) {
        stage.addActor(this);
        stage.addActor(label);
    }


    public void setPosicion(float x, float y) {
        setPosition(x,y);
        label.setPosition(getX() + 10, getY() -18);
    }

    public void actualizar(int nuevoValor){
        setValue(nuevoValor);
    }

    public LabelStandard getLabel() {
        return label;
    }
}
