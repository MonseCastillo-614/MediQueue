/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package salas;
import configuracion.Configuracion;
import estructuras.ColaTiquetes;
import modelo.Tiquete;
/**
 *
 * @author casti
 */
public class GestorSalas {

    private NodoSala inicioSalas;
    private NodoSala inicioEmergencias;
    private NodoConsultorio inicioConsultorios;

    private int cantidadSalasRegistradas;
    private int cantidadEmergenciasRegistradas;
    private int cantidadConsultoriosRegistrados;

    private Configuracion configuracion;

    public GestorSalas(Configuracion configuracion) {
        if (configuracion == null) {
            throw new IllegalArgumentException(
                    "La configuracion no puede ser nula.");
        }

        this.configuracion = configuracion;
        this.inicioSalas = null;
        this.inicioEmergencias = null;
        this.inicioConsultorios = null;

        this.cantidadSalasRegistradas = 0;
        this.cantidadEmergenciasRegistradas = 0;
        this.cantidadConsultoriosRegistrados = 0;
    }

    // REGISTRO DE SALAS MEDICAS

    public void registrarSala(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException(
                    "La sala no puede ser nula.");
        }

        if (Sala.TIPO_EMERGENCIA.equals(sala.getTipo())) {
            registrarSalaEmergencia(sala);
            return;
        }

        if (cantidadSalasRegistradas
                >= configuracion.getCantidadSalas()) {
            throw new IllegalArgumentException(
                    "Se alcanzo la cantidad de salas medicas configuradas.");
        }

        if (buscarSala(sala.getIdentificador()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una sala medica con ese identificador.");
        }

        if (Sala.TIPO_PREFERENCIAL.equals(sala.getTipo())) {
            if (buscarSalaPreferencial() != null) {
                throw new IllegalArgumentException(
                        "Solo puede existir una sala preferencial.");
            }

            if (sala.getIdentificador()
                    != configuracion.getSalaPreferencial()) {
                throw new IllegalArgumentException(
                        "La sala preferencial no coincide con la configuracion.");
            }
        }

        NodoSala nuevo = new NodoSala(sala);

        if (inicioSalas == null) {
            inicioSalas = nuevo;
        } else {
            NodoSala actual = inicioSalas;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }

        cantidadSalasRegistradas++;
    }

    // REGISTRO DE SALAS DE EMERGENCIA

    public void registrarSalaEmergencia(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException(
                    "La sala de emergencia no puede ser nula.");
        }

        if (!Sala.TIPO_EMERGENCIA.equals(sala.getTipo())) {
            throw new IllegalArgumentException(
                    "El espacio debe ser de tipo emergencia.");
        }

        if (cantidadEmergenciasRegistradas
                >= configuracion.getCantidadEmergencias()) {
            throw new IllegalArgumentException(
                    "Se alcanzo la cantidad de salas de emergencia configuradas.");
        }

        if (buscarSalaEmergencia(sala.getIdentificador()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una sala de emergencia con ese identificador.");
        }

        if (sala.getIdentificador()
                == configuracion.getSalaEmergenciaCritica()) {
            if (buscarSalaEmergencia() != null) {
                throw new IllegalArgumentException(
                        "La sala de emergencia critica ya fue registrada.");
            }
        }

        NodoSala nuevo = new NodoSala(sala);

        if (inicioEmergencias == null) {
            inicioEmergencias = nuevo;
        } else {
            NodoSala actual = inicioEmergencias;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }

        cantidadEmergenciasRegistradas++;
    }

    // REGISTRO DE CONSULTORIOS

    public void registrarConsultorio(Consultorio consultorio) {
        if (consultorio == null) {
            throw new IllegalArgumentException(
                    "El consultorio no puede ser nulo.");
        }

        if (cantidadConsultoriosRegistrados
                >= configuracion.getCantidadConsultorios()) {
            throw new IllegalArgumentException(
                    "Se alcanzo la cantidad de consultorios configurados.");
        }

        if (buscarConsultorio(consultorio.getIdentificador()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un consultorio con ese identificador.");
        }

        if (consultorio.esPreferencial()) {
            if (buscarConsultorioPreferencial() != null) {
                throw new IllegalArgumentException(
                        "Solo puede existir un consultorio preferencial.");
            }

            if (consultorio.getIdentificador()
                    != configuracion.getConsultorioPreferencial()) {
                throw new IllegalArgumentException(
                        "El consultorio preferencial no coincide con la configuracion.");
            }
        }

        NodoConsultorio nuevo = new NodoConsultorio(consultorio);

        if (inicioConsultorios == null) {
            inicioConsultorios = nuevo;
        } else {
            NodoConsultorio actual = inicioConsultorios;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }

        cantidadConsultoriosRegistrados++;
    }

    // BUSCAR SALA MEDICA

    public Sala buscarSala(int identificador) {
        NodoSala actual = inicioSalas;

        while (actual != null) {
            if (actual.getSala().getIdentificador() == identificador) {
                return actual.getSala();
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // BUSCAR SALA PREFERENCIAL

    public Sala buscarSalaPreferencial() {
        NodoSala actual = inicioSalas;

        while (actual != null) {
            Sala sala = actual.getSala();

            if (Sala.TIPO_PREFERENCIAL.equals(sala.getTipo())) {
                return sala;
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // BUSCAR SALA DE EMERGENCIA POR IDENTIFICADOR

    public Sala buscarSalaEmergencia(int identificador) {
        NodoSala actual = inicioEmergencias;

        while (actual != null) {
            if (actual.getSala().getIdentificador() == identificador) {
                return actual.getSala();
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // BUSCAR SALA DE EMERGENCIA CRITICA

    public Sala buscarSalaEmergencia() {
        return buscarSalaEmergencia(
                configuracion.getSalaEmergenciaCritica());
    }

    // BUSCAR CONSULTORIO

    public Consultorio buscarConsultorio(int identificador) {
        NodoConsultorio actual = inicioConsultorios;

        while (actual != null) {
            if (actual.getConsultorio().getIdentificador()
                    == identificador) {
                return actual.getConsultorio();
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // BUSCAR CONSULTORIO PREFERENCIAL

    public Consultorio buscarConsultorioPreferencial() {
        NodoConsultorio actual = inicioConsultorios;

        while (actual != null) {
            if (actual.getConsultorio().esPreferencial()) {
                return actual.getConsultorio();
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // BUSCAR SALA NORMAL CON MENOR ESPERA

    public Sala buscarSalaConMenorEspera() {
        NodoSala actual = inicioSalas;
        Sala seleccionada = null;

        while (actual != null) {
            Sala sala = actual.getSala();

            if (Sala.TIPO_NORMAL.equals(sala.getTipo())
                    && sala.estaDisponible()) {

                if (seleccionada == null
                        || sala.getCantidadEnEspera()
                        < seleccionada.getCantidadEnEspera()) {
                    seleccionada = sala;
                }
            }

            actual = actual.getSiguiente();
        }

        return seleccionada;
    }

    // BUSCAR CONSULTORIO CON MENOR ESPERA

    public Consultorio buscarConsultorioConMenorEspera() {
        NodoConsultorio actual = inicioConsultorios;
        Consultorio seleccionado = null;

        while (actual != null) {
            Consultorio consultorio = actual.getConsultorio();

            if (!consultorio.esPreferencial()
                    && consultorio.estaDisponible()) {

                if (seleccionado == null
                        || consultorio.getCantidadEnEspera()
                        < seleccionado.getCantidadEnEspera()) {
                    seleccionado = consultorio;
                }
            }

            actual = actual.getSiguiente();
        }

        return seleccionado;
    }

    // ASIGNAR TIQUETE SEGUN TIPO, CATEGORIA Y PRIORIDAD

    public boolean asignarTiquete(Tiquete tiquete) {
        validarTiquete(tiquete);

        String tipo = tiquete.getTipoAtencion();
        int nivel = tiquete.obtenerNivelPrioridad();

        // Las consultas externas siempre se atienden en consultorios.
        if (Tiquete.TIPO_CONSULTA.equals(tipo)) {
            return Tiquete.ATENCION_PREFERENCIAL.equals(tiquete.getAtencion())
                    ? asignarTiquetePreferencial(tiquete)
                    : asignarTiqueteNormal(tiquete);
        }

        // Las emergencias y los tiquetes de sala criticos/urgentes
        // se dirigen a una sala de emergencia.
        if (Tiquete.TIPO_EMERGENCIA.equals(tipo)
                || (Tiquete.TIPO_SALA.equals(tipo) && nivel <= 2)) {
            return asignarTiqueteEmergencia(tiquete);
        }

        if (Tiquete.TIPO_SALA.equals(tipo)) {
            return Tiquete.ATENCION_PREFERENCIAL.equals(tiquete.getAtencion())
                    ? asignarTiquetePreferencial(tiquete)
                    : asignarTiqueteNormal(tiquete);
        }

        return false;
    }

    // Busca la sala de emergencia disponible con menor cantidad en espera.
    private Sala buscarSalaEmergenciaConMenorEspera() {
        NodoSala actual = inicioEmergencias;
        Sala seleccionada = null;

        while (actual != null) {
            Sala sala = actual.getSala();
            if (sala.estaDisponible()
                    && (seleccionada == null
                    || sala.getCantidadEnEspera() < seleccionada.getCantidadEnEspera())) {
                seleccionada = sala;
            }
            actual = actual.getSiguiente();
        }
        return seleccionada;
    }

    // ASIGNAR TIQUETE NORMAL

    public boolean asignarTiqueteNormal(Tiquete tiquete) {
        validarTiquete(tiquete);

        if (!Tiquete.ATENCION_NORMAL.equals(tiquete.getAtencion())) {
            return false;
        }

        if (Tiquete.TIPO_SALA.equals(tiquete.getTipoAtencion())
                && tiquete.obtenerNivelPrioridad() <= 2) {
            return false;
        }

        if (Tiquete.TIPO_CONSULTA.equals(tiquete.getTipoAtencion())) {
            Consultorio consultorio = buscarConsultorioConMenorEspera();

            if (consultorio == null) {
                return false;
            }

            consultorio.agregarTiquete(tiquete);
            return true;
        }

        if (Tiquete.TIPO_SALA.equals(tiquete.getTipoAtencion())) {
            Sala sala = buscarSalaConMenorEspera();

            if (sala == null) {
                return false;
            }

            sala.agregarTiquete(tiquete);
            return true;
        }

        return false;
    }

    // ASIGNAR TIQUETE PREFERENCIAL

    public boolean asignarTiquetePreferencial(Tiquete tiquete) {
        validarTiquete(tiquete);

        if (!Tiquete.ATENCION_PREFERENCIAL.equals(tiquete.getAtencion())) {
            return false;
        }

        if (Tiquete.TIPO_CONSULTA.equals(tiquete.getTipoAtencion())) {
            Consultorio consultorio = buscarConsultorioPreferencial();

            if (consultorio == null || !consultorio.estaDisponible()) {
                return false;
            }

            consultorio.agregarTiquete(tiquete);
            return true;
        }

        if (Tiquete.TIPO_SALA.equals(tiquete.getTipoAtencion())) {
            Sala sala = buscarSalaPreferencial();

            if (sala == null || !sala.estaDisponible()) {
                return false;
            }

            sala.agregarTiquete(tiquete);
            return true;
        }

        return false;
    }

    // ASIGNAR TIQUETE DE EMERGENCIA

    public boolean asignarTiqueteEmergencia(Tiquete tiquete) {
        validarTiquete(tiquete);

        boolean esEmergencia = Tiquete.TIPO_EMERGENCIA.equals(
                tiquete.getTipoAtencion());
        boolean esSalaPrioritaria = Tiquete.TIPO_SALA.equals(
                tiquete.getTipoAtencion()) && tiquete.obtenerNivelPrioridad() <= 2;

        if (!esEmergencia && !esSalaPrioritaria) {
            return false;
        }

        if (tiquete.obtenerNivelPrioridad() > 2) {
            return false;
        }

        Sala sala;
        if (Tiquete.PRIORIDAD_CRITICA.equalsIgnoreCase(tiquete.getPrioridad())) {
            sala = buscarSalaEmergencia();
        } else {
            sala = buscarSalaEmergenciaConMenorEspera();
        }

        if (sala == null || !sala.estaDisponible()) {
            return false;
        }

        sala.agregarTiquete(tiquete);
        return true;
    }

    // VALIDAR TIQUETE

    private void validarTiquete(Tiquete tiquete) {
        if (tiquete == null) {
            throw new IllegalArgumentException(
                    "El tiquete no puede ser nulo.");
        }
    }

    // CONSULTAR PACIENTES POR SALA MEDICA

    public String mostrarPacientesPorSala(int identificador) {
        Sala sala = buscarSala(identificador);

        if (sala == null) {
            return "No existe una sala medica con ese identificador.";
        }

        return mostrarColaSala(sala);
    }

    // CONSULTAR PACIENTES POR SALA DE EMERGENCIA

    public String mostrarPacientesPorSalaEmergencia(int identificador) {
        Sala sala = buscarSalaEmergencia(identificador);

        if (sala == null) {
            return "No existe una sala de emergencia con ese identificador.";
        }

        return mostrarColaSala(sala);
    }

    private String mostrarColaSala(Sala sala) {
        String resultado = sala.toString() + "\n";
        ColaTiquetes cola = sala.getColaEspera();

        if (cola.estaVacia()) {
            return resultado + "No hay pacientes en espera.";
        }

        for (int i = 0; i < cola.tamano(); i++) {
            resultado += cola.obtener(i).getNombrePaciente() + "\n";
        }

        return resultado;
    }

    // CONSULTAR PACIENTES POR CONSULTORIO

    public String mostrarPacientesPorConsultorio(int identificador) {
        Consultorio consultorio = buscarConsultorio(identificador);

        if (consultorio == null) {
            return "No existe un consultorio con ese identificador.";
        }

        String resultado = consultorio.toString() + "\n";
        ColaTiquetes cola = consultorio.getColaEspera();

        if (cola.estaVacia()) {
            return resultado + "No hay pacientes en espera.";
        }

        for (int i = 0; i < cola.tamano(); i++) {
            resultado += cola.obtener(i).getNombrePaciente() + "\n";
        }

        return resultado;
    }

    // CONSULTAR PACIENTES POR MEDICO

    public String mostrarPacientesPorMedico(String nombreMedico) {
        if (nombreMedico == null || nombreMedico.trim().isEmpty()) {
            return "Debe indicar el nombre del medico.";
        }

        String resultado = "Pacientes del medico "
                + nombreMedico + "\n";

        resultado += mostrarPacientesSalasPorMedico(
                inicioSalas, nombreMedico);

        resultado += mostrarPacientesSalasPorMedico(
                inicioEmergencias, nombreMedico);

        NodoConsultorio actual = inicioConsultorios;

        while (actual != null) {
            Consultorio consultorio = actual.getConsultorio();

            if (nombreMedico.equalsIgnoreCase(
                    consultorio.getMedicoResponsable())) {

                ColaTiquetes cola = consultorio.getColaEspera();

                for (int i = 0; i < cola.tamano(); i++) {
                    resultado += cola.obtener(i).getNombrePaciente() + "\n";
                }
            }

            actual = actual.getSiguiente();
        }

        return resultado;
    }

    private String mostrarPacientesSalasPorMedico(
            NodoSala inicio, String nombreMedico) {

        String resultado = "";
        NodoSala actual = inicio;

        while (actual != null) {
            Sala sala = actual.getSala();

            if (nombreMedico.equalsIgnoreCase(
                    sala.getMedicoResponsable())) {

                ColaTiquetes cola = sala.getColaEspera();

                for (int i = 0; i < cola.tamano(); i++) {
                    resultado += cola.obtener(i).getNombrePaciente() + "\n";
                }
            }

            actual = actual.getSiguiente();
        }

        return resultado;
    }

    // CONSULTAR PACIENTES POR ENFERMERA

    public String mostrarPacientesPorEnfermera(String nombreEnfermera) {
        if (nombreEnfermera == null
                || nombreEnfermera.trim().isEmpty()) {
            return "Debe indicar el nombre de la enfermera.";
        }

        String resultado = "Pacientes de la enfermera "
                + nombreEnfermera + "\n";

        resultado += mostrarPacientesSalasPorEnfermera(
                inicioSalas, nombreEnfermera);

        resultado += mostrarPacientesSalasPorEnfermera(
                inicioEmergencias, nombreEnfermera);

        return resultado;
    }

    private String mostrarPacientesSalasPorEnfermera(
            NodoSala inicio, String nombreEnfermera) {

        String resultado = "";
        NodoSala actual = inicio;

        while (actual != null) {
            Sala sala = actual.getSala();

            if (nombreEnfermera.equalsIgnoreCase(
                    sala.getEnfermeraCargo())) {

                ColaTiquetes cola = sala.getColaEspera();

                for (int i = 0; i < cola.tamano(); i++) {
                    resultado += cola.obtener(i).getNombrePaciente() + "\n";
                }
            }

            actual = actual.getSiguiente();
        }

        return resultado;
    }

    // CONSULTAR ESPACIOS DISPONIBLES

    public String consultarSalasDisponibles() {
        String resultado = "SALAS MEDICAS DISPONIBLES\n";
        NodoSala actual = inicioSalas;
        boolean encontrada = false;

        while (actual != null) {
            Sala sala = actual.getSala();

            if (sala.estaDisponible()) {
                resultado += sala.toString() + "\n";
                encontrada = true;
            }

            actual = actual.getSiguiente();
        }

        if (!encontrada) {
            resultado += "No hay salas medicas disponibles.\n";
        }

        resultado += "\nSALAS DE EMERGENCIA DISPONIBLES\n";
        actual = inicioEmergencias;
        encontrada = false;

        while (actual != null) {
            Sala sala = actual.getSala();

            if (sala.estaDisponible()) {
                resultado += sala.toString() + "\n";
                encontrada = true;
            }

            actual = actual.getSiguiente();
        }

        if (!encontrada) {
            resultado += "No hay salas de emergencia disponibles.\n";
        }

        return resultado;
    }

    public String consultarConsultoriosDisponibles() {
        String resultado = "CONSULTORIOS DISPONIBLES\n";
        NodoConsultorio actual = inicioConsultorios;
        boolean encontrada = false;

        while (actual != null) {
            Consultorio consultorio = actual.getConsultorio();

            if (consultorio.estaDisponible()) {
                resultado += consultorio.toString() + "\n";
                encontrada = true;
            }

            actual = actual.getSiguiente();
        }

        if (!encontrada) {
            resultado += "No hay consultorios disponibles.";
        }

        return resultado;
    }

    // CONSULTAR CANTIDADES REGISTRADAS

    public int getCantidadSalasRegistradas() {
        return cantidadSalasRegistradas;
    }

    public int getCantidadEmergenciasRegistradas() {
        return cantidadEmergenciasRegistradas;
    }

    public int getCantidadConsultoriosRegistrados() {
        return cantidadConsultoriosRegistrados;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }


    /**
     * Retira un tiquete de cualquier sala o consultorio.
     * Se utiliza para revertir una asignacion si falla el registro del tiquete.
     */
    public boolean removerTiquete(String idTiquete) {
        if (idTiquete == null || idTiquete.trim().isEmpty()) {
            return false;
        }

        NodoSala actualSala = inicioSalas;
        while (actualSala != null) {
            if (actualSala.getSala().getColaEspera().removerPorId(idTiquete)) {
                return true;
            }
            actualSala = actualSala.getSiguiente();
        }

        actualSala = inicioEmergencias;
        while (actualSala != null) {
            if (actualSala.getSala().getColaEspera().removerPorId(idTiquete)) {
                return true;
            }
            actualSala = actualSala.getSiguiente();
        }

        NodoConsultorio actualConsultorio = inicioConsultorios;
        while (actualConsultorio != null) {
            if (actualConsultorio.getConsultorio().getColaEspera()
                    .removerPorId(idTiquete)) {
                return true;
            }
            actualConsultorio = actualConsultorio.getSiguiente();
        }

        return false;
    }
}