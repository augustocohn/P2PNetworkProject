package peer;

public interface GlobalConstants {
    
    //handshake message
    public static final String HS_HEADER = "P2PFILESHARINGPROJ";
    public static final int HS_ZERO_BIT_LEN = 10;
    public static final int HS_PEER_ID_LEN = 4;

    //actual messages
    public static final int MESSAGE_LENGTH_LEN = 4;
    public static final int MESSAGE_TYPE_LEN = 1;

    //message types
    public static final char MSG_TYPE_CHOKE = '0';
    public static final char MSG_TYPE_UNCHOKE = '1';
    public static final char MSG_TYPE_INTERESTED = '2';
    public static final char MSG_TYPE_NOT_INTERESTED = '3';
    public static final char MSG_TYPE_HAVE = '4';
    public static final char MSG_TYPE_BITFIELD = '5';
    public static final char MSG_TYPE_REQUEST = '6';
    public static final char MSG_TYPE_PIECE = '7';

}
