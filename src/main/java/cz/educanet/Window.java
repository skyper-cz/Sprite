package cz.educanet;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

import cz.educanet.Game;

import static cz.educanet.Main.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;


public class Window {
    public static void Okno() throws Exception {

        Game game = new Game();
        int current = 0;
        GLFW.glfwInit();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        long window = GLFW.glfwCreateWindow(800, 600, "Window", 0, 0);
        if (window == 0) {
            GLFW.glfwTerminate();
            throw new Exception("Can't open window");
        }
        GLFW.glfwMakeContextCurrent(window);

        GL.createCapabilities();
        GL33.glViewport(0, 0, 800, 600);

        GLFW.glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
            GL33.glViewport(0, 0, w, h);
        });
        game.init(800,600);
        while (!GLFW.glfwWindowShouldClose(window)) {
            current = (current+1)%(int) game.frames;
            // Key input management
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS)
                GLFW.glfwSetWindowShouldClose(window, true);
            GL33.glClearColor(0f, 0f, 0f, 1f);
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);

            game.render();
            game.update(current);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
        GLFW.glfwTerminate();
    }

}
