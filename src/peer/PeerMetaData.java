package peer;

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
