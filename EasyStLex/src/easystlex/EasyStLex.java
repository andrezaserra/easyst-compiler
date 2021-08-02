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
public class EasyStLex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        EasyStLexico lex = new EasyStLexico(args[0]);
        Token t;
        
        while((t = lex.proximoToken()).nome != TipoToken.eof){
            System.out.print(t);
        }
    }
    
}
