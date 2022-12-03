package peer;

import messages.MessageResponse;
import parsers.CommonConfigParser;
import parsers.PeerConfigParser;
import utils.BitFieldUtility;
import utils.Download;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Files;


public class Peer {

    // current peer's ID number
    private int peerID;

    // current peer's port number
    private int port_num;

    // should we have a static hashmap of ID's to servers so a getinstance method by ID will allow for the returning of the correct thread/object
    private static HashMap<Integer, Peer> peers = new HashMap<>();

    // this peer's current pieces (tracks what it has and what is needed)
    private byte[] bitField; //TODO figure out if last piece actually needs trailing zeros or not

    //stores the literal contents of the file
    private byte[] file;

    private Path fileLocation;

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

    // used in unison with the priority queue to determine the unchoked neighbors
    private HashSet<Integer> interested_neighbors;

    // recalculate after given interval
    // top k are the preferred neighbors
    // if there are any extra neighbors, of those one will be randomly unchoked on given interval
    private HashSet<Download> priority_neighbors_set;

    // will be NULL if <k preferred neighbors at a given time (will account for in functionality)
    private Integer optimistically_unchoked;

    // tracks whether or not the peer is allowed to close its connections or not
    private static boolean can_close_connection = false;

    // this is the process that waits and listens for all incoming messages and such
    private PeerProcess peer_process;

    // not sure if the arraylist of outgoing connections is needed - should be that way can be accessed easily in message actions and responses
    private ArrayList<OutgoingConnection> outgoingConnections = new ArrayList<>();

    // random generator (variable so "randomness" is not reset)
    public static Random random = new Random();

    public int getPeerID() {
        return this.peerID;
    }

    public byte[] getLocalBitField(){
        return this.bitField;
    }

    public void setLocalBitField(byte[] _bitField){
        this.bitField = _bitField;
    }

    public static HashMap<Integer, Peer> getPeers(){
        return peers;
    }

    public HashMap<Integer, byte[]> getNeighborBitFields(){
        return this.neighbor_bitFields;
    }

    public HashMap<Integer, Integer> getRequested_pieces(){
        return this.requested_pieces;
    }

    public HashSet<Integer> getChokedby(){
        return this.chokedby;
    }

    public HashSet<Integer> getUnchoked_neighbors(){
        return this.unchoked_neighbors;
    }
    public HashSet<Integer> getInterested_neighbors(){
        return this.interested_neighbors;
    }

    synchronized public HashSet<Download> getPriority_neighbors(){
        return this.priority_neighbors_set;
    }

    public ArrayList<OutgoingConnection> getOutgoingConnections() {
        return this.outgoingConnections;
    }

    public byte[] getFile(){
        return this.file;
    }

    public void setFile(byte[] _file){
        this.file = _file;
    }

    void initializeBitField(boolean hasFile, int pieces) {
        //TODO figure out how to add ones only to the values that need it
        // because there could be trailing zeros if the divisor of piecesize into filesize isn't exact

        // TODO we may not need this distinction of trailing zeros in the last piece depending on implementation
        int byteAmount = pieces%8 == 0 ? pieces/8 : pieces/8+1;
        this.bitField = new byte[byteAmount];
        for(int i = 0; i < byteAmount; i++){
            this.bitField[i] = hasFile ? (byte)0b11111111 : (byte)0b00000000;
        }
    }

    public Peer(int peerID) {
        this.peerID = peerID;
        peers.put(peerID, this);

        int pieceSize = CommonConfigParser.getCommonMetaData().getPieceSize();
        int fileSize = CommonConfigParser.getCommonMetaData().getFileSize();
        String fileName = CommonConfigParser.getCommonMetaData().getFileName();
        this.fileLocation = Paths.get(System.getProperty("user.dir") + "\\" + peerID + "\\" + fileName);
        boolean hasFile = PeerConfigParser.getPeerMetaData(peerID).hasFile();

        initializeBitField(hasFile, calculatePieces(fileSize, pieceSize));

        //TODO Initialize file[] to proper size
        file = new byte[CommonConfigParser.getCommonMetaData().getFileSize()];

        if(hasFile){
            try {
                file = Files.readAllBytes(this.fileLocation);
            } catch (Exception e){
                System.out.println("CANNOT READ FILE");
            }
        }

        this.run();

    }

    private int calculatePieces(int fileSize, int pieceSize){
        if(fileSize % pieceSize != 0){
            return (fileSize / pieceSize) + 1;
        } else {
            return (fileSize / pieceSize);
        }
    }

    // this is the method that exposes our Peer's to be called when methods need to be called for when messages are processed and state of
    //-values need to be updated (respective methods will be called)
    synchronized public static Peer getPeerByID(int ID) {
        return peers.get(ID);
    }

     synchronized public static boolean getCanCloseConnection() {
        return Peer.can_close_connection;
    }

    private void startPeerProcess() {
        this.port_num = getPortNumFromID();
        this.peer_process = new PeerProcess(this.port_num, this.peerID);
        this.peer_process.start();
    }

    private int getPortNumFromID() {
        ArrayList<PeerMetaData> peerCfgInfo = PeerConfigParser.getPeersMetaData();

        for(PeerMetaData peerMetaData : peerCfgInfo) {
            if(peerMetaData.getPeerID() == this.peerID) {
                return peerMetaData.getListeningPort();
            }
        }
        System.out.println("Invalid peer ID in peer object");
        return -1;
    }

    // this needs to be synchronized so that multiple threads don't access it at a time and thus they won't have inconcurrent values
    synchronized public HashSet<Integer> getInterestedNeighbors() {
        return this.interested_neighbors;
    }

    // for already created and established peerProcesses, connect to them (if bidirectional initiated connections are needed, then change this functionality)
    private void sendValidOutgoingConnections() {

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
        startPeerProcess();

        // send all outgoing connections
        sendValidOutgoingConnections();

        // updates preferred neighbors
        Timer timer1 = new Timer();
        UpdatePreferredNeighbors updatePreferredNeighbors = new UpdatePreferredNeighbors();
        timer1.schedule(updatePreferredNeighbors, 0, CommonConfigParser.getCommonMetaData().getUnchokingInterval()*1000);

        // updates optimistically unchoked neighbor
        Timer timer2 = new Timer();
        UpdateOptimisticallyUnchokedNeighbor updateOptimisticallyUnchokedNeighbor = new UpdateOptimisticallyUnchokedNeighbor();
        timer2.schedule(updateOptimisticallyUnchokedNeighbor, 0, CommonConfigParser.getCommonMetaData().getOptimUnchokingInterval()*1000);


        //TODO need an effective way to run these below based on the canCloseConnection boolean to kill the timer tasks
        // UPDATE: may not need to ever kill these timer tasks since I believe that once all user threads terminate, so do the timer tasks
//        updatePreferredNeighbors.cancel();
//        updateOptimisticallyUnchokedNeighbor.cancel();

        System.out.println("Client thread " + this.peerID + " has ended");

    }
    // to create functionality here, need to find out how to calculate the download rate for each neighbor
    class UpdatePreferredNeighbors extends TimerTask {
        synchronized public void run() { // this may need to be synchronized but I don't think it does
            //System.out.println("preferred neighbors updated for peer " + peerID);

            //TODO need functionality to calculate download rate for a given interval to thus update interested neighbors accordingly
            // priority queue that can somehow track the download rate as priority and the IDs as values

            //TODO
            // deconstructs priority queue (or destroys it, whatever)
            // reconstructs it using calculations via values from previous interval (make sure this calls get interested neighbors bc synchronous)
            PriorityQueue<Download> neighs = new PriorityQueue<>(priority_neighbors_set);
            getUnchoked_neighbors().clear();
            int count = 0;

            MessageResponse mr = new MessageResponse();

            BitFieldUtility bitUtil = new BitFieldUtility();

            if(bitUtil.isBitFieldFull(peerID)) {  //if bitfield is full

                // this peer has a full file, the preferred neighbors are randomly selected

                ArrayList<Integer> interested = new ArrayList<>(getInterestedNeighbors());

                int K = CommonConfigParser.getCommonMetaData().getNumOfPrefNeighbors();

                for(int i = 0; i < K; i++) {

                    if(interested.isEmpty()) {
                        break;
                    }

                    int index = random.nextInt(interested.size());
                    int tempInterested = interested.get(index);
                    interested.remove(index);

                    mr.addToUnchokedNeighbors(peerID, tempInterested);
                    mr.sendUnchokeMessage(peerID, tempInterested);

                }


            } else {
                while (!neighs.isEmpty()) {

                    if (count == CommonConfigParser.getCommonMetaData().getNumOfPrefNeighbors()) {
                        break;
                    }

                    Download top = neighs.poll();
                    if (getInterested_neighbors().contains(top.getPeerID())) {
                        mr.addToUnchokedNeighbors(peerID, top.getPeerID());
                        mr.sendUnchokeMessage(peerID, top.getPeerID());
                        count++;
                    }
                }

                for(Peer p : Peer.getPeers().values()) {
                    if(!getUnchoked_neighbors().contains(p)) {
                        mr.sendChokeMessage(peerID, p.getPeerID());
                    }
                }

            }
        }

    }

    class UpdateOptimisticallyUnchokedNeighbor extends TimerTask {
        synchronized public void run() { // this may need to be synchronized but I don't think it does

            PriorityQueue<Download> interested_neighbors_copy = new PriorityQueue<>(priority_neighbors_set);

            int K = CommonConfigParser.getCommonMetaData().getNumOfPrefNeighbors();

            for(int count = 0; count < K; count++) {
                // conditional for if we have fewer interested neighbors than we do preferred neighbors allowed
                if(interested_neighbors_copy.isEmpty()) {
                    optimistically_unchoked = null;
                    return;
                }
                interested_neighbors_copy.poll();
            }

            if(interested_neighbors_copy.isEmpty()) {
                optimistically_unchoked = null;
                return;
            }

            // randomly select a value from the priority queue (can't one liner this bc size needs to be extracted)
            ArrayList<Download> interested_neighbors_list = new ArrayList<>(interested_neighbors_copy);
            optimistically_unchoked = interested_neighbors_list.get(random.nextInt(interested_neighbors_list.size())).getPeerID();

        }
    }

}


