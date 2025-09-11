package io.github.package_game_survival.managers.Audio;

public final class AudioManager {

    private static AudioManager instance;
    private static AudioControler audioControler;

    private AudioManager(){
        audioControler = new AudioControler();
    }

    public static AudioControler getControler(){
        return getInstance().getAudioControler();
    }

    public static AudioControler getAudioControler() {
        return audioControler;
    }

    public static AudioManager getInstance() {
        if(instance == null){
            instance = new AudioManager();
        }
        return instance;
    }
}
