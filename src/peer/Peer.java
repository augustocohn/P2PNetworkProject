package peer;

import logger.Logger;
import messages.MessageResponse;
import parsers.CommonConfigParser;
import parsers.PeerConfigParser;
import utils.BitFieldUtility;
import utils.Download;
import utils.FileUtility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Files;


public class Peer extends Thread{

    // current peer's ID number
    private int peerID;

    // current peer's port number
    private int port_num;

    // logger to write log files for each peer
    private Logger logger;

    private int pieceCount;

    private boolean hasFile = false;

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
    // key is peerID, value is index
    private HashMap<Integer, Integer> requested_pieces = new HashMap<>();

    // update on given interval
    private HashSet<Integer> chokedby = new HashSet<>();

    // update on given interval
    private HashSet<Integer> unchoked_neighbors = new HashSet<>();

    // used in unison with the priority queue to determine the unchoked neighbors
    private HashSet<Integer> interested_neighbors = new HashSet<>();

    // recalculate after given interval
    // top k are the preferred neighbors
    // if there are any extra neighbors, of those one will be randomly unchoked on given interval
    private HashSet<Download> priorityNeighborsSet = new HashSet<>();

    // for logger functionality
    private HashSet<Integer> preferredNeighbors = new HashSet<>();

    // will be NULL if <k preferred neighbors at a given time (will account for in functionality)
    private Integer optimistically_unchoked; //TODO implement functionality for this (sending requests if optimistically unchoked timer task)

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

    public int getPieceCount() {
        return this.pieceCount;
    }

    public void incrementPieceCount() {
        this.pieceCount++;
    }

    public Integer getOptimistically_unchoked() {
        return optimistically_unchoked;
    }

    public void setLocalBitField(byte[] _bitField){
        this.bitField = _bitField;
    }

    synchronized public static HashMap<Integer, Peer> getPeers(){
        return peers;
    }

    public HashMap<Integer, byte[]> getNeighborBitFields(){
        return this.neighbor_bitFields;
    }

    public HashMap<Integer, Integer> getRequested_pieces(){
        return this.requested_pieces;
    }

    public ArrayList<Integer> getPreferredNeighbors() {
        return new ArrayList<>(this.preferredNeighbors);
    }

    public HashSet<Integer> getChokedby(){
        return this.chokedby;
    }

    public HashSet<Integer> getUnchoked_neighbors(){
        return this.unchoked_neighbors;
    }

    synchronized public HashSet<Download> getPriorityNeighbors(){
        return this.priorityNeighborsSet;
    }

    public ArrayList<OutgoingConnection> getOutgoingConnections() {
        return this.outgoingConnections;
    }

    public byte[] getFile(){
        return this.file;
    }

    public Path getFileLocation(){
        return this.fileLocation;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public boolean hasCompleteFile(){
        return this.hasFile;
    }

    public void downloadComplete(){
        this.hasFile = true;
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
        for(int i = 0; i < byteAmount-1; i++){
            this.bitField[i] = hasFile ? (byte)0b11111111 : (byte)0b00000000;
        }
        BitFieldUtility bitUtil = new BitFieldUtility();
        this.bitField[this.bitField.length-1] = hasFile ? bitUtil.getLastPos()[calculatePieces()%8] : (byte)0b00000000;
    }

    public Peer(int peerID) {
        this.peerID = peerID;
        peers.put(peerID, this);

        String fileName = CommonConfigParser.getCommonMetaData().getFileName();
        this.fileLocation = Paths.get(System.getProperty("user.dir") + "\\" + peerID + "\\" + fileName);
        this.hasFile = PeerConfigParser.getPeerMetaData(peerID).hasFile();

        this.logger = new Logger(this.peerID);

        initializeBitField(this.hasFile, calculatePieces());

        //TODO Initialize file[] to proper size
        file = new byte[CommonConfigParser.getCommonMetaData().getFileSize()];

        if(this.hasFile){
            try {
                pieceCount = calculatePieces();
                file = Files.readAllBytes(this.fileLocation);
            } catch (Exception e){
                System.out.println("CANNOT READ FILE");
            }
        } else {
            pieceCount = 0;
        }

    }

    public static int calculatePieces(){
        int fileSize = CommonConfigParser.getCommonMetaData().getFileSize();
        int pieceSize = CommonConfigParser.getCommonMetaData().getPieceSize();

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

    synchronized void checkIfCanClose() {
        BitFieldUtility bitUtil = new BitFieldUtility();

        Collection<Peer> tempPeers = Peer.getPeers().values();

        for (Peer tempPeer : tempPeers) {
            if (!tempPeer.hasCompleteFile()) {
                return;
            }
        }

        if(Peer.getPeers().size() != 1) {
            can_close_connection = true;
        }

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

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // updates preferred neighbors
        Timer timer1 = new Timer();
        UpdatePreferredNeighbors updatePreferredNeighbors = new UpdatePreferredNeighbors();
        timer1.schedule(updatePreferredNeighbors, 0, CommonConfigParser.getCommonMetaData().getUnchokingInterval() * 1000L);

        // updates optimistically unchoked neighbor
        Timer timer2 = new Timer();
        UpdateOptimisticallyUnchokedNeighbor updateOptimisticallyUnchokedNeighbor = new UpdateOptimisticallyUnchokedNeighbor();
        timer2.schedule(updateOptimisticallyUnchokedNeighbor, 0, CommonConfigParser.getCommonMetaData().getOptimUnchokingInterval() * 1000L);

        while(!can_close_connection) {
            checkIfCanClose();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //TODO need an effective way to run these below based on the canCloseConnection boolean to kill the timer tasks
        // UPDATE: may not need to ever kill these timer tasks since I believe that once all user threads terminate, so do the timer tasks
        updatePreferredNeighbors.cancel();
        updateOptimisticallyUnchokedNeighbor.cancel();

        if(!PeerConfigParser.getPeerMetaData(peerID).hasFile()) {
            FileUtility fileUtil = new FileUtility();
            fileUtil.writeFileArrayToFile(peerID);
        }

        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.closeFileWriter();

        System.out.println("Client thread " + this.peerID + " has ended");

    }
    // to create functionality here, need to find out how to calculate the download rate for each neighbor
    class UpdatePreferredNeighbors extends TimerTask {
        public void run() { // this may need to be synchronized but I don't think it does
            //System.out.println("preferred neighbors updated for peer " + peerID);

            if(can_close_connection) {
                return;
            }

            PriorityQueue<Download> neighs = new PriorityQueue<>(priorityNeighborsSet);

            getUnchoked_neighbors().clear();
            int count = 0;

            MessageResponse mr = new MessageResponse();
            BitFieldUtility bitUtil = new BitFieldUtility();

            preferredNeighbors.clear();

            if(hasCompleteFile()) {  //if bitfield is full

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
                    preferredNeighbors.add(tempInterested);
                    if(optimistically_unchoked == null || tempInterested != optimistically_unchoked && !Peer.getPeerByID(tempInterested).hasCompleteFile()) {
                        mr.sendUnchokeMessage(peerID, tempInterested);
                    }

                }
            } else {
                while (!neighs.isEmpty()) {

                    if (count == CommonConfigParser.getCommonMetaData().getNumOfPrefNeighbors()) {
                        break;
                    }

                    Download top = neighs.poll();
                    if (getInterestedNeighbors().contains(top.getPeerID())) {
                        mr.addToUnchokedNeighbors(peerID, top.getPeerID());
                        preferredNeighbors.add(top.getPeerID());
                        if(optimistically_unchoked == null || top.getPeerID() != optimistically_unchoked) {
                            mr.sendUnchokeMessage(peerID, top.getPeerID());
                        }
                        count++;
                    }
                }

                for(Peer p : Peer.getPeers().values()) {
                    if(!getUnchoked_neighbors().contains(p.getPeerID()) && (optimistically_unchoked == null || p.getPeerID() != optimistically_unchoked)) {
                        mr.sendChokeMessage(peerID, p.getPeerID());
                    }
                }

            }
            logger.changePreferredNeighborsLog();
        }

    }

    class UpdateOptimisticallyUnchokedNeighbor extends TimerTask {
        public void run() { // this may need to be synchronized but I don't think it does

            if(can_close_connection) {
                return;
            }

            PriorityQueue<Download> interestedNeighborsCopy = new PriorityQueue<>(priorityNeighborsSet);
            System.out.println(interestedNeighborsCopy);
//            if(priorityNeighborsSet != null) {
//                interestedNeighborsCopy = new PriorityQueue<>(priorityNeighborsSet);
//            } else {
//                interestedNeighborsCopy = new PriorityQueue<>();
//            }

            int K = CommonConfigParser.getCommonMetaData().getNumOfPrefNeighbors();

            for(int count = 0; count < K; count++) {
                // conditional for if we have fewer interested neighbors than we do preferred neighbors allowed
                if(interestedNeighborsCopy.isEmpty()) {
                    optimistically_unchoked = null;
                    return;
                }
                interestedNeighborsCopy.poll();
            }

            if(interestedNeighborsCopy.isEmpty()) {
                optimistically_unchoked = null;
                return;
            }

            // randomly select a value from the priority queue (can't one liner this bc size needs to be extracted)
            ArrayList<Download> interested_neighbors_list = new ArrayList<>(interestedNeighborsCopy);
            Integer optimistically_unchoked_prior = optimistically_unchoked;
            optimistically_unchoked = interested_neighbors_list.get(random.nextInt(interested_neighbors_list.size())).getPeerID();

            // if they're the same, then don't resend redundant unchoke message
            if(optimistically_unchoked_prior == null || !optimistically_unchoked_prior.equals(optimistically_unchoked)) {
                logger.changeOptimisticallyUnchokedLog();
                MessageResponse mr = new MessageResponse();
                mr.addToUnchokedNeighbors(peerID, optimistically_unchoked);
                mr.sendUnchokeMessage(peerID, optimistically_unchoked);
                System.out.println(peerID + " optimistically unchoked " + optimistically_unchoked);
            }

        }
    }

}


