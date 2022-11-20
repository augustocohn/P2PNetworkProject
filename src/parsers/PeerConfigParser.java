package parsers;

import java.io.*;
import java.util.*;

import peer.PeerMetaData;

public final class PeerConfigParser {

    private static ArrayList<PeerMetaData> peers = new ArrayList<>();

    public static ArrayList<PeerMetaData> getPeersMetaData(){
        return peers;
    }

    public static int getPeersMetaDataSize(){
        return peers.size();
    }

    public static PeerMetaData getPeerMetaData(int peerID) {
        for(PeerMetaData pmd : peers){
            if(pmd.getPeerID() == peerID){
                return pmd;
            }
        }
        return null;
    }

    public static void loadPeerMetaData(){
        parse("cfg\\PeerInfo.cfg");
    }

    private static void parse(String filename) {
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

    private static PeerMetaData parseLine(String line){
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
