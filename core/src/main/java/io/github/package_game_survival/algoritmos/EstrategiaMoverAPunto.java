package io.github.package_game_survival.algoritmos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;

public class EstrategiaMoverAPunto implements IEstrategiaMovimiento {

    private final Vector2 destino = new Vector2();
    private final Array<Bloque> obstaculos;
    private boolean terminado = false;

    // --- Variables Temporales para evitar Garbage Collection ---
    private final Vector2 tempPos = new Vector2();
    private final Vector2 tempDir = new Vector2();
    private final Rectangle tempRect = new Rectangle();

    public EstrategiaMoverAPunto(Vector2 destino, Array<Bloque> obstaculos) {
        this.destino.set(destino);
        this.obstaculos = obstaculos;
    }

    @Override
    public void actualizar(SerVivo serVivo, float delta) {
        if (terminado) return;

        tempPos.set(serVivo.getX(), serVivo.getY());

        // Calculamos vector dirección hacia el destino
        // Usamos el centro del SerVivo para calcular mejor la trayectoria
        float centroX = serVivo.getX() + serVivo.getAncho() / 2;
        float centroY = serVivo.getY() + serVivo.getAlto() / 2;

        tempDir.set(destino.x - centroX, destino.y - centroY);

        float distancia = tempDir.len();

        // Si está muy cerca, terminamos (reducido a 2.5f para mayor precisión)
        if (distancia < 2.5f) {
            terminado = true;
            return;
        }

        tempDir.nor(); // Normalizar
        float distanciaMovimiento = serVivo.getVelocidad() * delta;

        // Calculamos el movimiento deseado total
        float moveX = tempDir.x * distanciaMovimiento;
        float moveY = tempDir.y * distanciaMovimiento;

        float nextX = serVivo.getX() + moveX;
        float nextY = serVivo.getY() + moveY;

        // --- LÓGICA DE DESLIZAMIENTO (SLIDING) ---

        // 1. Intentar movimiento completo (Diagonal)
        if (!hayColision(nextX, nextY, serVivo)) {
            serVivo.setPosition(nextX, nextY);
        }
        else {
            // 2. Si falla, intentar solo eje X (Deslizar horizontalmente)
            boolean posibleX = !hayColision(nextX, serVivo.getY(), serVivo);

            // 3. Si falla, intentar solo eje Y (Deslizar verticalmente)
            boolean posibleY = !hayColision(serVivo.getX(), nextY, serVivo);

            if (posibleX) {
                serVivo.setPosition(nextX, serVivo.getY());
            } else if (posibleY) {
                serVivo.setPosition(serVivo.getX(), nextY);
            } else {
                // Si está totalmente bloqueado, no se mueve, pero no vibra.
                // Opcional: Podrías marcar 'terminado = true' si se queda atascado mucho tiempo
            }
        }
    }

    private boolean hayColision(float nextX, float nextY, SerVivo serVivo) {
        if (obstaculos == null || obstaculos.isEmpty()) return false;

        // Actualizamos el rectángulo temporal en la posición futura
        // Nota: Es importante usar el mismo tamaño de hitbox que usa el personaje
        tempRect.set(nextX, nextY, serVivo.getAncho(), serVivo.getAlto() / 2);

        // Iteración optimizada
        for (int i = 0; i < obstaculos.size; i++) {
            Bloque bloque = obstaculos.get(i);

            if (bloque.isTransitable()) continue;

            if (tempRect.overlaps(bloque.getRectColision())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean haTerminado(SerVivo serVivo) {
        return terminado;
    }

    public void setDestino(float x, float y) {
        this.destino.set(x, y);
        this.terminado = false;
    }

    public void setDestino(Vector2 nuevoDestino) {
        this.destino.set(nuevoDestino);
        this.terminado = false;
    }
}
