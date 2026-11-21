/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author casti
 */
public class RegistroHistorial {

    private Atencion atencion;
    private String resultado;

    public RegistroHistorial() {
    }

    public RegistroHistorial(Atencion atencion, String resultado) {
        setAtencion(atencion);
        setResultado(resultado);
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException(
                    "El registro debe tener una atencion.");
        }

        this.atencion = atencion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {

        if (resultado == null || resultado.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El resultado es obligatorio.");
        }

        String valor = resultado.trim();

        if (valor.equalsIgnoreCase(Atencion.EN_ATENCION)) {
            this.resultado = Atencion.EN_ATENCION;
        } else if (valor.equalsIgnoreCase(Atencion.FINALIZADA)) {
            this.resultado = Atencion.FINALIZADA;
        } else if (valor.equalsIgnoreCase(Atencion.CANCELADA)) {
            this.resultado = Atencion.CANCELADA;
        } else if (valor.equalsIgnoreCase(Atencion.REINGRESO)) {
            this.resultado = Atencion.REINGRESO;
        } else {
            throw new IllegalArgumentException(
                    "El resultado del historial no es valido.");
        }
    }

    @Override
    public String toString() {
        return "Registro: "
                + atencion.getTiquete().getId()
                + " | Resultado: " + resultado;
    }
}