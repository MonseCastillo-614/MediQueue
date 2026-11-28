/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author casti
 */
public class Atencion {

    // Estados posibles de una atención.
    public static final String EN_ATENCION = "En atención";
    public static final String FINALIZADA = "Finalizada";
    public static final String CANCELADA = "Cancelada";
    public static final String REINGRESO = "Reingreso";

    private Tiquete tiquete;
    private String medicoAsignado;
    private String diagnostico;
    private String medicamentos;
    private String observaciones;
    private String procedimientos;
    private String espacioAsignado;
    private double costo;
    private String estado;

    // Constructor vacío para facilitar la reconstrucción desde JSON.
    public Atencion() {
        this.estado = EN_ATENCION;
    }

    // Constructor principal.
    public Atencion(Tiquete tiquete, String medicoAsignado,
            String diagnostico, String medicamentos,
            String observaciones, String procedimientos,
            String espacioAsignado, double costo, String estado) {

        setTiquete(tiquete);
        setMedicoAsignado(medicoAsignado);
        setDiagnostico(diagnostico);
        setMedicamentos(medicamentos);
        setObservaciones(observaciones);
        setProcedimientos(procedimientos);
        setEspacioAsignado(espacioAsignado);
        setCosto(costo);
        setEstado(estado);
    }

    public Tiquete getTiquete() {
        return tiquete;
    }

    public void setTiquete(Tiquete tiquete) {
        if (tiquete == null) {
            throw new IllegalArgumentException(
                    "La atención debe tener un tiquete asociado.");
        }
        this.tiquete = tiquete;
    }

    public String getMedicoAsignado() {
        return medicoAsignado;
    }

    public void setMedicoAsignado(String medicoAsignado) {
        this.medicoAsignado = validarTexto(
                medicoAsignado, "El médico asignado es obligatorio.");
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = validarTexto(
                diagnostico, "El diagnóstico es obligatorio.");
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = validarTexto(
                medicamentos, "Los medicamentos son obligatorios.");
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = validarTexto(
                observaciones, "Las observaciones son obligatorias.");
    }

    public String getProcedimientos() {
        return procedimientos;
    }

    public void setProcedimientos(String procedimientos) {
        this.procedimientos = validarTexto(
                procedimientos, "Los procedimientos son obligatorios.");
    }

    public String getEspacioAsignado() {
        return espacioAsignado;
    }

    public void setEspacioAsignado(String espacioAsignado) {
        this.espacioAsignado = validarTexto(
                espacioAsignado, "El espacio asignado es obligatorio.");
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        if (costo < 0) {
            throw new IllegalArgumentException(
                    "El costo no puede ser negativo.");
        }
        this.costo = costo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El estado de la atención es obligatorio.");
        }

        String valor = estado.trim();

        if (valor.equalsIgnoreCase(EN_ATENCION)) {
            this.estado = EN_ATENCION;
        } else if (valor.equalsIgnoreCase(FINALIZADA)) {
            this.estado = FINALIZADA;
        } else if (valor.equalsIgnoreCase(CANCELADA)) {
            this.estado = CANCELADA;
        } else if (valor.equalsIgnoreCase(REINGRESO)) {
            this.estado = REINGRESO;
        } else {
            throw new IllegalArgumentException(
                    "El estado de la atención no es válido.");
        }
    }

    // Valida los textos requeridos.
    private String validarTexto(String texto, String mensaje) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
        return texto.trim();
    }

    @Override
    public String toString() {
        return "Atención del tiquete: "
                + (tiquete != null ? tiquete.getId() : "Sin tiquete")
                + " | Médico: " + medicoAsignado
                + " | Estado: " + estado
                + " | Costo: " + costo;
    }
}
