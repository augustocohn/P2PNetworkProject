package startup;

import constants.CommonMetaData;
import parsers.CommonConfigParser;
import parsers.PeerConfigParser;
import peer.Peer;
import peer.PeerMetaData;

import java.util.*;

public class StartUp extends Thread{

    public static void main(String[] args) throws InterruptedException {

        // global read-in/parsing of peer info configuration file
        PeerConfigParser.loadPeerMetaData();
        //ArrayList<PeerMetaData> peersMetaData = PeerConfigParser.getPeersMetaData();

        // global read-in/parsing of common info configuration file
        CommonConfigParser.loadCommonMetaData();
        //CommonMetaData commonMetaData = CommonConfigParser.get_common_meta_data();

        // start up connections with servers using read-in data
        for(PeerMetaData pmd : PeerConfigParser.getPeersMetaData()){
            System.out.println("Adding peer: " + pmd.getPeerID());
            Peer p = new Peer(pmd.getPeerID());
            p.start();
            Thread.sleep(2000);
        }

    }

}
