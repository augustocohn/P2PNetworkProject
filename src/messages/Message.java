package messages;

public class Message {

    private int type;

    private int message_length;

    private byte[] message_payload;

    public Message(int type, int message_length, byte[] message_payload) {
        this.type = type;
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



}
