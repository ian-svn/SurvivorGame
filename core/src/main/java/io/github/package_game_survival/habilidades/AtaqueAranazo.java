package io.github.package_game_survival.habilidades;

import com.badlogic.gdx.graphics.Color;
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

    // CAMBIO 1: Quitamos 'final' para poder modificarlo dinámicamente
    private float anchoArea;
    private final float FUERZA_EMPUJE = 15f;

    private final Color colorVisual;

    public AtaqueAranazo(float cooldown, float tiempoCasteo, int danio, float rango, float anchoArea, Class<? extends SerVivo> claseObjetivo, Color color) {
        super(cooldown, tiempoCasteo, danio, rango, claseObjetivo);
        this.anchoArea = anchoArea;
        this.colorVisual = color;
    }

    // CAMBIO 2: Métodos necesarios para la mejora de la Lana
    public void aumentarRango(float cantidad) {
        this.rango += cantidad;
        // Opcional: Límite para que no pegue en todo el mapa
        if (this.rango > 300) this.rango = 300;
    }

    public void aumentarArea(float cantidad) {
        this.anchoArea += cantidad;
        // Opcional: Límite de ancho
        if (this.anchoArea > 200) this.anchoArea = 200;
    }

    @Override
    protected void ejecutarEfecto(SerVivo atacante, Vector2 destino, IMundoJuego mundo) {
        Vector2 centroAtacante = new Vector2(atacante.getCentroX(), atacante.getY() + atacante.getAlto()/2);
        Vector2 direccion = new Vector2(destino).sub(centroAtacante).nor();
        float angulo = direccion.angleDeg();

        // 1. VISUAL
        TextureAtlas atlas = Assets.get(PathManager.ARANAZO_ANIMATION, TextureAtlas.class);
        if (atlas != null) {
            EfectoVisual efecto = new EfectoVisual(atlas, "aranazo", centroAtacante.x, centroAtacante.y - (anchoArea / 2f), 0.05f, angulo);

            // Usa 'this.rango' y 'this.anchoArea' que ahora pueden cambiar
            efecto.setSize(this.rango, this.anchoArea);
            efecto.setOrigin(0, this.anchoArea / 2f);
            efecto.setRotation(angulo);
            efecto.setColor(this.colorVisual);

            mundo.agregarActor(efecto);
        }

        // 2. FÍSICA
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
                int danioTotal = this.danio + atacante.getDanio();
                victima.alterarVida(-danioTotal);
                victima.recibirEmpuje(direccion.x * FUERZA_EMPUJE, direccion.y * FUERZA_EMPUJE);

                if (!huboImpacto) {
                    huboImpacto = true;
                }
            }
        }
    }
}
