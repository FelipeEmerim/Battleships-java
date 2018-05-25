/*
 * TCPCliente.java
 *
 * Redes de computadores
 * Estabelece conexao com o servidor e envia mensagem.
 * O servidor devolve a mensagem enviada e o cliente a imprime.
 * Argumentos: java TCPCliente <IP servidor> <porta> "msg a ser enviada"
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class TCPCliente {
    public static void main(String args[]) {
        Socket s = null;

        try {
            s = new Socket(args[0], Integer.parseInt(args[1])); // conecta o socket aa porta remota
            DataInputStream ent = new DataInputStream(s.getInputStream());
            DataOutputStream sai = new DataOutputStream(s.getOutputStream());
            Scanner scan = new Scanner(System.in); //recupera os dados informados pelo cliente
            String recebido; // inicializa variavel que irá conter os dados recebidos do servidor
            System.out.println(ent.readUTF()); //recebe o campo vazio do servidor
            while (true) {
                recebido = ent.readUTF(); //recebe solicitação de coordenadas do servidor
                System.out.println(recebido); //imprime para o cliente

                if (recebido.equals("game over")){ //se recebeu um game over
                    break; //encerra a conexão do cliente
                }

                sai.writeUTF(scan.nextLine()); //envia string de coordenadas ao servidor
                // le buffer de entrada
                recebido = ent.readUTF(); //recebe o campo atualizado pelo servidor ou a mensagem amigável de erro

                System.out.println("*** Recebido do servidor: \n" + recebido); //escreve na tela do cliente
            }
                } catch(UnknownHostException e){
                    System.out.println("!!! Servidor desconhecido: " + e.getMessage());
                } catch(EOFException e){
                    System.out.println("!!! Nao ha mais dados de entrada: " + e.getMessage());
                } catch(IOException e){
                    System.out.println("!!! E/S: " + e.getMessage());
                } finally{
                    if (s != null) {
                        try {
                            s.close();
                        } catch (IOException e) {
                            System.out.println("!!! Encerramento do socket falhou: " + e.getMessage());
                        }
                    }
                }
    }
}

