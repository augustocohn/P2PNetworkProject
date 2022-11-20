package messages;

import java.nio.ByteBuffer;
import java.util.*;

public class Message {

    private byte type;

    private int message_length;

    private byte[] message_payload;

    public Message(int type, int message_length, byte[] message_payload) {
        this.type = (byte)type;
        this.message_length = message_length;
        this.message_payload = message_payload;
    }

    public int getMessageType() {
        return type;
    }

    public int getMessageLength() {
        return message_length;
    }

    public byte[] getMessagePayload() {
        return message_payload;
    }

    public byte[] getByteMessage(){
        byte[] message = new byte[5 + message_length];
        ByteBuffer buff = ByteBuffer.wrap(message);
        buff.put(ByteBuffer.allocate(4).putInt(message_length).array());
        buff.put(type);
        buff.put(message_payload);
        return buff.array();
    }



}
