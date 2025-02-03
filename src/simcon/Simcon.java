package simcon;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Simcon {
    private int width = 800;
    private int height = 600;
    private long window;

    public void run() {
        init();
        loop();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Impossible d'initialiser GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "SimConLWJGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Échec de la création de la fenêtre GLFW");
        }

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(win, true);
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode vidmode = glfwGetVideoMode(monitor);
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();

        // Activer le test de profondeur
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        // Configuration de la projection en perspective
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = (float) width / height;
        float fov = 45.0f;
        float near = 0.1f;
        float far = 100.0f;
        float top = (float) Math.tan(Math.toRadians(fov / 2)) * near;
        float bottom = -top;
        float left = bottom * aspectRatio;
        float right = top * aspectRatio;
        glFrustum(left, right, bottom, top, near, far);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        while (!glfwWindowShouldClose(window)) {
            input();
            update();
            render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void input() {
        // Gestion des entrées clavier/souris ici
    }

    private void update() {
        // Logique de mise à jour ici
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        // Déplacer la caméra en arrière pour voir le cube
        glTranslatef(0.0f, 0.0f, -5.0f);

        glRotatef(65.0f, 1.0f, 1.0f, 0.0f);

        glColor3f(1.0f, 0.0f, 0.0f); // Cube rouge

        glBegin(GL_QUADS);

        // Face avant
        glVertex3f(-1.0f, -1.0f,  1.0f);
        glVertex3f( 1.0f, -1.0f,  1.0f);
        glVertex3f( 1.0f,  1.0f,  1.0f);
        glVertex3f(-1.0f,  1.0f,  1.0f);

        // Face arrière
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f,  1.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f, -1.0f);
        glVertex3f( 1.0f, -1.0f, -1.0f);

        // Face gauche
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, -1.0f,  1.0f);
        glVertex3f(-1.0f,  1.0f,  1.0f);
        glVertex3f(-1.0f,  1.0f, -1.0f);

        // Face droite
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f,  1.0f, -1.0f);
        glVertex3f(1.0f,  1.0f,  1.0f);
        glVertex3f(1.0f, -1.0f,  1.0f);

        // Face dessus
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f,  1.0f);
        glVertex3f( 1.0f, 1.0f,  1.0f);
        glVertex3f( 1.0f, 1.0f, -1.0f);

        // Face dessous
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f( 1.0f, -1.0f, -1.0f);
        glVertex3f( 1.0f, -1.0f,  1.0f);
        glVertex3f(-1.0f, -1.0f,  1.0f);

        glEnd();
    }

    public static void main(String[] args) {
        new Simcon().run();
    }
}
