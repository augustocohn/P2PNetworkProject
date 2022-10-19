package peer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class peer {

    // current peer's ID number
    private Integer peerID;

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

    //will be NULL if <k preferred neighbors at a given time (will account for in functionality)
    private Integer optimistically_unchoked;


}
