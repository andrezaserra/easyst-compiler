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
public class Token {
    
    public TipoToken nome;
    public String lexema;

    public Token(TipoToken nome, String lexema) {
        this.nome = nome;
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return "<"+nome+", "+lexema+">"; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
