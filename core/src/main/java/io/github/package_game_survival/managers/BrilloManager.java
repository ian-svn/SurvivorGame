package io.github.package_game_survival.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class BrilloManager {

    private static ShaderProgram shaderBrillo;
    private static FrameBuffer fbo;
    private static SpriteBatch batchShader;

    private static float brillo = 1f;

    public static void inicializar() {
        ShaderProgram.pedantic = false;

        shaderBrillo = new ShaderProgram(
            Gdx.files.internal(PathManager.BRILLO_VERT),
            Gdx.files.internal(PathManager.BRILLO_FRAG)
        );

        if (!shaderBrillo.isCompiled()) {
            System.err.println("❌ Error en shader: " + shaderBrillo.getLog());
        }

        fbo = new FrameBuffer(
            Pixmap.Format.RGBA8888,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            false
        );

        batchShader = new SpriteBatch();
    }

    public static void setBrillo(float valor) {
        brillo = valor;
    }

    public static float getBrillo() {
        return brillo;
    }

    public static ShaderProgram getShader() {
        return shaderBrillo;
    }

    public static FrameBuffer getFBO() {
        return fbo;
    }

    public static SpriteBatch getBatchShader() {
        return batchShader;
    }
}
