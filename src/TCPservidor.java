import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPservidor {

    public static void main(String[] args){

        ServerSocket servidor = null;
        Socket sc = null;
        final int puerto = 5000;
        DataInputStream in;
        DataOutputStream out;

        try {
            servidor = new ServerSocket(puerto);
            System.out.println("servidor iniciado");

            while(true){
                sc = servidor.accept();
                System.out.println("cliente conectado");
                in = new DataInputStream(sc.getInputStream());
                out = new DataOutputStream(sc.getOutputStream());

                String mensaje = in.readUTF();
                System.out.println(mensaje);
                out.writeUTF("hola desde el servidor");

                sc.close();
                System.out.println("cliente desconectado");
            }
        } catch (IOException e) {
            Logger.getLogger(TCPservidor.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}

