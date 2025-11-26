package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class ObjetoColeccionable extends Objeto {

    public ObjetoColeccionable(String nombre, float x, float y, Texture texture) {
        super(nombre, x, y, texture);
        setName(nombre);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getTooltip() != null) getTooltip().actualizarPosicion();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Si quieres usar la textura del padre (GestorAnimacion), llama a super.draw(batch, parentAlpha)
        // Si quieres forzar la poción:
        batch.draw(Assets.get(PathManager.POCION_TEXTURE, Texture.class), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
        // Aquí podrías agregar tooltip si quisieras
    }
}
