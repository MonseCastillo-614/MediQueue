package gestion;

public class Medicamento {
    
    private String nombre;
    private String usoRecomendado;
    private String efectosSecundarios;
    private String advertencias;

    public Medicamento(String nombre, String usoRecomendado, String efectosSecundarios, String advertencias) {
        this.nombre = nombre;
        this.usoRecomendado = usoRecomendado;
        this.efectosSecundarios = efectosSecundarios;
        this.advertencias = advertencias;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsoRecomendado() {
        return usoRecomendado;
    }

    public String getEfectosSecundarios() {
        return efectosSecundarios;
    }

    public String getAdvertencias() {
        return advertencias;
    }
}