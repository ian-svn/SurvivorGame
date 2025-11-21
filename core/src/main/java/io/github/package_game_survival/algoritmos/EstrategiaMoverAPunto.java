package io.github.package_game_survival.algoritmos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array; // Usamos Array de LibGDX
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.interfaces.IEstrategiaMovimiento;

public class EstrategiaMoverAPunto implements IEstrategiaMovimiento {

    private final Vector2 destino = new Vector2();
    private final Array<Bloque> obstaculos; // Referencia a la lista optimizada
    private boolean terminado = false;

    // --- Variables Temporales para evitar Garbage Collection (Punto 3) ---
    private final Vector2 tempPos = new Vector2();
    private final Vector2 tempDir = new Vector2();
    private final Vector2 tempDesviada = new Vector2();
    private final Rectangle tempRect = new Rectangle();

    /**
     * @param destino Coordenada a la que ir.
     * @param obstaculos Lista de bloques (se debe pasar desde el Escenario o Jugador) para evitar iterar todo el stage.
     */
    public EstrategiaMoverAPunto(Vector2 destino, Array<Bloque> obstaculos) {
        this.destino.set(destino); // Copiamos valores, no referencias
        this.obstaculos = obstaculos;
    }

    @Override
    public void actualizar(SerVivo serVivo, float delta) {
        if (terminado) return;

        // 1. Usamos vectores temporales en lugar de 'new Vector2'
        tempPos.set(serVivo.getCentroX(), serVivo.getY());

        // Calculamos dirección: Destino - Posición
        tempDir.set(destino).sub(tempPos);

        float distancia = tempDir.len();

        if (distancia < 3f) {
            terminado = true;
            return;
        }

        tempDir.nor(); // Normalizamos el vector reutilizado
        float distanciaMovimiento = serVivo.getVelocidad() * delta;

        // Calculamos siguiente posición teórica
        float nextX = serVivo.getX() + tempDir.x * distanciaMovimiento;
        float nextY = serVivo.getY() + tempDir.y * distanciaMovimiento;

        // 2. Verificación de colisión optimizada
        if (hayColision(nextX, nextY, serVivo)) {
            boolean esquivado = false;

            // Algoritmo de evasión simple
            for (int angulo = -90; angulo <= 90; angulo += 15) {
                if (angulo == 0) continue; // Ya probamos 0 grados arriba

                // Reutilizamos tempDesviada en lugar de crear copias
                tempDesviada.set(tempDir).rotateDeg(angulo);

                float desX = serVivo.getX() + tempDesviada.x * distanciaMovimiento;
                float desY = serVivo.getY() + tempDesviada.y * distanciaMovimiento;

                if (!hayColision(desX, desY, serVivo)) {
                    serVivo.setPosition(desX, desY);
                    esquivado = true;
                    break;
                }
            }
            if (!esquivado) {
                // Si no puede moverse ni esquivar, a veces es mejor terminar o esperar
                // Por ahora, simplemente no se mueve.
            }
        } else {
            serVivo.setPosition(nextX, nextY);
        }
    }

    /**
     * Comprueba colisión iterando SOLO sobre los obstáculos (Punto 4).
     * Reutiliza tempRect para no generar basura en memoria (Punto 3).
     */
    private boolean hayColision(float nextX, float nextY, SerVivo serVivo) {
        if (obstaculos == null || obstaculos.isEmpty()) return false;

        // Actualizamos el rectángulo temporal con las dimensiones del ser vivo y la nueva posición
        tempRect.set(nextX, nextY, serVivo.getAncho(), serVivo.getAlto() / 2); // Asumiendo hitbox de mitad de altura como tenías

        // Iteración optimizada sobre Array de LibGDX
        for (int i = 0; i < obstaculos.size; i++) {
            Bloque bloque = obstaculos.get(i);

            // Check rápido: si es transitable, saltar
            if (bloque.isTransitable()) continue;

            // Check de superposición
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

    // ... código existente ...

    // Método original (mantenlo si quieres)
    public void setDestino(Vector2 nuevoDestino) {
        this.destino.set(nuevoDestino);
        this.terminado = false;
    }

    // --- AGREGA ESTE MÉTODO NUEVO ---
    // Permite actualizar el destino sin crear un 'new Vector2()'
    public void setDestino(float x, float y) {
        this.destino.set(x, y);
        this.terminado = false;
    }
}
