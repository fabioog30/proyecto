package com.fabio.modelo;

/**
 * Clase abstracta que representa un personaje en el juego.
 * Contiene las propiedades y métodos comunes a todos los personajes.
 */
public abstract class Personaje {
    protected int salud;
    protected int saludMaxima;
    protected int ataque;
    protected int defensa;
    protected int velocidad;
    protected int percepcion;
    protected int posX;
    protected int posY;
    
    /**
     * Constructor de la clase Personaje.
     * 
     * @param salud Salud inicial del personaje.
     * @param ataque Valor de ataque del personaje.
     * @param defensa Valor de defensa del personaje.
     * @param velocidad Valor de velocidad del personaje.
     * @param percepcion Rango de percepción del personaje.
     * @param posX Posición X inicial del personaje.
     * @param posY Posición Y inicial del personaje.
     */
    public Personaje(int salud, int ataque, int defensa, int velocidad, int percepcion, int posX, int posY) {
        this.salud = salud;
        this.saludMaxima = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.percepcion = percepcion;
        this.posX = posX;
        this.posY = posY;
    }
    
    // Getters y setters
    public int getSalud() { return salud; }
    public int getSaludMaxima() { return saludMaxima; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getVelocidad() { return velocidad; }
    public int getPercepcion() { return percepcion; }
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    
    public void setPosicion(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public void setSaludMaxima(int saludNueva){
        this.saludMaxima = saludNueva;
    }
    
    /**
     * Método para recibir daño.
     * 
     * @param cantidad Cantidad de daño a recibir.
     */
    public void recibirDanio(int cantidad) {
        this.salud = Math.max(0, this.salud - cantidad);
    }
    
    /**
     * Método para verificar si el personaje está vivo.
     * 
     * @return true si el personaje tiene salud mayor que 0, false en caso contrario.
     */
    public boolean estaVivo() {
        return salud > 0;
    }
    
    /**
     * Método abstracto para mover al personaje.
     * 
     * @param escenario Escenario donde se mueve el personaje.
     * @param dx Dirección en X (-1, 0, 1).
     * @param dy Dirección en Y (-1, 0, 1).
     * @return true si el movimiento fue exitoso, false en caso contrario.
     */
    public abstract boolean mover(Escenario escenario, int dx, int dy);
}
