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
    private final int DIA_FINAL = 6;

    private static final float BRILLO_MAXIMO = 1.0f;
    private static final float BRILLO_MINIMO = 0.5f;
    private static final int HORA_AMANECER = 5;
    private static final int HORA_ANOCHECER = 20;

    // Tiempos
    private static final float DURACION_REAL_DIA = 60f;
    private static final float DURACION_REAL_NOCHE = 120f;
    private static final int MINUTOS_JUEGO_DIA = (HORA_ANOCHECER - HORA_AMANECER) * 60;
    private static final int MINUTOS_JUEGO_NOCHE = (24 - (HORA_ANOCHECER - HORA_AMANECER)) * 60;
    private static final float SEG_POR_MIN_DIA = DURACION_REAL_DIA / MINUTOS_JUEGO_DIA;
    private static final float SEG_POR_MIN_NOCHE = DURACION_REAL_NOCHE / MINUTOS_JUEGO_NOCHE;

    // Transiciones shader
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
        actualizarTextoLabels();
    }

    private void inicializarUI() {
        labelReloj = new LabelStandard("");
        labelDia = new LabelStandard("Dia 1");
        labelReloj.setAlignment(Align.left);
        labelDia.setAlignment(Align.left);
        labelReloj.setFontScale(0.9f);
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

        float multiplicador = Gdx.input.isKeyPressed(Input.Keys.T) ? 50.0f : 1.0f;
        acumuladorTiempo += delta * multiplicador;

        float umbral = esDeNoche() ? SEG_POR_MIN_NOCHE : SEG_POR_MIN_DIA;

        while (acumuladorTiempo >= umbral) {
            acumuladorTiempo -= umbral;
            avanzarMinuto();
        }
        actualizarTextoLabels();
    }

    private void avanzarMinuto() {
        minuto++;
        if (minuto >= 60) {
            minuto = 0;
            hora++;

            // CORREGIDO: Ganas solo si amaneces el día 6
            if (hora == HORA_AMANECER) {
                verificarFinJuego();
            }

            if (hora >= 24) {
                hora = 0;
                dia++;
            }
        }
    }

    private void actualizarTextoLabels() {
        float segRestantes;
        String estado;
        int minActual = hora * 60 + minuto;

        if (esDeNoche()) {
            int minObjetivo = HORA_AMANECER * 60;
            int minFaltantes;
            if (hora >= HORA_ANOCHECER) minFaltantes = (24 * 60 - minActual) + minObjetivo;
            else minFaltantes = minObjetivo - minActual;

            segRestantes = (minFaltantes * SEG_POR_MIN_NOCHE) - acumuladorTiempo;
            estado = "Amanecer en: ";
            labelReloj.setColor(com.badlogic.gdx.graphics.Color.CYAN);
        } else {
            int minObjetivo = HORA_ANOCHECER * 60;
            int minFaltantes = minObjetivo - minActual;
            segRestantes = (minFaltantes * SEG_POR_MIN_DIA) - acumuladorTiempo;
            estado = "Noche en: ";
            labelReloj.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
        }

        if (segRestantes < 0) segRestantes = 0;
        int dMin = (int) (segRestantes / 60);
        int dSeg = (int) (segRestantes % 60);

        labelReloj.setText(estado + String.format("%02d:%02d", dMin, dSeg));
        labelDia.setText("Dia " + dia);
    }

    public boolean esDeNoche() {
        return hora >= HORA_ANOCHECER || hora < HORA_AMANECER;
    }

    public void hacerDeNoche() {
        this.hora = 20;
        this.minuto = 0;
        this.acumuladorTiempo = 0;
        actualizarTextoLabels();
    }

    public float getFactorBrillo() {
        float h = hora + (minuto / 60f);
        if (h >= HORA_AMANECER_INICIO && h < HORA_AMANECER_FIN)
            return MathUtils.lerp(BRILLO_MINIMO, BRILLO_MAXIMO, (h - HORA_AMANECER_INICIO) / (HORA_AMANECER_FIN - HORA_AMANECER_INICIO));
        else if (h >= HORA_AMANECER_FIN && h < HORA_ATARDECER_INICIO) return BRILLO_MAXIMO;
        else if (h >= HORA_ATARDECER_INICIO && h < HORA_ATARDECER_FIN)
            return MathUtils.lerp(BRILLO_MAXIMO, BRILLO_MINIMO, (h - HORA_ATARDECER_INICIO) / (HORA_ATARDECER_FIN - HORA_ATARDECER_INICIO));
        else return BRILLO_MINIMO;
    }

    private void verificarFinJuego() {
        if (dia >= DIA_FINAL) juegoGanado = true;
    }

    public boolean isJuegoGanado() { return juegoGanado; }
    public int getHora() { return hora; }
    public int getDia() { return dia; }
}
