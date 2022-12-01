package utils;

import parsers.CommonConfigParser;
import peer.Peer;

import java.nio.ByteBuffer;

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

    public void placePiece(int peerID, int index, byte[] piece){
        int size = CommonConfigParser.getCommonMetaData().getPieceSize();
        Peer peer = Peer.getPeerByID(peerID);
        ByteBuffer buffer = ByteBuffer.wrap(peer.getFile());
        buffer.put(piece, size*index, piece.length);
        peer.setFile(buffer.array());
    }

    public void updateNeighborBitField(int peerID, int connectedPeerID, int piece){
        Peer peer = Peer.getPeerByID(peerID);
        int index = piece/8;
        int pos_index = piece%8;
        byte[] updated_bitfield = peer.getNeighborBitFields().get(connectedPeerID);
        updated_bitfield[index] = (byte)((updated_bitfield[index]) | pos[pos_index]);
        peer.getNeighborBitFields().replace(connectedPeerID, updated_bitfield);
    }



}
