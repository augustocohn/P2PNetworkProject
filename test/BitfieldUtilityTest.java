import parsers.CommonConfigParser;
import parsers.PeerConfigParser;
import utils.BitFieldUtility;
import peer.Peer;

import java.util.Arrays;

public class BitfieldUtilityTest {

    public static void main(String[] args){

        CommonConfigParser.loadCommonMetaData();
        PeerConfigParser.loadPeerMetaData();

        //testUpdateBitField();

        testBitfieldFull();
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

    public static void testBitfieldFull(){

        BitFieldUtility bitUtil = new BitFieldUtility();

        byte[] testLastPiece = new byte[] {(byte)0b11111111, (byte)0b11111111, (byte)0b11100000};
        byte[] testInnerPiece = new byte[] {(byte)0b11011111,(byte)0b11111111, (byte)0b11100000};
        byte[] testEmpty = new byte[] {(byte)0b00000000, (byte)0b00000000, (byte)0b00000000};

        if(!bitUtil.isBitFieldFull(testLastPiece)){
            System.out.println("Full last piece broken");
        }
        if(bitUtil.isBitFieldFull(testInnerPiece)){
            System.out.println("Inner piece broken");
        }
        if(bitUtil.isBitFieldFull(testEmpty)){
            System.out.println("Empty broken");
        }

    }



}
