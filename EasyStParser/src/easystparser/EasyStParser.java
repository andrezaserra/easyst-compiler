/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easystparser;

import easystlex.EasyStLexico;
import easystlex.TipoToken;
import easystlex.Token;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author acbse
 */
public class EasyStParser {

    private final static int TAMANHO_BUFFER = 10;
    boolean chegouNoFim = false;
    List<Token> bufferTokens;
    EasyStLexico lex;
    
    public EasyStParser(EasyStLexico lex){
        this.lex = lex;
        bufferTokens = new ArrayList<>();
        lerToken();
    }
    
    private void lerToken(){
        
        if(bufferTokens.size()>0) bufferTokens.remove(0);
        
        while(bufferTokens.size() < TAMANHO_BUFFER && !chegouNoFim){
            Token proximo = lex.proximoToken();
            bufferTokens.add(proximo);
            
            if(proximo.nome == TipoToken.eof) chegouNoFim = true;
        }
        System.out.println("Lido: "+ lookahead(1));
    }
    
    Token lookahead(int k){
        
        if(bufferTokens.isEmpty()) return null;
        
        if(k-1 >= bufferTokens.size()) return bufferTokens.get(bufferTokens.size()-1);
        
        return bufferTokens.get(k-1);
    }
    
    void match(TipoToken tipo){
        if(lookahead(1).nome == tipo) {
            System.out.println("Match: "+lookahead(1));
            lerToken();
        }else{
            erroSintatico(tipo.toString());
        }
    }
    
    void erroSintatico(String... tokensEsperados){
        
        String mensagem = "Erro sintático: esperando um dos seguintes tokens (";
        
        for(int i=0; i<tokensEsperados.length; i++){
            mensagem += tokensEsperados[i];
            
            if(i<tokensEsperados.length-1) mensagem += ",";
        }
        
        mensagem += "), mas foi encontrado "+lookahead(1);
        
        throw new RuntimeException(mensagem);
    }
    
    //programa: listaDeclaracoes listaComandos
    public void programa(){
        listaDeclaracoes();
        listaComandos();
    }
    
    //listaDeclaracoes: declaracao listaDeclaracoes | declaracao
    void listaDeclaracoes(){
        Token a = lookahead(5);
        if(null == a.nome){
            erroSintatico();
        }else switch (a.nome) {
            case PRNUM:
            case PRSTR:
            case PRBOOL:
                declaracao(); //possui sempre 4 símbolos (tipo, nome, atrib, valor, pontovígula), por isso eu olho depois do 4º
                listaDeclaracoes();
                break;
            case PRIF:
            case PRELSE:
            case PRELIF:
            case PRWHILE:
            case PRFOR:
            case PRLEIA:
            case PREOF:
            case numInt:
            case numReal:
            case str:
            case eof:
            case identificador:
            case relMaior:
            case relMenor:
            case relIgual:
            case relMaiorIgual:
            case relMenorIgual:
            case relDiferente:
            case delAbrePar:
            case delFechaPar:
            case delAbreChave:
            case delFechaChave:
            case delAbreColch:
            case delFechaColch:
            case delFimLinha:
            case logE:
            case logOu:
            case opSoma:
            case opSub:
            case opMult:
            case opDiv:
            case atr:
                declaracao();
                break;
            default:
                erroSintatico();
                break;
        }
    }
    
    //declaracao: tipoVar VARIAVEL "=" valorVar ";"
    void declaracao(){
        tipoVar();
        match(TipoToken.identificador);
        match(TipoToken.atr);
        valorVar();
    }
    
    //tipoVar: "NUM" | "STR" | "BOOL"
    void tipoVar(){
        Token a = lookahead(1);
        if(null == a.nome) erroSintatico();
        else switch (a.nome) {
            case PRNUM:
                match(TipoToken.PRNUM);
                break;
            case PRSTR:
                match(TipoToken.PRSTR);
                break;
            case PRBOOL:
                match(TipoToken.PRBOOL);
                break;
            default:
                erroSintatico();
                break;
        }
    }
    
    //valorVar: numerico | cadeia de caracteres | binário
    void valorVar(){
        Token a = lookahead(1);
        if(null == a.nome) erroSintatico();
        else switch (a.nome) {
            case numInt:
                match(TipoToken.numInt);
                break;
            case numReal:
                match(TipoToken.numReal);
                break;
            case str:
                match(TipoToken.str);
                break;
            case bool:
                match(TipoToken.bool);
                break;
            default:
                erroSintatico();
                break;
        }
    }
    
    //expressaoAritimetica: expressaoAritmetica '+' termoAritimetico
    //                      expressaoArtimetica '-' termoAritimetico
    //                      termoAritimetico
    
    //------------------------ fatorar ------------------------------
    //expressaoAritimetica: expressaoAritmetica ('+' termoAritimetico | '-' termoAritimetico) 
    //                      | termoAritimetico]
    
    //--------------- remover recursão à esquerda -------------------
    //espressaoAritimetica: termoAritimetico expressaoAritmetica2
    
    void expressaoAritimetica(){
        termoAritimetico();
        espressaoAritimetica2();
    }
    
    //expressaoAritmetica2: expressaoAritmetica2SubRegra1 expressaoAritmetica2
    //                      | >>vazio<<
    
    void espressaoAritimetica2(){
        Token t = lookahead(1);
        if(t.nome == TipoToken.opSoma || t.nome == TipoToken.opSub){
            expressaoAritmetica2SubRegra1();
            espressaoAritimetica2();
        }else{
            //>>vazio<<
        }
    }
    
    //expressaoAritmetica2SubRegra1: ('+' termoAritimetico | '-' termoAritimetico)
    void expressaoAritmetica2SubRegra1(){
        Token t = lookahead(1);
        if(null == t.nome){
            erroSintatico("+", "-");
        }else switch (t.nome) {
            case opSoma:
                match(TipoToken.opSoma);
                termoAritimetico();
                break;
            case opSub:
                match(TipoToken.opSub);
                break;
            default:
                erroSintatico("+", "-");
                break;
        }
    }

    //termoAritimetico: termoAritimetico '*' fatorAritimetico
    //                  |termoAritimetico '/' fatorAritimetico
    //                  |fatorAritimetico
    // FAZER A MESMA COISA QUE COM EXPRESSÃO
    
    //------------------------ fatorar ------------------------------
    //termoAritimetico: termoAritimetico ('*' fatorAritimetico | '/' fatorAritimetico) 
    //                  | fatorAritimetico
    
    //--------------- remover recursão à esquerda -------------------
    //termoAritimetica: fatorAritimetico termoAritmetico2
    void termoAritimetico(){
        fatorAritimetico();
        termoAritimetico2();
    }
    
    //termoAritmetico2: termoAritmetico2SubRegra1 termoAritmetico2
    //                  | >>vazio<<
    void termoAritimetico2(){
        Token t = lookahead(1);
        if(t.nome == TipoToken.opMult || t.nome == TipoToken.opDiv){
            termoAritmetico2SubRegra1();
            termoAritimetico2();
        }else{
            //>>vazio<<
        }
    }
    
    //termoAritmetico2SubRegra1: ('*' fatorAritimetico | '/' fatorAritimetico)
    void termoAritmetico2SubRegra1(){
        Token t = lookahead(1);
        if(null == t.nome){
            erroSintatico("*", "/");
        }else switch (t.nome) {
            case opMult:
                match(TipoToken.opMult);
                fatorAritimetico();
                break;
            case opDiv:
                match(TipoToken.opDiv);
                fatorAritimetico();
                break;
            default:
                erroSintatico("*", "/");
                break;
        }
    }
    
    //fatorAritimetico: numInt
    //                 |numReal
    //                 |identificador
    //                 |'(' expressaoAritimetica ')'
    void fatorAritimetico(){
        Token t = lookahead(1);
        if(null == t.nome){
            erroSintatico("numInt", "numReal", "identificador", "(");
        }else switch (t.nome) {
            case numInt:
                match(TipoToken.numInt);
                break;
            case numReal:
                match(TipoToken.numReal);
                break;
            case identificador:
                match(TipoToken.identificador);
                break;
            case delAbrePar:
                match(TipoToken.delAbrePar);
                expressaoAritimetica();
                match(TipoToken.delFechaPar);
                break;
            default:
                erroSintatico("numInt", "numReal", "identificador", "(");
                break;
        }
    }
    
    
    //expressaoRelacional: expressaoRelacional operadorLogico termoRelacional
    //                     | termoRelacional
    
    //-----------------removendo recursao à esquerda ------------------
    //expressaoRelacional: termoRelacional expressaoRelacional2
    
    void expressaoRelacional(){
        termoRelacional();
        expressaoRelacional2();
    }
    
    //expresaoRelacional2: operadorLogico termoRelacional expressaoRelacional2
    //                     | >>vazio<< 
    void expressaoRelacional2(){
        Token t = lookahead(1);
        if(t.nome == TipoToken.logE || t.nome == TipoToken.logOu){
            operadorLogico();
            termoRelacional();
            expressaoRelacional2();
        }else{
            // >>vazio<< 
        }
    }
    
    //termoRelacional: expressaoAritimetica operadorRelacional expressaoAritimetica
    void termoRelacional(){
        Token t = lookahead(1);
        if(t.nome == TipoToken.numInt 
                    || t.nome == TipoToken.numReal
                    || t.nome == TipoToken.identificador
                    || t.nome == TipoToken.delAbrePar){
            expressaoAritimetica();
            operadorRelacional();
            expressaoAritimetica();
        }else{
            erroSintatico("numInt", "numReal", "(");
        }
    }
    
    //operadorRelacional: '>' | '>=' | '<' | '<=' | '==' |'!='
    void operadorRelacional(){
        Token t = lookahead(1);
        if(null == t.nome) erroSintatico("!=", "==", ">", ">=", "<", "<=");
        else switch (t.nome) {
            case relDiferente:
                match(TipoToken.relDiferente);
                break;
            case relIgual:
                match(TipoToken.relIgual);
                break;
            case relMaior:
                match(TipoToken.relMaior);
                break;
            case relMaiorIgual:
                match(TipoToken.relMaior);
                break;
            case relMenor:
                match(TipoToken.relMenor);
                break;
            case relMenorIgual:
                match(TipoToken.relMenorIgual);
                break;
            default:
                erroSintatico("!=", "==", ">", ">=", "<", "<=");
                break;
        }
    }
    
    //operadorLogico: '&&' | '||'
    void operadorLogico(){
        Token t = lookahead(1);
        if(null == t.nome) erroSintatico("&&", "||");
        else switch (t.nome) {
            case logE:
                match(TipoToken.logE);
                break;
            case logOu:
                match(TipoToken.logOu);
                break;
            default:
                erroSintatico("&&", "||");
                break;
        }
    }
    
    //listaComandos: comando listaComandos | comando
    
    //-------------------- fatorar --------------------------
    //listaComandos: comando listaComandosSubRegra1
    void listaComandos(){
        comando();
        listaComandosSubRegra1();
    }
    
    //listaComandosSubRegra1: (listaComandos | >>vazio<<)
    void listaComandosSubRegra1(){
        Token t = lookahead(1);
        if(t.nome == TipoToken.atr
           ||t.nome == TipoToken.PRIF
           ||t.nome == TipoToken.PRELSE
           ||t.nome == TipoToken.PRELIF
           ||t.nome == TipoToken.PRFOR
           ||t.nome == TipoToken.PRWHILE
           ||t.nome == TipoToken.PRLEIA) listaComandos();
        else; //vazio
    }
    
    //comando: comandoAtribuicao
    //        |comandoEntrada
    //        |comandoCondicional
    //        |comandoRepeticao
    void comando(){
        Token t = lookahead(1);
        if(null == t.nome) erroSintatico("=", "IF", "ELSE", "ELIF", "FOR", "WHILE", "LEIA");
        else switch (t.nome) {
            case atr:
                comandoAtribuicao();
                break;
            case PRLEIA:
                comandoEntrada();
                break;
            case PRIF:
            case PRELIF:
            case PRELSE:
                comandoCondicional();
                break;
            case PRFOR:
            case PRWHILE:
                comandoRepeticao();
                break;
            default:
                erroSintatico("=", "IF", "ELSE", "ELIF", "FOR", "WHILE", "LEIA");
                break;
        }
    }
    
    //comandoAtribuicao: identificador "=" expressaoAritimetica
    void comandoAtribuicao(){
        match(TipoToken.identificador);
        match(TipoToken.atr);
        expressaoAritimetica();
    }
    
    //comandoEntrada: "LEIA" "(" identificador ")"
    void comandoEntrada(){
        match(TipoToken.PRLEIA);
        match(TipoToken.delAbrePar);
        match(TipoToken.identificador);
        match(TipoToken.delFechaPar);
    }
    
    //comandoCondicional: 'IF' '(' expressaoRelacional ')' '{' listaComandos '}'
    //                   |'ELIF' '(' expressaoRelacional ')' '{' listaComandos '}'
    //                   |'ELSE' '{' listaComandos '}'
    
    //------------------------- FATORAR À ESQUERDA ----------------------------
    // comandoCondicional: 'IF' '(' expressaoRelacional ')' '{' listaComandos '}' comandoCondicional2
    
    void comandoCondicional(){
        match(TipoToken.PRIF);
        match(TipoToken.delAbrePar);
        expressaoRelacional();
        match(TipoToken.delFechaPar);
        match(TipoToken.delAbreChave);
        comando();
        match(TipoToken.delFechaChave);
        comandoCondicional2();
    }
    
    // comandoCondicional2: 'ELIF' '(' expressaoRelacional ')' '{' listaComandos '}'
    //                      |'ELSE' '{' listaComandos '}'
    //------------------------- FATORAR À ESQUERDA ----------------------------
    //comandoCondicional2:  'ELIF' '(' expressaoRelacional ')' '{' listaComandos '}' comandoCondicional2SubRegra1
    //                      
    void comandoCondicional2(){
        Token t = lookahead(1);
        if(t.nome == TipoToken.PRELIF){
            match(TipoToken.PRELIF);
            match(TipoToken.delAbrePar);
            expressaoRelacional();
            match(TipoToken.delFechaPar);
            match(TipoToken.delAbreChave);
            listaComandos();
            match(TipoToken.delFechaChave);
            comandoCondicional2SubRegra1();
        }else{
            //vazio
        }
    }
    
    //comandoCondicional2SubRegra1: 'ELSE' '{' comando '}' | >>vazio<<
    void comandoCondicional2SubRegra1(){
        Token t = lookahead(1);
        if(t.nome == TipoToken.PRELSE){
            match(TipoToken.PRELSE);
            match(TipoToken.delAbreChave);
            listaComandos();
            match(TipoToken.delFechaChave);
        }else{
            //>>vazio<<
        }
    }
    
    //comandoRepetição: "WHILE" "(" expressaoRelacional ")" "{" listaComandos "}"
    void comandoRepeticao(){
        match(TipoToken.PRWHILE);
        match(TipoToken.delAbrePar);
        expressaoRelacional();
        match(TipoToken.delFechaPar);
        match(TipoToken.delAbreChave);
        listaComandos();
        match(TipoToken.delFechaChave);
    }
}