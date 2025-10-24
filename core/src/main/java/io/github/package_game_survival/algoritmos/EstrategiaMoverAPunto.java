package io.github.package_game_survival.algoritmos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.entidades.Bloque;
import io.github.package_game_survival.entidades.Personaje;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;

public class EstrategiaMoverAPunto implements IEstrategiaMovimiento {

    private Vector2 destino;
    private boolean terminado = false;

    public EstrategiaMoverAPunto(Vector2 destino) {
        this.destino = destino;
    }

    @Override
    public void actualizar(Personaje personaje, float delta) {
        if (terminado) return;

        Vector2 posicion = new Vector2(personaje.getCentroX(), personaje.getY());
        Vector2 direccion = destino.cpy().sub(posicion);
        float distancia = direccion.len();

        if (distancia < 3f) {
            terminado = true;
            return;
        }

        direccion.nor();
        float distanciaMovimiento = personaje.getVelocidad() * delta;

        float nextX = personaje.getX() + direccion.x * distanciaMovimiento;
        float nextY = personaje.getY() + direccion.y * distanciaMovimiento;

        boolean colision = hayColision(nextX, nextY, personaje);
        if (colision) {
            boolean esquivado = false;
            for (int angulo = -90; angulo <= 90; angulo += 15) {
                Vector2 desviada = direccion.cpy().rotateDeg(angulo);
                float desX = personaje.getX() + desviada.x * distanciaMovimiento;
                float desY = personaje.getY() + desviada.y * distanciaMovimiento;
                if (!hayColision(desX, desY, personaje)) {
                    personaje.setPosition(desX, desY);
                    esquivado = true;
                    break;
                }
            }
            if (!esquivado) return;
        } else {
            personaje.setPosition(nextX, nextY);
        }
    }

    /**
     * Comprueba colisión usando una copia temporal del rectángulo de bounds del personaje,
     * posicionada en (nextX,nextY). Nunca modifica la hitbox real del personaje.
     */
    private boolean hayColision(float nextX, float nextY, Personaje personaje) {
        if (personaje.getStage() == null) return false;

        // Copia la hitbox actual para no modificar la real
        Rectangle testRect = new Rectangle(personaje.getBounds());
        testRect.setPosition(nextX, nextY);

        for (Actor actor : personaje.getStage().getActors()) {
            if (actor instanceof Bloque bloque && !bloque.atravesable) {
                if (testRect.overlaps(bloque.getBounds())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean haTerminado(Personaje personaje) {
        return terminado;
    }

    public void setDestino(Vector2 vector2) {
        this.destino = vector2;
        this.terminado = false;
    }
}
