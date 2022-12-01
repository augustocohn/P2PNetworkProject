import peer.Peer;

import java.util.Arrays;

public class FileInitTest {

    public static void main(String[] args){

        Loader.loadConfigs();

        Peer peer = new Peer(1001);

        System.out.println(peer.getFile().length);

    }


}
