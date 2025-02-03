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
    // Le test de git 2
    // Taille de la fenêtre
    private int width = 800;
    private int height = 600;
    private long window;

    public void run() {
        init();
        loop();
        // Libération des ressources et fermeture
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Configuration du callback d'erreur
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialisation de GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Impossible d'initialiser GLFW");
        }

        // Configuration des hints de la fenêtre
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // La fenêtre restera cachée après sa création
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Création de la fenêtre
        window = glfwCreateWindow(width, height, "SimConLWJGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Échec de la création de la fenêtre GLFW");
        }

        // Configuration d'un callback clavier (ici, pour fermer la fenêtre avec la touche Échap)
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(win, true);
        });

        // Centrage de la fenêtre sur l'écran
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            glfwGetWindowSize(window, pWidth, pHeight);
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode vidmode = glfwGetVideoMode(monitor);
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Création du contexte OpenGL et activation du contexte courant
        glfwMakeContextCurrent(window);
        // Activation de la synchronisation verticale (v-sync)
        glfwSwapInterval(1);
        // Affichage de la fenêtre
        glfwShowWindow(window);
    }

    private void loop() {
        // Initialisation des capacités OpenGL
        GL.createCapabilities();

        // Configuration d'une projection orthographique pour avoir un repère en pixels
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // On définit (0,0) en haut à gauche et (width, height) en bas à droite
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // Boucle principale (équivalent à AnimationTimer en JavaFX)
        while (!glfwWindowShouldClose(window)) {
            input();
            update();
            render();

            // Échange des buffers et traitement des événements
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
        // Efface l'écran
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Dessine un carré rouge à (100, 100) de taille 200x200
        glColor3f(1.0f, 0.0f, 0.0f); // Couleur rouge

        // Utilisation du mode immédiat (attention : ce mode est obsolète en OpenGL moderne)
        glBegin(GL_QUADS);
        glVertex2f(100, 100);
        glVertex2f(300, 100);
        glVertex2f(300, 300);
        glVertex2f(100, 300);
        glEnd();
    }

    public static void main(String[] args) {
        new Simcon().run();
    }
}
