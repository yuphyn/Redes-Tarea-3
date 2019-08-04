import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPcliente {

    public static void main (String[] args){

        final String HOST = "127.0.0.1";
        final int puerto = 5000;

        DataInputStream in;
        DataOutputStream out;

        try{
            Socket sc = new Socket(HOST,puerto);

            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            out.writeUTF("hola desde el cliente");

            String mensaje = in.readUTF();
            System.out.println(mensaje);

            sc.close();

        } catch(IOException e) {
            Logger.getLogger(TCPservidor.class.getName()).log(Level.SEVERE, null, e);
        }


    }
}