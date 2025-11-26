#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;
uniform sampler2D u_texture;
uniform float u_brightness;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords) * v_color;
    color.rgb *= u_brightness;
    gl_FragColor = color;
}
