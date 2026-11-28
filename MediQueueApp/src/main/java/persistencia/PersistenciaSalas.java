/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;
import estructuras.ColaTiquetes;
import modelo.Paciente;
import modelo.Tiquete;
import salas.Consultorio;
import salas.GestorSalas;
import salas.Sala;
import java.io.IOException;
/**
 *
 * @author casti
 */
public class PersistenciaSalas {

    private final PersistenciaDatos persistencia;

    public PersistenciaSalas(PersistenciaDatos persistencia) {

        if (persistencia == null) {
            throw new IllegalArgumentException(
                    "La persistencia es obligatoria.");
        }

        this.persistencia = persistencia;
    }

    // =================================================
    // GUARDAR SALAS, EMERGENCIAS Y CONSULTORIOS
    // =================================================

    public void guardar(GestorSalas gestor) throws IOException {

        if (gestor == null) {
            throw new IllegalArgumentException(
                    "El gestor de salas es obligatorio.");
        }

        StringBuilder json = new StringBuilder();

        json.append("{");

        json.append("\"salas\":[");

        boolean primero = true;

        for (int i = 1;
                i <= gestor.getConfiguracion().getCantidadSalas();
                i++) {

            Sala sala = gestor.buscarSala(i);

            if (sala != null) {

                if (!primero) {
                    json.append(",");
                }

                json.append(convertirSala(sala));

                primero = false;
            }
        }

        json.append("],");

        json.append("\"emergencias\":[");

        primero = true;

        for (int i = 1;
                i <= gestor.getConfiguracion().getCantidadEmergencias();
                i++) {

            Sala emergencia = gestor.buscarSalaEmergencia(i);

            if (emergencia != null) {

                if (!primero) {
                    json.append(",");
                }

                json.append(convertirSala(emergencia));

                primero = false;
            }
        }

        json.append("],");

        json.append("\"consultorios\":[");

        primero = true;

        for (int i = 1;
                i <= gestor.getConfiguracion().getCantidadConsultorios();
                i++) {

            Consultorio consultorio = gestor.buscarConsultorio(i);

            if (consultorio != null) {

                if (!primero) {
                    json.append(",");
                }

                json.append(convertirConsultorio(consultorio));

                primero = false;
            }
        }

        json.append("]}");

        persistencia.guardarSalas(json.toString());
    }

    // =================================================
    // CONVERTIR UNA SALA A JSON
    // =================================================

    private String convertirSala(Sala sala) {

        StringBuilder json = new StringBuilder();

        json.append("{");

        json.append("\"identificador\":");
        json.append(sala.getIdentificador());

        json.append(",\"capacidad\":");
        json.append(sala.getCapacidad());

        json.append(",\"tipo\":");
        json.append(EscritorJson.cadena(sala.getTipo()));

        json.append(",\"medicoResponsable\":");
        json.append(EscritorJson.cadena(
                sala.getMedicoResponsable()));

        json.append(",\"enfermeraCargo\":");
        json.append(EscritorJson.cadena(
                sala.getEnfermeraCargo()));

        json.append(",\"cola\":");
        json.append(convertirCola(sala.getColaEspera()));

        json.append("}");

        return json.toString();
    }

    // =================================================
    // CONVERTIR UN CONSULTORIO A JSON
    // =================================================

    private String convertirConsultorio(Consultorio consultorio) {

        StringBuilder json = new StringBuilder();

        json.append("{");

        json.append("\"identificador\":");
        json.append(consultorio.getIdentificador());

        json.append(",\"capacidad\":");
        json.append(consultorio.getCapacidad());

        json.append(",\"especialidad\":");
        json.append(EscritorJson.cadena(
                consultorio.getEspecialidad()));

        json.append(",\"medicoResponsable\":");
        json.append(EscritorJson.cadena(
                consultorio.getMedicoResponsable()));

        json.append(",\"preferencial\":");
        json.append(EscritorJson.cadena(
                String.valueOf(consultorio.esPreferencial())));

        json.append(",\"cola\":");
        json.append(convertirCola(consultorio.getColaEspera()));

        json.append("}");

        return json.toString();
    }

    // =================================================
    // CONVERTIR UNA COLA DE TIQUETES A JSON
    // =================================================

    private String convertirCola(ColaTiquetes cola) {

        StringBuilder json = new StringBuilder();

        json.append("[");

        for (int i = 0; i < cola.tamano(); i++) {

            if (i > 0) {
                json.append(",");
            }

            json.append(convertirTiquete(cola.obtener(i)));
        }

        json.append("]");

        return json.toString();
    }

    // =================================================
    // CONVERTIR UN TIQUETE A JSON
    // =================================================

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

    // =================================================
    // CARGAR LAS SALAS Y CONSULTORIOS
    // =================================================

    public void cargar(GestorSalas gestor) throws IOException {

        if (gestor == null) {
            throw new IllegalArgumentException(
                    "El gestor de salas es obligatorio.");
        }

        String contenido = persistencia.cargarSalas();

        if (contenido == null) {
            return;
        }

        JsonValor raiz = new LectorJson(contenido).leer();

        raiz.exigirTipo(JsonValor.OBJETO);

        cargarSalasMedicas(
                raiz.campo("salas"), gestor);

        cargarSalasEmergencia(
                raiz.campo("emergencias"), gestor);

        cargarConsultorios(
                raiz.campo("consultorios"), gestor);
    }

    // =================================================
    // RECUPERAR SALAS MEDICAS
    // =================================================

    private void cargarSalasMedicas(
            JsonValor registros,
            GestorSalas gestor) {

        registros.exigirTipo(JsonValor.ARREGLO);

        JsonValor actual = registros.getPrimerHijo();

        while (actual != null) {

            int id = actual.campo(
                    "identificador").comoEntero();

            Sala sala = gestor.buscarSala(id);

            if (sala == null) {
                throw new IllegalArgumentException(
                        "No existe la sala configurada: " + id);
            }

            reconstruirSala(actual, sala);

            actual = actual.getSiguiente();
        }
    }

    // =================================================
    // RECUPERAR SALAS DE EMERGENCIA
    // =================================================

    private void cargarSalasEmergencia(
            JsonValor registros,
            GestorSalas gestor) {

        registros.exigirTipo(JsonValor.ARREGLO);

        JsonValor actual = registros.getPrimerHijo();

        while (actual != null) {

            int id = actual.campo(
                    "identificador").comoEntero();

            Sala sala = gestor.buscarSalaEmergencia(id);

            if (sala == null) {
                throw new IllegalArgumentException(
                        "No existe la sala de emergencia: " + id);
            }

            reconstruirSala(actual, sala);

            actual = actual.getSiguiente();
        }
    }

    // =================================================
    // RECUPERAR LOS DATOS DE UNA SALA
    // =================================================

    private void reconstruirSala(
            JsonValor valor,
            Sala sala) {

        int capacidad = valor.campo(
                "capacidad").comoEntero();

        sala.setCapacidad(capacidad);

        sala.setMedicoResponsable(
                valor.campo("medicoResponsable").comoTexto());

        sala.setEnfermeraCargo(
                valor.campo("enfermeraCargo").comoTexto());

        cargarCola(
                valor.campo("cola"),
                sala.getColaEspera());
    }

    // =================================================
    // RECUPERAR CONSULTORIOS
    // =================================================

    private void cargarConsultorios(
            JsonValor registros,
            GestorSalas gestor) {

        registros.exigirTipo(JsonValor.ARREGLO);

        JsonValor actual = registros.getPrimerHijo();

        while (actual != null) {

            int id = actual.campo(
                    "identificador").comoEntero();

            Consultorio consultorio =
                    gestor.buscarConsultorio(id);

            if (consultorio == null) {
                throw new IllegalArgumentException(
                        "No existe el consultorio configurado: " + id);
            }

            reconstruirConsultorio(actual, consultorio);

            actual = actual.getSiguiente();
        }
    }

    // =================================================
    // RECUPERAR LOS DATOS DE UN CONSULTORIO
    // =================================================

    private void reconstruirConsultorio(
            JsonValor valor,
            Consultorio consultorio) {

        int capacidad = valor.campo(
                "capacidad").comoEntero();

        consultorio.setCapacidad(capacidad);

        consultorio.setEspecialidad(
                valor.campo("especialidad").comoTexto());

        consultorio.setMedicoResponsable(
                valor.campo("medicoResponsable").comoTexto());

        cargarCola(
                valor.campo("cola"),
                consultorio.getColaEspera());
    }

    // =================================================
    // RECUPERAR UNA COLA DE TIQUETES
    // =================================================

    private void cargarCola(
            JsonValor registros,
            ColaTiquetes cola) {

        registros.exigirTipo(JsonValor.ARREGLO);

        if (!cola.estaVacia()) {
            throw new IllegalStateException(
                    "La cola debe estar vacia antes de cargarla.");
        }

        JsonValor actual = registros.getPrimerHijo();

        while (actual != null) {

            Tiquete tiquete = reconstruirTiquete(actual);

            cola.encolar(tiquete);

            actual = actual.getSiguiente();
        }
    }

    // =================================================
    // RECONSTRUIR UN TIQUETE Y SU PACIENTE
    // =================================================

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

