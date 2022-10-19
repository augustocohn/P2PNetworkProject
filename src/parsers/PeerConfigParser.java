package parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class PeerConfigParser {

    private ArrayList<PeerMetaData> peers;

    public PeerConfigParser(){}

    public ArrayList<PeerMetaData> getPeersMetaData(){
        return peers;
    }

    public void parse(String filename) {
        try{
            Scanner scanner = new Scanner(new File(filename));
            while(scanner.hasNextLine()) {
                peers.add(parseLine(scanner.nextLine()));
            }
        } catch(Exception e){
            System.out.print("Failed to open file");
        }
    }

    public PeerMetaData parseLine(String line){
        int peerID_;
        String hostname_;
        int listeningPort_;
        boolean file_;

        String[] tokens = line.split(" ");
        peerID_ = Integer.parseInt(tokens[0]);
        hostname_ = tokens[1];
        listeningPort_ = Integer.parseInt(tokens[2]);
        file_ = Integer.parseInt(tokens[3]) == 1;

        return new PeerMetaData(peerID_, hostname_, listeningPort_, file_);
    }

    public class PeerMetaData{
        private final int peerID;
        private final String hostname;
        private final int listeningPort;
        private final boolean file;

        public PeerMetaData(int peerID_, String hostname_, int listeningPort_, boolean file_){
            this.peerID = peerID_;
            this.hostname = hostname_;
            this.listeningPort = listeningPort_;
            this.file = file_;
        }

        public int getPeerID(){
            return peerID;
        }

        public String getHostname() {
            return hostname;
        }

        public int getListeningPort() {
            return listeningPort;
        }

        public boolean hasFile(){
            return file;
        }
    }

}
