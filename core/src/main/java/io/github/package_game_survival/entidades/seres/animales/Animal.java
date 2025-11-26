package io.github.package_game_survival.entidades.seres.animales;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAleatorio;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;
import io.github.package_game_survival.standards.TooltipStandard;

public abstract class Animal extends SerVivo {

    protected Jugador objetivo;
    protected EstrategiaMoverAleatorio estrategia;
    private Rectangle hitbox;

    // Variables para controlar el FLIP (Espejado)
    private Texture texturaSimple; // Guardamos la textura aquí
    private float lastX; // Para saber hacia dónde se movió
    private boolean mirandoDerecha = false; // Estado del flip

    // Constructor 1: Para Atlas completo (si usas uno real)
    public Animal(String nombre, float x, float y, float ancho, float alto,
                  int vidaInicial, int vidaMaxima, int velocidad, int danio, TextureAtlas atlas) {
        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio, atlas);
        this.estrategia = null;
        this.texturaSimple = null;
    }

    // Constructor 2: Para Textura simple (PNG) - Este es el que usas
    public Animal(String nombre, float x, float y, float ancho, float alto,
                  int vidaInicial, int vidaMaxima, int velocidad, int danio, Texture texture) {
        // Pasamos el atlas falso al padre para que no crashee la lógica interna
        super(nombre, x, y, ancho, alto, vidaInicial, vidaMaxima, velocidad, danio, crearAtlasDesdeTextura(texture));

        this.texturaSimple = texture;
        this.estrategia = null;
        this.lastX = x;
    }

    /**
     * Mantenemos esto SOLO para satisfacer al constructor de SerVivo.
     * Ya no dependemos de esto para dibujar el flip, lo haremos manualmente en draw().
     */
    private static TextureAtlas crearAtlasDesdeTextura(Texture texture) {
        TextureAtlas atlas = new TextureAtlas();
        TextureRegion region = new TextureRegion(texture);
        String[] claves = {
            "Der1", "der2", "der3", "arriba", "arriba1", "abajo1", "abajo2",
            "diagnalDer2", "diagnalDer3", "abajoIdle", "DerIdle", "diagonalDerIdle"
        };
        for (String clave : claves) {
            atlas.addRegion(clave, region);
        }
        return atlas;
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

        // --- LÓGICA DE DETECCIÓN DE GIRO ---
        float diff = getX() - lastX;

        // Si se mueve a la derecha (diferencia positiva), activamos el flip
        if (diff > 0) {
            mirandoDerecha = true;
        }
        // Si se mueve a la izquierda (diferencia negativa), desactivamos el flip
        else if (diff < 0) {
            mirandoDerecha = false;
        }

        lastX = getX(); // Guardamos posición para el siguiente frame

        if (estrategia != null) {
            estrategia.actualizar(this, delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // SI tenemos una textura simple (Vaca/Jabalí), dibujamos manualmente con Flip
        if (texturaSimple != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

            // batch.draw con todos los argumentos para poder usar flipX
            batch.draw(
                texturaSimple,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation(),
                0, 0,
                texturaSimple.getWidth(), texturaSimple.getHeight(),
                mirandoDerecha, // flipX: true invierte la imagen (mira a la derecha)
                false           // flipY
            );

            batch.setColor(Color.WHITE); // Restaurar color
        } else {
            // Si es un animal con Atlas completo, usamos el dibujo normal del padre
            super.draw(batch, parentAlpha);
        }
    }

    @Override
    public Rectangle getRectColision() {
        if (hitbox == null) {
            hitbox = new Rectangle(getX(), getY(), getAncho(), getAlto()/2);
        }
        hitbox.setPosition(getX(), getY());
        return hitbox;
    }
}
