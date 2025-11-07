package io.github.package_game_survival.algoritmos;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;

import java.util.Random;

public class EstrategiaMoverAleatorio implements IEstrategiaMovimiento {

    private float direccionX = 1;
    private float direccionY = 0;
    private float tiempoCambio = 0;
    private float tiempoObjetivo = 2f;
    private boolean terminado = false;
    private final Random random = new Random();

    @Override
    public void actualizar(SerVivo serVivo, float delta) {
        if (terminado) return;

        tiempoCambio += delta;

        // Cambiar direccion cada ciertos segundos
        if (tiempoCambio >= tiempoObjetivo) {
            tiempoCambio = 0;
            tiempoObjetivo = 1.5f + random.nextFloat() * 2f; // entre 1.5 y 3.5 seg

            int decisionX = random.nextInt(3) - 1; // -1, 0 o 1
            int decisionY = random.nextInt(3) - 1; // -1, 0 o 1

            // Evitar quedarse completamente quieto siempre
            if (decisionX == 0 && decisionY == 0 && random.nextBoolean()) {
                decisionX = random.nextBoolean() ? 1 : -1;
            }

            direccionX = decisionX;
            direccionY = decisionY;
        }

        // Si no se mueve, no hacer nada
        if (direccionX == 0 && direccionY == 0) return;

        float distanciaMovimiento = serVivo.getVelocidad() * delta;
        float nextX = serVivo.getX() + direccionX * distanciaMovimiento;
        float nextY = serVivo.getY() + direccionY * distanciaMovimiento;

        boolean colision = hayColision(nextX, nextY, serVivo);

        if (colision) {
            // Probar invertir direcciones según el eje que causa colisión
            boolean colisionX = hayColision(serVivo.getX() + direccionX * distanciaMovimiento, serVivo.getY(), serVivo);
            boolean colisionY = hayColision(serVivo.getX(), serVivo.getY() + direccionY * distanciaMovimiento, serVivo);

            if (colisionX) direccionX *= -1;
            if (colisionY) direccionY *= -1;

            return; // no avanzar este ciclo
        }

        serVivo.setPosition(nextX, nextY);
    }

    private boolean hayColision(float nextX, float nextY, SerVivo serVivo) {
        if (serVivo.getStage() == null) return false;

        Rectangle testRect = new Rectangle(serVivo.getRectColision());
        testRect.setPosition(nextX, nextY);

        for (Actor actor : serVivo.getStage().getActors()) {
            if (actor instanceof Bloque bloque && !bloque.transitable) {
                if (testRect.overlaps(bloque.getRectColision())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean haTerminado(SerVivo serVivo) {
        return terminado;
    }
}
