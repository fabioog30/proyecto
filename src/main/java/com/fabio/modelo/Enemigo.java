package com.fabio.modelo;

import java.util.Random;

/**
 * Clase que representa a un enemigo en el juego.
 */
public class Enemigo extends Personaje {
    private String tipo;
    
    /**
     * Constructor de la clase Enemigo.
     * 
     * @param tipo Tipo de enemigo.
     * @param salud Salud inicial del enemigo.
     * @param ataque Valor de ataque del enemigo.
     * @param defensa Valor de defensa del enemigo.
     * @param velocidad Valor de velocidad del enemigo.
     * @param percepcion Rango de percepción del enemigo.
     * @param posX Posición X inicial del enemigo.
     * @param posY Posición Y inicial del enemigo.
     */
    public Enemigo(String tipo, int salud, int ataque, int defensa, int velocidad, int percepcion, int posX, int posY) {
        super(salud, ataque, defensa, velocidad, percepcion, posX, posY);
        this.tipo = tipo;
    }
    
    public String getTipo() { return tipo; }
    
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
                escenario.eliminarEnemigo((Enemigo)objetivo);
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

    
    /**
     * Método para que el enemigo decida su movimiento.
     * 
     * @param escenario Escenario actual del juego.
     * @param protagonista Protagonista del juego.
     * @return true si se movió, false en caso contrario.
     */
    public boolean decidirMovimiento(Escenario escenario, Protagonista protagonista) {
        // Calcular distancia al protagonista
        int distanciaX = protagonista.getPosX() - posX;
        int distanciaY = protagonista.getPosY() - posY;
        double distancia = Math.sqrt(distanciaX * distanciaX + distanciaY * distanciaY);
        
        if (distancia <= percepcion) {
            // Moverse hacia el protagonista
            int dx = Integer.signum(distanciaX);
            int dy = Integer.signum(distanciaY);
            
            // Priorizar movimiento en la dirección con mayor distancia
            if (Math.abs(distanciaX) > Math.abs(distanciaY)) {
                return mover(escenario, dx, 0) || mover(escenario, 0, dy);
            } else {
                return mover(escenario, 0, dy) || mover(escenario, dx, 0);
            }
        } else {
            // Movimiento aleatorio
            Random rand = new Random();
            int direccion = rand.nextInt(4);
            
            switch (direccion) {
                case 0: return mover(escenario, 1, 0);  // Derecha
                case 1: return mover(escenario, -1, 0); // Izquierda
                case 2: return mover(escenario, 0, 1);  // Abajo
                case 3: return mover(escenario, 0, -1); // Arriba
                default: return false;
            }
        }
    }
}
