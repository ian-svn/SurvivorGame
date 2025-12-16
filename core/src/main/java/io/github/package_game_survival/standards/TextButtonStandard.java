package io.github.package_game_survival.standards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;

public class TextButtonStandard extends TextButton {

    public TextButtonStandard(String text) {
        super(text, Assets.get(PathManager.TEXT_BUTTON, Skin.class));

        // Listener para el cursor (Hover)
        this.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        // Listener para el sonido por defecto
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Asegúrate que tu AudioManager tiene el método getControler() o getAudioControler()
                // Uso getControler() basado en tus clases anteriores.
                AudioManager.getControler().playSound("click");
            }
        });
    }

    public void setClickListener(Runnable run){
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                run.run();
                // Restauramos cursor al hacer click por si cambia de pantalla
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });
    }

    public void setEscalaFuente(float escala){
        this.setScale(escala);
    }

}
