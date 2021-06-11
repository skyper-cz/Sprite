package cz.educanet;

import cz.educanet.Shaders;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Game {

    private static final float[] vertices = {
            0.5f, 0.5f, 0.0f, // 0 -> Top right
            0.5f, -0.5f, 0.0f, // 1 -> Bottom right
            -0.5f, -0.5f, 0.0f, // 2 -> Bottom left
            -0.5f, 0.5f, 0.0f, // 3 -> Top left
    };

    private static final int[] indices = {
            0, 1, 3, // First triangle
            1, 2, 3 // Second triangle
    };

    public static final float[] colors = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f,
    };

    public static final float[] snimek1 = {
            1/6f, 0.0f,
            1/6f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };

    public static final float[] snimek2 = {
            2/6f, 0.0f,
            2/6f, 1.0f,
            1/6f, 1.0f,
            1/6f, 0.0f,
    };

    public static final float[] snimek3 = {
            3/6f, 0.0f,
            3/6f, 1.0f,
            2/6f, 1.0f,
            2/6f, 0.0f,
    };

    public static final float[] snimek4 = {
            4/6f, 0.0f,
            4/6f, 1.0f,
            3/6f, 1.0f,
            3/6f, 0.0f,
    };

    public static final float[] snimek5 = {
            5/6f, 0.0f,
            5/6f, 1.0f,
            4/6f, 1.0f,
            4/6f, 0.0f,
    };

    public static final float[] snimek6 = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            5/6f, 1.0f,
            5/6f, 0.0f,
    };

    private static int squareVaoId;
    private static int squareVboId;
    private static int squareEboId;
    public static int textureIndicesId;

    private static int textureId;

    public static void init(long window) {
        // Setup shaders
        Shaders.initShaders();

        // Generate all the ids
        squareVaoId = GL33.glGenVertexArrays();
        squareVboId = GL33.glGenBuffers();
        squareEboId = GL33.glGenBuffers();
        textureIndicesId = GL33.glGenBuffers();

        textureId = GL33.glGenTextures();
        loadImage();

        // Tell OpenGL we are currently using this object (vaoId)
        GL33.glBindVertexArray(squareVaoId);

        // Tell OpenGL we are currently writing to this buffer (eboId)
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, squareEboId);
        IntBuffer ib = BufferUtils.createIntBuffer(indices.length)
                .put(indices)
                .flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

        // Change to VBOs...
        // Tell OpenGL we are currently writing to this buffer (vboId)
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareVboId);

        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length)
                .put(vertices)
                .flip();

        // Send the buffer (positions) to the GPU
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        // Clear the buffer from the memory (it's saved now on the GPU, no need for it here)
        MemoryUtil.memFree(fb);



    }

    public static void render(long window) {
        GL33.glUseProgram(Shaders.shaderProgramId);

        // Draw using the glDrawElements function
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
        GL33.glBindVertexArray(squareVaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, indices.length, GL33.GL_UNSIGNED_INT, 0);
    }
    public static double frame = 1;
    public static void update(long window) {

        float[] snimek = {};
        float[][] sets = {Game.snimek1, Game.snimek2, Game.snimek3, Game.snimek4, Game.snimek5, Game.snimek6};

        switch((int) Game.frame) {
            case 1 -> {
                snimek = snimek1;
            }
            case 2 -> {
                snimek = snimek2;
            }
            case 3 -> {
                snimek = snimek3;
            }
            case 4 -> {
                snimek = snimek4;
            }
            case 5 -> {
                snimek = snimek5;
            }
            case 6 -> {
                snimek = snimek6;
            }
        }

        if (frame < 6) {
            frame += 0.025;
        } else {
            frame = 1;
        }

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureIndicesId);
        FloatBuffer tb = BufferUtils.createFloatBuffer(snimek.length)
                .put(snimek)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, tb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);
        MemoryUtil.memFree(tb);
    }

    private static void loadImage() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer img = STBImage.stbi_load("res/img.png", w, h, comp, 3);
            if (img != null) {
                img.flip();

                GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, w.get(), h.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, img);
                GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

                STBImage.stbi_image_free(img);
            }
        }
    }

}
