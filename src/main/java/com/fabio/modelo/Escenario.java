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
     * @param rutaEscenario Ruta al archivo con el diseño del escenario.
     * @param rutaEnemigos  Ruta al archivo con los datos de los enemigos.
     * @throws IOException Si ocurre un error al leer los archivos.
     */
    public Escenario(String rutaEscenario, String rutaEnemigos) throws IOException {
        System.out.println("Buscando recursos en:");
        System.out.println("mapa.txt -> " + getClass().getResource(rutaEscenario));
        System.out.println("enemigos.txt -> " + getClass().getResource(rutaEnemigos));

        InputStream escenarioStream = getClass().getResourceAsStream(rutaEscenario);
        InputStream enemigosStream = getClass().getResourceAsStream(rutaEnemigos);

        if (escenarioStream == null) {
            throw new IOException("No se encontró mapa.txt en: " + rutaEscenario);
        }
        if (enemigosStream == null) {
            throw new IOException("No se encontró enemigos.txt en: " + rutaEnemigos);
        }

        cargarEscenario(escenarioStream);
        cargarEnemigos(enemigosStream);
    }

    private void cargarEscenario(InputStream archivo) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(archivo));

        // Leer primera línea con las dimensiones
        String primeraLinea = br.readLine();
        if (primeraLinea == null) {
            throw new IOException("Archivo de mapa vacío");
        }
        
        String[] dimensiones = primeraLinea.split(" ");
        if (dimensiones.length != 2) {
            throw new IOException("Formato de dimensiones incorrecto en la primera línea");
        }
        
        try {
            ancho = Integer.parseInt(dimensiones[0]);
            alto = Integer.parseInt(dimensiones[1]);
        } catch (NumberFormatException e) {
            throw new IOException("Dimensiones no válidas: " + primeraLinea);
        }

        // Leer las líneas del mapa (sin incluir la primera línea de dimensiones)
        List<String> lineasMapa = new ArrayList<>();
        String linea;
        while ((linea = br.readLine()) != null) {
            lineasMapa.add(linea);
        }
        br.close();

        // Verificar que el número de líneas coincida con la altura especificada
        if (lineasMapa.size() != alto) {
            System.err.println("Advertencia: El archivo tiene " + lineasMapa.size() + 
                             " líneas de mapa, pero se especificó una altura de " + alto);
        }

        // Crear el array de celdas
        celdas = new Celda[ancho][alto];

        // Procesar cada línea del mapa
        for (int y = 0; y < alto && y < lineasMapa.size(); y++) {
            String lineaMapa = lineasMapa.get(y);
            
            // Verificar que la línea tenga la longitud correcta
            if (lineaMapa.length() != ancho) {
                System.err.println("Advertencia: La línea " + (y + 2) + // +2 porque la primera es dimensiones
                                 " tiene " + lineaMapa.length() + 
                                 " caracteres, pero se esperaban " + ancho);
            }
            
            for (int x = 0; x < ancho && x < lineaMapa.length(); x++) {
                char c = lineaMapa.charAt(x);
                celdas[x][y] = new Celda(c == '#');
            }
        }
        
        System.out.println("Mapa cargado correctamente: " + ancho + "x" + alto);
    }

    private void cargarEnemigos(InputStream archivo) throws IOException {
        enemigos = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(archivo));

        String linea;
        int lineNumber = 0;
        int enemigosCargados = 0;
        
        System.out.println("=== INICIANDO CARGA DE ENEMIGOS ===");
        System.out.println("Dimensiones del mapa: " + ancho + "x" + alto);
        
        while ((linea = br.readLine()) != null) {
            lineNumber++;
            System.out.println("Línea " + lineNumber + ": '" + linea + "'");
            
            linea = linea.trim();
            if (linea.isEmpty()) {
                System.out.println("  -> Línea vacía, omitiendo");
                continue;
            }
            if (linea.startsWith("#")) {
                System.out.println("  -> Comentario, omitiendo");
                continue;
            }

            String[] datos = linea.split(",");
            System.out.println("  -> Datos separados: " + datos.length + " elementos");
            for (int i = 0; i < datos.length; i++) {
                System.out.println("    [" + i + "]: '" + datos[i].trim() + "'");
            }
            
            if (datos.length != 8) {
                System.err.println("  -> ERROR: Se esperaban 8 valores, encontrados " + datos.length);
                continue;
            }

            try {
                String tipo = datos[0].trim();
                int salud = Integer.parseInt(datos[1].trim());
                int ataque = Integer.parseInt(datos[2].trim());
                int defensa = Integer.parseInt(datos[3].trim());
                int velocidad = Integer.parseInt(datos[4].trim());
                int percepcion = Integer.parseInt(datos[5].trim());
                int x = Integer.parseInt(datos[6].trim());
                int y = Integer.parseInt(datos[7].trim());

                System.out.println("  -> Datos parseados: " + tipo + " en (" + x + "," + y + ")");

                // Verificar que las coordenadas estén dentro del mapa
                if (!estaDentroDelEscenario(x, y)) {
                    System.err.println("  -> ERROR: Posición (" + x + "," + y + ") fuera de límites (0-" + (ancho-1) + ", 0-" + (alto-1) + ")");
                    continue;
                }
                
                // Verificar que la celda sea transitable
                if (!celdas[x][y].esTransitable()) {
                    System.err.println("  -> ERROR: Celda (" + x + "," + y + ") no es transitable (es un muro)");
                    continue;
                }
                
                // Verificar que no haya ya un personaje en esa posición
                if (celdas[x][y].getPersonaje() != null) {
                    System.err.println("  -> ERROR: Ya existe un personaje en (" + x + "," + y + ")");
                    continue;
                }

                Enemigo enemigo = new Enemigo(tipo, salud, ataque, defensa, velocidad, percepcion, x, y);
                enemigos.add(enemigo);
                celdas[x][y].setPersonaje(enemigo);
                enemigosCargados++;
                
                System.out.println("  -> ✓ ENEMIGO CARGADO EXITOSAMENTE: " + tipo + " en (" + x + "," + y + ")");
                
            } catch (NumberFormatException e) {
                System.err.println("  -> ERROR: Valor numérico inválido - " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("  -> ERROR INESPERADO: " + e.getMessage());
                e.printStackTrace();
            }
        }
        br.close();
        
        System.out.println("=== CARGA DE ENEMIGOS COMPLETADA ===");
        System.out.println("Líneas procesadas: " + lineNumber);
        System.out.println("Enemigos cargados exitosamente: " + enemigosCargados);
        System.out.println("Total enemigos en lista: " + enemigos.size());
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
