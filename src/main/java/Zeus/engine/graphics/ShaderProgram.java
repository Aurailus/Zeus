package Zeus.engine.graphics;

import Zeus.engine.graphics.light.DirectionalLight;
import Zeus.engine.graphics.light.PointLight;
import Zeus.engine.graphics.light.SpotLight;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;


public class ShaderProgram {
    private final int programID;

    private int vertexShaderID;

    private int fragmentShaderID;

    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        programID = glCreateProgram();
        if (programID == 0) throw new Exception("Could not create Shader.");
        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programID, uniformName);
        if (uniformLocation < 0) throw new Exception("Could not find uniform " + uniformName + ".");
        uniforms.put(uniformName, uniformLocation);
    }

    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".att.constant");
        createUniform(uniformName + ".att.linear");
        createUniform(uniformName + ".att.exponent");
    }

    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        createPointLightUniform(uniformName + ".pl");
        createUniform(uniformName + ".conedir");
        createUniform(uniformName + ".cutoff");
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(name), false, fb);
        }
    }

    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    public void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }

    public void setUniform(String name, Vector3f value) {
        glUniform3f(uniforms.get(name), value.x, value.y, value.z);
    }

    public void setUniform(String name, Vector4f value) {
        glUniform4f(uniforms.get(name), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String name, PointLight[] pointLights) {
        int numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            setUniform(name, pointLights[i], i);
        }
    }

    public void setUniform(String name, PointLight pointLight, int pos) {
        setUniform(name + "[" + pos + "]", pointLight);
    }

    public void setUniform(String name, PointLight pointLight) {
        setUniform(name + ".color", pointLight.getColor());
        setUniform(name + ".position", pointLight.getPosition());
        setUniform(name + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(name + ".att.constant", att.getConstant());
        setUniform(name + ".att.linear", att.getLinear());
        setUniform(name + ".att.exponent", att.getExponent());
    }

    public void setUniform(String name, SpotLight[] spotLights) {
        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            setUniform(name, spotLights[i], 1);
        }
    }

    public void setUniform(String name, SpotLight spotLight, int pos) {
        setUniform(name + "[" + pos + "]", spotLight);
    }

    public void setUniform(String name, SpotLight spotLight) {
        setUniform(name + ".pl", spotLight.getPointLight());
        setUniform(name + ".conedir", spotLight.getConeDirection());
        setUniform(name + ".cutoff", spotLight.getCutOff());
    }

    public void setUniform(String name, DirectionalLight dirLight) {
        setUniform(name + ".color", dirLight.getColor());
        setUniform(name + ".direction", dirLight.getDirection());
        setUniform(name + ".intensity", dirLight.getIntensity());
    }

    public void setUniform(String name, Material material) {
        setUniform(name + ".ambient", material.getAmbientColor());
        setUniform(name + ".diffuse", material.getDiffuseColor());
        setUniform(name + ".specular", material.getSpecularColor());
        setUniform(name + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(name + ".reflectance", material.getReflectance());
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = glCreateShader(shaderType);
        if (shaderID == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType + ".");
        }

        glShaderSource(shaderID, shaderCode);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderID, 1024));
        }

        glAttachShader(programID, shaderID);
        return shaderID;
    }

    public void link() throws Exception {
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programID, 1024));
        }

        if (vertexShaderID != 0) {
            glDetachShader(programID, vertexShaderID);
        }
        if (fragmentShaderID != 0) {
            glDetachShader(programID, fragmentShaderID);
        }

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programID, 1024));
        }
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programID != 0) {
            glDeleteProgram(programID);
        }
    }
}
