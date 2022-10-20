package peer;

import parsers.PeerConfigParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class peer {

    // current peer's ID number
    private int peerID;

    // current peer's port number
    private int port_num;

    // this peer's current pieces (tracks what it has and what is needed)
    private byte[] bitField;

    // add new when new connection made
    // update whenever a "have" message is received
    // key is peerID, value is that neighbors bitField
    private HashMap<Integer ,byte[]> neighbor_bitFields;

    // add to when requested a piece
    // check if present when requesting to not re-request
    // remove if choked by a requester
    // key is piece index, value is peerID
    private HashMap<Integer, Integer> requested_pieces;

    // update on given interval
    private HashSet<Integer> unchoked_neighbors;

    // update on given interval
    private HashSet<Integer> choked_neighbors;

    // recalculate after given interval
    // top k are the preferred neighbors
    // if there are any extra neighbors, of those one will be randomly unchoked on given interval
    private PriorityQueue<Integer> interested_neighbors;

    // will be NULL if <k preferred neighbors at a given time (will account for in functionality)
    private Integer optimistically_unchoked;

    // tracks whether or not the peer is allowed to close its connections or not
    private boolean can_close_connection;

    // this is the process that waits and listens for all incoming messages and such
    private PeerProcess peer_process;


    public peer(int peerID) {
        this.peerID = peerID;

        //TODO need to find a way to make this more efficient
        PeerConfigParser.loadPeerMetaData();
        ArrayList<PeerMetaData> peerCfgInfo = PeerConfigParser.getPeersMetaData();

        this.can_close_connection = false;

        // missing something here, maybe bit field stuff from config file?
        // assign initial bit field depending on the value of has file in the meta data for the particular peer
    }

    boolean get_can_close_connection() {
        return this.can_close_connection;
    }

    private void start_peer_process() {
        this.port_num = get_port_num_from_ID();
        this.peer_process = new PeerProcess(this.port_num, this.peerID);
    }

    private int get_port_num_from_ID() {
        //TODO reduce this with updated config parser to one line of code
        PeerConfigParser.loadPeerMetaData();
        ArrayList<PeerMetaData> peerCfgInfo = PeerConfigParser.getPeersMetaData();

        for(PeerMetaData peerMetaData : peerCfgInfo) {
            if(peerMetaData.getPeerID() == this.peerID) {
                return peerMetaData.getListeningPort();
            }
        }
        return -1;
    }

    public void run() {

        // start peerProcess
        start_peer_process();

        // send all outgoing connections



    }


}
