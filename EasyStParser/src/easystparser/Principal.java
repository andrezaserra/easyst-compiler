/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easystparser;

import easystlex.EasyStLexico;

/**
 *
 * @author acbse
 */
public class Principal {
    public static void main(String[] args) {
        EasyStLexico lex = new EasyStLexico(args[0]);
        EasyStParser parser = new EasyStParser(lex);
        
        parser.programa();
    }
}
