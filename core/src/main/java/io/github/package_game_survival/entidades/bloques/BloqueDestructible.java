package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;

public class BloqueDestructible extends Bloque {

    private int vida = 50;
    private String objetoTirado;

    public BloqueDestructible(float x, float y, String tipo, String objetoTirado) {
        super(x, y, tipo);
        this.objetoTirado = objetoTirado;
        this.transitable = false;

        switch(objetoTirado){
            case "palo":

                break;
            case "piedra":

                break;
            case "agua":

                break;
        }
    }

    public void recibirDanio(int danio, Jugador jugador) {
        vida -= danio;
        //if (vida <= 0) destruir(jugador);
    }

    @Override
    public Rectangle getRectColision() {
        return new Rectangle(getX(),getY(),getAncho(),getAlto());
    }

//    private void destruir(Jugador jugador) {
//        Item item = crearItemDrop();
//        if (item != null) {
//            if (jugador.getInventario().tieneEspacio()) {
//                jugador.getInventario().agregarItem(item);
//            } else {
//                jugador.getEscenario().agregar(item);
//                item.setPosition(getX(), getY());
//            }
//        }
//
//        // aca podes cambiar el tile del mapa a pasto o vacio
//        jugador.getEscenario().getMapa().removerTileEn(getX(), getY());
//        removerTileEn(getX(),getY());
//    }

//    public void removerTileEn(float x, float y) {
//        int tileX = (int)(x / tileWidth);
//        int tileY = (int)(y / tileHeight);
//        mapa.getLayers().get("bloques").getObjects().remove(tileX, tileY);
//    }



}
