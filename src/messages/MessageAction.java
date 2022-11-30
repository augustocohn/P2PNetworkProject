package messages;

import peer.Peer;
import utils.BitFieldUtility;

public class MessageAction {

    // this class contains all of the methods to be called for each action
    // choke, unchoke, interested, uninterested, bitfield, have, request, piece


    // these following two methods might be better off in the Peer class itself
    public void addToChokedBy(int peerID, int connectedPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.getChokedby().add(connectedPeerID);
    }

    public void removeFromChokedBy(int peerID, int connectedPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.getChokedby().remove(connectedPeerID);
    }

    public void addToChokedNeighbors(int peerID, int connectedPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.getChoked_neighbors().add(connectedPeerID);
        peer.getUnchoked_neighbors().remove(connectedPeerID);
    }

    public void addToUnchokedNeighbors(int peerID, int connectPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.getUnchoked_neighbors().add(connectPeerID);
        peer.getChoked_neighbors().remove(connectPeerID);
    }

    public void addToInterestedNeighbors(int peerID, int connectPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        peer.getInterested_neighbors().add(peerID);
    }

    public void removeFromInterestedNeighbors(int peerID, int connectedPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        peer.getInterested_neighbors().remove(connectedPeerID);
    }

    public void updateNeighborBitFields(int peerID, int connectedPeerID, byte[] bitfield){
        Peer peer = Peer.getPeerByID(peerID);
        peer.getNeighbor_bitFields().put(connectedPeerID, bitfield);
    }

    public void updateChokeSets(int peerID, int connectedPeerID) {
        addToChokedBy(peerID, connectedPeerID);
    }

    public void updateUnchokedSets(int peerID, int connectedPeerID) {
        removeFromChokedBy(peerID, connectedPeerID);
    }

    public void placePiece(int peerID, int index, byte[] piece){
        BitFieldUtility bitUtil = new BitFieldUtility();
        bitUtil.placePiece(peerID, index, piece);
    }

    public void updateBitField(int peerID, int index){
        BitFieldUtility bitUtil = new BitFieldUtility();
        bitUtil.updateBitfield(peerID, index);
    }








}
