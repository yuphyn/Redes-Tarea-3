import java.io.*;
import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Usuario
{
    public static void main(String[] args) throws IOException
    {
        System.out.println ("Seleccione: \n"+ "1: Servidor  \n"+ "2: Cliente");
        System.out.println("Ingrese : ");
        String entradaTeclado = "";
        Scanner entradaEscaner = new Scanner (System.in); //Creación de un objeto Scanner
        entradaTeclado = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner
        if (entradaTeclado.equals("1")){
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
                    socket = ss.accept();

                    Socket transmision1 = new Socket();
                    Socket transmision2 = new Socket();

                    System.out.println("Se conectó un nuevo cliente : " + socket);

                    // obtaining input and out streams
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    System.out.println("Assigning new thread for this client"+ transmision1);

                    //create a new thread object
                    Thread t = new ClientHandler2(socket, in, out,transmision1, transmision2);

                    // Invoking the start() method
                    t.start();

                } catch (IOException e) {
                    System.out.println("I/O error: " + e);
                }
            }
        }
        if(entradaTeclado.equals("2")){
            try
            {
                Scanner scn = new Scanner(System.in);
                // getting localhost ip
                InetAddress ip = InetAddress.getByName("localhost");
                // establish the connection with server port 5056
                Socket s = new Socket(ip, 6666);
                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                int[] intArray = new int[]{ 15,30,60};

                int verificar = 0;

                while (true)
                {
                    if (verificar==0){
                        System.out.println("sockets:"+dis.readUTF());
                        System.out.println("Estado de la coneccion: idle");
                        verificar=1;
                    }
                    else{
                        dis.readUTF();
                    }
                    dos.writeUTF("OK");
                    String mensaje = dis.readUTF();

                    // If client sends exit,close this connection
                    // and then break from the while loop
                    if(mensaje.equals("close"))
                    {
                        System.out.println("Estado de la coneccion: close \n Closing this connection : " + s);
                        s.close();
                        System.out.println("Connection closed");
                        break;
                    }
                    else if (mensaje.equals("MOSTRAR")){
                        System.out.println("mostrar");
                    }
                    else if (mensaje.equals("enviando")){
                        Random rand = new Random();
                        int value = rand.nextInt(3);
                        System.out.println("Estado de la conección: streaming <"+intArray[value]+">");
                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (Exception e) {
                            System.out.println(".");
                        }
                        System.out.println("Estado de la coneccion: idle");
                    }

                }
                scn.close();
                dis.close();
                dos.close();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class
class ClientHandler2 extends Thread
{
    final DataInputStream in;
    final DataOutputStream out;
    final Socket s;
    final Socket transmision1;
    final Socket transmision2;


    // Constructor
    public ClientHandler2(Socket s, DataInputStream in, DataOutputStream out, Socket transmision1, Socket transmision2)
    {
        this.s = s;
        this.in = in;
        this.out = out;
        this.transmision1 =transmision1;
        this.transmision2 =transmision2;
    }

    @Override
    public void run()
    {
        String received;
        label:
        while (true)
        {
            try {
                //Menu del servidor
                Random rand = new Random();
                int value = rand.nextInt(2000);
                int value1=6000+value;
                int value2=value1+1;
                out.writeUTF(s.getPort()+";"+value1+";"+value2);
                in.readUTF();
                System.out.println ("Seleccione: \n"+ "1: Mostrar videos  \n"+ "2: Reproducir \n"+ "3: Salir");
                System.out.println("Ingrese : ");
                String entradaTeclado = "";
                Scanner entradaEscaner = new Scanner (System.in); //Creación de un objeto Scanner
                entradaTeclado = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner
                //entradaEscaner.close();
                switch (entradaTeclado) {
                    case "1":
                        Path dir = Paths.get("./media");
                        StringBuilder names = new StringBuilder();
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                            for (Path file : stream) {
                                names.append(file).append("\n");
                            }
                        }
                        System.out.println(names);
                        out.writeUTF("MOSTRAR");
                        break;
                    case "3":
                        out.writeUTF("close");
                        this.s.close();
                        System.out.println("Conección cerrada");
                        break label;
                    case "2":
                        int FLAG=0;
                        System.out.println ("Ingrese el nombre del video");
                        String nombre = "";
                        nombre = entradaEscaner.nextLine ();
                        String auxiliar2 = "../media/"+nombre;
                        Path auxiliar = Paths.get(auxiliar2);
                        Path dir2 = Paths.get("./media");
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir2)) {
                            for (Path file : stream) {
                                if (file.equals(auxiliar)){
                                    FLAG=1;
                                }
                            }
                        }
                        if(FLAG==0){
                            System.out.println("Error: video no encontrado");
                            out.writeUTF("nada");
                        }
                        else {
                            out.writeUTF("enviando");
                            int i = 0;
                            while (i < 10) {
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                    System.out.println("streaming... "+nombre+"  "+s.getPort());
                                } catch (Exception e) {
                                    System.out.println(".");
                                }
                                i = i + 1;
                            }
                        }
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