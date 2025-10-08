/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package paralela;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author chemo
 */
public class ServidorMulticliente
{

    public static void main(String[] args) throws IOException
    {
        ServerSocket servidor = new ServerSocket(5000);
        System.out.println("Servidor multicliente iniciado...");

        while (true)
        {
            Socket cliente = servidor.accept();
            String ipCliente = cliente.getInetAddress().getHostAddress();
            System.out.println("Nuevo cliente conectado: " + ipCliente);

            new Thread(() -> manejarCliente(cliente)).start();
        }
    }

    private static void manejarCliente(Socket cliente)
    {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream())))
        {
            String mensaje;
            while ((mensaje = entrada.readLine()) != null)
            {
                System.out.println("Mensaje de " + cliente.getInetAddress().getHostAddress() + ": " + mensaje);
            }
        } catch (IOException e)
        {
            System.out.println("Cliente desconectado: " + cliente.getInetAddress().getHostAddress());
        }
    }
}
