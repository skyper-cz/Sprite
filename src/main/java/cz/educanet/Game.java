package cz.educanet;


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
    float imgWidth = 288;
    float imgHeight = 48;
    float imgSize = 2;
    float frames = 6;

    private FloatBuffer tb1 = BufferUtils.createFloatBuffer(8);

    private float[] vertices;

    private final float[] colors = {
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
    };

    private final int[] indices = {
            0, 1, 3, // First triangle
            1, 2, 3 // Second triangle
    };
    private float[] textures = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };
    private int squareVaoId;
    private int squareVboId;
    private int squareEboId;
    private int colorsId;
    private int textureIndicesId;
    private int uniformColorLocation;
    private int uniformMatrixLocation;
    private int textureId;

    private Matrix4f matrix = new Matrix4f()
            .identity();
    private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public void setVertices(float width,float height){
        float x = -imgWidth*imgSize/width;
        float y = imgHeight*imgSize/height;
        float sizeX = 2*Math.abs(x);
        float sizeY = 2*Math.abs(y);
        System.out.println(x + " : " + y+" : " +sizeX+ " : "+sizeY);
        vertices = new float[]{

                x + sizeX, y, 0.0f, //right top
                x + sizeX, y - sizeY, 0.0f, //right bottom
                x, y - sizeY, 0.0f, //left bottom
                x, y, 0.0f,//left top
        };
    }

    public void init(int width,int height) {
        setVertices(width*frames, height);
        Shaders.initShaders();
        loadImage();

        uniformColorLocation = GL33.glGetUniformLocation(Shaders.shaderProgramId, "outColor");
        uniformMatrixLocation = GL33.glGetUniformLocation(Shaders.shaderProgramId, "matrix");
        squareVaoId = GL33.glGenVertexArrays();
        squareVboId = GL33.glGenBuffers();
        squareEboId = GL33.glGenBuffers();
        colorsId = GL33.glGenBuffers();
        textureIndicesId = GL33.glGenBuffers();
        textureId = GL33.glGenTextures();

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


        GL33.glUseProgram(Shaders.shaderProgramId);
        GL33.glUniform3f(uniformColorLocation, 1.0f, 0.0f, 0.0f);

        matrix.get(matrixBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixBuffer);

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0,3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, colorsId);

        FloatBuffer cb = BufferUtils.createFloatBuffer(colors.length)
                .put(colors)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(1,3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(1);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureIndicesId);

        FloatBuffer tb = BufferUtils.createFloatBuffer(textures.length)
                .put(textures)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, tb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);

        MemoryUtil.memFree(fb);
    }

    public void render() {
        matrix.get(matrixBuffer);

        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixBuffer);
        GL33.glUseProgram(Shaders.shaderProgramId);
        GL33.glBindVertexArray(squareVaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, vertices.length, GL33.GL_UNSIGNED_INT, 0);
    }

    public void update(float frame) {
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureIndicesId);
        tb1.clear().put(textures).flip();
        float leftX = frame/frames;
        float rightX = (frame+1)/frames;

        textures = new float[] {
                rightX, 0.0f,// 0 -> Top right - tohle se musi menit kdyz chce jinou cast obrazku
                rightX, 1.0f,// 1 -> Bottom right
                leftX, 1.0f,// 2 -> Bottom left
                leftX, 0.0f,// 3 -> Top left
        };


        // Send the buffer (positions) to the GPU
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, tb1, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);
    }

    public void loadImage() {
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
