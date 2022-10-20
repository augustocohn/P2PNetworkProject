package peer;


import java.net.Socket;

public class OutgoingConnection extends Thread {


    private int peerID;
    private int destinationPeerID;
    private String destinationHost;
    private int destinationPortNum;

    private Socket portConnection;


    public OutgoingConnection(int peerID, int destinationPeerID, String destinationHost, int destinationPortNum) {
        this.peerID = peerID;
        this.destinationPeerID = destinationPeerID;
        this.destinationHost = destinationHost;
        this.destinationPortNum = destinationPortNum;
    }


    public void run() {

        // get that connection
        while(true) {

            try {
                portConnection = new Socket(this.destinationHost, this.destinationPortNum);
                break;
            } catch(Exception e) {
                System.out.println("Waiting: trying to connect");
            }
        }

        // handshake stuff

        // send bit field message stuff

        // while connection is open, do some periodic messaging

        // once connection is allowed to be closed (boolean will break while loop above), close the outgoing connections

    }



}
