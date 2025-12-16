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

    private final Vector2 tempDir = new Vector2();
    private final Rectangle tempRect = new Rectangle();

    public EstrategiaMoverAPunto(Vector2 destino, Array<Bloque> obstaculos) {
        this.destino.set(destino);
        this.obstaculos = obstaculos;
    }

    @Override
    public void actualizar(SerVivo serVivo, float delta) {
        if (terminado) return;

        // Vector hacia destino desde el CENTRO del personaje (más preciso)
        float centroX = serVivo.getX() + serVivo.getAncho() / 2;
        float centroY = serVivo.getY() + serVivo.getAlto() / 2;

        tempDir.set(destino.x - centroX, destino.y - centroY);

        // Distancia mínima para considerar que llegó (evita vibración final)
        if (tempDir.len() < 3.0f) {
            terminado = true;
            return;
        }

        tempDir.nor();
        float distanciaMovimiento = serVivo.getVelocidad() * delta;

        float moveX = tempDir.x * distanciaMovimiento;
        float moveY = tempDir.y * distanciaMovimiento;

        float nextX = serVivo.getX() + moveX;
        float nextY = serVivo.getY() + moveY;

        // 1. Intento mover en diagonal
        if (!hayColision(nextX, nextY, serVivo)) {
            serVivo.setPosition(nextX, nextY);
        }
        else {
            // SLIDING (Deslizamiento)

            // 2. Intento mover solo en X
            boolean puedeEnX = !hayColision(nextX, serVivo.getY(), serVivo);

            // 3. Intento mover solo en Y
            boolean puedeEnY = !hayColision(serVivo.getX(), nextY, serVivo);

            if (puedeEnX) {
                serVivo.setPosition(nextX, serVivo.getY());
            } else if (puedeEnY) {
                serVivo.setPosition(serVivo.getX(), nextY);
            } else {
                // Bloqueado total. No nos movemos, pero NO cancelamos la estrategia.
                // El jugador podría deslizarse si cambia ligeramente el ángulo.
            }
        }
    }

    private boolean hayColision(float nextX, float nextY, SerVivo serVivo) {
        if (obstaculos == null || obstaculos.isEmpty()) return false;

        // IMPORTANTE: Usamos getRectColision() del SerVivo, que ahora es 30x30
        Rectangle hitbox = serVivo.getRectColision();
        tempRect.set(nextX, nextY, hitbox.width, hitbox.height);

        // Ajuste de posición: La hitbox del jugador es relativa a su sprite.
        // getRectColision() devuelve la caja YA posicionada.
        // Pero aquí queremos probar una posición FUTURA.
        // El offset X es (anchoSprite - anchoHitbox) / 2
        float offsetX = (serVivo.getWidth() - hitbox.width) / 2;
        float offsetY = 0; // Hitbox en los pies

        tempRect.setPosition(nextX + offsetX, nextY + offsetY);

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

    public void setDestino(Vector2 nuevoDestino) {
        this.destino.set(nuevoDestino);
        this.terminado = false;
    }
}
