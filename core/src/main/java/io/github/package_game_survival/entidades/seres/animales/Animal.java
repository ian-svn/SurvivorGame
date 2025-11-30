package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAleatorio;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Animal extends SerVivo {

    protected Jugador objetivo;
    protected EstrategiaMoverAleatorio estrategia;
    private Rectangle hitbox;

    // Control visual
    private Texture texturaSimple;
    private float lastX;
    private boolean mirandoDerecha = false;

    // --- SISTEMA DE DROPS ---
    // Lista de CLASES de objetos que puede soltar (ej: Carne.class, Piedra.class)
    protected Array<Class<? extends Objeto>> posiblesDrops;

    public Animal(String nombre, float x, float y, float ancho, float alto,
                  int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio, atlas);
        inicializarComunes(x);
    }

    public Animal(String nombre, float x, float y, float ancho, float alto,
                  int vidaInicial, int vidaMaxima, int velocidad, int danio, Texture texture) {
        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio, (TextureAtlas) null); // null al padre
        this.texturaSimple = texture;
        inicializarComunes(x);
    }

    private void inicializarComunes(float x) {
        this.estrategia = null;
        this.lastX = x;
        this.posiblesDrops = new Array<>();
    }

    // --- MÉTODO PARA SOLTAR ITEM AL MORIR ---
    @Override
    public void delete() {
        super.delete(); // Se borra del mundo visual

        if (mundo != null && posiblesDrops.notEmpty()) {
            try {
                // 1. Elegir un objeto random de la lista
                Class<? extends Objeto> claseElegida = posiblesDrops.random();

                // 2. Crear una NUEVA instancia de ese objeto en la posición del animal
                // Asumimos que los objetos tienen constructor (float x, float y)
                Objeto loot = claseElegida.getConstructor(float.class, float.class)
                    .newInstance(getX(), getY());

                // 3. Agregarlo al mundo
                loot.agregarAlMundo(mundo);
                if (mundo instanceof Escenario) {
                    ((Escenario) mundo).getObjetos().add(loot);
                }

                //Gdx.app.log("ANIMAL", "Dropeado: " + loot.getName());

            } catch (Exception e) {
                //Gdx.app.error("ANIMAL", "Error al generar loot: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        setMundo(mundo);
        mundo.agregarActor(this);
        this.objetivo = mundo.getJugador();
        this.estrategia = new EstrategiaMoverAleatorio();

        if (mundo instanceof Escenario) {
            instanciarTooltip(new TooltipStandard(getName(), this, (Escenario) mundo));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float diff = getX() - lastX;
        if (diff > 0) mirandoDerecha = true;
        else if (diff < 0) mirandoDerecha = false;
        lastX = getX();

        if (estrategia != null) estrategia.actualizar(this, delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (texturaSimple != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(texturaSimple, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(),
                0, 0, texturaSimple.getWidth(), texturaSimple.getHeight(), mirandoDerecha, false);
            batch.setColor(Color.WHITE);
        } else {
            super.draw(batch, parentAlpha);
        }
    }

    @Override
    public Rectangle getRectColision() {
        if (hitbox == null) hitbox = new Rectangle(getX(), getY(), getAncho(), getAlto()/2);
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }

    // Método para que las hijas agreguen sus drops
    protected void agregarDrop(Class<? extends Objeto> claseObjeto) {
        this.posiblesDrops.add(claseObjeto);
    }
}
