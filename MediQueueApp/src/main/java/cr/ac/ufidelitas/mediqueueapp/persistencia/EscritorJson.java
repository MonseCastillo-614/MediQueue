
package cr.ac.ufidelitas.mediqueueapp.persistencia;

public class EscritorJson {

    private EscritorJson() {
    }

    public static String cadena(String dato) {
        if (dato == null) {
            return "null";
        }

        StringBuilder salida = new StringBuilder("\"");

        for (int i = 0; i < dato.length(); i++) {
            char c = dato.charAt(i);

            switch (c) {
                case '"':
                    salida.append("\\\"");
                    break;
                case '\\':
                    salida.append("\\\\");
                    break;
                case '\n':
                    salida.append("\\n");
                    break;
                case '\r':
                    salida.append("\\r");
                    break;
                case '\t':
                    salida.append("\\t");
                    break;
                case '\b':
                    salida.append("\\b");
                    break;
                case '\f':
                    salida.append("\\f");
                    break;
                default:
                    if (c < 32) {
                        String hex = Integer.toHexString(c);
                        salida.append("\\u");

                        for (int j = hex.length(); j < 4; j++) {
                            salida.append('0');
                        }

                        salida.append(hex);
                    } else {
                        salida.append(c);
                    }
            }
        }

        return salida.append('"').toString();
    }
}
