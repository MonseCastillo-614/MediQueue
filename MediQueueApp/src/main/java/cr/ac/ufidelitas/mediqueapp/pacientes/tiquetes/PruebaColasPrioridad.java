package cr.ac.ufidelitas.mediqueapp.pacientes.tiquetes;

import java.util.List;

/**
 * 
 * 
 * @author aedgell555
 */
public class PruebaColasPrioridad {

    private static int pruebas = 0;

    public static void main(String[] args) {
        ordenPorPrioridad();
        preferencialAntesQueNormal();
        fifoAIgualdad();
        colaVaciaLanzaExcepcion();
        gestorRegistraYAtiende();
        gestorRechazaDuplicadosYDatosInvalidos();
        gestorCargaPendientes();
        System.out.println("OK: " + pruebas + " pruebas pasaron.");
    }

    private static Tiquete t(String id, String ident, int edad,
            String prioridad, String tipo, String atencion) {
        return new Tiquete(id, new Paciente(ident, "Paciente " + id, edad, "CCSS"),
                prioridad, tipo, atencion);
    }

    private static void verificar(boolean condicion, String mensaje) {
        pruebas++;
        if (!condicion) {
            throw new AssertionError("FALLO: " + mensaje);
        }
        System.out.println("  ok - " + mensaje);
    }

    private static void ordenPorPrioridad() {
        System.out.println("Orden por prioridad medica");
        ColaPrioridad cola = new ColaPrioridad();
        cola.encolar(t("1", "100", 30, "Baja", "C", "N"));
        cola.encolar(t("2", "101", 30, "Media", "C", "N"));
        cola.encolar(t("3", "102", 30, "Critica", "E", "N"));
        cola.encolar(t("4", "103", 30, "Alta", "S", "N"));

        verificar(cola.tamano() == 4, "tamano 4 tras encolar 4");
        verificar(cola.frente().getId().equals("3"), "frente es la Critica");
        verificar(cola.desencolar().getId().equals("3"), "sale Critica");
        verificar(cola.desencolar().getId().equals("4"), "sale Alta");
        verificar(cola.desencolar().getId().equals("2"), "sale Media");
        verificar(cola.desencolar().getId().equals("1"), "sale Baja");
        verificar(cola.estaVacia(), "cola vacia al final");
    }

    private static void preferencialAntesQueNormal() {
        System.out.println("Preferencial antes que normal (misma prioridad)");
        ColaPrioridad cola = new ColaPrioridad();
        cola.encolar(t("N1", "200", 30, "Media", "C", "N"));
        cola.encolar(t("P1", "201", 70, "Media", "C", "P"));
        cola.encolar(t("A1", "202", 30, "Alta", "C", "N"));

        verificar(cola.desencolar().getId().equals("A1"),
                "Alta normal sale antes que Media preferencial");
        verificar(cola.desencolar().getId().equals("P1"),
                "Media preferencial sale antes que Media normal");
        verificar(cola.desencolar().getId().equals("N1"), "Media normal sale al final");
    }

    private static void fifoAIgualdad() {
        System.out.println("FIFO a igual prioridad y categoria");
        ColaPrioridad cola = new ColaPrioridad();
        cola.encolar(t("a", "300", 30, "Baja", "C", "N"));
        cola.encolar(t("b", "301", 30, "Baja", "C", "N"));
        cola.encolar(t("c", "302", 30, "Baja", "C", "N"));
        List<Tiquete> orden = cola.aLista();
        verificar(orden.get(0).getId().equals("a")
                && orden.get(1).getId().equals("b")
                && orden.get(2).getId().equals("c"), "aLista respeta orden de llegada");
        verificar(cola.tamano() == 3, "aLista no modifica la cola");
    }

    private static void colaVaciaLanzaExcepcion() {
        System.out.println("Cola vacia");
        ColaPrioridad cola = new ColaPrioridad();
        boolean lanzo = false;
        try {
            cola.desencolar();
        } catch (java.util.NoSuchElementException ex) {
            lanzo = true;
        }
        verificar(lanzo, "desencolar en cola vacia lanza excepcion");
        lanzo = false;
        try {
            cola.frente();
        } catch (java.util.NoSuchElementException ex) {
            lanzo = true;
        }
        verificar(lanzo, "frente en cola vacia lanza excepcion");
    }

    private static void gestorRegistraYAtiende() {
        System.out.println("Gestor: registro y atencion");
        GestorTiquetes g = new GestorTiquetes();
        Tiquete a = g.registrarPaciente("1-1111-1111", "Ana", 25, "CCSS", "Baja", "C", "N");
        Tiquete b = g.registrarPaciente("2-2222-2222", "Beto", 80, "Privado", "Baja", "Consulta", "P");
        Tiquete c = g.registrarPaciente("3-3333-3333", "Carla", 40, "CCSS", "Critica", "E", "N");

        verificar(a.getId().equals("C-001") && b.getId().equals("C-002")
                && c.getId().equals("E-001"), "ids consecutivos por tipo");
        verificar(g.totalPendientes() == 3, "3 pendientes");
        verificar(!a.estaAtendido(), "tiquete nuevo no esta atendido");

        verificar(g.verSiguiente("C").getId().equals("C-002"),
                "en consultas el preferencial va primero");
        Tiquete atendido = g.atenderSiguiente("C");
        verificar(atendido.getId().equals("C-002") && atendido.estaAtendido(),
                "atender registra la hora de atencion");
        verificar(g.atenderSiguiente("E").esEmergenciaCritica(),
                "emergencia critica atendida");
        verificar(g.totalPendientes() == 1 && g.getAtendidos().size() == 2,
                "quedan 1 pendiente y 2 atendidos");
        verificar(!g.hayPendientes("S"), "sin pendientes en salas");

        boolean lanzo = false;
        try {
            g.atenderSiguiente("S");
        } catch (IllegalStateException ex) {
            lanzo = true;
        }
        verificar(lanzo, "atender sin pendientes lanza excepcion");
    }

    private static void gestorRechazaDuplicadosYDatosInvalidos() {
        System.out.println("Gestor: validaciones");
        GestorTiquetes g = new GestorTiquetes();
        g.registrarPaciente("1", "Ana", 25, "CCSS", "Media", "C", "N");

        boolean duplicado = false;
        try {
            g.registrarPaciente("1", "Ana", 25, "CCSS", "Alta", "S", "N");
        } catch (IllegalStateException ex) {
            duplicado = true;
        }
        verificar(duplicado, "no permite dos tiquetes pendientes del mismo paciente");

        boolean invalido = false;
        try {
            g.registrarPaciente("2", "Luis", 200, "CCSS", "Media", "C", "N");
        } catch (IllegalArgumentException ex) {
            invalido = true;
        }
        verificar(invalido, "edad invalida se rechaza");

        invalido = false;
        try {
            g.registrarPaciente("3", "Eva", 30, "CCSS", "Urgente", "C", "N");
        } catch (IllegalArgumentException ex) {
            invalido = true;
        }
        verificar(invalido, "prioridad invalida se rechaza");
        verificar(g.totalPendientes() == 1, "los rechazos no dejan tiquetes en cola");

        g.atenderSiguiente("C");
        g.registrarPaciente("1", "Ana", 25, "CCSS", "Media", "C", "N");
        verificar(g.totalPendientes() == 1, "tras ser atendido puede volver a registrarse");
    }

    private static void gestorCargaPendientes() {
        System.out.println("Gestor: carga de pendientes");
        GestorTiquetes g = new GestorTiquetes();
        java.util.List<Tiquete> guardados = new java.util.ArrayList<>();
        guardados.add(t("C-007", "10", 30, "Media", "C", "N"));
        guardados.add(t("C-008", "11", 30, "Alta", "C", "N"));
        g.cargarPendientes(guardados);

        verificar(g.verSiguiente("C").getId().equals("C-008"),
                "al cargar se reordena por prioridad");
        Tiquete nuevo = g.registrarPaciente("12", "Nuevo", 30, "CCSS", "Baja", "C", "N");
        verificar(nuevo.getId().equals("C-009"), "el contador continua despues del mayor id cargado");
        verificar(g.listarTodosPendientes().size() == 3, "listarTodosPendientes devuelve 3");
    }
}
