package peer;

import parsers.PeerConfigParser;

import java.util.*;

public class Peer {

    // current peer's ID number
    private int peerID;

    // current peer's port number
    private int port_num;

    // this peer's current pieces (tracks what it has and what is needed)
    private byte[] bitField; //TODO must be initialized with proper size in constructor

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

    // not sure if the arraylist of outgoing connections is needed
    private ArrayList<OutgoingConnection> outgoingConnections = new ArrayList<>();


    public Peer(int peerID) {
        this.peerID = peerID;

        this.run();

        // missing something here, maybe bit field stuff from config file?
        // assign initial bit field depending on the value of has file in the meta data for the particular peer

        // TODO need to initialize bitfield (and maybe neighbor bitfields too) here (create and call a method)
    }

    static boolean get_can_close_connection() {
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

    // for already created and established peerProcesses, connect to them (if bidirectional initiated connections are needed, then change this functionality)
    private void send_valid_outgoing_connections() {

        //TODO figure out if this requires bidirectional connections or if only one connection in either direction is needed
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


    }


}
