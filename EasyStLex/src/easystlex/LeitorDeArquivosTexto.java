/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easystlex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acbse
 */
public class LeitorDeArquivosTexto {
    
    private final static int TAMANHO_BUFFER = 10;
    int[] bufferLeitura;
    int ponteiro, bufferAtual, inicioLexema;
    private String lexema;
    
    InputStream is;

    public LeitorDeArquivosTexto(String arquivo) {
        try {
            is = new FileInputStream(new File(arquivo));
            inicializarBuffer();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LeitorDeArquivosTexto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void inicializarBuffer(){
        bufferAtual = 2;
        inicioLexema = 0;
        lexema = "";
        bufferLeitura = new int [TAMANHO_BUFFER * 2];
        ponteiro = 0;
        recarregarBuffer1();
    }
    
    private void incrementarPonteiro(){
        ponteiro++;
        //lógica circular
        if(ponteiro == TAMANHO_BUFFER) recarregarBuffer2();
            else if(ponteiro == TAMANHO_BUFFER*2){
                recarregarBuffer1();
                ponteiro = 0;
            }
    }
    
    private void recarregarBuffer1(){
        if(bufferAtual == 2){
            bufferAtual = 1;
            for(int i=0; i<TAMANHO_BUFFER; i++){
                try {
                    bufferLeitura[i] = is.read();
                    if(bufferLeitura[i] == -1) break;
                } catch (IOException ex) {
                    Logger.getLogger(LeitorDeArquivosTexto.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void recarregarBuffer2(){
        if(bufferAtual == 1){
            bufferAtual = 2;
            for(int i=TAMANHO_BUFFER; i<TAMANHO_BUFFER*2; i++){
                try {
                    bufferLeitura[i] = is.read();
                } catch (IOException ex) {
                    Logger.getLogger(LeitorDeArquivosTexto.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(bufferLeitura[i] == -1) break;
            }
        }
    }
    
    public int lerCaractereBuffer(){
        int ret = bufferLeitura[ponteiro];
        System.out.println(this);
        incrementarPonteiro();
        return ret;
    }
    
    public int lerProximoCaractere(){
        int c = lerCaractereBuffer();
        lexema += (char)c;
        return c;
    }
    
    public void retroceder(){
        ponteiro--;
        lexema = lexema.substring(0, lexema.length()-1);
        if(ponteiro < 0) ponteiro = (TAMANHO_BUFFER*2)-1;
    }
    
    public void zerar(){
        ponteiro = inicioLexema;
        lexema = "";
    }
    
    public void confirmar(){
        inicioLexema = ponteiro;
        lexema = "";
    }
    
    public String getLexema(){
        return lexema;
    }

    @Override
    public String toString() {
        
        String ret = "Buffer: [";
        
        for(int i: bufferLeitura){
            char c = (char)i;
            if(Character.isWhitespace(c)) ret += ' ';
                    else ret += (char)i;
        }
        
        ret += "]\n     ";
        
        for(int i=0; i<TAMANHO_BUFFER; i++){
            if(i == inicioLexema && i==ponteiro) ret += '%';
                else if(i == inicioLexema) ret += '^';
                else if(i == ponteiro) ret += '*';
                    else ret += ' ';
        }
        return ret;
    }
    
    
}
