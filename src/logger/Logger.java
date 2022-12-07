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
        //uh oh spaghetti o
    }

    public void unchokeByLog(int connectedPeerID) throws IOException {
        String toWrite  = generateCurrentDateTime() + " : Peer " + this.peerID + " is unchoked by " + connectedPeerID + ".\n";
        this.fileWriter.write(toWrite);
    }

    public void chokeByLog(int connectedPeerID) throws IOException {
        String toWrite  = generateCurrentDateTime() + " : Peer " + this.peerID + " is choked by " + connectedPeerID + ".\n";
        this.fileWriter.write(toWrite);
    }

    public void receiveHaveLog(int connectedPeerID, int pieceIndex) throws IOException {
        String toWrite  = generateCurrentDateTime() + " : Peer " + this.peerID + " received the  ‘have’ message from " + connectedPeerID + " for the piece " + pieceIndex + ".\n";
        this.fileWriter.write(toWrite);
    }

    public void receiveInterestedLog(int connectedPeerID) throws IOException {
        String toWrite  = generateCurrentDateTime() + " : Peer " + this.peerID + " received the ‘interested’ message from " + connectedPeerID + ".\n";
        this.fileWriter.write(toWrite);
    }

    public void receiveNotInterestedLog(int connectedPeerID) throws IOException {
        String toWrite  = generateCurrentDateTime() + " : Peer " + this.peerID + " received the ‘not interested’ message from " + connectedPeerID + ".\n";
        this.fileWriter.write(toWrite);
    }

    public void downloadPieceFromLog(int connectedPeerID, int pieceIndex, int pieceCount) throws IOException {
        String toWrite  = generateCurrentDateTime() + " : Peer " + this.peerID + " has downloaded the piece " + pieceIndex + " from " + connectedPeerID +
                ". Now the number of pieces it has is " + pieceCount + ".\n";
        this.fileWriter.write(toWrite);
    }

    // can call this by running is bit field full when receiving a piece and printing if true
    public void completeFileDownloadLog() throws IOException {
        String toWrite  = generateCurrentDateTime() + " : Peer " + this.peerID + " has downloaded the complete file" + ".\n";
        this.fileWriter.write(toWrite);
    }

    // to be called when the Peer thread shuts down
    public void closeFileWriter() throws IOException {
        fileWriter.close();
    }

}
