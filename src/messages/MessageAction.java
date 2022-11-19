package messages;

import peer.Peer;

public class MessageAction {

    // this class contains all of the methods to be called for each action
    // choke, unchoke, interested, uninterested, bitfield, have, request, piece


    // these following two methods might be better off in the Peer class itself
    public void updateChokeSets(int peerID, int connectedPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.addToChokedBy(connectedPeerID);

        Peer connectedPeer = Peer.getPeerByID(connectedPeerID);
        connectedPeer.addToChokedNeighbors(peerID); // also removes it from unchoked neighbors
    }

    public void updateUnchokedSets(int peerID, int connectedPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.removeFromChokedBy(connectedPeerID);

        Peer connectedPeer = Peer.getPeerByID(connectedPeerID);
        connectedPeer.addToUnchokedNeighbors(peerID); // also removes it from choked neighbors
    }






}
