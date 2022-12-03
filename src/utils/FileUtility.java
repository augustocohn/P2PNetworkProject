package utils;

import parsers.CommonConfigParser;
import peer.Peer;

import java.nio.ByteBuffer;
import java.nio.file.Files;

public class FileUtility {

    public byte[] getFilePieceBytes(int peerID, int index){
        Peer peer = Peer.getPeerByID(peerID);
        int pieceSize = CommonConfigParser.getCommonMetaData().getPieceSize();
        int fileSize = CommonConfigParser.getCommonMetaData().getFileSize();
        int offset = pieceSize * index;

        if(pieceSize > fileSize-offset){
            pieceSize = fileSize-offset;
        }

        ByteBuffer buf = ByteBuffer.wrap(peer.getFile(), offset, pieceSize);
        return buf.array();
    }

    public void writeFileArrayToFile(int peerID){
        Peer peer = Peer.getPeerByID(peerID);
        try {
            Files.write(peer.getFileLocation(), peer.getFile());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
