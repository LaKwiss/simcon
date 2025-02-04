package simcon;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.IntBuffer;
import java.util.Random; // Importation de la classe Random pour générer des couleurs aléatoires

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Simcon {
    // Taille de la fenêtre en pixels
    private int width = 800;
    private int height = 600;
    // Identifiant de la fenêtre GLFW
    private long window;

    // Variables pour la couleur du carré (rouge, vert, bleu)
    private float r = 1.0f, g = 0.0f, b = 0.0f;
    // Temps (en nanosecondes) du dernier changement de couleur
    private long lastColorChangeTime = System.nanoTime();
    // Générateur de nombres aléatoires pour modifier la couleur
    private Random random = new Random();

    public void run() {
        init();   // Initialise GLFW, la fenêtre et le contexte OpenGL
        loop();   // Démarre la boucle principale de rendu

        glfwFreeCallbacks(window);   // Libère les callbacks associés à la fenêtre
        glfwDestroyWindow(window);   // Détruit la fenêtre GLFW
        glfwTerminate();             // Termine GLFW
        glfwSetErrorCallback(null).free();  // Libère le callback d'erreur
    }

    private void init() {
        // Création et configuration du callback d'erreur pour afficher les erreurs sur la console
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialisation de GLFW ; si l'initialisation échoue, une exception est lancée
        if (!glfwInit()) {
            throw new IllegalStateException("Impossible d'initialiser GLFW");
        }

        glfwDefaultWindowHints();                         // Réinitialise les hints de la fenêtre aux valeurs par défaut
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);         // La fenêtre sera cachée après sa création
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);         // La fenêtre sera redimensionnable

        // Création de la fenêtre avec les dimensions et le titre spécifiés
        window = glfwCreateWindow(width, height, "SimConLWJGL", NULL, NULL);
        if (window == NULL) {  // Vérifie que la création de la fenêtre a réussi
            throw new RuntimeException("Échec de la création de la fenêtre GLFW");
        }

        // Configuration d'un callback clavier pour fermer la fenêtre lorsque l'on appuie sur Échap
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(win, true); // Demande la fermeture de la fenêtre
            }
        });

        // Centrage de la fenêtre sur l'écran
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);  // Allocation d'un buffer pour la largeur de la fenêtre
            IntBuffer pHeight = stack.mallocInt(1); // Allocation d'un buffer pour la hauteur de la fenêtre

            glfwGetWindowSize(window, pWidth, pHeight);  // Récupère les dimensions de la fenêtre

            long monitor = glfwGetPrimaryMonitor();       // Récupère le moniteur principal
            GLFWVidMode vidmode = glfwGetVideoMode(monitor); // Récupère les propriétés vidéo du moniteur

            // Calcule et positionne la fenêtre pour qu'elle soit centrée sur l'écran
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);  // Crée et active le contexte OpenGL pour la fenêtre
        glfwSwapInterval(1);             // Active la synchronisation verticale (v-sync)
        glfwShowWindow(window);          // Affiche la fenêtre
    }

    private void loop() {
        GL.createCapabilities();  // Initialise les capacités OpenGL

        // Configuration de la projection pour une vue orthographique (repère en pixels)
        glMatrixMode(GL_PROJECTION);  // Sélectionne la matrice de projection
        glLoadIdentity();             // Réinitialise la matrice de projection
        glOrtho(0, width, height, 0, -1, 1);  // Définit une projection orthographique avec (0,0) en haut à gauche
        glMatrixMode(GL_MODELVIEW);   // Sélectionne la matrice de modélisation
        glLoadIdentity();             // Réinitialise la matrice de modélisation

        // Boucle principale qui continue jusqu'à la fermeture de la fenêtre
        while (!glfwWindowShouldClose(window)) {
            input();   // Gère les entrées clavier/souris
            update();  // Met à jour la logique du programme (notamment le changement de couleur)
            render();  // Dessine la scène

            glfwSwapBuffers(window);  // Échange les buffers pour afficher l'image
            glfwPollEvents();         // Traite les événements (entrée clavier/souris, etc.)
        }
    }

    private void input() {
        // La gestion des entrées peut être ajoutée ici
    }

    private void update() {
        // Récupère le temps actuel en nanosecondes
        long currentTime = System.nanoTime();
        // Si au moins une seconde (1 000 000 000 nanosecondes) s'est écoulée depuis le dernier changement de couleur
        if (currentTime - lastColorChangeTime >= 1_000_000_000L) {
            // Génère de nouvelles valeurs aléatoires pour la couleur
            r = random.nextFloat(); // Nouvelle valeur pour le rouge (entre 0.0 et 1.0)
            g = random.nextFloat(); // Nouvelle valeur pour le vert (entre 0.0 et 1.0)
            b = random.nextFloat(); // Nouvelle valeur pour le bleu (entre 0.0 et 1.0)
            lastColorChangeTime = currentTime; // Met à jour le temps du dernier changement de couleur
        }
        // D'autres mises à jour logiques peuvent être ajoutées ici
    }

    private void render() {
        // Efface l'écran en vidant le tampon de couleur et le tampon de profondeur
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Définit la couleur actuelle pour dessiner le carré
        glColor3f(r, g, b);

        // Dessine un carré en mode immédiat (ce mode est obsolète en OpenGL moderne)
        glBegin(GL_QUADS);        // Début de la définition d'un quadrilatère
        glVertex2f(100, 100);      // Premier sommet du carré
        glVertex2f(300, 100);      // Deuxième sommet du carré
        glVertex2f(300, 300);      // Troisième sommet du carré
        glVertex2f(100, 300);      // Quatrième sommet du carré
        glEnd();                  // Fin de la définition du quadrilatère
    }

    public static void main(String[] args) {
        new Simcon().run();  // Crée une instance de Simcon et démarre l'exécution du programme
    }
}
