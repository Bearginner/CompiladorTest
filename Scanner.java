import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Predicate;
import java.util.List;

public class Scanner {
    private static Font fuente = new Font("Comic Sans", 16, 22);
    private static int LineaCont = 1;
    private static boolean isError = false;
    static ArrayList<String[]> tokens = new ArrayList<>();
    static ArrayList<String[]> simbolos = new ArrayList<>();

    private static String[] palabrasReservadas = {
           "clase", "leer", "mostrar", "asignar", 
           "calcular", "int", "double" };

    private static final List<Predicate<String>> VALIDADORES = Arrays.asList(
            Scanner::validaID,
            Scanner::validaNumero,
            Scanner::validaAritmeticos,
            Scanner::validaDelimitador);

    static Hashtable<String, String> symbols = new Hashtable<String, String>();

    static void escanear() {
        symbols.put("(", "Parentesis izquierdo");
        symbols.put(")", "Parentesis derecho");
        symbols.put("{", "Corchete izquierdo");
        symbols.put("}", "Corchete derecho");
        symbols.put(";", "Punto y coma");
        symbols.put("\"", "Slash invertido");
        symbols.put(",", "Coma");
        LineaCont = 1;
        isError = false;
        tokens.clear();
        simbolos.clear();
        Compilador.Limpiar();
        Compilador.error.setText("");
        String[] lineas = Compilador.txt.getText().split("\\r?\\n");

        for (String linea : lineas) {
            validarToken(linea);
            LineaCont++;
            if (isError)
                return;
        }

        ArrayList<JLabel> labels = new ArrayList<JLabel>();
        ArrayList<JLabel> simbolabels = new ArrayList<JLabel>();

        for (String[] token : tokens) {
            labels.add(new JLabel(token[0]));
            labels.add(new JLabel(token[1]));
        }

        for (String[] simbolo : simbolos) {
            simbolabels.add(new JLabel(simbolo[0]));
            simbolabels.add(new JLabel(simbolo[1]));
        }

        for (JLabel label : labels) {
            label.setMinimumSize(new Dimension(100, 30));
            label.setMaximumSize(new Dimension(100, 30));
            label.setPreferredSize(new Dimension(100, 30));
            label.setFont(fuente);
            Compilador.listado.add(label);
        }

        for (JLabel simbolo : simbolabels) {
            simbolo.setMinimumSize(new Dimension(100, 30));
            simbolo.setMaximumSize(new Dimension(100, 30));
            simbolo.setPreferredSize(new Dimension(100, 30));
            simbolo.setFont(fuente);
            Compilador.simbolos.add(simbolo);
        }

        Compilador.error.setText("");
        Compilador.valida();
        Compilador.listado.update(Compilador.listado.getGraphics());
        Compilador.simbolos.update(Compilador.simbolos.getGraphics());
    }

    private static void validarToken(String linea) {
        String token = "", cadena = "";
        linea = linea.trim();
        if (linea.contains("\"")) {
            cadena = linea.replaceAll(".*\"([^\"]*)\".*", "$1 ");
            cadena = cadena.replaceAll(" ", "_");
            linea = linea.replaceAll("\"([^\"]*)\"", "\"" + cadena + "\"");
        }

        linea = linea.replaceAll("(.);", "$1 ;");// 0; -> 0 ;
        linea = linea.replaceAll("\"(\\S*)", "\" $1");// "texto -> " texto
        linea = linea.replaceAll("(\\S*)\"", "$1 \"");// texto" -> texto "
        linea = linea.replaceAll("\\(\"", "( \"");// (" -> ( "
        linea = linea.replaceAll("\",", "\" ,");// ", -> " ,
        linea = linea.replaceAll("\"\\)", "\" )");// ") -> " )
        linea = linea.replaceAll("([0-9])\\)", "$1 )");// ") -> " )
        linea = linea.replaceAll("([a-zA-Z]+)\\(", "$1 (");// texto( -> texto (
        linea = linea.replaceAll("(\\-\\-|\\+\\+|[a-zA-Z]{1}[a-zA-Z0-9]*)\\)", "$1 )");// ++) -> ++ )
        linea = linea.replaceAll("([a-zA-Z]{1}[a-zA-Z0-9]*)(\\-\\-|\\+\\+)", "$1 $2");// ID++ -> ID ++
        linea = linea.replaceAll("(\\()([a-zA-Z]+|\\))", "$1 $2");// (texto -> ( texto
        linea = linea.replaceAll("\\s{2,}", " ");// elimina 2 o mas espacios y deja uno solo
        linea += " ";
        int length = linea.length();

        for (int i = 0; i < length; i++) {
            char actual = linea.charAt(i);
            if (actual == ' ') {
                String tokenAux = token;
                token = "";
                if (tokenAux.contains("_")) {
                    cadena = cadena.replaceAll("_", " ");
                    cadena = cadena.replaceAll("\"", "");
                    tokens.add(new String[] { cadena, "cadena", String.valueOf(LineaCont ) });
                    tokenAux = "";
                    continue;
                }
                boolean valido = false;
                for (Predicate<String> validador : VALIDADORES)
                    if (validador.test(tokenAux)) {
                        valido = true;
                        break;
                    }
                if (length < 1) {
                    Compilador.error.setForeground(Color.red);
                    Compilador.error.setText("Programa vacío.");
                    return;
                    }

                if (!valido) {
                    mensajeError("Token inválido: '" + tokenAux + "'");
                    return;
                }
                continue;
            }
            token += actual;
        }
    }

    private static boolean validaID(String token) {
        Pattern patron = Pattern.compile("[a-zA-Z]{1}[a-zA-Z0-9]*");
        Matcher matcher = patron.matcher(token);
        if (!matcher.matches())
            return false;
        token = token.toLowerCase();
        if (Arrays.asList(palabrasReservadas).contains(token))
            tokens.add(new String[] { token, "Palabra reservada", String.valueOf(LineaCont )});
        else {
            tokens.add(new String[] { token, "Identificador", String.valueOf(LineaCont ) });
            if(!existe(token))
            simbolos.add(new String[] { token, "Simbolo"});              
        }
        token = "";
        return true;
    }
    
    private static boolean existe(String identificador){
        for (int i=0; i < simbolos.size(); i++){
            if(simbolos.get(i)[0].equals(identificador)){
                return true;
            }
        }
        return false;
    }

    private static boolean validaNumero(String token) {
        Pattern patron = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher matcher = patron.matcher(token);
        if (!matcher.matches())
            return false;
        tokens.add(new String[] { token, "Numero", String.valueOf(LineaCont ) });
        token = "";
        return true;
    }

    private static boolean validaAritmeticos(String token) {
        Pattern patron = Pattern.compile("(\\+|\\-|\\/|\\*|\\^|\\%|\\+\\+|\\-\\-|=){1}");
        Matcher matcher = patron.matcher(token);
        if (!matcher.matches())
            return false;
        if (token.equals("="))
            tokens.add(new String[] {token, "Operador de asignacion", String.valueOf(LineaCont ) });
        else
            tokens.add(new String[] {token, "Operador aritmetico", String.valueOf(LineaCont ) });
        token = "";
        return true;
    }

    private static boolean validaDelimitador(String token) {
        Pattern patron = Pattern.compile("(\\{|\\}|\\(|\\)|;|\"|,){1}");
        Matcher matcher = patron.matcher(token);
        if (!matcher.matches())
            return false;
        tokens.add(new String[] { token, symbols.get(token), String.valueOf(LineaCont ) });
        token = "";
        return true;
    }

    private static void mensajeError(String mensaje) {
        isError = true;
        Compilador.error.setForeground(Color.red);
        Compilador.error.setText(mensaje + " Linea: " + LineaCont);
        tokens.clear();
        Compilador.Limpiar();
    }
}
