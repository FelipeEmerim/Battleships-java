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

        String campo = "\n";
        for(int cont = 0; cont < 6; cont++){
            campo = campo.concat("|");
            for (int cont2 = 0; cont2 < 6; cont2++){

                if(mat[cont][cont2] == 2){
                    campo = campo.concat("O|");
                }
                else if(mat[cont][cont2] == 3){
                    campo = campo.concat("X|");
                }
                else{
                    campo = campo.concat(" |");
                }

            }
            campo=campo.concat("\n");
        }

        return campo;
    }

    private int tiro(String coords, int[][]campo){

        if(coords.length() != 3){
            return -1;
        }

        int linha = Character.getNumericValue(coords.charAt(0)) - 1;
        int coluna = Character.getNumericValue(coords.charAt(2)) - 1;


        if(linha > 5 || linha < 0 || coluna > 5 || coluna < 0){
            return -1;
        }

        if(campo[linha][coluna] == 0){
            campo[linha][coluna] = 2;
        }
        else if (campo[linha][coluna] == 1){
            campo[linha][coluna] = 3;
            return 1;
        }

        return 0;
    }

    private boolean randomChance(){

        double luck = Math.random();
        return luck > 0.9;

    }

    private void preparaCampo(int[][] campo){
        int boat = 6;

        while (boat > 0) {
            for (int cont = 0; cont < 6; cont++) {
                for (int cont2 = 0; cont2 < 6; cont2++) {

                    if (randomChance() && campo[cont][cont2] == 0) {
                        campo[cont][cont2] = 1;
                        boat--;
                    }
                }
            }
        }
    }

    public void run() {

        int cont = 6;
        int[][]campo = new int[6][6];
        preparaCampo(campo);
        try {
            sai.writeUTF(desenhaCampo(campo));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (cont > 0) {
            try {

                sai.writeUTF("Informe as coordenadas do ataque: ");
                int shot = tiro(ent.readUTF(), campo);
                if (shot == 1 || shot == 0) {
                    sai.writeUTF(desenhaCampo(campo));
                    if(shot == 1){
                        cont --;
                    }
                }
                else{
                    sai.writeUTF("Inseto Insignificante");
                }
                if(cont == 0){
                    sai.writeUTF("game over");
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
