package io.github.package_game_survival.entidades.mapas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import io.github.package_game_survival.managers.PathManager;

public class Mapa {
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderer;

    public Mapa() {
        mapa = new TmxMapLoader().load(PathManager.MAPA_BOSQUE);
        renderer = new OrthogonalTiledMapRenderer(mapa, 1f); // escala = 1
    }

    public void render(OrthographicCamera camara) {
        renderer.setView(camara);
        renderer.render();
    }

    public void dispose() {
        mapa.dispose();
        renderer.dispose();
    }

    public TiledMap getMapa() { return mapa; }
    public OrthogonalTiledMapRenderer getRenderer() { return renderer; }
}
