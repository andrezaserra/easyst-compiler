/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easystlex;

/**
 *
 * @author acbse
 */
public class EasyStLexico {
    
    LeitorDeArquivosTexto lat;

    public EasyStLexico(String arquivo) {
        lat = new LeitorDeArquivosTexto(arquivo);
    }
    
    public Token proximoToken(){
        
        Token proximo;
        
        espacosEComentarios();
        lat.confirmar();
        
        proximo = fim();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        proximo = operadorAritimetico();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        proximo = delimitador();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        proximo = operadorRelacionalOuAtribuicao();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        proximo = operadorLogico();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        proximo = numeros();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        proximo = cadeia();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        proximo = palavrasReservadas();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        proximo = identificador();
        if(proximo == null) lat.zerar();
        else{
            lat.confirmar();
            return proximo;
        }
        
        System.out.println("Ocorreu um erro léxico!");
        System.out.println(lat.toString());
        return null;
    }
    
    private void espacosEComentarios() {
        int estado = 1;
        while (true) {
            char c = (char) lat.lerProximoCaractere();
            switch (estado) {
                case 1:
                    if (Character.isWhitespace(c) || c == ' ') estado = 2;
                    else if (c == '%') estado = 3;
                    else {
                            lat.retroceder();
                            return;
                    }break;
                case 2:
                    if (c == '%') estado = 3;
                    else if (!(Character.isWhitespace(c) || c == ' ')) {
                        lat.retroceder();
                        return;
                    }break;
                case 3:
                    if (c == '\n') {
                        return;
                    }break;
                default: break;
            }
        }
    }
    
    private Token operadorAritimetico(){
        int caractereLido = lat.lerProximoCaractere();
        char c = (char)caractereLido;
        
        switch (c) {
            case '+':
                return new Token(TipoToken.opSoma, "+");
            case '-':
                return new Token(TipoToken.opSub, "-");
            case '*':
                return new Token(TipoToken.opMult, "*");
            case '/':
                if(c=='/') break;
                else{
                    lat.retroceder();
                    return new Token(TipoToken.opDiv, "/");
                } 
            default: break;
        }
        return null;
    }
    
    private Token delimitador(){
        int caractereLido = lat.lerProximoCaractere();
        char c = (char)caractereLido;
        switch (c) {
            case '(':
                return new Token(TipoToken.delAbrePar, "(");
            case ')':
                return new Token(TipoToken.delFechaPar, ")");
            case '{':
                return new Token(TipoToken.delAbreChave, "{");
            case '}':
                return new Token(TipoToken.delFechaChave, "}");
            case '[':
                return new Token(TipoToken.delAbreColch, "[");
            case ']':
                return new Token(TipoToken.delFechaColch, "]");
            case ';':
                return new Token(TipoToken.delFimLinha, ";");
            default: break;
        }
        return null;
    }
    
    private Token operadorRelacionalOuAtribuicao(){
        int caractereLido = lat.lerProximoCaractere();
        char c = (char)caractereLido;
        switch (c) {
            case '<':
                c = (char)lat.lerProximoCaractere();
                if(c=='=') return new Token(TipoToken.relMenorIgual, "<=");
                else{
                    lat.retroceder();
                    return new Token(TipoToken.relMenor, "<");
                }
            case '>':
                c = (char)lat.lerProximoCaractere();
                if(c=='=') return new Token(TipoToken.relMaiorIgual, ">=");
                else{
                    lat.retroceder();
                    return new Token(TipoToken.relMaior, ">");
                }
            case '!':
                c = (char)lat.lerProximoCaractere();
                if(c=='=') return new Token(TipoToken.relDiferente, "!=");
                else{
                    lat.retroceder();
                    break;
                }
            case '=':
                c = (char)lat.lerProximoCaractere();
                if(c=='=') return new Token(TipoToken.relIgual, "==");
                else{
                    lat.retroceder();
                    return new Token(TipoToken.atr, "=");
                }
            default: break;
        }
        return null;
    }
    
    private Token operadorLogico(){
        int caractereLido = lat.lerProximoCaractere();
        char c = (char)caractereLido;
        switch (c) {
            case '&':
                c = (char)lat.lerProximoCaractere();
                if(c=='&') return new Token(TipoToken.logE, "&&");
                else{
                    lat.retroceder();
                    break;
                }
            case '|':
                c = (char)lat.lerProximoCaractere();
                if(c=='|') return new Token(TipoToken.logOu, "||");
                else{
                    lat.retroceder();
                    break;
                }
            default: break;
        }
        return null;
    }
    
    private Token numeros() {
        int estado = 1;
        while (true) {
            char c = (char) lat.lerProximoCaractere();
            switch (estado) {
                case 1:
                    if (Character.isDigit(c)) {
                        estado = 2;
                    } else {
                        return null;
                    }   break;
                case 2:
                    if (c == '.') {
                        c = (char) lat.lerProximoCaractere();
                        if (Character.isDigit(c)) {
                            estado = 3;
                        } else {
                            return null;
                        }
                    } else if (!Character.isDigit(c)) {
                        lat.retroceder();
                        return new Token(TipoToken.numInt, lat.getLexema());
                    }   break;
                case 3:
                    if (!Character.isDigit(c)) {
                        lat.retroceder();
                        return new Token(TipoToken.numReal, lat.getLexema());
                    }   break;
                default:
                    break;
            }
        }
    }
    
    private Token cadeia() {
        int estado = 1;
        while (true) {
            char c = (char) lat.lerProximoCaractere();
            switch (estado) {
                case 1:
                    if (c == '\'') estado = 2;
                    else return null;
                    break;
                case 2:
                    if (c == '\n') return null;
                    if (c == '\'') return new Token(TipoToken.str, lat.getLexema());
                    else if (c == '\\') estado = 3;
                    break;
                case 3:
                    if (c == '\n') return null;
                    else estado = 2;
                    break;
                default: break;
            }
        }
    }
    
    private Token identificador() {
        int estado = 1;
        while (true) {
            char c = (char) lat.lerProximoCaractere();
            if (estado == 1) {
                if (Character.isLetter(c)) estado = 2;
                else return null;
            } else if (estado == 2) {
                if (!Character.isLetterOrDigit(c)) {
                    lat.retroceder();
                    return new Token(TipoToken.identificador, lat.getLexema());
                }
            }
        }
    }
    
    private Token palavrasReservadas() {
        while (true) {
            char c = (char) lat.lerProximoCaractere();
            if (!Character.isLetter(c)) {
                lat.retroceder();
                String lexema = lat.getLexema();
                if(lexema.equals("NUM")) return new Token(TipoToken.PRNUM, lexema);
                if(lexema.equals("STR")) return new Token(TipoToken.PRSTR, lexema);
                if(lexema.equals("BOOL")) return new Token(TipoToken.PRBOOL, lexema);
                if(lexema.equals("IF")) return new Token(TipoToken.PRIF, lexema);
                if(lexema.equals("ELSE")) return new Token(TipoToken.PRELSE, lexema);
                if(lexema.equals("ELIF")) return new Token(TipoToken.PRELIF, lexema);
                if(lexema.equals("WHILE")) return new Token(TipoToken.PRWHILE, lexema);
                if(lexema.equals("FOR")) return new Token(TipoToken.PRFOR, lexema);
                if(lexema.equals("LEIA")) return new Token(TipoToken.PRLEIA, lexema);
                    else return null;
            }
        }
    }
    
    private Token fim() {
        int caractereLido = lat.lerProximoCaractere();
        if (caractereLido == -1) {
            return new Token(TipoToken.eof, "Fim");
        }
        return null;
    }

}
