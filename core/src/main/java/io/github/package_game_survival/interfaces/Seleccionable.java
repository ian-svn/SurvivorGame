package io.github.package_game_survival.interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Seleccionable extends ScreenAdapter {

    private Stage stage;
    private Actor selectedActor = null; // Variable para guardar el actor seleccionado

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // --- Configuración de las imágenes y la tabla ---
        // Asume que tienes una imagen "ui_icon.png"
        Texture texture = new Texture(Gdx.files.internal("sprites/marco.png"));
        TextureRegion region = new TextureRegion(texture);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);

        // Creamos tres Image que serán seleccionables
        Image item1 = new Image(drawable);
        Image item2 = new Image(drawable);
        Image item3 = new Image(drawable);

        // Creamos una tabla para organizarlas
        Table table = new Table();
        table.setFillParent(true);
        table.add(item1).pad(20);
        table.add(item2).pad(20);
        table.add(item3).pad(20);
        stage.addActor(table);

        // --- Lógica de selección con un ClickListener para cada Image ---
        setupClickListener(item1);
        setupClickListener(item2);
        setupClickListener(item3);
    }

    private void setupClickListener(final Image item) {
        item.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 1. Deseleccionar el elemento anterior
                if (selectedActor != null) {
                    selectedActor.setColor(Color.WHITE); // Vuelve al color original
                }

                // 2. Seleccionar el nuevo elemento
                selectedActor = event.getListenerActor();
                selectedActor.setColor(Color.GREEN); // Cambia al color de selección

                // Aquí puedes agregar tu lógica de lo que sucede al seleccionar
                System.out.println("Seleccionado: " + selectedActor);
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
