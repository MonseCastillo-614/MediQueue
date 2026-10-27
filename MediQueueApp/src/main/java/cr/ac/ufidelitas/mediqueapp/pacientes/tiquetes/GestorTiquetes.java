package cr.ac.ufidelitas.mediqueapp.pacientes.tiquetes;

import java.util.ArrayList;
import java.util.List;

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

    // Tiquetes ya atendidos, en el orden en que se atendieron
    private final List<Tiquete> atendidos = new ArrayList<>();

    // Registra un paciente nuevo y genera su tiquete 
    public Tiquete registrarPaciente(String identificacion, String nombre,
            int edad, String tipoSeguro, String prioridad,
            String tipoAtencion, String atencion) {

        Paciente paciente = new Paciente(identificacion, nombre, edad, tipoSeguro);

        if (tienePendiente(paciente.getIdentificacion())) {
            throw new IllegalStateException(
                    "El paciente " + paciente.getIdentificacion()
                    + " ya tiene un tiquete pendiente.");
        }

        // Se crea primero con id temporal para normalizar el tipo (C/S/E)
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

    // Encola un tiquete ya construido (por ejemplo, al cargar tiquetes.json)
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

    // Carga varios pendientes (los que devuelva la persistencia)
    public void cargarPendientes(Iterable<Tiquete> tiquetes) {
        for (Tiquete t : tiquetes) {
            agregarTiquete(t);
        }
    }

    // Atiende al siguiente de un tipo (C, S o E): lo saca y registra la hora
    public Tiquete atenderSiguiente(String tipoAtencion) {
        ColaPrioridad cola = colaPara(normalizarTipo(tipoAtencion));

        if (cola.estaVacia()) {
            throw new IllegalStateException(
                    "No hay pacientes esperando para ese tipo de atencion.");
        }

        Tiquete tiquete = cola.desencolar();
        tiquete.registrarAtencion();
        atendidos.add(tiquete);
        return tiquete;
    }

    // Muestra el siguiente a atender sin sacarlo
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

    // Pendientes de un tipo, en el orden de atencion
    public List<Tiquete> listarPendientes(String tipoAtencion) {
        return colaPara(normalizarTipo(tipoAtencion)).aLista();
    }

    // Todos los pendientes (emergencias, salas, consultas); util para guardar tiquetes.json
    public List<Tiquete> listarTodosPendientes() {
        List<Tiquete> todos = new ArrayList<>();
        todos.addAll(emergencias.aLista());
        todos.addAll(salas.aLista());
        todos.addAll(consultas.aLista());
        return todos;
    }

    public List<Tiquete> getAtendidos() {
        return new ArrayList<>(atendidos);
    }

    // Indica si el paciente ya esta esperando en alguna cola
    public boolean tienePendiente(String identificacion) {
        if (identificacion == null) {
            return false;
        }
        String id = identificacion.trim();
        for (Tiquete t : listarTodosPendientes()) {
            if (t.getIdentificacionPaciente().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    // ---- Auxiliares ----

    private String normalizarTipo(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de atencion es obligatorio.");
        }
        String v = tipo.trim();
        if (v.equalsIgnoreCase("C") || v.equalsIgnoreCase("Consulta")) {
            return Tiquete.TIPO_CONSULTA;
        }
        if (v.equalsIgnoreCase("S") || v.equalsIgnoreCase("Sala")) {
            return Tiquete.TIPO_SALA;
        }
        if (v.equalsIgnoreCase("E") || v.equalsIgnoreCase("Emergencia")
                || v.equalsIgnoreCase("Emergencias")) {
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
        return emergencias;
    }

    // Genera ids como C-001, S-001, E-001
    private String siguienteId(String tipoNormalizado) {
        int numero;
        if (null == tipoNormalizado) {
            numero = ++contadorEmergencias;
        } else switch (tipoNormalizado) {
            case Tiquete.TIPO_CONSULTA:
                numero = ++contadorConsultas;
                break;
            case Tiquete.TIPO_SALA:
                numero = ++contadorSalas;
                break;
            default:
                numero = ++contadorEmergencias;
                break;
        }
        return String.format("%s-%03d", tipoNormalizado, numero);
    }

    // Evita repetir ids al cargar tiquetes con formato X-nnn
    private void actualizarContador(Tiquete tiquete) {
        String id = tiquete.getId();
        int guion = id.indexOf('-');
        if (guion < 0) {
            return;
        }
        try {
            int numero = Integer.parseInt(id.substring(guion + 1));
            String tipo = tiquete.getTipoAtencion();
            if (null == tipo) {
                contadorEmergencias = Math.max(contadorEmergencias, numero);
            } else switch (tipo) {
                case Tiquete.TIPO_CONSULTA:
                    contadorConsultas = Math.max(contadorConsultas, numero);
                    break;
                case Tiquete.TIPO_SALA:
                    contadorSalas = Math.max(contadorSalas, numero);
                    break;
                default:
                    contadorEmergencias = Math.max(contadorEmergencias, numero);
                    break;
            }
        } catch (NumberFormatException ex) {
            // id con otro formato: no se ajusta el contador
        }
    }
}
