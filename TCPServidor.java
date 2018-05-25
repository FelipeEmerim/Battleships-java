/*
 * TCPServidor.java
 *
 * Redes de computadores
 *
 * Servidor ECHO (Coulouris, 2001) pg. 137
 * Passar numero da porta como argumento (opcional)
 * Ex. java TCPServidor <porta> ou
 *     java TCPServidor
 */
import java.net.*;
import java.io.*;

public class TCPServidor {
    public static void main(String args[]) {
        try {
            int porta = 6789; // porta do servico
            if (args.length > 0) porta = Integer.parseInt(args[0]);
            ServerSocket escuta = new ServerSocket(porta);
            System.out.println("*** Servidor ***");
            System.out.println("*** Porta de escuta (listen): " + porta);
            //Servidor aceita várias conexões
            while (true) {
                // accept bloqueia ateh que chegue um pedido de conexao de um cliente
                Socket cliente = escuta.accept();
                System.out.println("*** conexao aceita de (remoto): " + cliente.getRemoteSocketAddress());
                // quando chega, cria nova thread para atender em especial o cliente
                Conexao c = new Conexao(cliente);
            }
        } catch (IOException e) {
            System.out.println("Erro na escuta: " + e.getMessage());
        }
    }    
}

class Conexao extends Thread {
    private DataInputStream ent;
    private DataOutputStream sai;
    private Socket cliente;
    
    Conexao(Socket s) {
        try {
            cliente = s;
            ent = new DataInputStream(cliente.getInputStream());
            sai = new DataOutputStream(cliente.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Erro IO Conexao: " + e.getMessage());
        }
    }


    private String desenhaCampo(int[][]mat){
    //cria a string do campo para ser retornada ao jogador
        String campo = "\n";
        for(int cont = 0; cont < 6; cont++){
            campo = campo.concat("|"); //insere a primeira célula em cada linha
            for (int cont2 = 0; cont2 < 6; cont2++){

                if(mat[cont][cont2] == 2){
                    campo = campo.concat("O|"); //insere O quando for um tiro na água
                }
                else if(mat[cont][cont2] == 3){ //insere X quando for um barco
                    campo = campo.concat("X|");
                }
                else{
                    campo = campo.concat(" |"); //Deixa em branco em qualquer outro caso
                }

            }
            campo=campo.concat("\n"); //finaliza linha
        }

        return campo;
    }

    private int tiro(String coords, int[][]campo){
    //Este método gerencia os tiros do jogador

        if(coords.length() != 3){ //Valida a string de coordenadas
            return -1;
        }


        //converte a string de coordenadas em valores inteiros que podem ser usados para referenciar o campo
        int linha = Character.getNumericValue(coords.charAt(0)) - 1;
        int coluna = Character.getNumericValue(coords.charAt(2)) - 1;


        //verifica se as coordenadas são válidas
        if(linha > 5 || linha < 0 || coluna > 5 || coluna < 0){
            return -1; //informa que o disparo foi inválido
        }

        //verifica se o tiro foi na água
        if(campo[linha][coluna] == 0){
            campo[linha][coluna] = 2; //escreve tiro na água na matriz controle
        }
        //verifica se o tiro foi em um barco
        else if (campo[linha][coluna] == 1){
            campo[linha][coluna] = 3; //escreve tiro em barco na matriz controle
            return 1; //informa que o tiro acertou um barco
        }

        return 0; //informa que o tiro acertou a agua
    }

    private boolean randomChance(){

        double luck = Math.random();
        return luck > 0.9; //chance de uma celula conter um barco é 10%

    }

    private void preparaCampo(int[][] campo){
    //inicializa o campo posicionando os barcos aleatoriamente
        int boat = 6; //posiciona seis barcos

        while (boat > 0) {
            for (int cont = 0; cont < 6; cont++) {
                for (int cont2 = 0; cont2 < 6; cont2++) {

                    if (randomChance() && campo[cont][cont2] == 0) { //se ocorrerem os 10% de chance
                        campo[cont][cont2] = 1; //gera um barco
                        boat--; //diminui o contador de barcos
                    }
                }
            }
        }
    }

    public void run() {

        int cont = 6; //contador de barcos restantes
        int[][]campo = new int[6][6]; //matriz de controle
        preparaCampo(campo);
        try {
            sai.writeUTF(desenhaCampo(campo)); //desenha o campo vazio para o cliente
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (cont > 0) { //roda enquanto existirem barcos não descobertos
            try {

                sai.writeUTF("Informe as coordenadas do ataque: "); //solicita coordenadas do ataque
                int shot = tiro(ent.readUTF(), campo); //chama a função de tiro e recebe seu retorno
                if (shot == 1 || shot == 0) { //se o tiro for válido
                    sai.writeUTF(desenhaCampo(campo)); //desenha o campo
                    if(shot == 1){ //se o tiro acertou um barco
                        cont --; //decrementa o contador de barcos restantes
                    }
                }
                else{ //se o tiro for inválido
                    sai.writeUTF("Inseto Insignificante"); //mostra mensagem amigável
                }
                if(cont == 0){ //se não há barcos restantes
                    sai.writeUTF("game over"); //exibe mensagem de fim de jogo
                }
            } catch (EOFException e) {
                System.out.println("Conexao: EOFException " + e.getMessage());
                break;
            } catch (IOException e) {
                System.out.println("Conexao: IOException " + e.getMessage());
                break;
            }

        }
        try {
            cliente.close();
        } catch (IOException e) {
            System.out.println("Conexao: erro close do socket");
        }
    }
}
