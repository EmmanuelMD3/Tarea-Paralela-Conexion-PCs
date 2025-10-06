/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package paralela;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author chemo
 */
public class SumaDistribuida
{

    public static void main(String[] args) throws IOException
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("¿Quieres ejecutar como servidor (s) o cliente (c)? ");
        String modo = sc.nextLine();

        if (modo.equalsIgnoreCase("s"))
        {
            ejecutarServidor();
        } else if (modo.equalsIgnoreCase("c"))
        {
            ejecutarCliente();
        } else
        {
            System.out.println("Opción no válida.");
        }
    }

    // ================= SERVIDOR ====================
    public static void ejecutarServidor()
    {
        final int PUERTO = 5000;
        try (ServerSocket servidor = new ServerSocket(PUERTO))
        {
            System.out.println("Servidor escuchando en el puerto " + PUERTO + "...");
            System.out.println("Esperando 2 clientes...");

            // Lista para guardar los números que envían los clientes
            List<Integer> numeros = Collections.synchronizedList(new ArrayList<>());

            // Aceptar dos conexiones (pueden ser más si lo deseas)
            for (int i = 1; i <= 2; i++)
            {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado desde: " + cliente.getInetAddress());

                // Crear un hilo que reciba el número de cada cliente
                new Thread(new ManejadorCliente(cliente, numeros)).start();
            }

            // Esperar a que lleguen los dos números
            while (numeros.size() < 2)
            {
                Thread.sleep(500);
            }

            // Sumar los números recibidos
            int suma = numeros.get(0) + numeros.get(1);
            System.out.println("\n=== Resultado final ===");
            System.out.println(numeros.get(0) + " + " + numeros.get(1) + " = " + suma);

        } catch (Exception e)
        {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    // Clase interna para manejar clientes con hilos
    static class ManejadorCliente implements Runnable
    {

        private Socket socket;
        private List<Integer> numeros;

        public ManejadorCliente(Socket socket, List<Integer> numeros)
        {
            this.socket = socket;
            this.numeros = numeros;
        }

        @Override
        public void run()
        {
            try (BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())))
            {

                String recibido = entrada.readLine();
                int numero = Integer.parseInt(recibido);
                synchronized (numeros)
                {
                    numeros.add(numero);
                }
                System.out.println("Número recibido de " + socket.getInetAddress() + ": " + numero);

            } catch (IOException e)
            {
                System.out.println("Error con cliente: " + e.getMessage());
            }
        }
    }

    // ================= CLIENTE ====================
    public static void ejecutarCliente()
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingresa la IP del servidor (por ejemplo 192.168.0.10 o localhost): ");
        String ip = sc.nextLine();

        System.out.print("Ingresa el número que deseas enviar: ");
        String numero = sc.nextLine();

        try (Socket socket = new Socket(ip, 5000); PrintWriter salida = new PrintWriter(socket.getOutputStream(), true))
        {

            salida.println(numero);
            System.out.println("Número enviado correctamente al servidor.");

        } catch (IOException e)
        {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}
