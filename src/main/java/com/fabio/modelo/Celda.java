package com.fabio.modelo;

/**
 * Clase que representa una celda del escenario.
 */
public class Celda {
    private boolean esPared;
    private boolean esMaldicion;
    private Personaje personaje;
    
    public Celda(boolean esPared, boolean esMaldicion) {
        this.esPared = esPared;
        this.esMaldicion = esMaldicion;
    }
    
    public boolean esPared() {
        return esPared;
    }
    
    public boolean esMaldicion() {
        return esMaldicion;
    }
    
    public boolean esTransitable() {
        return !esPared && personaje == null;
    }
    
    public Personaje getPersonaje() {
        return personaje;
    }
    
    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
        // Aplicar maldición si la celda es de tipo maldición
        if (esMaldicion && personaje != null) {
            aplicarMaldicionAleatoria();
        }
    }
    
    public void quitarPersonaje() {
        this.personaje = null;
    }
    
    private void aplicarMaldicionAleatoria() {
        // Obtener todos los personajes vivos en el escenario
        List<Personaje> posiblesObjetivos = new ArrayList<>();
        
        // Añadir protagonista si está vivo
        if (personaje.getEscenario().getProtagonista() != null && 
            personaje.getEscenario().getProtagonista().estaVivo()) {
            posiblesObjetivos.add(personaje.getEscenario().getProtagonista());
        }
        
        // Añadir enemigos vivos
        for (Enemigo enemigo : personaje.getEscenario().getEnemigos()) {
            if (enemigo.estaVivo()) {
                posiblesObjetivos.add(enemigo);
            }
        }
        
        // Si hay al menos un personaje vivo, seleccionar uno al azar
        if (!posiblesObjetivos.isEmpty()) {
            Random rand = new Random();
            Personaje objetivo = posiblesObjetivos.get(rand.nextInt(posiblesObjetivos.size()));
            
            // Aplicar maldición: reducir salud y salud máxima en un 25%
            int reduccion = (int) (objetivo.getSaludMaxima() * 0.25);
            objetivo.setSaludMaxima(objetivo.getSaludMaxima() - reduccion);
            objetivo.recibirDanio(reduccion);
            
            System.out.println("¡Maldición aplicada! " + 
                (objetivo instanceof Protagonista ? "Protagonista" : "Enemigo") + 
                " perdió 25% de salud máxima y actual");
        }
    }
    
    // Necesitamos que la celda tenga acceso al escenario
    private Escenario escenario;
    public void setEscenario(Escenario escenario) {
        this.escenario = escenario;
    }
    public Escenario getEscenario() {
        return escenario;
    }
}
