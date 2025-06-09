package com.fabio.modelo;

/**
 * Clase que representa una celda del escenario.
 */
public class Celda {
    private boolean esPared;
    private Personaje personaje;
    
    /**
     * Constructor de la clase Celda.
     * 
     * @param esPared true si la celda es una pared, false si es suelo.
     */
    public Celda(boolean esPared) {
        this.esPared = esPared;
        this.personaje = null;
    }
    
    // Getters y setters
    public boolean esPared() { return esPared; }
    public boolean esTransitable() { return !esPared && personaje == null; }
    public Personaje getPersonaje() { return personaje; }
    
    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }
    
    public void quitarPersonaje() {
        this.personaje = null;
    }
}