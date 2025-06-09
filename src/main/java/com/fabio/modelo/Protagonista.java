package com.fabio.modelo;

/**
 * Clase que representa al personaje protagonista controlado por el jugador.
 */
public class Protagonista extends Personaje {
    private String nombre;
    
    /**
     * Constructor de la clase Protagonista.
     * 
     * @param nombre Nombre del protagonista.
     * @param salud Salud inicial del protagonista.
     * @param ataque Valor de ataque del protagonista.
     * @param defensa Valor de defensa del protagonista.
     * @param velocidad Valor de velocidad del protagonista.
     * @param percepcion Rango de percepción del protagonista.
     * @param posX Posición X inicial del protagonista.
     * @param posY Posición Y inicial del protagonista.
     */
    public Protagonista(String nombre, int salud, int ataque, int defensa, int velocidad, int percepcion, int posX, int posY) {
        super(salud, ataque, defensa, velocidad, percepcion, posX, posY);
        this.nombre = nombre;
    }
    
    public String getNombre() { return nombre; }
    
    @Override
    public boolean mover(Escenario escenario, int dx, int dy) {
        int nuevaX = posX + dx;
        int nuevaY = posY + dy;
        
        // Verificar si la nueva posición está dentro del escenario
        if (!escenario.estaDentroDelEscenario(nuevaX, nuevaY)) {
            return false;
        }
        
        // Verificar si la celda es transitable
        if (!escenario.getCelda(nuevaX, nuevaY).esTransitable()) {
            return false;
        }
        
        // Verificar si hay otro personaje en la celda
        Personaje otro = escenario.getPersonajeEn(nuevaX, nuevaY);
        if (otro != null) {
            // Atacar al otro personaje
            int danio = Math.max(1, ataque - otro.getDefensa() / 2);
            otro.recibirDanio(danio);
            return false; // No hubo movimiento, hubo ataque
        }
        
        // Mover al protagonista
        escenario.moverPersonaje(this, nuevaX, nuevaY);
        return true;
    }
}