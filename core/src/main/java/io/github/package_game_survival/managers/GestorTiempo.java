package io.github.package_game_survival.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import io.github.package_game_survival.standards.LabelStandard;

public class GestorTiempo {

    private int dia;
    private int hora;
    private int minuto;
    private float acumuladorTiempo; // Cuenta cuánto falta para el siguiente minuto de juego

    private LabelStandard labelReloj; // Ahora mostrará la cuenta regresiva
    private LabelStandard labelDia;
    private Table tablaUI;

    private boolean juegoGanado = false;
    private final int DIA_FINAL = 6;

    private static final float BRILLO_MAXIMO = 1.0f;
    private static final float BRILLO_MINIMO = 0.5f;

    // Horarios
    private static final int HORA_AMANECER = 5;
    private static final int HORA_ANOCHECER = 20;

    // Tiempos reales
    private static final float DURACION_REAL_DIA = 60f;   // 1 minuto
    private static final float DURACION_REAL_NOCHE = 120f; // 2 minutos

    private static final int MINUTOS_JUEGO_DIA = (HORA_ANOCHECER - HORA_AMANECER) * 60;
    private static final int MINUTOS_JUEGO_NOCHE = (24 - (HORA_ANOCHECER - HORA_AMANECER)) * 60;

    // Cuántos segundos reales dura 1 minuto del juego
    private static final float SEG_POR_MIN_DIA = DURACION_REAL_DIA / MINUTOS_JUEGO_DIA;
    private static final float SEG_POR_MIN_NOCHE = DURACION_REAL_NOCHE / MINUTOS_JUEGO_NOCHE;

    // Transiciones visuales (Shader)
    private static final float HORA_AMANECER_INICIO = 5f;
    private static final float HORA_AMANECER_FIN = 9f;
    private static final float HORA_ATARDECER_INICIO = 18f;
    private static final float HORA_ATARDECER_FIN = 21f;

    public GestorTiempo() {
        this.dia = 1;
        this.hora = 12;
        this.minuto = 0;
        this.acumuladorTiempo = 0f;

        inicializarUI();
        actualizarTextoLabels(); // Actualizar texto inicial
    }

    private void inicializarUI() {
        labelReloj = new LabelStandard(""); // Se llena dinámicamente
        labelDia = new LabelStandard("Dia 1");

        labelReloj.setAlignment(Align.left);
        labelDia.setAlignment(Align.left);

        labelReloj.setFontScale(0.9f); // Un poco más chico para que entre el texto largo
        labelDia.setFontScale(1f);

        tablaUI = new Table();
        tablaUI.setFillParent(true);
        tablaUI.top().left();

        tablaUI.add(labelReloj).padTop(10).padLeft(20).row();
        tablaUI.add(labelDia).padLeft(20);
    }

    public void agregarAlStage(Stage stageUI) {
        stageUI.addActor(tablaUI);
    }

    public void update(float delta) {
        if (juegoGanado) return;

        float multiplicadorVelocidad = 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.T)) {
            multiplicadorVelocidad = 50.0f;
        }

        acumuladorTiempo += delta * multiplicadorVelocidad;

        float umbralMinuto = esDeNoche() ? SEG_POR_MIN_NOCHE : SEG_POR_MIN_DIA;

        while (acumuladorTiempo >= umbralMinuto) {
            acumuladorTiempo -= umbralMinuto;
            avanzarMinuto();
        }

        // Llamamos a actualizar texto en cada frame para que los segundos bajen fluidos
        // aunque no haya cambiado el minuto del juego.
        actualizarTextoLabels();
    }

    private void avanzarMinuto() {
        minuto++;
        if (minuto >= 60) {
            minuto = 0;
            hora++;
            if (hora >= 24) {
                hora = 0;
                dia++;
                verificarFinJuego();
            }
        }
    }

    // --- CAMBIO PRINCIPAL: LÓGICA DE VISUALIZACIÓN ---
    private void actualizarTextoLabels() {
        float segundosRealesRestantes;
        String estado;

        int minutosActualesEnJuego = hora * 60 + minuto;

        if (esDeNoche()) {
            // Objetivo: AMANECER (05:00) -> Minuto 300
            int minutosObjetivo = HORA_AMANECER * 60;
            int minutosFaltantesJuego;

            if (hora >= HORA_ANOCHECER) {
                // Caso: Estamos entre 20:00 y 23:59.
                // Faltan lo que queda hasta las 24:00 + las 5 horas de la madrugada
                minutosFaltantesJuego = (24 * 60 - minutosActualesEnJuego) + minutosObjetivo;
            } else {
                // Caso: Estamos entre 00:00 y 05:00.
                minutosFaltantesJuego = minutosObjetivo - minutosActualesEnJuego;
            }

            // Convertimos minutos de juego a segundos reales
            // Restamos 'acumuladorTiempo' para que el segundero sea suave y no salte
            segundosRealesRestantes = (minutosFaltantesJuego * SEG_POR_MIN_NOCHE) - acumuladorTiempo;
            estado = "Amanecer en: ";
            labelReloj.setColor(com.badlogic.gdx.graphics.Color.CYAN); // Color azulado para noche

        } else {
            // Objetivo: ANOCHECER (20:00) -> Minuto 1200
            int minutosObjetivo = HORA_ANOCHECER * 60;
            int minutosFaltantesJuego = minutosObjetivo - minutosActualesEnJuego;

            // Convertimos minutos de juego a segundos reales
            segundosRealesRestantes = (minutosFaltantesJuego * SEG_POR_MIN_DIA) - acumuladorTiempo;
            estado = "Noche en: ";
            labelReloj.setColor(com.badlogic.gdx.graphics.Color.YELLOW); // Color amarillo para día
        }

        // Formatear a MM:SS
        if (segundosRealesRestantes < 0) segundosRealesRestantes = 0; // Evitar negativos en transición
        int minDisplay = (int) (segundosRealesRestantes / 60);
        int segDisplay = (int) (segundosRealesRestantes % 60);

        String textoFinal = estado + String.format("%02d:%02d", minDisplay, segDisplay);

        labelReloj.setText(textoFinal);
        labelDia.setText("Dia " + dia);
    }

    public boolean esDeNoche() {
        return hora >= HORA_ANOCHECER || hora < HORA_AMANECER;
    }

    public void hacerDeNoche() {
        this.hora = 20;
        this.minuto = 0;
        this.acumuladorTiempo = 0; // Resetear acumulador para evitar saltos raros
        actualizarTextoLabels();
    }

    public float getFactorBrillo() {
        float horaDecimal = hora + (minuto / 60f);

        if (horaDecimal >= HORA_AMANECER_INICIO && horaDecimal < HORA_AMANECER_FIN) {
            float progreso = (horaDecimal - HORA_AMANECER_INICIO) / (HORA_AMANECER_FIN - HORA_AMANECER_INICIO);
            return MathUtils.lerp(BRILLO_MINIMO, BRILLO_MAXIMO, progreso);
        } else if (horaDecimal >= HORA_AMANECER_FIN && horaDecimal < HORA_ATARDECER_INICIO) {
            return BRILLO_MAXIMO;
        } else if (horaDecimal >= HORA_ATARDECER_INICIO && horaDecimal < HORA_ATARDECER_FIN) {
            float progreso = (horaDecimal - HORA_ATARDECER_INICIO) / (HORA_ATARDECER_FIN - HORA_ATARDECER_INICIO);
            return MathUtils.lerp(BRILLO_MAXIMO, BRILLO_MINIMO, progreso);
        } else {
            return BRILLO_MINIMO;
        }
    }

    private void verificarFinJuego() {
        if (dia >= DIA_FINAL) {
            juegoGanado = true;
        }
    }

    public boolean isJuegoGanado() { return juegoGanado; }
    public int getHora() { return hora; }
    public int getDia() { return dia; }
}
