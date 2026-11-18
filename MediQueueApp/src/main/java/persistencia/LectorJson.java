package persistencia;

import persistencia.JsonValor;

public class LectorJson {

    private final String texto;
    private int posicion;

    public LectorJson(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException(
                "El texto JSON no puede ser null."
            );
        }

        this.texto = texto;
    }

    public JsonValor leer() {
        JsonValor resultado = valor();
        espacios();

        if (posicion != texto.length()) {
            throw error("Contenido extra despues del JSON");
        }

        return resultado;
    }

    private JsonValor valor() {
        espacios();

        if (posicion >= texto.length()) {
            throw error("JSON incompleto");
        }

        char c = texto.charAt(posicion);

        if (c == '{') {
            return objeto();
        }

        if (c == '[') {
            return arreglo();
        }

        if (c == '"') {
            return new JsonValor(
                JsonValor.CADENA, cadena()
            );
        }

        if (c == '-' || (c >= '0' && c <= '9')) {
            return numero();
        }

        if (coincide("true")) {
            return new JsonValor(
                JsonValor.BOOLEANO, "true"
            );
        }

        if (coincide("false")) {
            return new JsonValor(
                JsonValor.BOOLEANO, "false"
            );
        }

        if (coincide("null")) {
            return new JsonValor(
                JsonValor.NULO, null
            );
        }

        throw error("Valor JSON no reconocido");
    }

    private JsonValor objeto() {
        consumir('{');

        JsonValor resultado = new JsonValor(
            JsonValor.OBJETO, null
        );

        espacios();

        if (consumirSi('}')) {
            return resultado;
        }

        while (true) {
            espacios();

            if (posicion >= texto.length()
                    || texto.charAt(posicion) != '"') {
                throw error("Se esperaba el nombre de un campo");
            }

            String nombre = cadena();

            espacios();
            consumir(':');

            resultado.agregarHijo(nombre, valor());

            espacios();

            if (consumirSi('}')) {
                return resultado;
            }

            consumir(',');
        }
    }

    private JsonValor arreglo() {
        consumir('[');

        JsonValor resultado = new JsonValor(
            JsonValor.ARREGLO, null
        );

        espacios();

        if (consumirSi(']')) {
            return resultado;
        }

        while (true) {
            resultado.agregarHijo(null, valor());

            espacios();

            if (consumirSi(']')) {
                return resultado;
            }

            consumir(',');
        }
    }

    private String cadena() {
        consumir('"');

        StringBuilder salida = new StringBuilder();

        while (posicion < texto.length()) {
            char c = texto.charAt(posicion++);

            if (c == '"') {
                return salida.toString();
            }

            if (c < 32) {
                throw error(
                    "Caracter de control no permitido"
                );
            }

            if (c != '\\') {
                salida.append(c);
                continue;
            }

            if (posicion >= texto.length()) {
                throw error("Escape incompleto");
            }

            char escape = texto.charAt(posicion++);

            switch (escape) {
                case '"':
                    salida.append('"');
                    break;

                case '\\':
                    salida.append('\\');
                    break;

                case '/':
                    salida.append('/');
                    break;

                case 'b':
                    salida.append('\b');
                    break;

                case 'f':
                    salida.append('\f');
                    break;

                case 'n':
                    salida.append('\n');
                    break;

                case 'r':
                    salida.append('\r');
                    break;

                case 't':
                    salida.append('\t');
                    break;

                case 'u':
                    if (posicion + 4 > texto.length()) {
                        throw error("Unicode incompleto");
                    }

                    String codigo = texto.substring(
                        posicion, posicion + 4
                    );

                    try {
                        salida.append(
                            (char) Integer.parseInt(codigo, 16)
                        );
                    } catch (NumberFormatException ex) {
                        throw error("Unicode invalido");
                    }

                    posicion += 4;
                    break;

                default:
                    throw error("Escape no reconocido");
            }
        }

        throw error("Cadena sin cierre");
    }

    private JsonValor numero() {
        int inicio = posicion;

        consumirSi('-');

        if (consumirSi('0')) {
            if (posicion < texto.length()
                    && esDigitoAscii(texto.charAt(posicion))) {
                throw error("Numero con cero inicial");
            }
        } else {
            digitos();
        }

        if (consumirSi('.')) {
            digitos();
        }

        if (consumirSi('e') || consumirSi('E')) {
            if (!consumirSi('+')) {
                consumirSi('-');
            }

            digitos();
        }

        return new JsonValor(
            JsonValor.NUMERO,
            texto.substring(inicio, posicion)
        );
    }

    private void digitos() {
        int inicio = posicion;

        while (posicion < texto.length()
                && esDigitoAscii(texto.charAt(posicion))) {
            posicion++;
        }

        if (inicio == posicion) {
            throw error("Se esperaba un digito");
        }
    }

    private boolean esDigitoAscii(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean coincide(String literal) {
        if (texto.startsWith(literal, posicion)) {
            posicion += literal.length();
            return true;
        }

        return false;
    }

    private void espacios() {
        while (posicion < texto.length()) {
            char c = texto.charAt(posicion);

            if (c != ' ' && c != '\n'
                    && c != '\r' && c != '\t') {
                return;
            }

            posicion++;
        }
    }

    private boolean consumirSi(char requerido) {
        if (posicion < texto.length()
                && texto.charAt(posicion) == requerido) {
            posicion++;
            return true;
        }

        return false;
    }

    private void consumir(char requerido) {
        if (!consumirSi(requerido)) {
            throw error("Se esperaba: " + requerido);
        }
    }

    private IllegalArgumentException error(String detalle) {
        return new IllegalArgumentException(
            detalle + " (posicion " + posicion + ")"
        );
    }
}