import parsers.CommonConfigParser;
import parsers.PeerConfigParser;

public final class Loader {

    public static void loadConfigs(){
        CommonConfigParser.loadCommonMetaData();
        PeerConfigParser.loadPeerMetaData();
    }

}
