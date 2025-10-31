package io.github.package_game_survival.entidades.mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.bloques.BloqueDeBarro;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.objetos.PocionDeAmatista;
import io.github.package_game_survival.entidades.seres.Enemigo;
import io.github.package_game_survival.entidades.seres.InvasorArquero;
import io.github.package_game_survival.entidades.seres.InvasorDeLaLuna;
import io.github.package_game_survival.entidades.seres.Jugador;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

import java.util.ArrayList;

public class Escenario {

    private Stage stage;
    private Jugador jugador;
    private ArrayList<Bloque> bloques;
    private ArrayList<Objeto> objetos;
    private ArrayList<Enemigo> enemigos;

    private float anchoMundo;
    private float altoMundo;

    public Escenario(Stage stage, Jugador jugador, float anchoMundo, float altoMundo) {
        this.stage = stage;
        this.jugador = jugador;
        this.anchoMundo = anchoMundo;
        this.altoMundo = altoMundo;

        this.bloques = new ArrayList<>();
        this.objetos = new ArrayList<>();
        this.enemigos = new ArrayList<>();

        Texture fondoTexture = Assets.get(PathManager.GAME_BACKGROUND_TEXTURE, Texture.class);
        Image fondo = new Image(fondoTexture);
        fondo.setSize(anchoMundo, altoMundo);
        fondo.setPosition(0, 0);
        agregar(fondo);

        inicializarBloques();
        inicializarObjetos();
        inicializarEnemigos();
        agregarEntidadesAlStage();
    }

    private void inicializarBloques() {
        for (int x = 0; x < anchoMundo; x += Bloque.ANCHO) {
            bloques.add(new BloqueDeBarro(x, 0));
            bloques.add(new BloqueDeBarro(x, (int) altoMundo - Bloque.ALTO));
        }
        for (int y = Bloque.ALTO; y < altoMundo - Bloque.ALTO; y += Bloque.ALTO) {
            bloques.add(new BloqueDeBarro(0, y));
            bloques.add(new BloqueDeBarro((int) anchoMundo - Bloque.ANCHO, y));
        }

        bloques.add(new BloqueDeBarro(Bloque.ANCHO * 3, Bloque.ALTO * 3));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO * 4, Bloque.ALTO * 3));
        bloques.add(new BloqueDeBarro(Bloque.ANCHO * 10, Bloque.ALTO * 3));
    }

    private void inicializarObjetos() {
        objetos.add(new PocionDeAmatista(300, 100));
        objetos.add(new PocionDeAmatista(900, 400));
        objetos.add(new PocionDeAmatista(400, 700));
    }

    private void inicializarEnemigos() {
        enemigos.add(new InvasorDeLaLuna(100, 400));
        enemigos.add(new InvasorArquero(400, 100));
    }

    private void agregarEntidadesAlStage() {

        for (Bloque bloque : bloques) bloque.agregarAlEscenario(this);
        for (Objeto objeto : objetos) objeto.agregarAlEscenario(this);
        for (Enemigo enemigo : enemigos) enemigo.agregarAlEscenario(this);
        jugador.agregarAlEscenario(this);
    }

    public void agregar(Actor actor) {
        getStage().addActor(actor);
    }

    public Stage getStage() {
        return stage;
    }

    public ArrayList<Bloque> getBloques() {
        return bloques;
    }

    public ArrayList<Objeto> getObjetos() {
        return objetos;
    }

    public ArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }

    public Jugador getJugador() {
        return jugador;
    }
}
