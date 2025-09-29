package io.github.package_game_survival.managers.Audio;

public interface AudioService {
    void playSound(String soundId);
    void playMusic(String musicId, boolean looping);
    void stopMusic();
    void setVolume(float volume);
}
