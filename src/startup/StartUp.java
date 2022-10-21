package startup;

import parsers.CommonConfigParser;
import parsers.PeerConfigParser;

public class StartUp {

    public static void main(String[] args) {

        // global read-in/parsing of peer info configuration file
        PeerConfigParser.loadPeerMetaData();

        // global read-in/parsing of common info configuration file
        CommonConfigParser.loadCommonMetaData();

        // start up connections with servers using read-in data

    }

}
