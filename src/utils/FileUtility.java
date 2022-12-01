package utils;

import parsers.CommonConfigParser;
import peer.Peer;

import java.nio.ByteBuffer;

public class FileUtility {

    public byte[] getFilePieceBytes(int peerID, int index){
        Peer peer = Peer.getPeerByID(peerID);
        int pieceSize = CommonConfigParser.getCommonMetaData().getPieceSize();
        int offset = pieceSize * index;
        ByteBuffer buf = ByteBuffer.wrap(peer.getFile(), offset, pieceSize);
        return buf.array();
    }

}
