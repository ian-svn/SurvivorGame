package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.math.Rectangle;

public class BloqueNoTransitable extends Bloque {

    public BloqueNoTransitable(float x, float y, String tipo) {
        super(x, y, tipo);
        // CORRECCIÓN: Si es "NoTransitable", esto debería ser false
        this.transitable = false;
    }

    // Ya no hace falta sobrescribir getRectColision aquí si el padre (Bloque)
    // ya lo hace de forma optimizada, pero si quieres mantenerlo:
    @Override
    public Rectangle getRectColision() {
        return super.getRectColision();
    }
}
