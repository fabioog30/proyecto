package com.fabio.modelo;

/**
 * Clase que representa una celda del escenario.
 */
public class Celda {
    private boolean esPared;
    private boolean esMaldicion;
    private Personaje personaje;
    
    /**
     * Constructor de la clase Celda.
     * 
     * @param esPared true si la celda es una pared, false si es suelo.
     */
    public Celda(boolean esPared, boolean esMaldicion) {
        this.esPared = esPared;
        this.personaje = null;
        this.esMaldicion = esMaldicion;
    }
    
    // Getters y setters
    public boolean esPared() { return esPared; }
    public boolean esMaldicion() { return esMaldicion; }
    public boolean esTransitable() { return !esPared && personaje == null; }
    public Personaje getPersonaje() { return personaje; }
    
    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;

        if(esMaldicion && personaje != null) {
            aplicarMaldicion(personaje);
        }
    }
    
    public void quitarPersonaje() {
        this.personaje = null;
    }

    private void aplicarMaldicion(Personaje personaje) {
        // Reducir salud y salud máxima en un 25%
        int nuevaSaludMaxima = (int)(personaje.getSaludMaxima() * 0.75);
        int nuevaSalud = (int)(personaje.getSalud() * 0.75);
        
        personaje.recibirDanio(personaje.getSalud() - nuevaSalud);
        personaje.setSaludMaxima(nuevaSaludMaxima);
    }
}
