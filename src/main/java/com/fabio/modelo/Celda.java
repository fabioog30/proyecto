package com.fabio.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    }

    public void quitarPersonaje() {
        this.personaje = null;
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
