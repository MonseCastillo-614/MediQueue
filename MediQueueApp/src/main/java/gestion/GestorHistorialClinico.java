
package gestion;

import modelo.Atencion;
import modelo.HistorialPaciente;
import modelo.RegistroHistorial;
import estructuras.NodoHistorialPaciente;
import estructuras.NodoPila;
import java.io.FileWriter;
import java.io.IOException;

public class GestorHistorialClinico {

    // Lista enlazada simple para almacenar el historial de TODOS los pacientes
    private NodoHistorialPaciente inicioHistoriales;

    public GestorHistorialClinico() {
        this.inicioHistoriales = null;
    }

    
    public void registrarMovimientoEnHistorial(Atencion atencion, String estadoResultado) {
        if (atencion == null || atencion.getTiquete() == null || atencion.getTiquete().getPaciente() == null) {
            throw new IllegalArgumentException("Datos insuficientes para registrar en el historial.");
        }

        String identificacion = atencion.getTiquete().getPaciente().getIdentificacion();
        
        // Buscar el historial del paciente, si no existe lo crea
        HistorialPaciente historial = buscarOCrearHistorial(identificacion);
        
        // Crear el nuevo registro
        RegistroHistorial nuevoRegistro = new RegistroHistorial(atencion, estadoResultado);
        
        // Empujar el registro en la pila del paciente
        historial.agregarRegistro(nuevoRegistro);
    }

   
    public RegistroHistorial consultarUltimoRegistro(String identificacion) {
        HistorialPaciente historial = buscarHistorial(identificacion);
        if (historial != null && !historial.getRegistros().estaVacia()) {
            return historial.getRegistros().consultarTope();
        }
        return null;
    }

    
    public void cancelarAtencion(Atencion atencion) {
        atencion.setEstado(Atencion.CANCELADA);
        registrarMovimientoEnHistorial(atencion, Atencion.CANCELADA);
    }

   
    public void reingresarPaciente(Atencion atencionAnterior, String nuevoEspacio, String nuevoMedico) {
        Atencion nuevaAtencion = new Atencion(
                atencionAnterior.getTiquete(),
                nuevoMedico,
                "Pendiente (Reingreso)",
                "Pendiente",
                "Pendiente",
                "Pendiente",
                nuevoEspacio,
                atencionAnterior.getCosto(),
                Atencion.REINGRESO
        );
        
        registrarMovimientoEnHistorial(nuevaAtencion, Atencion.REINGRESO);
    }

    
    public String mostrarUltimosRegistros(String identificacion, int n) {
        HistorialPaciente historial = buscarHistorial(identificacion);
        if (historial == null || historial.getRegistros().estaVacia()) {
            return "No hay registros clínicos para el paciente.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Últimos ").append(n).append(" registros de ").append(identificacion).append(" ---\n");

        NodoPila actual = historial.getRegistros().getTope();
        int contador = 0;

        while (actual != null && contador < n) {
            RegistroHistorial registro = actual.getDato();
            sb.append(contador + 1).append(". Fecha: ").append(registro.getAtencion().getTiquete().getFechaHoraAtencion())
              .append(" | Médico: ").append(registro.getAtencion().getMedicoAsignado())
              .append(" | Estado: ").append(registro.getResultado()).append("\n");
            
            actual = actual.getSiguiente();
            contador++;
        }
        return sb.toString();
    }

    
    public String obtenerHistorialCompleto(String identificacion) {
        HistorialPaciente historial = buscarHistorial(identificacion);
        if (historial == null || historial.getRegistros().estaVacia()) {
            return "No hay registros clínicos para el paciente con identificación: " + identificacion;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Historial Clínico Completo de ").append(identificacion).append(" ---\n");

        NodoPila actual = historial.getRegistros().getTope();
        int contador = 1;
        
        while (actual != null) {
            RegistroHistorial registro = actual.getDato();
            sb.append(contador).append(". Fecha: ").append(registro.getAtencion().getTiquete().getFechaHoraAtencion())
              .append(" | Médico: ").append(registro.getAtencion().getMedicoAsignado())
              .append(" | Diagnóstico: ").append(registro.getAtencion().getDiagnostico())
              .append(" | Estado: ").append(registro.getResultado()).append("\n");
            
            actual = actual.getSiguiente();
            contador++;
        }
        return sb.toString();
    }

    
    public void exportarHistorialFisico(String identificacion) {
        HistorialPaciente historial = buscarHistorial(identificacion);
        if (historial == null || historial.getRegistros().estaVacia()) {
            System.out.println("No hay historial para exportar.");
            return;
        }

        String nombreArchivo = "historial_" + identificacion + ".txt";
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            writer.write("=== HISTORIAL CLÍNICO: " + identificacion + " ===\n\n");
            
            NodoPila actual = historial.getRegistros().getTope();
            while (actual != null) {
                RegistroHistorial registro = actual.getDato();
                writer.write("Fecha: " + registro.getAtencion().getTiquete().getFechaHoraAtencion() + "\n");
                writer.write("Médico: " + registro.getAtencion().getMedicoAsignado() + "\n");
                writer.write("Diagnóstico: " + registro.getAtencion().getDiagnostico() + "\n");
                writer.write("Medicamentos: " + registro.getAtencion().getMedicamentos() + "\n");
                writer.write("Estado: " + registro.getResultado() + "\n");
                writer.write("----------------------------------------\n");
                actual = actual.getSiguiente();
            }
            System.out.println("Historial exportado correctamente en: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al exportar el historial: " + e.getMessage());
        }
    }

    
    public boolean puedeReingresarASalon(String identificacion) {
        HistorialPaciente historial = buscarHistorial(identificacion);
        if (historial == null || historial.getRegistros().estaVacia()) {
            return false;
        }

        NodoPila actual = historial.getRegistros().getTope();
        while (actual != null) {
            if (Atencion.FINALIZADA.equals(actual.getDato().getResultado())) {
                return true; 
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    // --- Métodos para manejar la lista de historiales ---

    private HistorialPaciente buscarHistorial(String identificacion) {
        NodoHistorialPaciente actual = inicioHistoriales;
        while (actual != null) {
            
            if (actual.getDato().getIdentificacion().equalsIgnoreCase(identificacion)) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    private HistorialPaciente buscarOCrearHistorial(String identificacion) {
        HistorialPaciente encontrado = buscarHistorial(identificacion);
        if (encontrado != null) {
            return encontrado;
        }

        HistorialPaciente nuevoHistorial = new HistorialPaciente(identificacion);
        NodoHistorialPaciente nuevoNodo = new NodoHistorialPaciente(nuevoHistorial);

        if (inicioHistoriales == null) {
            inicioHistoriales = nuevoNodo;
        } else {
            nuevoNodo.setSiguiente(inicioHistoriales);
            inicioHistoriales = nuevoNodo;
        }

        return nuevoHistorial;
    }

    // --- MÉTODOS AÑADIDOS PARA SINCRONIZAR LA PERSISTENCIA JSON ---

    public void setInicioHistoriales(NodoHistorialPaciente inicio) {
        this.inicioHistoriales = inicio;
    }

    public NodoHistorialPaciente getInicioHistoriales() {
        return this.inicioHistoriales;
    }
}