package logger;


import peer.Peer;

import java.util.ArrayList;
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


    public Logger(int peerID) {
        try {
            this.peerID = peerID;

            // deletes the file if it exists
            try {
                File file = new File("log_peer_" + this.peerID + ".log");
                file.delete();
            } catch (Exception e) {
                System.out.println("File did not exist to delete");
            }

            this.fileWriter = new FileWriter("log_peer_" + this.peerID + ".log");
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    // method to be called when writing a log to generate the time at which that log was generated
    private String generateCurrentDateTime() {
        return Logger.formatter.format(new Date());
    }

    public void tcpConnectionToLog(int connectedPeerID) {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " makes a connection to " + connectedPeerID + "\n";
            this.fileWriter.write(toWrite);
        }  catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void tcpConnectionFromLog(int connectedPeerID) {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " is connected from " + connectedPeerID + "\n";
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void changePreferredNeighborsLog() {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " has the list of preferred neighbors ";
            ArrayList<Integer> peerList = Peer.getPeerByID(this.peerID).getPreferredNeighbors();

            for(int i = 0; i < peerList.size(); i++) {
                if(i == peerList.size() - 1) {
                    toWrite = toWrite + peerList.get(i);
                } else {
                    toWrite = toWrite + peerList.get(i) + ", ";
                }
            }
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void changeOptimisticallyUnchokedLog() {
        try {
            Peer peer = Peer.getPeerByID(this.peerID);
            String toWrite;
            if (peer.getOptimistically_unchoked() == null) {
                toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " does not have an optimistically unchoked neighbor " + ".\n";
            } else {
                toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " has the optimistically unchoked neighbor " + peer.getOptimistically_unchoked() + ".\n";
            }
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void unchokeByLog(int connectedPeerID) {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " is unchoked by " + connectedPeerID + ".\n";
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void chokeByLog(int connectedPeerID) {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " is choked by " + connectedPeerID + ".\n";
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void receiveHaveLog(int connectedPeerID, int pieceIndex) {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " received the  ‘have’ message from " + connectedPeerID + " for the piece " + pieceIndex + ".\n";
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void receiveInterestedLog(int connectedPeerID) {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " received the ‘interested’ message from " + connectedPeerID + ".\n";
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void receiveNotInterestedLog(int connectedPeerID) {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " received the ‘not interested’ message from " + connectedPeerID + ".\n";
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    public void downloadPieceFromLog(int connectedPeerID, int pieceIndex, int pieceCount) {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " has downloaded the piece " + pieceIndex + " from " + connectedPeerID +
                    ". Now the number of pieces it has is " + pieceCount + ".\n";
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    // can call this by running is bit field full when receiving a piece and printing if true
    public void completeFileDownloadLog() {
        try {
            String toWrite = generateCurrentDateTime() + " : Peer " + this.peerID + " has downloaded the complete file" + ".\n";
            this.fileWriter.write(toWrite);
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

    // to be called when the Peer thread shuts down
    public void closeFileWriter() {
        try {
            fileWriter.close();
        } catch(Exception e) {
            System.out.println("Logger error");
        }
    }

}
