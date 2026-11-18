/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author casti
 */
public class Usuario {

    // Guarda el nombre que se utiliza para iniciar sesion
    private String nombreUsuario;

    // Almacena la contraseña del usuario
    private String contrasena;

    // Indica el tipo de usuario dentro del sistema
    private String rol;

    // Crea un usuario con sus datos de acceso
    public Usuario(String nombreUsuario, String contrasena, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Devuelve el nombre de usuario
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    // Permite modificar el nombre de usuario
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    // Retorna la contraseña registrada
    public String getContrasena() {
        return contrasena;
    }

    // Actualiza la contraseña del usuario
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // Indica el rol asignado al usuario
    public String getRol() {
        return rol;
    }

    // Cambia el rol del usuario
    public void setRol(String rol) {
        this.rol = rol;
    }
}