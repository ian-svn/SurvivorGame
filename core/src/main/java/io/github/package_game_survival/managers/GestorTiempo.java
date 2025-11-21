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
    private final int DIA_FINAL = 4;

    private static final float BRILLO_MAXIMO = 1.0f;
    private static final float BRILLO_MINIMO = 0.2f;

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

        // Alineamos la tabla arriba a la izquierda
        tablaUI.top().left();

        // CAMBIO AQUÍ: Reducimos el margen izquierdo a 5 (antes era 20)
        tablaUI.add(labelReloj).padTop(10).row();
        tablaUI.add(labelDia);
    }

    public void agregarAlStage(Stage stageUI) {
        stageUI.addActor(tablaUI);
    }

    public void update(float delta) {
        if (juegoGanado) return;

        float velocidad = 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.T)) {
            velocidad = 200.0f;
        }

        acumuladorTiempo += delta * velocidad;

        while (acumuladorTiempo >= 1.0f) {
            acumuladorTiempo -= 1.0f;
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
}
