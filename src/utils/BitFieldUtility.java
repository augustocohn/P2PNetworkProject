package utils;

import messages.MessageAction;
import messages.MessageResponse;
import parsers.CommonConfigParser;
import peer.Peer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public final class BitFieldUtility {

    private static Random random = new Random();

    final static byte[] pos = new byte[] {(byte)0b10000000, (byte)0b01000000, (byte)0b00100000, (byte)0b00010000,
            (byte)0b00001000, (byte)0b00000100, (byte)0b00000010, (byte)0b00000001};

    final static byte[] lastPos = new byte[] {(byte)0b11111111, (byte)0b10000000, (byte)0b11000000, (byte)0b11100000, (byte)0b11110000,
            (byte)0b11111000, (byte)0b11111100, (byte)0b11111110};


    public byte[] getLastPos(){
        return lastPos;
    }

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

    public boolean isBitFieldFull(int peerID) {
        Peer peer = Peer.getPeerByID(peerID);

        byte full = (byte)0b11111111;
        //accounts for all parts of the bitfield except the last byte
        for(int i = 0; i < peer.getLocalBitField().length - 1; i++) {

            byte temp = (byte)(full & peer.getLocalBitField()[i]);
            if(temp != full) {
                return false;
            }

        }

        int pieces = Peer.calculatePieces();
        //account for that last byte
        int mod = pieces%8;
        if(lastPos[mod] != peer.getLocalBitField()[peer.getLocalBitField().length - 1]) {
            return false;
        }
        return true;

    }

    //TEST FUNCTION
    public boolean isBitFieldFull(byte[] b){
        byte temp = (byte)(0b11111111);

        //accounts for all parts of the bitfield except the last byte
        for(int i = 0; i < b.length - 1; i++) {

            byte tempByte = (byte)(temp & b[i]);
            if(tempByte != temp) {
                return false;
            }

        }

        //account for that last byte
        int pieces = 19;
        int mod = pieces % 8;
        if(lastPos[mod] != b[b.length - 1]) {
            return false;
        }

        return true;
    }

    //TODO test the next 3 methods, might not work
    private byte[] desiredBits(int peerID, int connectedPeerID) { //returns array where bits that are 1 are requestable
        Peer peer = Peer.getPeerByID(peerID);
        Peer connectedPeer = Peer.getPeerByID(connectedPeerID);

        byte[] peerBitField = peer.getLocalBitField().clone();
        byte[] connectedPeerBitField = connectedPeer.getLocalBitField().clone();

        byte[] output = new byte[peerBitField.length];

        for(int i = 0; i < peerBitField.length; i++) {
            output[i] = (byte)((~peerBitField[i]) & (connectedPeerBitField[i]));
        }

        return output;
    }

    //TODO need to test
    private ArrayList<Integer> mapDesiredBits(int peerID, int connectedPeerID) {
        ArrayList<Integer> list = new ArrayList<>();

        byte[] desiredBits = desiredBits(peerID, connectedPeerID);

        for(int i = 0; i < desiredBits.length; i++) {
            for(int j = 0; j < pos.length; j++) {
                byte tempByte = (byte)(pos[j] & desiredBits[i]);

                if(tempByte == pos[j]) {
                    list.add((i*8) + j);
                }
            }
        }

        return list;
    }

    //TODO need to test
    public int getRequestIndex(int peerID, int connectedPeerID) {
        ArrayList<Integer> indices = mapDesiredBits(peerID, connectedPeerID);

        return indices.get(random.nextInt(indices.size()));
    }


}
