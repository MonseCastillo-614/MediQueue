/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;
import estructuras.NodoHistorialPaciente;
import estructuras.NodoPila;
import modelo.Atencion;
import modelo.HistorialPaciente;
import modelo.RegistroHistorial;
import java.io.IOException;
/**
 *
 * @author casti
 */
public class PersistenciaHistorial {

    private final PersistenciaDatos persistencia;
    private final PersistenciaAtenciones persistenciaAtenciones;

    public PersistenciaHistorial(PersistenciaDatos persistencia) {

        if (persistencia == null) {
            throw new IllegalArgumentException(
                    "La persistencia es obligatoria.");
        }

        this.persistencia = persistencia;

        this.persistenciaAtenciones =
                new PersistenciaAtenciones(persistencia);
    }

    // Guarda todos los historiales de los pacientes.
    public void guardar(NodoHistorialPaciente primero)
            throws IOException {

        StringBuilder json = new StringBuilder();

        json.append("{\"historiales\":[");

        NodoHistorialPaciente actual = primero;
        boolean primerHistorial = true;

        while (actual != null) {

            if (!primerHistorial) {
                json.append(",");
            }

            HistorialPaciente historial = actual.getDato();

            json.append("{");

            json.append("\"identificacion\":");
            json.append(EscritorJson.cadena(
                    historial.getIdentificacion()));

            json.append(",\"registros\":[");

            NodoPila registro = historial.getRegistros().getTope();
            boolean primerRegistro = true;

            while (registro != null) {

                if (!primerRegistro) {
                    json.append(",");
                }

                RegistroHistorial dato = registro.getDato();

                json.append("{\"atencion\":");

                json.append(
                        persistenciaAtenciones.convertirAtencion(
                                dato.getAtencion()));

                json.append(",\"resultado\":");

                json.append(EscritorJson.cadena(
                        dato.getResultado()));

                json.append("}");

                primerRegistro = false;
                registro = registro.getSiguiente();
            }

            json.append("]}");

            primerHistorial = false;
            actual = actual.getSiguiente();
        }

        json.append("]}");

        persistencia.guardarHistorial(json.toString());
    }

    // Carga todos los historiales desde el archivo JSON.
    public NodoHistorialPaciente cargar() throws IOException {

        String contenido = persistencia.cargarHistorial();

        if (contenido == null) {
            return null;
        }

        JsonValor raiz = new LectorJson(contenido).leer();
        raiz.exigirTipo(JsonValor.OBJETO);

        JsonValor historiales = raiz.campo("historiales");
        historiales.exigirTipo(JsonValor.ARREGLO);

        NodoHistorialPaciente primero = null;
        NodoHistorialPaciente ultimo = null;

        JsonValor actual = historiales.getPrimerHijo();

        while (actual != null) {

            HistorialPaciente historial =
                    reconstruirHistorial(actual);

            NodoHistorialPaciente nuevo =
                    new NodoHistorialPaciente(historial);

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

    // Reconstruye el historial de un paciente.
    private HistorialPaciente reconstruirHistorial(
            JsonValor valor) {

        valor.exigirTipo(JsonValor.OBJETO);

        String identificacion = valor.campo(
                "identificacion").comoTexto();

        HistorialPaciente historial =
                new HistorialPaciente(identificacion);

        JsonValor registros = valor.campo("registros");
        registros.exigirTipo(JsonValor.ARREGLO);

        // Se utiliza una pila temporal para conservar
        // el orden original de los registros.
        NodoPila temporal = null;

        JsonValor actual = registros.getPrimerHijo();

        while (actual != null) {

            Atencion atencion =
                    persistenciaAtenciones.reconstruirAtencion(
                            actual.campo("atencion"));

            String resultado = actual.campo(
                    "resultado").comoTexto();

            RegistroHistorial registro =
                    new RegistroHistorial(atencion, resultado);

            NodoPila nuevo = new NodoPila(registro);

            nuevo.setSiguiente(temporal);
            temporal = nuevo;

            actual = actual.getSiguiente();
        }

        // Se recupera la pila original.
        while (temporal != null) {

            historial.agregarRegistro(temporal.getDato());

            temporal = temporal.getSiguiente();
        }

        return historial;
    }
}