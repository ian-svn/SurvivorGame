package io.github.package_game_survival.habilidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.efectos.EfectoVisual;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.interfaces.IMundoJuego;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.Audio.AudioManager;
import io.github.package_game_survival.managers.PathManager;

public class AtaqueAranazo extends AtaqueBase {

    private float anchoArea;
    private final Color colorVisual;

    // Guardamos la geometría calculada al inicio para usarla al final
    private final Polygon areaGolpeSnapshot;
    private final Vector2 direccionEmpuje = new Vector2();

    // Variables para guardar dónde pintar el efecto al final
    private float xVisual, yVisual, rotacionVisual;

    public AtaqueAranazo(float cooldown, float duracionCast, int danio, float rango,
                         float ancho, Class<? extends SerVivo> objetivo, Color color) {
        super(cooldown, duracionCast, danio, rango, objetivo);

        this.anchoArea = ancho;
        this.colorVisual = color;
        this.areaGolpeSnapshot = new Polygon();

        recalcularDimensionesPoligono();
    }

    public void aumentarRango(float r) { this.rango += r; recalcularDimensionesPoligono(); }
    public void aumentarArea(float a) { this.anchoArea += a; recalcularDimensionesPoligono(); }

    private void recalcularDimensionesPoligono() {
        float[] vertices = new float[] {
            0, -anchoArea/2,
            rango, -anchoArea/2,
            rango, anchoArea/2,
            0, anchoArea/2
        };
        areaGolpeSnapshot.setVertices(vertices);
        areaGolpeSnapshot.setOrigin(0, 0);
    }

    // 1. AL HACER CLICK: Solo calculamos y bloqueamos el destino (Snapshot)
    @Override
    protected void onInicioCasteo(SerVivo atacante, Vector2 destino, IMundoJuego mundo) {
        float cx = atacante.getCentroX();
        float cy = atacante.getCentroY();

        Vector2 dir = new Vector2(destino).sub(cx, cy).nor();
        float angulo = dir.angleDeg();

        // Guardamos vector de empuje
        this.direccionEmpuje.set(dir);

        // Guardamos la hitbox física (Invisible por ahora)
        areaGolpeSnapshot.setPosition(cx, cy);
        areaGolpeSnapshot.setRotation(angulo);

        // Guardamos datos para pintar el efecto LUEGO (cuando termine el tiempo)
        this.xVisual = cx;
        this.yVisual = cy;
        this.rotacionVisual = angulo;

        // Opcional: Sonido de "preparación" o "carga" aquí si quisieras
    }

    // 2. AL TERMINAR EL TIEMPO: ¡ZAS! Visual + Daño juntos
    @Override
    protected void ejecutarGolpe(SerVivo atacante, Vector2 destino, IMundoJuego mundo) {
        // AHORA mostramos el efecto visual (Flash instantáneo)
        spawnEfectoVisual(mundo);

        // Sonido de impacto
        try { AudioManager.getControler().playSound("ataque"); } catch (Exception e) {}

        Array<Actor> actores = mundo.getStageMundo().getActors();

        for (Actor a : actores) {
            if (a instanceof SerVivo) {
                SerVivo victima = (SerVivo) a;

                if (victima == atacante) continue;
                if (victima.getVida() <= 0) continue;

                if (claseObjetivo.isInstance(victima)) {
                    // Usamos el área que calculamos AL PRINCIPIO
                    if (Intersector.overlapConvexPolygons(areaGolpeSnapshot, rectToPoly(victima.getRectColision()))) {

                        int danioTotal = this.danioBase + atacante.getDanio();
                        victima.alterarVida(-danioTotal);
                        victima.recibirEmpuje(direccionEmpuje.x * 20f, direccionEmpuje.y * 20f);
                    }
                }
            }
        }
    }

    private void spawnEfectoVisual(IMundoJuego mundo) {
        TextureAtlas atlas = Assets.get(PathManager.ARANAZO_ANIMATION, TextureAtlas.class);
        if (atlas != null) {
            // El efecto dura poco (0.15s) para que se sienta como un golpe rápido
            EfectoVisual efecto = new EfectoVisual(atlas, "aranazo", 0.05f, false);

            // Usamos las coordenadas guardadas al inicio
            efecto.setPosition(xVisual, yVisual - (anchoArea / 2f));
            efecto.setOrigin(0, anchoArea / 2f);
            efecto.setSize(rango, anchoArea);
            efecto.setRotation(rotacionVisual);
            efecto.setColor(this.colorVisual);

            mundo.agregarActor(efecto);
        }
    }

    private Polygon rectToPoly(Rectangle r) {
        float[] v = new float[] {
            r.x, r.y,
            r.x + r.width, r.y,
            r.x + r.width, r.y + r.height,
            r.x, r.y + r.height
        };
        return new Polygon(v);
    }

    public float getAncho() { return anchoArea; }
}
