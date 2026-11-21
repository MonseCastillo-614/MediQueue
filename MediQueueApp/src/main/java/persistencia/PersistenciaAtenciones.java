/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;
import estructuras.NodoAtencion;
import modelo.Atencion;
import modelo.Paciente;
import modelo.Tiquete;
import java.io.IOException;

/**
 *
 * @author casti
 */
public class PersistenciaAtenciones {

    private final PersistenciaDatos persistencia;

    public PersistenciaAtenciones(PersistenciaDatos persistencia) {

        if (persistencia == null) {
            throw new IllegalArgumentException(
                    "La persistencia de datos es obligatoria.");
        }

        this.persistencia = persistencia;
    }

    public void guardar(NodoAtencion primero) throws IOException {

        StringBuilder json = new StringBuilder();
        json.append("{\"atenciones\":[");

        NodoAtencion actual = primero;
        boolean primerRegistro = true;

        while (actual != null) {

            if (!primerRegistro) {
                json.append(",");
            }

            json.append(convertirAtencion(actual.getDato()));

            primerRegistro = false;
            actual = actual.getSiguiente();
        }

        json.append("]}");

        persistencia.guardarAtenciones(json.toString());
    }

    public NodoAtencion cargar() throws IOException {

        String contenido = persistencia.cargarAtenciones();

        if (contenido == null) {
            return null;
        }

        JsonValor raiz = new LectorJson(contenido).leer();
        raiz.exigirTipo(JsonValor.OBJETO);

        JsonValor registros = raiz.campo("atenciones");
        registros.exigirTipo(JsonValor.ARREGLO);

        NodoAtencion primero = null;
        NodoAtencion ultimo = null;

        JsonValor actual = registros.getPrimerHijo();

        while (actual != null) {

            Atencion atencion = reconstruirAtencion(actual);
            NodoAtencion nuevo = new NodoAtencion(atencion);

            if (primero == null) {
                primero = nuevo;
            } else {
                ultimo.setSiguiente(nuevo);
            }

            ultimo = nuevo;
            actual = actual.getSiguiente();
        }

        return primero;
    }

    // Convierte una atencion a JSON.
    String convertirAtencion(Atencion atencion) {

        StringBuilder json = new StringBuilder();

        json.append("{");

        json.append("\"tiquete\":");
        json.append(convertirTiquete(atencion.getTiquete()));

        json.append(",\"medicoAsignado\":");
        json.append(EscritorJson.cadena(
                atencion.getMedicoAsignado()));

        json.append(",\"diagnostico\":");
        json.append(EscritorJson.cadena(
                atencion.getDiagnostico()));

        json.append(",\"medicamentos\":");
        json.append(EscritorJson.cadena(
                atencion.getMedicamentos()));

        json.append(",\"observaciones\":");
        json.append(EscritorJson.cadena(
                atencion.getObservaciones()));

        json.append(",\"procedimientos\":");
        json.append(EscritorJson.cadena(
                atencion.getProcedimientos()));

        json.append(",\"espacioAsignado\":");
        json.append(EscritorJson.cadena(
                atencion.getEspacioAsignado()));

        json.append(",\"costo\":");
        json.append(atencion.getCosto());

        json.append(",\"estado\":");
        json.append(EscritorJson.cadena(
                atencion.getEstado()));

        json.append("}");

        return json.toString();
    }

    private String convertirTiquete(Tiquete tiquete) {

        Paciente paciente = tiquete.getPaciente();

        StringBuilder json = new StringBuilder();

        json.append("{");

        json.append("\"id\":");
        json.append(EscritorJson.cadena(tiquete.getId()));

        json.append(",\"fechaHoraIngreso\":");
        json.append(EscritorJson.cadena(
                tiquete.getFechaHoraIngreso()));

        json.append(",\"fechaHoraAtencion\":");
        json.append(EscritorJson.cadena(
                tiquete.getFechaHoraAtencion()));

        json.append(",\"prioridad\":");
        json.append(EscritorJson.cadena(
                tiquete.getPrioridad()));

        json.append(",\"tipoAtencion\":");
        json.append(EscritorJson.cadena(
                tiquete.getTipoAtencion()));

        json.append(",\"atencion\":");
        json.append(EscritorJson.cadena(
                tiquete.getAtencion()));

        json.append(",\"paciente\":{");

        json.append("\"identificacion\":");
        json.append(EscritorJson.cadena(
                paciente.getIdentificacion()));

        json.append(",\"nombre\":");
        json.append(EscritorJson.cadena(
                paciente.getNombre()));

        json.append(",\"edad\":");
        json.append(paciente.getEdad());

        json.append(",\"tipoSeguro\":");
        json.append(EscritorJson.cadena(
                paciente.getTipoSeguro()));

        json.append("}");

        json.append("}");

        return json.toString();
    }

    // Reconstruye una atencion desde JSON.
    Atencion reconstruirAtencion(JsonValor valor) {

        valor.exigirTipo(JsonValor.OBJETO);

        Tiquete tiquete = reconstruirTiquete(
                valor.campo("tiquete"));

        String medico = valor.campo(
                "medicoAsignado").comoTexto();

        String diagnostico = valor.campo(
                "diagnostico").comoTexto();

        String medicamentos = valor.campo(
                "medicamentos").comoTexto();

        String observaciones = valor.campo(
                "observaciones").comoTexto();

        String procedimientos = valor.campo(
                "procedimientos").comoTexto();

        String espacio = valor.campo(
                "espacioAsignado").comoTexto();

        double costo = Double.parseDouble(
                valor.campo("costo").comoNumeroTexto());

        String estado = valor.campo(
                "estado").comoTexto();

        return new Atencion(
                tiquete,
                medico,
                diagnostico,
                medicamentos,
                observaciones,
                procedimientos,
                espacio,
                costo,
                estado
        );
    }

    private Tiquete reconstruirTiquete(JsonValor valor) {

        valor.exigirTipo(JsonValor.OBJETO);

        JsonValor pacienteJson = valor.campo("paciente");

        Paciente paciente = new Paciente(
                pacienteJson.campo("identificacion").comoTexto(),
                pacienteJson.campo("nombre").comoTexto(),
                pacienteJson.campo("edad").comoEntero(),
                pacienteJson.campo("tipoSeguro").comoTexto()
        );

        return new Tiquete(
                valor.campo("id").comoTexto(),
                paciente,
                valor.campo("fechaHoraIngreso").comoTexto(),
                valor.campo("fechaHoraAtencion").comoTexto(),
                valor.campo("prioridad").comoTexto(),
                valor.campo("tipoAtencion").comoTexto(),
                valor.campo("atencion").comoTexto()
        );
    }
}