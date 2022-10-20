
import parsers.PeerConfigParser;
import peer.PeerMetaData;

import java.util.ArrayList;

public class PeerConfigParserTest {

    public static boolean compare(PeerMetaData peer1, PeerMetaData peer2){
        return peer1.getPeerID() == peer2.getPeerID() && peer1.getHostname().equals(peer2.getHostname()) &&
                peer1.getListeningPort() == peer2.getListeningPort() && peer1.hasFile() == peer2.hasFile();
    }

    public static boolean testPeersCreatedProperly(ArrayList<PeerMetaData> peers){

        PeerMetaData peer1 = new PeerMetaData(1001, "localhost", 8000, false);
        PeerMetaData peer2 = new PeerMetaData(1002, "localhost", 8001, false);

        ArrayList<PeerMetaData> truth = new ArrayList<>();
        truth.add(peer1);
        truth.add(peer2);

        for(int i = 0; i < truth.size(); i++){
            if(!compare(truth.get(i), peers.get(i))){
                return false;
            }
        }

        return true;
    }


    public static void main(String[] args){
        PeerConfigParser.loadPeerMetaData();

        boolean result = testPeersCreatedProperly(PeerConfigParser.getPeersMetaData());

        System.out.println(result);
    }
}
