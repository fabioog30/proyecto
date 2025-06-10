package com.fabio.modelo;

/**
 * Clase que representa al personaje protagonista controlado por el jugador.
 */
public class Protagonista extends Personaje {
    private String nombre;

    /**
     * Constructor de la clase Protagonista.
     * 
     * @param nombre     Nombre del protagonista.
     * @param salud      Salud inicial del protagonista.
     * @param ataque     Valor de ataque del protagonista.
     * @param defensa    Valor de defensa del protagonista.
     * @param velocidad  Valor de velocidad del protagonista.
     * @param percepcion Rango de percepción del protagonista.
     * @param posX       Posición X inicial del protagonista.
     * @param posY       Posición Y inicial del protagonista.
     */
    public Protagonista(String nombre, int salud, int ataque, int defensa, int velocidad, int percepcion, int posX,
            int posY) {
        super(salud, ataque, defensa, velocidad, percepcion, posX, posY);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean mover(Escenario escenario, int dx, int dy) {
        int nuevaX = posX + dx;
        int nuevaY = posY + dy;

        if (!escenario.estaDentroDelEscenario(nuevaX, nuevaY)) {
            return false;
        }

        Celda celdaDestino = escenario.getCelda(nuevaX, nuevaY);
        Personaje objetivo = celdaDestino.getPersonaje();

        // ATAQUE (si hay personaje en la celda destino)
        if (objetivo != null) {
            int danio = Math.max(1, ataque - objetivo.getDefensa() / 2);
            objetivo.recibirDanio(danio);

            // Verifica si el objetivo murió
            if (!objetivo.estaVivo()) {
                if (objetivo instanceof Enemigo) {
                    escenario.eliminarEnemigo((Enemigo) objetivo);
                }
            }
            return true; // Cambio clave: Debe retornar true para contar como acción
        }

        // MOVIMIENTO (si la celda está vacía)
        if (celdaDestino.esTransitable()) {
            escenario.moverPersonaje(this, nuevaX, nuevaY);
            return true;
        }

        return false;
    }

}
