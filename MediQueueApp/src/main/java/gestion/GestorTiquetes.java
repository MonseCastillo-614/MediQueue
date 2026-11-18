/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestion;

import estructuras.NodoTiquete;
import estructuras.ColaPrioridad;
import modelo.Tiquete;
import modelo.Paciente;

/**
 * 
 * 
 * @author aedgell555
 */
public class GestorTiquetes {

    private final ColaPrioridad consultas = new ColaPrioridad();
    private final ColaPrioridad salas = new ColaPrioridad();
    private final ColaPrioridad emergencias = new ColaPrioridad();

    private int contadorConsultas = 0;
    private int contadorSalas = 0;
    private int contadorEmergencias = 0;

    // Tiquetes atendidos, guardados en el orden de atencion.
    private NodoTiquete frenteAtendidos;
    private NodoTiquete finAtendidos;
    private int cantidadAtendidos;

    public GestorTiquetes() {
        frenteAtendidos = null;
        finAtendidos = null;
        cantidadAtendidos = 0;
    }

    // Registra un paciente nuevo y genera su tiquete.
    public Tiquete registrarPaciente(String identificacion, String nombre,
            int edad, String tipoSeguro, String prioridad,
            String tipoAtencion, String atencion) {

        Paciente paciente = new Paciente(identificacion, nombre, edad, tipoSeguro);

        if (tienePendiente(paciente.getIdentificacion())) {
            throw new IllegalStateException(
                    "El paciente " + paciente.getIdentificacion()
                    + " ya tiene un tiquete pendiente.");
        }

        // Se crea primero con un ID temporal para obtener los valores normalizados.
        Tiquete temporal = new Tiquete("TMP", paciente, prioridad,
                tipoAtencion, atencion);

        String id = siguienteId(temporal.getTipoAtencion());

        Tiquete tiquete = new Tiquete(id, paciente,
                temporal.getFechaHoraIngreso(), Tiquete.SIN_ATENDER,
                temporal.getPrioridad(), temporal.getTipoAtencion(),
                temporal.getAtencion());

        colaPara(tiquete.getTipoAtencion()).encolar(tiquete);
        return tiquete;
    }

    // Agrega un tiquete pendiente, por ejemplo, al cargar el archivo JSON.
    public void agregarTiquete(Tiquete tiquete) {
        if (tiquete == null) {
            throw new IllegalArgumentException("El tiquete no puede ser nulo.");
        }
        if (tiquete.estaAtendido()) {
            throw new IllegalArgumentException(
                    "El tiquete " + tiquete.getId() + " ya fue atendido.");
        }
        if (tienePendiente(tiquete.getIdentificacionPaciente())) {
            throw new IllegalStateException(
                    "El paciente " + tiquete.getIdentificacionPaciente()
                    + " ya tiene un tiquete pendiente.");
        }

        actualizarContador(tiquete);
        colaPara(tiquete.getTipoAtencion()).encolar(tiquete);
    }

    // Carga los tiquetes pendientes que recibe de la persistencia.
    public void cargarPendientes(Iterable<Tiquete> tiquetes) {
        if (tiquetes == null) {
            throw new IllegalArgumentException("Los tiquetes no pueden ser null.");
        }
        for (Tiquete tiquete : tiquetes) {
            agregarTiquete(tiquete);
        }
    }

    // Atiende el siguiente tiquete de un tipo de atencion.
    public Tiquete atenderSiguiente(String tipoAtencion) {
        ColaPrioridad cola = colaPara(normalizarTipo(tipoAtencion));

        if (cola.estaVacia()) {
            throw new IllegalStateException(
                    "No hay pacientes esperando para ese tipo de atencion.");
        }

        Tiquete tiquete = cola.desencolar();
        tiquete.registrarAtencion();
        agregarAtendido(tiquete);
        return tiquete;
    }

    // Muestra el siguiente tiquete sin quitarlo de la cola.
    public Tiquete verSiguiente(String tipoAtencion) {
        ColaPrioridad cola = colaPara(normalizarTipo(tipoAtencion));

        if (cola.estaVacia()) {
            throw new IllegalStateException(
                    "No hay pacientes esperando para ese tipo de atencion.");
        }
        return cola.frente();
    }

    public boolean hayPendientes(String tipoAtencion) {
        return !colaPara(normalizarTipo(tipoAtencion)).estaVacia();
    }

    public int totalPendientes() {
        return consultas.tamano() + salas.tamano() + emergencias.tamano();
    }

    // Devuelve cuantos tiquetes pendientes tiene un tipo de atencion.
    public int cantidadPendientes(String tipoAtencion) {
        return colaPara(normalizarTipo(tipoAtencion)).tamano();
    }

    // Devuelve un tiquete pendiente de un tipo, segun su orden de atencion.
    public Tiquete obtenerPendiente(String tipoAtencion, int posicion) {
        return colaPara(normalizarTipo(tipoAtencion)).obtener(posicion);
    }

    // Devuelve un pendiente en este orden: emergencias, salas y consultas.
    public Tiquete obtenerPendiente(int posicion) {
        if (posicion < 0 || posicion >= totalPendientes()) {
            throw new IndexOutOfBoundsException(
                    "Posicion fuera de los tiquetes pendientes: " + posicion);
        }

        if (posicion < emergencias.tamano()) {
            return emergencias.obtener(posicion);
        }
        posicion -= emergencias.tamano();

        if (posicion < salas.tamano()) {
            return salas.obtener(posicion);
        }
        posicion -= salas.tamano();

        return consultas.obtener(posicion);
    }

    public int cantidadAtendidos() {
        return cantidadAtendidos;
    }

    // Devuelve un tiquete atendido según el orden en que se atendió.
    public Tiquete obtenerAtendido(int posicion) {
        if (posicion < 0 || posicion >= cantidadAtendidos) {
            throw new IndexOutOfBoundsException(
                    "Posicion fuera de los tiquetes atendidos: " + posicion);
        }

        NodoTiquete actual = frenteAtendidos;
        int indice = 0;
        while (indice < posicion) {
            actual = actual.getSiguiente();
            indice++;
        }
        return actual.getTiquete();
    }

    // Agrega un tiquete al final de la lista enlazada de atendidos.
    private void agregarAtendido(Tiquete tiquete) {
        NodoTiquete nuevo = new NodoTiquete(tiquete);

        if (finAtendidos == null) {
            frenteAtendidos = nuevo;
        } else {
            finAtendidos.setSiguiente(nuevo);
        }

        finAtendidos = nuevo;
        cantidadAtendidos++;
    }

    // Revisa si el paciente ya tiene un tiquete en espera.
    public boolean tienePendiente(String identificacion) {
        if (identificacion == null) {
            return false;
        }

        String id = identificacion.trim();
        return estaEnCola(consultas, id)
                || estaEnCola(salas, id)
                || estaEnCola(emergencias, id);
    }

    private boolean estaEnCola(ColaPrioridad cola, String identificacion) {
        for (int i = 0; i < cola.tamano(); i++) {
            Tiquete tiquete = cola.obtener(i);
            if (tiquete.getIdentificacionPaciente().equalsIgnoreCase(identificacion)) {
                return true;
            }
        }
        return false;
    }

    // Convierte el tipo de atención a C, S o E.
    private String normalizarTipo(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de atencion es obligatorio.");
        }

        String valor = tipo.trim();
        if (valor.equalsIgnoreCase("C") || valor.equalsIgnoreCase("Consulta")) {
            return Tiquete.TIPO_CONSULTA;
        }
        if (valor.equalsIgnoreCase("S") || valor.equalsIgnoreCase("Sala")) {
            return Tiquete.TIPO_SALA;
        }
        if (valor.equalsIgnoreCase("E") || valor.equalsIgnoreCase("Emergencia")
                || valor.equalsIgnoreCase("Emergencias")) {
            return Tiquete.TIPO_EMERGENCIA;
        }

        throw new IllegalArgumentException(
                "Tipo de atencion invalido. Use C, S o E.");
    }

    private ColaPrioridad colaPara(String tipoNormalizado) {
        if (Tiquete.TIPO_CONSULTA.equals(tipoNormalizado)) {
            return consultas;
        }
        if (Tiquete.TIPO_SALA.equals(tipoNormalizado)) {
            return salas;
        }
        if (Tiquete.TIPO_EMERGENCIA.equals(tipoNormalizado)) {
            return emergencias;
        }
        throw new IllegalArgumentException("Tipo de atencion invalido.");
    }

    // Genera IDs como C-001, S-001 o E-001.
    private String siguienteId(String tipoNormalizado) {
        int numero;
        switch (tipoNormalizado) {
            case Tiquete.TIPO_CONSULTA:
                numero = ++contadorConsultas;
                break;
            case Tiquete.TIPO_SALA:
                numero = ++contadorSalas;
                break;
            case Tiquete.TIPO_EMERGENCIA:
                numero = ++contadorEmergencias;
                break;
            default:
                throw new IllegalArgumentException("Tipo de atencion invalido.");
        }
        return String.format("%s-%03d", tipoNormalizado, numero);
    }

    // Ajusta los contadores al cargar tiquetes con formato X-nnn.
    private void actualizarContador(Tiquete tiquete) {
        String id = tiquete.getId();
        int guion = id.indexOf('-');
        if (guion < 0) {
            return;
        }

        try {
            int numero = Integer.parseInt(id.substring(guion + 1));
            switch (tiquete.getTipoAtencion()) {
                case Tiquete.TIPO_CONSULTA:
                    contadorConsultas = Math.max(contadorConsultas, numero);
                    break;
                case Tiquete.TIPO_SALA:
                    contadorSalas = Math.max(contadorSalas, numero);
                    break;
                case Tiquete.TIPO_EMERGENCIA:
                    contadorEmergencias = Math.max(contadorEmergencias, numero);
                    break;
            }
        } catch (NumberFormatException ex) {
            // Si el ID tiene otro formato, no se cambia el contador.
        }
    }
}