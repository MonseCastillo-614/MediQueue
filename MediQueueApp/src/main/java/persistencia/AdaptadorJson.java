package persistencia;

/**
 * Permite convertir objetos del proyecto a JSON
 * y reconstruirlos posteriormente.
 */
public interface AdaptadorJson<T> {

    String convertirAJson(T datos);

    T convertirDesdeJson(String json);
}