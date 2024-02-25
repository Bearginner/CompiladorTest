
package compilador;

import java.awt.Color;
import java.util.ArrayList;

public class Parser {
    private static int cont;
    private static ArrayList<String[]> lista;
    private static boolean valido;
    private static String error;

    public static void Parsear() {
        cont = 0;
        valido = false;
        lista = Scanner.tokens;
        error = "Programa incorrecto.";
        if (lista.size() == 0) {
            Compilador.error.setForeground(Color.red);
            Compilador.error.setText("Error: no puede realizarse sin el escaner antes.");
            return;
        }
        try {
            clase();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (!valido) {
            Compilador.error.setForeground(Color.red);
            Compilador.error.setText(error);
            return;
        }
        Compilador.error.setForeground(Color.green);
        Compilador.error.setText("Programa correcto.");
    }

    private static void clase() {
        if (!lista.get(0)[0].equals("clase")){
            error = "Se esperaba clase al incio de la funcion "+"linea: " + lista.get(0)[1];
            return;
        }
        cont++;
        if (!lista.get(cont)[1].equals("Identificador")){
            error = "Se esperaba un identificador despues de clase "+"linea: " + lista.get(0)[1];
            return;
        }
        cont++;
        if (!bloque())
            return;
        if (cont != lista.size() - 1){
            error = "No se esperaba nada en la "+"linea: " + lista.get(cont)[1];
            return;
        }
        valido = true;
    } 

    private static boolean bloque() {
        if (!lista.get(cont)[1].equals("Corchete izquierdo")){
            error = "Se esperaba { "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (!instr())
            return false;
        if (!lista.get(cont)[1].equals("Corchete derecho")){
            error = "Se esperaba } "+"linea: " + lista.get(cont)[1];
            return false;
        }
        return true;
    }

    private static boolean instr() {
        if (!(asig() || leer() || mostrar() || calc())){
            error = "No se cumplio ninguna instruccion "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (!lista.get(cont)[1].equals("Corchete derecho")){
            error = "Se esperaba '}' "+"linea: " + lista.get(cont)[1];
            return instr();
        }
        else
            return true;
    }

    private static boolean mostrar() {
        if (!lista.get(cont)[0].equals("mostrar"))
            return false;
        cont++;
        if (!lista.get(cont)[1].equals("Parentesis izquierdo")){
            error = "Se esperaba '(' "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (lista.get(cont)[1].equals("Identificador")) {
            cont++;
            if (!lista.get(cont)[1].equals("Parentesis derecho")){
                error = "Se esperaba ')' "+"linea: " + lista.get(cont)[1];
                return false;
            }
            cont++;
            if (lista.get(cont)[0].equals(";"))
                return true;
            error = "Se esperaba ';' "+"linea: " + lista.get(cont)[1];
            return false;
        }
        if (!lista.get(cont)[0].equals("\"")){
            error = "Se esperaba una cadena "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (!lista.get(cont)[1].equals("cadena")){
            error = "Se esperaba una cadena "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (!lista.get(cont)[0].equals("\"")){{
            error = "Se esperaba una cadena "+"linea: " + lista.get(cont)[1];
            return false;
        }
        }
        cont++;
        if (lista.get(cont)[1].equals("Parentesis derecho")) {
            cont++;
            if (lista.get(cont)[0].equals(";"))
                return true;
            error = "Se esperaba ';' "+"linea: " + lista.get(cont)[1];
            return false;
        }
        if (!lista.get(cont)[1].equals("coma")){
            error = "Se esperaba ',' concatenacion "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (!lista.get(cont)[1].equals("Identificador")){
            error = "Se esperaba una cadena "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (!lista.get(cont)[1].equals("Parentesis derecho")){
            error = "Se esperaba ')' "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (lista.get(cont)[0].equals(";"))
            return true;
        error = "Se esperaba ';' "+"linea: " + lista.get(cont)[1];
        return false;
    }

    private static boolean leer() {
        if (!lista.get(cont)[1].equals("Identificador"))
            return false;
        cont++;
        if (!lista.get(cont)[0].equals("=")){
            error = "Se esperaba una asignacion "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (!lista.get(cont)[0].equals("leerdato"))
            return false;
        cont++;
        if (!lista.get(cont)[1].equals("Parentesis izquierdo")){
            error = "Se esperaba '(' "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (!lista.get(cont)[1].equals("Parentesis derecho")){
            error = "Se esperaba ')' "+"linea: " + lista.get(cont)[1];
            return false;
        }
        cont++;
        if (lista.get(cont)[0].equals(";"))
            return true;
        error = "Se esperaba ';' "+"linea: " + lista.get(cont)[1];
        return false;
    }

    private static boolean asig() {
        if (!lista.get(cont)[1].equals("Identificador"))
            return false;
        cont++;
        if (!lista.get(cont)[0].equals("=")) {
            error = "Se esperaba una asignacion "+"linea: " + lista.get(cont)[1];
            cont--;
            return false;
        }
        cont++;
        if (!calc())
            return false;
        return true;
    }

    private static boolean calc() {
        if (!(lista.get(cont)[1].equals("Identificador") ||
                lista.get(cont)[1].equals("numero"))) {
            error = "Se esperaba una variable o constante "+"linea: " + lista.get(cont)[1];
            cont -= 2;
            return false;
        }
        cont++;
        if (lista.get(cont)[0].equals(";")){
            return true;
        }
        if (!(lista.get(cont)[1].equals("Operador Aritmetico"))) {
            error = "Se esperaba ';' o un operador arit "+"linea: " + lista.get(cont)[1];
            cont--;
            return false;
        }
        cont++;
        if (!(lista.get(cont)[1].equals("Identificador") ||
                lista.get(cont)[1].equals("numero"))) {
            error = "Se esperaba una variable o constante "+"linea: " + lista.get(cont)[1];
            cont--;
            return false;
        }
        cont++;
        if (lista.get(cont)[0].equals(";"))
            return true;
        cont -= 3;
        error = "Se esperaba ';' "+"linea: " + lista.get(cont)[1];
        return false;
    }
}
