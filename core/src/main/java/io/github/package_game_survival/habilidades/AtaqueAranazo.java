package io.github.package_game_survival.habilidades;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.efectos.EfectoVisual;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.entidades.seres.animales.Animal;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class AtaqueAranazo extends AtaqueBase {

    private final float anchoArea;
    private final float FUERZA_EMPUJE = 15f;

    public AtaqueAranazo(float cooldown, float tiempoCasteo, int danio, float rango, float anchoArea, Class<? extends SerVivo> claseObjetivo) {
        super(cooldown, tiempoCasteo, danio, rango, claseObjetivo);
        this.anchoArea = anchoArea;
    }

    @Override
    protected void ejecutarEfecto(SerVivo atacante, Vector2 destino, IMundoJuego mundo) {
        // ... (Cálculo de vectores y spawn de EfectoVisual igual que antes) ...
        Vector2 centroAtacante = new Vector2(atacante.getCentroX(), atacante.getY() + atacante.getAlto()/2);
        Vector2 direccion = new Vector2(destino).sub(centroAtacante).nor();
        float angulo = direccion.angleDeg();
        Vector2 posEfecto = new Vector2(centroAtacante).mulAdd(direccion, this.rango * 0.6f);

        TextureAtlas atlas = Assets.get(PathManager.ARANAZO_ANIMATION, TextureAtlas.class);
        if (atlas != null) {
            EfectoVisual efecto = new EfectoVisual(atlas, "aranazo", posEfecto.x - 16, posEfecto.y - 16, 0.05f, angulo);
            mundo.agregarActor(efecto);
        }

        Polygon areaAtaque = new Polygon(new float[]{0, 0, rango, 0, rango, anchoArea, 0, anchoArea});
        areaAtaque.setOrigin(0, anchoArea / 2);
        areaAtaque.setPosition(centroAtacante.x, centroAtacante.y - (anchoArea/2));
        areaAtaque.setRotation(angulo);

        // --- LÓGICA DE BÚSQUEDA ACTUALIZADA ---
        Array<SerVivo> posiblesObjetivos = new Array<>();

        if (claseObjetivo.getSimpleName().equals("Jugador")) {
            // Si el objetivo es matar al jugador
            if (mundo.getJugador() != null) posiblesObjetivos.add(mundo.getJugador());
        }
        else {
            // Si el objetivo es SerVivo (General) o Enemigo
            // 1. Agregamos Enemigos
            if (mundo.getEnemigos() != null) {
                posiblesObjetivos.addAll(mundo.getEnemigos());
            }

            // 2. Si la clase objetivo incluye animales (SerVivo o Animal), los agregamos
            if (claseObjetivo.isAssignableFrom(Animal.class) && mundo.getAnimales() != null) {
                posiblesObjetivos.addAll(mundo.getAnimales());
            }
        }

        // Procesar colisiones
        for (SerVivo victima : posiblesObjetivos) {
            if (victima == null) continue;

            // Evitar que el jugador se pegue a sí mismo si usamos SerVivo.class
            if (victima == atacante) continue;

            Polygon hitBoxVictima = new Polygon(new float[]{0,0, victima.getAncho(), 0, victima.getAncho(), victima.getAlto(), 0, victima.getAlto()});
            hitBoxVictima.setPosition(victima.getX(), victima.getY());

            if (Intersector.overlapConvexPolygons(areaAtaque, hitBoxVictima)) {
                victima.alterarVida(-danio);

                float pushX = direccion.x * FUERZA_EMPUJE;
                float pushY = direccion.y * FUERZA_EMPUJE;
                victima.recibirEmpuje(pushX, pushY);
            }
        }
    }
}
