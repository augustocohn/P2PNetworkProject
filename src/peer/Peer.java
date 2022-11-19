package peer;

import constants.CommonMetaData;
import parsers.CommonConfigParser;
import parsers.PeerConfigParser;

import java.util.*;

public class Peer {

    // current peer's ID number
    private int peerID;

    // current peer's port number
    private int port_num;

    // should we have a static hashmap of ID's to servers so a getinstance method by ID will allow for the returning of the correct thread/object
    private static HashMap<Integer, Peer> peers = new HashMap<>();

    // this peer's current pieces (tracks what it has and what is needed)
    private byte[] bitField; //TODO figure out if last piece actually needs trailing zeros or not

    // add new when new connection made
    // update whenever a "have" message is received
    // key is peerID, value is that neighbors bitField
    private HashMap<Integer, byte[]> neighbor_bitFields = new HashMap<>();

    // add to when requested a piece
    // check if present when requesting to not re-request
    // remove if choked by a requester
    // key is piece index, value is peerID
    private HashMap<Integer, Integer> requested_pieces = new HashMap<>();

    // update on given interval
    private HashSet<Integer> chokedby = new HashSet<>();

    // update on given interval
    private HashSet<Integer> unchoked_neighbors = new HashSet<>();

    // update on given interval
    private HashSet<Integer> choked_neighbors = new HashSet<>();

    // recalculate after given interval
    // top k are the preferred neighbors
    // if there are any extra neighbors, of those one will be randomly unchoked on given interval
    private PriorityQueue<Integer> interested_neighbors;

    // will be NULL if <k preferred neighbors at a given time (will account for in functionality)
    private Integer optimistically_unchoked;

    // tracks whether or not the peer is allowed to close its connections or not
    private static boolean can_close_connection = false;

    // this is the process that waits and listens for all incoming messages and such
    private PeerProcess peer_process;

    // not sure if the arraylist of outgoing connections is needed - should be that way can be accessed easily in message actions and responses
    private ArrayList<OutgoingConnection> outgoingConnections = new ArrayList<>();

    void initializeFileToOnes(byte[] bits, int fileLength) {
        //TODO figure out how to add ones only to the values that need it
        // because there could be trailing zeros if the divisor of piecesize into filesize isn't exact

        // TODO we may not need this distinction of trailing zeros in the last piece depending on implementation
    }

    public Peer(int peerID) {
        this.peerID = peerID;
        peers.put(peerID, this);

        int pieceSize = CommonConfigParser.get_common_meta_data().get_piece_size();
        int fileSize = CommonConfigParser.get_common_meta_data().get_file_size();

        ArrayList<PeerMetaData> peesrMetaData = PeerConfigParser.getPeersMetaData();

        boolean hasFile = false;
        for(PeerMetaData peerMetaData : peesrMetaData) {
            if(peerMetaData.getPeerID() == peerID) {
                hasFile = peerMetaData.hasFile(); // will turn it true only if the peer has the file to start with
            }
        }

        if(fileSize % pieceSize != 0) {
            int temp = (fileSize / pieceSize) + 1;
            this.bitField = new byte[temp];
        } else {
            int temp = (fileSize / pieceSize);
            this.bitField = new byte[temp];
        }

        if(hasFile) {
            initializeFileToOnes(this.bitField, fileSize);
        }

        this.run();

    }

    // this is the method that exposes our Peer's to be called when methods need to be called for when messages are processed and state of
    //-values need to be updated (respective methods will be called)
    synchronized public static Peer getPeerByID(int ID) {
        return peers.get(ID);
    }

     synchronized public static boolean get_can_close_connection() {
        return Peer.can_close_connection;
    }

    private void start_peer_process() {
        this.port_num = get_port_num_from_ID();
        this.peer_process = new PeerProcess(this.port_num, this.peerID);
        this.peer_process.start();
    }

    private int get_port_num_from_ID() {
        ArrayList<PeerMetaData> peerCfgInfo = PeerConfigParser.getPeersMetaData();

        for(PeerMetaData peerMetaData : peerCfgInfo) {
            if(peerMetaData.getPeerID() == this.peerID) {
                return peerMetaData.getListeningPort();
            }
        }
        System.out.println("Invalid peer ID in peer object");
        return -1;
    }

    public void addToChokedBy(int connectedPeerID) {
        this.chokedby.add(connectedPeerID);
    }

    public void removeFromChokedBy(int connectedPeerID) {
        this.chokedby.remove(connectedPeerID);
    }

    public void addToChokedNeighbors(int peerID) {
        this.choked_neighbors.add(peerID);
        this.unchoked_neighbors.remove(peerID);
    }

    public void addToUnchokedNeighbors(int peerID) {
        this.unchoked_neighbors.add(peerID);
        this.choked_neighbors.remove(peerID);
    }

    // this needs to be synchronized so that multiple threads don't access it at a time and thus they won't have inconcurrent values
    synchronized public PriorityQueue<Integer> getInterestedNeighbors() {
        return this.interested_neighbors;
    }

    // for already created and established peerProcesses, connect to them (if bidirectional initiated connections are needed, then change this functionality)
    private void send_valid_outgoing_connections() {

        // bidirectional connections are needed and that is thus how the following loop is defined
        for (PeerMetaData peerMetaData : PeerConfigParser.getPeersMetaData()) {
            if (peerMetaData.getPeerID() == this.peerID) {
                continue;
            }
            OutgoingConnection outgoingConnection = new OutgoingConnection(this.peerID,
                    peerMetaData.getPeerID(), peerMetaData.getHostname(), peerMetaData.getListeningPort());
            outgoingConnections.add(outgoingConnection);
            outgoingConnection.start();

        }
    }

    public void run() {

        // start peerProcess
        start_peer_process();

        // send all outgoing connections
        send_valid_outgoing_connections();

        // updates preferred neighbors
        Timer timer1 = new Timer();
        timer1.schedule(new UpdatePreferredNeighbors(), 0, CommonConfigParser.get_common_meta_data().get_unchoking_interval()*1000);

        // updates optimistically unchoked neighbor
        Timer timer2 = new Timer();
        timer2.schedule(new UpdateOptimisticallyUnchokedNeighbor(), 0, CommonConfigParser.get_common_meta_data().get_optim_unchoking_interval()*1000);

        System.out.println("Client thread " + this.peerID + " has ended");

    }

    // to create functionality here, need to find out how to calculate the download rate for each neighbor
    class UpdatePreferredNeighbors extends TimerTask {
        public void run() { // this may need to be synchronized but I don't think it does
            System.out.println("preferred neighbors updated for peer " + peerID);

            //TODO need functionality to calculate download rate for a given interval to thus update preferred neighbors accordingly
            // priority queue that can somehow track the download rate as priority and the IDs as values

        }

    }

    class UpdateOptimisticallyUnchokedNeighbor extends TimerTask {
        public void run() { // this may need to be synchronized but I don't think it does
            System.out.println("optimistically unchoked neighbor updated for peer " + peerID);

            //TODO remember, this optimistically unchoked neighbor should only exist if: (# of interested neighbors > k) o.w. it should be null

        }
    }

}


