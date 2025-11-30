package io.github.package_game_survival.habilidades;

import com.badlogic.gdx.Gdx; // Importar para logs
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
        Vector2 centroAtacante = new Vector2(atacante.getCentroX(), atacante.getY() + atacante.getAlto()/2);
        Vector2 direccion = new Vector2(destino).sub(centroAtacante).nor();
        float angulo = direccion.angleDeg();

        // Visual
        TextureAtlas atlas = Assets.get(PathManager.ARANAZO_ANIMATION, TextureAtlas.class);
        if (atlas != null) {
            Vector2 posEfecto = new Vector2(centroAtacante).mulAdd(direccion, this.rango * 0.6f);
            EfectoVisual efecto = new EfectoVisual(atlas, "aranazo", posEfecto.x - 16, posEfecto.y - 16, 0.05f, angulo);
            mundo.agregarActor(efecto);
        }

        // Física
        Polygon areaAtaque = new Polygon(new float[]{0, 0, rango, 0, rango, anchoArea, 0, anchoArea});
        areaAtaque.setOrigin(0, anchoArea / 2);
        areaAtaque.setPosition(centroAtacante.x, centroAtacante.y - (anchoArea/2));
        areaAtaque.setRotation(angulo);

        Array<SerVivo> posiblesObjetivos = new Array<>();
        if (claseObjetivo.getSimpleName().equals("Jugador")) {
            if (mundo.getJugador() != null) posiblesObjetivos.add(mundo.getJugador());
        } else {
            if (mundo.getEnemigos() != null) posiblesObjetivos.addAll(mundo.getEnemigos());
            if (claseObjetivo.isAssignableFrom(Animal.class) && mundo.getAnimales() != null) {
                posiblesObjetivos.addAll(mundo.getAnimales());
            }
        }

        boolean huboImpacto = false;

        for (SerVivo victima : posiblesObjetivos) {
            if (victima == null || victima == atacante) continue;
            if (victima.isMuerto() || victima.getVida() <= 0) continue;

            Polygon hitBoxVictima = new Polygon(new float[]{0,0, victima.getAncho(), 0, victima.getAncho(), victima.getAlto(), 0, victima.getAlto()});
            hitBoxVictima.setPosition(victima.getX(), victima.getY());

            if (Intersector.overlapConvexPolygons(areaAtaque, hitBoxVictima)) {

                // --- CORRECCIÓN DE DAÑO ---
                // Sumamos el daño base del ataque (this.danio) + el daño del personaje (atacante.getDanio())
                int danioTotal = this.danio + atacante.getDanio();

                victima.alterarVida(-danioTotal);
                victima.recibirEmpuje(direccion.x * FUERZA_EMPUJE, direccion.y * FUERZA_EMPUJE);

//                // Debug para verificar en consola
//                if (atacante.getNombre().equals("Ian")) { // Asumiendo que tu jugador se llama Ian o usa getName()
//                    Gdx.app.log("COMBATE", "Golpeaste por " + danioTotal + " de daño (Base: " + this.danio + " + Bonus: " + atacante.getDanio() + ")");
//                }

                if (!huboImpacto) {
                    Vector2 posImpacto = new Vector2(victima.getCentroX(), victima.getY() + victima.getAlto()/2);
                    spawnearEfectoVisual(mundo, posImpacto, angulo);
                    huboImpacto = true;
                }
            }
        }

        if (!huboImpacto) {
            Vector2 posMiss = new Vector2(centroAtacante).mulAdd(direccion, 30f);
            spawnearEfectoVisual(mundo, posMiss, angulo);
        }
    }

    private void spawnearEfectoVisual(IMundoJuego mundo, Vector2 posicion, float angulo) {
        TextureAtlas atlas = Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class);
        if (atlas != null) {
            EfectoVisual efecto = new EfectoVisual(atlas, "aranazo", posicion.x - 16, posicion.y - 16, 0.05f, angulo);
            mundo.agregarActor(efecto);
        }
    }
}
