package messages;

import constants.GlobalConstants; // might move this to an enumeration in the messages class

import java.util.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HandshakeMessage{

    private final byte[] header = GlobalConstants.HS_HEADER.getBytes(StandardCharsets.UTF_8);
    private final byte[] pad = new byte[GlobalConstants.HS_ZERO_BIT_LEN];
    private byte[] peerID;

    public HandshakeMessage(int peerID_){
        //convert int to byte array
        peerID = ByteBuffer.allocate(4).putInt(peerID_).array();
    }

    public byte[] getByteMessage(){
        byte[] message = new byte[GlobalConstants.HS_MESSAGE_LEN];
        ByteBuffer buff = ByteBuffer.wrap(message);
        buff.put(header);
        buff.put(pad);
        buff.put(peerID);
        return buff.array();
    }

    public String getStringMessage(){
        return new String(getByteMessage(), StandardCharsets.UTF_8);
    }



}
