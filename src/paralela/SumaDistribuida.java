package paralela;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
        File carpeta = new File("ArchivosRecibidos");
        if (!carpeta.exists())
        {
            carpeta.mkdir();
        }

        try (ServerSocket servidor = new ServerSocket(PUERTO))
        {
            System.out.println("Servidor escuchando en el puerto " + PUERTO + "...");
            System.out.println("Esperando clientes para recibir archivos...");

            while (true)
            {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado desde: " + cliente.getInetAddress());
                new Thread(new ManejadorCliente(cliente, carpeta)).start();
            }

        } catch (Exception e)
        {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    static class ManejadorCliente implements Runnable
    {

        private Socket socket;
        private File carpetaDestino;

        public ManejadorCliente(Socket socket, File carpetaDestino)
        {
            this.socket = socket;
            this.carpetaDestino = carpetaDestino;
        }

        @Override
        public void run()
        {
            try (DataInputStream entrada = new DataInputStream(socket.getInputStream()))
            {

                // Leer el nombre del archivo
                String nombreArchivo = entrada.readUTF();

                // Crear archivo en la carpeta destino
                File archivoDestino = new File(carpetaDestino, nombreArchivo);

                // Leer el tamaño del archivo
                long tamano = entrada.readLong();

                // Leer contenido y escribirlo en disco
                try (FileOutputStream fos = new FileOutputStream(archivoDestino))
                {
                    byte[] buffer = new byte[4096];
                    long bytesRecibidos = 0;

                    while (bytesRecibidos < tamano)
                    {
                        int leidos = entrada.read(buffer);
                        fos.write(buffer, 0, leidos);
                        bytesRecibidos += leidos;
                    }
                }

                System.out.println("Archivo recibido: " + nombreArchivo
                        + " (" + tamano + " bytes) desde " + socket.getInetAddress());

            } catch (IOException e)
            {
                System.out.println("Error recibiendo archivo: " + e.getMessage());
            }
        }
    }

    // ================= CLIENTE ====================
    public static void ejecutarCliente()
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingresa la IP del servidor (por ejemplo 192.168.0.10 o localhost): ");
        String ip = sc.nextLine();

        System.out.print("Ruta completa del archivo .txt a enviar: ");
        String rutaArchivo = sc.nextLine();

        File archivo = new File(rutaArchivo);
        if (!archivo.exists())
        {
            System.out.println("El archivo no existe.");
            return;
        }

        try (Socket socket = new Socket(ip, 5000); DataOutputStream salida = new DataOutputStream(socket.getOutputStream()); FileInputStream fis = new FileInputStream(archivo))
        {

            // Enviar nombre del archivo
            salida.writeUTF(archivo.getName());

            // Enviar tamaño del archivo
            salida.writeLong(archivo.length());

            // Enviar contenido del archivo
            byte[] buffer = new byte[4096];
            int leidos;
            while ((leidos = fis.read(buffer)) != -1)
            {
                salida.write(buffer, 0, leidos);
            }

            System.out.println("Archivo enviado correctamente al servidor.");

        } catch (IOException e)
        {
            System.out.println("Error al conectar o enviar archivo: " + e.getMessage());
        }
    }
}
