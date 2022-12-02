package utils;

import messages.MessageAction;
import messages.MessageResponse;
import parsers.CommonConfigParser;
import peer.Peer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Stream;

public final class BitFieldUtility {

    final static byte[] pos = new byte[] {(byte)0b10000000, (byte)0b01000000, (byte)0b00100000, (byte)0b00010000,
            (byte)0b00001000, (byte)0b00000100, (byte)0b00000010, (byte)0b00000001};


    public void updateBitfield(int peerID, int piece){
        Peer peer = Peer.getPeerByID(peerID);
        int index = piece/8;
        int pos_index = piece%8;
        byte[] updated_bitfield = peer.getLocalBitField();
        updated_bitfield[index] = (byte)(updated_bitfield[index] | pos[pos_index]);
        peer.setLocalBitField(updated_bitfield);
    }

    public void updateNeighborBitField(int peerID, int connectedPeerID, int piece){
        Peer peer = Peer.getPeerByID(peerID);
        int index = piece/8;
        int pos_index = piece%8;
        byte[] updated_bitfield = peer.getNeighborBitFields().get(connectedPeerID);
        updated_bitfield[index] = (byte)((updated_bitfield[index]) | pos[pos_index]);
        peer.getNeighborBitFields().replace(connectedPeerID, updated_bitfield);
    }

    public void compareBitfields(int peerID){
        MessageResponse mr = new MessageResponse();
        Peer peer = Peer.getPeerByID(peerID);

        for(Peer p : Peer.getPeers().values()){
            if(compareBitfield(peer, p)) {
                mr.sendNotInterestedMessage(peerID, p.getPeerID());
            } else {
                mr.sendInterestedMessage(peerID, p.getPeerID());
            }
        }
    }

    public boolean compareBitfield(Peer peer, Peer connectedPeer) {

        byte[] peerBitfield = peer.getLocalBitField().clone();
        byte[] connectedPeerBitfield = peer.getNeighborBitFields().get(connectedPeer.getPeerID());

        for(int i = 0; i < peerBitfield.length; i++) {
            byte tempByte = (byte)(peerBitfield[i] | connectedPeerBitfield[i]);
            if(tempByte != peerBitfield[i]) {
                return false;
            }
        }

        return true;
    }

    public void placePiece(int peerID, int index, byte[] piece){
        int size = CommonConfigParser.getCommonMetaData().getPieceSize();
        Peer peer = Peer.getPeerByID(peerID);
        ByteBuffer buffer = ByteBuffer.wrap(peer.getFile());
        buffer.put(piece, size*index, piece.length);
        peer.setFile(buffer.array());
    }




}
