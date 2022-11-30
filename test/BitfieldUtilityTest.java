import parsers.CommonConfigParser;
import parsers.PeerConfigParser;
import utils.BitFieldUtility;
import peer.Peer;

import java.util.Arrays;

public class BitfieldUtilityTest {

    public static void main(String[] args){

        CommonConfigParser.loadCommonMetaData();
        PeerConfigParser.loadPeerMetaData();

        testUpdateBitField();

        testPlacePiece();
    }

    public static void testUpdateBitField(){
        BitFieldUtility bitUtil = new BitFieldUtility();

        byte[] test = new byte[] {(byte)0b10001010, (byte)0b00110110};

        System.out.println(Arrays.toString(test));

        Peer peer = new Peer(1001);
        peer.setLocalBitField(test);

        bitUtil.updateBitfield(1001, 8);

        System.out.println(Arrays.toString(peer.getLocalBitField()));
    }

    public static void testPlacePiece(){

    }



}
