import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.*;
import java.util.Scanner;
public class Server
{
    public static void main(String[] args) throws IOException
    {
        System.out.println ("Seleccione: \n"+ "1: Servidor  \n"+ "2: Cliente");
        System.out.println("Ingrese : ");
        String entradaTeclado = "";
        Scanner entradaEscaner = new Scanner (System.in); //Creación de un objeto Scanner
        entradaTeclado = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner
        if(entradaTeclado.equals("1") ) {
            Socket socket = null;
            // server is listening on port 6666
            ServerSocket ss = new ServerSocket(6666);
            System.out.println("Server inicializado en port: 6666");
            // running infinite loop for getting
            // client request
            while (true) {
                //Socket s = null;
                try {
                    // socket object to receive incoming client requests
                    //s = ss.accept();
                    socket = ss.accept();

                    System.out.println("Se conectó un nuevo cliente : " + socket);


                    // obtaining input and out streams
                    //DataInputStream in = new DataInputStream(s.getInputStream());
                    //DataOutputStream out = new DataOutputStream(s.getOutputStream());

                    //System.out.println("Assigning new thread for this client");

                    // create a new thread object
                    //Thread t = new ClientHandler(s, in, out);

                    // Invoking the start() method
                    //t.start();

                } catch (IOException e) {
                    System.out.println("I/O error: " + e);
                    //assert s != null;
                    //s.close();
                    //e.printStackTrace();
                    // new thread for a client
                    new EchoThread(socket).start();
                }
            }
        }
        if(entradaTeclado.equals("2"))
        {
            System.out.println("Soy un cliente");
        }
    }
}



class EchoThread extends Thread {
    private Socket socket;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());


            System.out.println("Assigning new thread for this client");

            // create a new thread object
            //Thread t = new ClientHandler(s, in, out);

            // Invoking the start() method
            //t.start();
        } catch (IOException e) {
            return;
        }
        String line;
        while (true) {
            try {
                line = brinp.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    out.writeBytes(line + "\n\r");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}





// ClientHandler class
class ClientHandler extends Thread
{
    final DataInputStream in;
    final DataOutputStream out;
    final Socket s;


    // Constructor
    public ClientHandler(Socket s, DataInputStream in, DataOutputStream out)
    {
        this.s = s;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run()
    {
        String received;
        while (true)
        {
            try {

                // Ask user what he wants
                out.writeUTF("Server Conectado, ingrese instrucción:");

                // receive the answer from client
                received = in.readUTF();

                String[] comando= received.split(" ",2);

                if(comando[0].equals("Exit"))
                {
                    System.out.println("Cliente " + this.s + " ingresa exit...");
                    System.out.println("Cerrando la conección.");
                    this.s.close();
                    System.out.println("Conección cerrada");
                    break;
                }


                switch (comando[0]) {
                    case "ls":
                        Path dir = Paths.get("./");
                        System.out.println("entre a ls");
                        StringBuilder names = new StringBuilder();
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                            for (Path file : stream) {
                                names.append(file+"\n");
                            }
                        }
                        out.writeUTF(names.toString());
                        break;
                    case "get":
                        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                        File file2 = new File(comando[1]);
                        DataOutputStream o2 = new DataOutputStream(s.getOutputStream());
                        // Obtenemos el tamaño del archivo
                        int tamanoArchivo = ( int )file2.length();
                        // Enviamos el tamaño del archivo
                        dos.writeInt( tamanoArchivo );
                        byte[] bytes2 = new byte[tamanoArchivo];
                        InputStream i2 = new FileInputStream(file2);
                        int count2;
                        while ((count2 = i2.read(bytes2)) > 0) {
                            o2.write(bytes2, 0, count2);
                        }
                        System.out.println("archivo "+ comando[1] +" enviado por el servidor");
                        break;
                    case "put":
                        // Creamos flujo de entrada para leer los datos que envia el cliente
                        DataInputStream dis = new DataInputStream( s.getInputStream());
                        OutputStream o = new FileOutputStream(comando[1]);
                        // Obtenemos el tamaño del archivo
                        int tam = dis.readInt();
                        byte[] bytes = new byte[tam];
                        System.out.println("empezando a copiar");
                        // Obtenemos el archivo mediante la lectura de bytes enviados
                        for( int i = 0; i < bytes.length; i++ )
                        {
                            bytes[i] = ( byte )in.read( );
                        }
                        // Escribimos el archivo
                        o.write(bytes);
                        System.out.println("Archivo "+comando[1] + "recibido por servidor");
                        out.writeUTF("Archivo "+comando[1]+" copiado en el servidor\n");
                        break;
                    case "delete":
                        String path_to_remove = "./"+comando[1];
                        File file = new File(path_to_remove);
                        if(file.delete())
                        {
                            out.writeUTF("Archivo borrado");
                        }
                        else
                        {
                            out.writeUTF("Error, archivo no encontrado");
                        }
                        break;

                    default:
                        out.writeUTF("Input inválido");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // closing resources
            this.in.close();
            this.out.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}