package logger;


import peer.Peer;

import java.util.Date;
import java.text.SimpleDateFormat;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

// this logger should be a class attribute for Peer so that each peer can generate its own logs
public class Logger {

    // I think we should have a constructor that should create the log file upon creation for that particular peer
    // Logger should be an attribute for a given peer

    private int peerID;
    private FileWriter fileWriter;

    public static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


    public Logger(int peerID) throws IOException {
        this.peerID = peerID;
        this.fileWriter = new FileWriter("log_peer_" + this.peerID + ".log");
    }

    // method to be called when writing a log to generate the time at which that log was generated
    private String generateCurrentDateTime() {
        return Logger.formatter.format(new Date());
    }

    public void tcpConnectionToLog(int connectedPeerID) throws IOException {
        String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " makes a connection to " + connectedPeerID + "\n";
        this.fileWriter.write(toWrite);
    }

    public void tcpConnectionFromLog(int connectedPeerID) throws IOException {
        String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " is connected from " + connectedPeerID + "\n";
        this.fileWriter.write(toWrite);
    }

    public void changePreferredNeighborsLog(int connectedPeerID) throws IOException {
        String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " has the list of preferred neighbors ";
//        for(Peer peer : Peer.getPeerByID(this.peerID).get) //write a method to get the preferred neighbors (need the data structure and add and clear in TimerTask
        this.fileWriter.write(toWrite);
    }

    public void changeOptimisticallyUnchokedLog(int connectedPeerID) throws IOException {

    }

    public void unchokeByLog(int connectedPeerID) throws IOException {

    }

    public void chokeByLog(int connectedPeerID) throws IOException {

    }

    public void receiveHaveLog(int connectedPeerID) throws IOException {

    }

    public void receiveInterestedLog(int connectedPeerID) throws IOException {

    }

    public void receiveNotInterestedLog(int connectedPeerID) throws IOException {

    }

    public void downloadPieceFromLog(int connectedPeerID) throws IOException {

    }

    // can call this by running is bit field full when receiving a piece and printing if true
    public void completeFileDownloadLog(int connectedPeerID) throws IOException {

    }

    // to be called when the Peer thread shuts down
    public void closeFileWriter() throws IOException {
        fileWriter.close();
    }

}
