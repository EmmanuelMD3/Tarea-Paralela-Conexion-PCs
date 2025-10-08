package paralela;

import java.io.*;
import java.net.*;

public class Cliente
{

    public static void main(String[] args)
    {
        String host = "172.17.244.57"; 
        int puerto = 5000;         

        try (Socket socket = new Socket(host, puerto); PrintWriter salida = new PrintWriter(socket.getOutputStream(), true); BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in)))
        {

            System.out.println("Conectado al servidor en " + host + ":" + puerto);
            System.out.println("Escribe un mensaje (o 'salir' para terminar):");

            String mensaje;
            while ((mensaje = teclado.readLine()) != null)
            {
                if (mensaje.equalsIgnoreCase("salir"))
                {
                    break;
                }
                salida.println(mensaje);
            }

            System.out.println("Cliente desconectado.");

        } catch (IOException e)
        {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}
