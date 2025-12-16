package io.github.package_game_survival.managers.Audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class AudioControler implements AudioService {

    private final Map<String, Sound> sounds = new HashMap<>();
    private final Map<String, Music> musics = new HashMap<>();
    private Music currentMusic;

    public void loadSound(String id, String filePath) {
        sounds.put(id, Gdx.audio.newSound(Gdx.files.internal(filePath)));
    }

    public void loadMusic(String id, String filePath) {
        musics.put(id, Gdx.audio.newMusic(Gdx.files.internal(filePath)));
    }

    @Override
    public void playSound(String soundId) {
        Sound sound = sounds.get(soundId);
        if (sound != null) {
            sound.play(1.0f);
        }
    }

    @Override
    public void playMusic(String musicId, boolean looping) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = musics.get(musicId);
        if (currentMusic != null) {
            currentMusic.setLooping(looping);
            currentMusic.play();
        }
    }

    public void stopSound(String nombre) {
        Sound sonido = sounds.get(nombre);
        if (sonido != null) {
            sonido.stop();
        }
    }

    public long loopSound(String nombre) {
        Sound sonido = sounds.get(nombre);
        if (sonido != null) {
            // .loop() inicia el sonido y lo repite automáticamente
            return sonido.loop();
        }
        return -1;
    }

    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
    }

    public void changeMusic(String musicId, String filePath, boolean looping){
        currentMusic.stop();
        loadMusic(musicId, filePath);
        playMusic(musicId, looping);
    }

    @Override
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    @Override
    public void setVolume(float volume) {
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
    }
}
