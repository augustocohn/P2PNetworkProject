package parsers;

import java.io.*;
import java.util.*;

import peer.PeerMetaData;

public class PeerConfigParser {

    private ArrayList<PeerMetaData> peers;

    public PeerConfigParser(){
        peers = new ArrayList<>();
    }

    public ArrayList<PeerMetaData> getPeersMetaData(){
        return peers;
    }

    public void parse(String filename) {
        try{
            //Scanner scanner = new Scanner(new File(filename));
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = in.readLine();
            while(line != null) {
                peers.add(parseLine(line));
                line = in.readLine();
            }
        } catch(Exception e){
            System.out.println("Failed to open file");
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

}
