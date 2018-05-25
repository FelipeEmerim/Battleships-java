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
            Scanner scan = new Scanner(System.in);
            String recebido = "";
            System.out.println(ent.readUTF());
            while (true) {
                recebido = ent.readUTF();
                System.out.println(recebido);

                if (recebido.equals("game over")){
                    break;
                }

                sai.writeUTF(scan.nextLine());
                // le buffer de entrada
                recebido = ent.readUTF();

                System.out.println("*** Recebido do servidor: \n" + recebido);
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

