package startup;

import constants.CommonMetaData;
import parsers.CommonConfigParser;
import parsers.PeerConfigParser;
import peer.Peer;
import peer.PeerMetaData;

public class StartUp extends Thread{

    public void run() {
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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //to kill the JVM and administrative threads that are created with the complex threading in occurrence
        java.lang.System.exit(0);
    }

}
