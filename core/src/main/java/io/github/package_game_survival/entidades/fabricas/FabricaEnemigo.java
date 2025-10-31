package io.github.package_game_survival.factories;

import io.github.package_game_survival.entidades.seres.*;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.interfaces.TipoEnemigo;

public class FabricaEnemigo {

    public static Enemigo crear(TipoEnemigo tipo, float x, float y, Escenario escenario) {
        Enemigo enemigo;

        switch (tipo) {
            case INVASOR_DE_LA_LUNA:
                enemigo = new InvasorDeLaLuna(x, y);
                break;
            case INVASOR_ARQUERO:
                enemigo = new InvasorArquero(x, y);
                break;
            case INVASOR_MAGO:
                enemigo = new InvasorMago(x, y);
                break;
            default:
                throw new IllegalArgumentException("Tipo de enemigo no reconocido: " + tipo);
        }

        enemigo.setObjetivo(escenario.getJugador());
        enemigo.setBloques(escenario.getBloques());
        escenario.getStage().addActor(enemigo);

        return enemigo;
    }
}
