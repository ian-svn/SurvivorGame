package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.math.Rectangle;

public class BloqueNoTransitable extends Bloque {

    public BloqueNoTransitable(float x, float y, String tipo) {
        super(x, y, tipo);
        this.transitable = true;
    }

    @Override
    public Rectangle getRectColision() {
        return new Rectangle(getX(),getY(),getAncho(),getAlto());
    }
}
