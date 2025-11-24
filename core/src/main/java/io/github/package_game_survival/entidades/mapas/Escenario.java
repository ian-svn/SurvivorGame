package io.github.package_game_survival.entidades.mapas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.package_game_survival.entidades.bloques.*;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.objetos.PocionDeAmatista;
import io.github.package_game_survival.entidades.seres.animales.Animal;
import io.github.package_game_survival.entidades.seres.animales.Jabali;
import io.github.package_game_survival.entidades.seres.animales.Vaca;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.enemigos.InvasorArquero;
import io.github.package_game_survival.entidades.seres.enemigos.InvasorDeLaLuna;
import io.github.package_game_survival.entidades.seres.enemigos.InvasorMago;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

import java.util.ArrayList;
import java.util.List;

public class Escenario {

    private final Stage stage;
    private final Jugador jugador;
    private final ArrayList<Bloque> bloques;
    private final ArrayList<Enemigo> enemigos;
    private final ArrayList<Animal> animales;
    private final ArrayList<Objeto> objetos;
    private final Mapa mapa;

    public Escenario(Stage stage, Jugador jugador) {
        this.stage = stage;
        this.jugador = jugador;
        this.bloques = new ArrayList<>();
        this.animales = new ArrayList<>();
        this.objetos = new ArrayList<>();
        this.enemigos = new ArrayList<>();

        mapa = new Mapa();

        inicializarEnemigos();
        inicializarAnimales();
        inicializarObjetos();
        inicializarBloquesDesdeMapa();
        agregarEntidadesAlStage();
    }

    private void inicializarObjetos() {
        objetos.add(new PocionDeAmatista(200,600));
        objetos.add(new PocionDeAmatista(400,400));
        objetos.add(new PocionDeAmatista(600,200));
    }

    private void inicializarAnimales() {
        animales.add(new Vaca(400,300));
        animales.add(new Jabali(20,100));
        animales.add(new Vaca(350,500));
    }

    private void inicializarEnemigos() {
        enemigos.add(new InvasorArquero(200,500));
        enemigos.add(new InvasorMago(50,100));
        enemigos.add(new InvasorDeLaLuna(280,100));
    }

    private void inicializarBloquesDesdeMapa() {
        TiledMap tiledMap = mapa.getMapa();

        for (MapLayer layer : tiledMap.getLayers()) {
            if (!(layer instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer tileLayer)) continue;

            for (int x = 0; x < tileLayer.getWidth(); x++) {
                for (int y = 0; y < tileLayer.getHeight(); y++) {
                    var cell = tileLayer.getCell(x, y);
                    if (cell == null || cell.getTile() == null) continue;

                    var tile = cell.getTile();
                    var props = tile.getProperties();

                    boolean transitable = props.get("transitable", true, Boolean.class);
                    if (!transitable) {
                        String tipo = props.get("tipo", "bloque", String.class);
                        boolean destructible = props.get("destructible", false, Boolean.class);
                        String objetoTirado = props.get("objetoTirado", null, String.class);

                        Bloque bloque = destructible
                            ? new BloqueDestructible(x * 32, y * 32, tipo, objetoTirado)
                            : new BloqueNoTransitable(x * 32, y * 32, tipo);

                        bloques.add(bloque);
                    }
                }
            }
        }
    }


    public List<Rectangle> getRectangulosBloquesNoTransitables() {
        List<Rectangle> rectangulos = new ArrayList<>();
        for (Bloque bloque : bloques) {
            if (!bloque.isTransitable()) {
                rectangulos.add(bloque.getRectColision());
            }
        }
        return rectangulos;
    }


    private void agregarEntidadesAlStage() {
        for (Bloque bloque : bloques)
            bloque.agregarAlEscenario(this);
        for (Enemigo enemigo : enemigos)
            enemigo.agregarAlEscenario(this);
        for (Animal animal : animales)
            animal.agregarAlEscenario(this);
        for (Objeto objeto : objetos)
            objeto.agregarAlEscenario(this);
        jugador.agregarAlEscenario(this);
    }

    public void renderMapa(OrthographicCamera camara) {
        mapa.render(camara);
    }

    public void dispose() {
        mapa.dispose();
    }

    public void agregar(Actor actor) {
        stage.addActor(actor);
    }

    public ArrayList<Bloque> getBloques() {
        return bloques;
    }

    public OrthographicCamera getCamara(){
        return (OrthographicCamera) this.stage.getCamera();
    }

    public Jugador getJugador() {
        return jugador;
    }

    public ArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }

    public ArrayList<Objeto> getObjetos() {
        return objetos;
    }

    public ArrayList<Animal> getAnimales() {
        return animales;
    }
}
