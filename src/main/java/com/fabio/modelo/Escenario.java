package com.fabio.modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa el escenario del juego.
 */
public class Escenario {
    private Celda[][] celdas;
    private List<Enemigo> enemigos;
    private Protagonista protagonista;
    private int ancho;
    private int alto;

    /**
     * Constructor de la clase Escenario.
     * 
     * @param archivoEscenario Ruta al archivo con el diseño del escenario.
     * @param archivoEnemigos  Ruta al archivo con los datos de los enemigos.
     * @throws IOException Si ocurre un error al leer los archivos.
     */
    public Escenario(String rutaEscenario, String rutaEnemigos) throws IOException {
        try (InputStream escenarioStream = getClass().getResourceAsStream(rutaEscenario);
                InputStream enemigosStream = getClass().getResourceAsStream(rutaEnemigos)) {

            if (escenarioStream == null || enemigosStream == null) {
                throw new IOException("No se pudieron cargar los recursos: " + rutaEscenario + " o " + rutaEnemigos);
            }

            cargarEscenario(escenarioStream);
            cargarEnemigos(enemigosStream);
        }
    }

    private void cargarEscenario(InputStream archivo) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(archivo));

        List<String> lineas = new ArrayList<>();

        String linea;
        while ((linea = br.readLine()) != null) {
            lineas.add(linea);
        }
        br.close();

        alto = lineas.size();
        ancho = lineas.get(0).length();
        celdas = new Celda[ancho][alto];

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                char c = lineas.get(y).charAt(x);
                celdas[x][y] = new Celda(c == '#');
            }
        }
    }

    private void cargarEnemigos(InputStream archivo) throws IOException {
        enemigos = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(archivo));

        String linea;
        while ((linea = br.readLine()) != null) {
            String[] datos = linea.split(",");
            String tipo = datos[0];
            int salud = Integer.parseInt(datos[1]);
            int ataque = Integer.parseInt(datos[2]);
            int defensa = Integer.parseInt(datos[3]);
            int velocidad = Integer.parseInt(datos[4]);
            int percepcion = Integer.parseInt(datos[5]);
            int x = Integer.parseInt(datos[6]);
            int y = Integer.parseInt(datos[7]);

            Enemigo enemigo = new Enemigo(tipo, salud, ataque, defensa, velocidad, percepcion, x, y);
            enemigos.add(enemigo);
            celdas[x][y].setPersonaje(enemigo);
        }
        br.close();
    }

    // Getters
    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public Celda getCelda(int x, int y) {
        return celdas[x][y];
    }

    public List<Enemigo> getEnemigos() {
        return enemigos;
    }

    public Protagonista getProtagonista() {
        return protagonista;
    }

    public void setProtagonista(Protagonista protagonista) {
        this.protagonista = protagonista;
        celdas[protagonista.getPosX()][protagonista.getPosY()].setPersonaje(protagonista);
    }

    /**
     * Verifica si una posición está dentro de los límites del escenario.
     * 
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @return true si la posición es válida, false en caso contrario.
     */
    public boolean estaDentroDelEscenario(int x, int y) {
        return x >= 0 && x < ancho && y >= 0 && y < alto;
    }

    /**
     * Obtiene el personaje en una posición específica.
     * 
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @return El personaje en esa posición, o null si no hay ninguno.
     */
    public Personaje getPersonajeEn(int x, int y) {
        return celdas[x][y].getPersonaje();
    }

    /**
     * Mueve un personaje a una nueva posición.
     * 
     * @param personaje Personaje a mover.
     * @param nuevaX    Nueva coordenada X.
     * @param nuevaY    Nueva coordenada Y.
     */
    public void moverPersonaje(Personaje personaje, int nuevaX, int nuevaY) {
        // Quitar personaje de su posición actual
        celdas[personaje.getPosX()][personaje.getPosY()].quitarPersonaje();

        // Actualizar posición del personaje
        personaje.setPosicion(nuevaX, nuevaY);

        // Colocar personaje en la nueva posición
        celdas[nuevaX][nuevaY].setPersonaje(personaje);
    }

    /**
     * Elimina un enemigo del escenario.
     * 
     * @param enemigo Enemigo a eliminar.
     */
    public void eliminarEnemigo(Enemigo enemigo) {
        celdas[enemigo.getPosX()][enemigo.getPosY()].quitarPersonaje();
        enemigos.remove(enemigo);
    }
}