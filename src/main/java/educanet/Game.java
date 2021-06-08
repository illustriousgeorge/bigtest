package educanet;


import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Game {
    public static float position = -0.0125f;
    public static float speed = 0.0003f;

    private final static float[] vertices = {
            0.125f, 0.125f, 0f, // 0 -> Top right
            0.125f, -0.125f, 0f, // 1 -> Bottom right
            -0.125f, -0.125f, 0f, // 2 -> Bottom left
            -0.125f, 0.125f, 0f, // 3 -> Top left
    };

    private static final float[] colors = {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
    };

    private final static int[] indices = {
            0, 1, 3, // First triangle
            1, 2, 3 // Second triangle
    };
    private static int squareVaoId;
    private static int squareVboId;
    private static int squareEboId;
    private static int colorsId;
    private static int uniformMatrixLocation;

    private static Matrix4f matrix = new Matrix4f()
            .identity();
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public static void init() {
        Shaders.initShaders();

        uniformMatrixLocation = GL33.glGetUniformLocation(Shaders.shaderProgramId, "matrix");
        squareVaoId = GL33.glGenVertexArrays();
        squareVboId = GL33.glGenBuffers();
        squareEboId = GL33.glGenBuffers();
        colorsId = GL33.glGenBuffers();

        GL33.glBindVertexArray(squareVaoId);

        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, squareEboId);
        IntBuffer ib = BufferUtils.createIntBuffer(indices.length)
                .put(indices)
                .flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareVboId);

        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length)
                .put(vertices)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        MemoryUtil.memFree(fb);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, colorsId);

        FloatBuffer cb = BufferUtils.createFloatBuffer(colors.length)
                .put(colors)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(1);

        GL33.glUseProgram(Shaders.shaderProgramId);

        // Sending Mat4 to GPU
        matrix.get(matrixBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixBuffer);

        MemoryUtil.memFree(cb);
        MemoryUtil.memFree(fb);
    }

    public static void render() {
        matrix.get(matrixBuffer);

        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixBuffer);
        GL33.glUseProgram(Shaders.shaderProgramId);
        GL33.glBindVertexArray(squareVaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, vertices.length, GL33.GL_UNSIGNED_INT, 0);
    }

    public static void update() {
        position += speed;

        if(position > 1f - 0.125f || position < -1f + 0.125f){
            speed *= -1;
        }

        matrix = matrix.translate(speed, 0f, 0f);
    }

}
