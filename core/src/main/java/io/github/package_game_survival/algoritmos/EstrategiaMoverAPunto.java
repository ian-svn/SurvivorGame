package io.github.package_game_survival.algoritmos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;

public class EstrategiaMoverAPunto implements IEstrategiaMovimiento {

    private Vector2 destino;
    private boolean terminado = false;

    public EstrategiaMoverAPunto(Vector2 destino) {
        this.destino = destino;
    }

    @Override
    public void actualizar(SerVivo serVivo, float delta) {
        if (terminado) return;

        Vector2 posicion = new Vector2(serVivo.getCentroX(), serVivo.getY());
        Vector2 direccion = destino.cpy().sub(posicion);
        float distancia = direccion.len();

        if (distancia < 3f) {
            terminado = true;
            return;
        }

        direccion.nor();
        float distanciaMovimiento = serVivo.getVelocidad() * delta;

        float nextX = serVivo.getX() + direccion.x * distanciaMovimiento;
        float nextY = serVivo.getY() + direccion.y * distanciaMovimiento;

        boolean colision = hayColision(nextX, nextY, serVivo);
        if (colision) {
            boolean esquivado = false;
            for (int angulo = -90; angulo <= 90; angulo += 15) {
                Vector2 desviada = direccion.cpy().rotateDeg(angulo);
                float desX = serVivo.getX() + desviada.x * distanciaMovimiento;
                float desY = serVivo.getY() + desviada.y * distanciaMovimiento;
                if (!hayColision(desX, desY, serVivo)) {
                    serVivo.setPosition(desX, desY);
                    esquivado = true;
                    break;
                }
            }
            if (!esquivado) return;
        } else {
            serVivo.setPosition(nextX, nextY);
        }
    }

    /**
     * Comprueba colisión usando una copia temporal del rectángulo de bounds del personaje,
     * posicionada en (nextX,nextY). Nunca modifica la hitbox real del personaje.
     */
    private boolean hayColision(float nextX, float nextY, SerVivo serVivo) {
        if (serVivo.getStage() == null) return false;

        // Copia la hitbox actual para no modificar la real
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

    public void setDestino(Vector2 vector2) {
        this.destino = vector2;
        this.terminado = false;
    }

    public Vector2 getX(){
        return Vector2.X;
    }

    public Vector2 getY(){
        return Vector2.Y;
    }
}
