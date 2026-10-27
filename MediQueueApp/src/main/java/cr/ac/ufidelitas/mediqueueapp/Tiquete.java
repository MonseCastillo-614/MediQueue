package cr.ac.ufidelitas.mediqueueapp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 *
 * @author dahyi97
 */
public class Tiquete {

    // Tipo de atencion
    public static final String TIPO_CONSULTA = "C";
    public static final String TIPO_SALA = "S";
    public static final String TIPO_EMERGENCIA = "E";

    // Categoria de atencion
    public static final String ATENCION_PREFERENCIAL = "P";
    public static final String ATENCION_NORMAL = "N";

    // Prioridades que pide el proyecto
    public static final String PRIORIDAD_CRITICA = "Critico";
    public static final String PRIORIDAD_URGENTE = "Urgente";
    public static final String PRIORIDAD_REGULAR = "Regular";
    public static final String PRIORIDAD_CONTROL = "Control";

    // Se usa para no romper codigo con los nombres anteriores.
    public static final String PRIORIDAD_ALTA = PRIORIDAD_URGENTE;
    public static final String PRIORIDAD_MEDIA = PRIORIDAD_REGULAR;
    public static final String PRIORIDAD_BAJA = PRIORIDAD_CONTROL;

    public static final String SIN_ATENDER = "-1";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String id;
    private Paciente paciente;
    private String fechaHoraIngreso;
    private String fechaHoraAtencion;
    private String prioridad;
    private String tipoAtencion;
    private String atencion;
    private Tiquete siguienteRegistro;

    public Tiquete() {
        this.fechaHoraAtencion = SIN_ATENDER;
    }

    // Crea un tiquete con la fecha actual.
    public Tiquete(String id, Paciente paciente, String prioridad,
            String tipoAtencion, String atencion) {
        this(id, paciente,
                LocalDateTime.now().format(FORMATO_FECHA),
                SIN_ATENDER, prioridad, tipoAtencion, atencion);
    }

    public Tiquete(String id, Paciente paciente, String fechaHoraIngreso,
            String fechaHoraAtencion, String prioridad,
            String tipoAtencion, String atencion) {

        validarId(id);
        validarPaciente(paciente);
        validarFechaIngreso(fechaHoraIngreso);

        this.id = id.trim();
        this.paciente = paciente;
        this.fechaHoraIngreso = fechaHoraIngreso.trim();
        this.fechaHoraAtencion = (fechaHoraAtencion == null
                || fechaHoraAtencion.trim().isEmpty())
                ? SIN_ATENDER : fechaHoraAtencion.trim();
        this.prioridad = normalizarPrioridad(prioridad);
        this.tipoAtencion = normalizarTipoAtencion(tipoAtencion);
        this.atencion = normalizarAtencion(atencion);
    }

    private void validarId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del tiquete es obligatorio.");
        }
    }

    private void validarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El tiquete debe tener un paciente asociado.");
        }
    }

    private void validarFechaIngreso(String fechaHoraIngreso) {
        if (fechaHoraIngreso == null || fechaHoraIngreso.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha y hora de ingreso son obligatorias.");
        }
    }

    // Acepta tambien los nombres anteriores para poder leer tiquetes ya guardados.
    private String normalizarPrioridad(String prioridad) {
        if (prioridad == null || prioridad.trim().isEmpty()) {
            throw new IllegalArgumentException("La prioridad medica es obligatoria.");
        }

        String valor = prioridad.trim();

        if (valor.equalsIgnoreCase("Critico")
                || valor.equalsIgnoreCase("Crítico")
                || valor.equalsIgnoreCase("Critica")
                || valor.equalsIgnoreCase("Crítica")) {
            return PRIORIDAD_CRITICA;
        }
        if (valor.equalsIgnoreCase(PRIORIDAD_URGENTE)
                || valor.equalsIgnoreCase("Alta")) {
            return PRIORIDAD_URGENTE;
        }
        if (valor.equalsIgnoreCase(PRIORIDAD_REGULAR)
                || valor.equalsIgnoreCase("Media")) {
            return PRIORIDAD_REGULAR;
        }
        if (valor.equalsIgnoreCase(PRIORIDAD_CONTROL)
                || valor.equalsIgnoreCase("Baja")) {
            return PRIORIDAD_CONTROL;
        }

        throw new IllegalArgumentException(
                "Prioridad invalida. Use: Critico, Urgente, Regular o Control.");
    }

    private String normalizarTipoAtencion(String tipoAtencion) {
        if (tipoAtencion == null || tipoAtencion.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de atencion es obligatorio.");
        }

        String valor = tipoAtencion.trim();

        if (valor.equalsIgnoreCase("C")
                || valor.equalsIgnoreCase("Consulta")
                || valor.equalsIgnoreCase("Consulta Externa")) {
            return TIPO_CONSULTA;
        }
        if (valor.equalsIgnoreCase("S")
                || valor.equalsIgnoreCase("Sala")
                || valor.equalsIgnoreCase("Salon")
                || valor.equalsIgnoreCase("Salón")) {
            return TIPO_SALA;
        }
        if (valor.equalsIgnoreCase("E")
                || valor.equalsIgnoreCase("Emergencia")
                || valor.equalsIgnoreCase("Emergencias")) {
            return TIPO_EMERGENCIA;
        }

        throw new IllegalArgumentException(
                "Tipo de atencion invalido. Use C (Consulta), S (Sala) o E (Emergencias).");
    }

    private String normalizarAtencion(String atencion) {
        if (atencion == null || atencion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La categoria de atencion (P o N) es obligatoria.");
        }

        String valor = atencion.trim();

        if (valor.equalsIgnoreCase("P")
                || valor.equalsIgnoreCase("Preferencial")) {
            return ATENCION_PREFERENCIAL;
        }
        if (valor.equalsIgnoreCase("N")
                || valor.equalsIgnoreCase("Normal")) {
            return ATENCION_NORMAL;
        }

        throw new IllegalArgumentException(
                "Atencion invalida. Use P (Preferencial) o N (Normal).");
    }

    public void registrarAtencion() {
        this.fechaHoraAtencion = LocalDateTime.now().format(FORMATO_FECHA);
    }

    public void registrarAtencion(String fechaHora) {
        if (fechaHora == null || fechaHora.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de atencion no puede estar vacia.");
        }
        this.fechaHoraAtencion = fechaHora.trim();
    }

    public boolean estaAtendido() {
        return fechaHoraAtencion != null && !SIN_ATENDER.equals(fechaHoraAtencion);
    }

    public boolean esPreferencial() {
        return ATENCION_PREFERENCIAL.equalsIgnoreCase(atencion);
    }

    public boolean esEmergenciaCritica() {
        return TIPO_EMERGENCIA.equalsIgnoreCase(tipoAtencion)
                && PRIORIDAD_CRITICA.equalsIgnoreCase(prioridad);
    }

    // El nivel 1 es el de mayor prioridad.
    public int obtenerNivelPrioridad() {
        if (PRIORIDAD_CRITICA.equalsIgnoreCase(prioridad)) {
            return 1;
        }
        if (PRIORIDAD_URGENTE.equalsIgnoreCase(prioridad)) {
            return 2;
        }
        if (PRIORIDAD_REGULAR.equalsIgnoreCase(prioridad)) {
            return 3;
        }
        if (PRIORIDAD_CONTROL.equalsIgnoreCase(prioridad)) {
            return 4;
        }
        throw new IllegalStateException("Prioridad no valida: " + prioridad);
    }

    // Enlace para recorrer los tiquetes registrados.
    Tiquete getSiguienteRegistro() {
        return siguienteRegistro;
    }

    void setSiguienteRegistro(Tiquete siguienteRegistro) {
        this.siguienteRegistro = siguienteRegistro;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        validarId(id);
        this.id = id.trim();
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        validarPaciente(paciente);
        this.paciente = paciente;
    }

    public String getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(String fechaHoraIngreso) {
        validarFechaIngreso(fechaHoraIngreso);
        this.fechaHoraIngreso = fechaHoraIngreso.trim();
    }

    public String getFechaHoraAtencion() {
        return fechaHoraAtencion;
    }

    public void setFechaHoraAtencion(String fechaHoraAtencion) {
        if (fechaHoraAtencion == null || fechaHoraAtencion.trim().isEmpty()) {
            this.fechaHoraAtencion = SIN_ATENDER;
        } else {
            this.fechaHoraAtencion = fechaHoraAtencion.trim();
        }
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = normalizarPrioridad(prioridad);
    }

    public String getTipoAtencion() {
        return tipoAtencion;
    }

    public void setTipoAtencion(String tipoAtencion) {
        this.tipoAtencion = normalizarTipoAtencion(tipoAtencion);
    }

    public String getAtencion() {
        return atencion;
    }

    public void setAtencion(String atencion) {
        this.atencion = normalizarAtencion(atencion);
    }

    public String getNombrePaciente() {
        return paciente != null ? paciente.getNombre() : "";
    }

    public String getIdentificacionPaciente() {
        return paciente != null ? paciente.getIdentificacion() : "";
    }

    public int getEdadPaciente() {
        return paciente != null ? paciente.getEdad() : 0;
    }

    public String getTipoSeguroPaciente() {
        return paciente != null ? paciente.getTipoSeguro() : "";
    }

    @Override
    public String toString() {
        return "Tiquete [" + id + "] | "
                + (paciente != null ? paciente.toString() : "Sin paciente")
                + " | Prioridad: " + prioridad
                + " | Tipo: " + tipoAtencion
                + " | Atencion: " + atencion
                + " | Ingreso: " + fechaHoraIngreso
                + " | Atendido: " + (estaAtendido()
                ? fechaHoraAtencion : "Pendiente (-1)");
    }
}