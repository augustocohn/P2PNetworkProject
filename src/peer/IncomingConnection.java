package peer;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IncomingConnection extends Thread {

    private int peerID;
    private Socket portConnection;

    private InputStream inputStream;
    private OutputStream outputStream;

    public IncomingConnection(int peerID, Socket portConnection) {
        this.peerID = peerID;
        this.portConnection = portConnection;
    }


    public void run() {

        try {
            inputStream = new ObjectInputStream(portConnection.getInputStream());
            outputStream = new ObjectOutputStream(portConnection.getOutputStream());

            

        } catch(Exception e) {
            e.printStackTrace();
        }


    }

}
