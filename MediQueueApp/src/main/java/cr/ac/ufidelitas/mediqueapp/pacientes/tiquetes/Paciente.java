
package cr.ac.ufidelitas.mediqueapp.pacientes.tiquetes;

/**
 * 
 *
 * @author dahyi97
 */
public class Paciente {

    private String identificacion;
    private String nombre;
    private int edad;
    private String tipoSeguro;

    // Constructor vacío requerido para el JSON / Gson
    public Paciente() {
    }

    // Constructor principal con validaciones
    public Paciente(String identificacion, String nombre, int edad, String tipoSeguro) {
        validarTexto(identificacion, "La identificacion del paciente es obligatoria.");
        validarTexto(nombre, "El nombre del paciente es obligatorio.");
        validarEdad(edad);
        validarTexto(tipoSeguro, "El tipo de seguro es obligatorio.");

        this.identificacion = identificacion.trim();
        this.nombre = nombre.trim();
        this.edad = edad;
        this.tipoSeguro = tipoSeguro.trim();
    }

    private void validarTexto(String texto, String mensajeError) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    private void validarEdad(int edad) {
        if (edad < 0 || edad > 130) {
            throw new IllegalArgumentException(
                    "La edad del paciente debe estar entre 0 y 130 años.");
        }
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        validarTexto(identificacion, "La identificacion del paciente es obligatoria.");
        this.identificacion = identificacion.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        validarTexto(nombre, "El nombre del paciente es obligatorio.");
        this.nombre = nombre.trim();
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        validarEdad(edad);
        this.edad = edad;
    }

    public String getTipoSeguro() {
        return tipoSeguro;
    }

    public void setTipoSeguro(String tipoSeguro) {
        validarTexto(tipoSeguro, "El tipo de seguro es obligatorio.");
        this.tipoSeguro = tipoSeguro.trim();
    }

    // Indica si por edad (adulto mayor) aplica sugerencia de trato preferencial
    public boolean esAdultoMayor() {
        return edad >= 65;
    }

    @Override
    public String toString() {
        return "Paciente: " + nombre
                + " | ID: " + identificacion
                + " | Edad: " + edad
                + " | Seguro: " + tipoSeguro;
    }
}