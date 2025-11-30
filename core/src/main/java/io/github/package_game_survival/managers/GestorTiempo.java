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
    private float acumuladorTiempo;

    private LabelStandard labelReloj;
    private LabelStandard labelDia;
    private Table tablaUI;

    private boolean juegoGanado = false;

    // --- CAMBIO: AHORA SON 5 DÍAS JUGABLES (Ganas al llegar al día 6) ---
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

    private static final float SEG_POR_MIN_DIA = DURACION_REAL_DIA / MINUTOS_JUEGO_DIA;
    private static final float SEG_POR_MIN_NOCHE = DURACION_REAL_NOCHE / MINUTOS_JUEGO_NOCHE;

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
    }

    private void inicializarUI() {
        labelReloj = new LabelStandard("12:00");
        labelDia = new LabelStandard("Dia 1");

        labelReloj.setAlignment(Align.left);
        labelDia.setAlignment(Align.left);

        labelReloj.setFontScale(1f);
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
        actualizarTextoLabels();
    }

    public boolean esDeNoche() {
        return hora >= HORA_ANOCHECER || hora < HORA_AMANECER;
    }

    public void hacerDeNoche() {
        this.hora = 20;
        this.minuto = 0;
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

    private void actualizarTextoLabels() {
        String textoReloj = String.format("%02d:%02d", hora, minuto);
        String textoDia = "Dia " + dia;

        labelReloj.setText(textoReloj);
        labelDia.setText(textoDia);
    }

    public boolean isJuegoGanado() { return juegoGanado; }
    public int getHora() { return hora; }
    public int getDia() { return dia; }
}
