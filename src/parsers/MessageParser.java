package parsers;

import messages.Message;
import messages.MessageAction;
import peer.Peer;
import utils.BitFieldUtility;

import java.nio.ByteBuffer;

public class MessageParser {

    public static void ParseMessage(Message message, int peerID, int connectedPeer) {

        MessageAction ma = new MessageAction();
        BitFieldUtility bitUtil = new BitFieldUtility();

        // parse the message depending on the corresponding message type and forward it to the message action
        switch(message.getMessageType()) {
            case 0:
                ma.addToChokedBy(peerID, connectedPeer);
                break;
            case 1:
                ma.removeFromChokedBy(peerID, connectedPeer);
                break;

            case 2:
                ma.addToInterestedNeighbors(peerID, connectedPeer);
                break;

            case 3:
                ma.removeFromInterestedNeighbors(peerID, connectedPeer);
                break;

            case 4:
                //TODO
                break;

            case 5:
                ma.updateNeighborBitFields(peerID, connectedPeer, message.getMessagePayload());
                break;

            case 6:
                //request message | kick off sending piece message
                //Message Action to send message containing literal file contents
                break;

            case 7:
                //piece message | update bitfield
                //Add content to file
                int piece = (ByteBuffer.wrap(message.getMessagePayload())).getInt();

                bitUtil.placePiece(peerID, piece, message.getMessagePayload());
                //Update bitfield
                bitUtil.updateBitfield(peerID, piece);
                //
                //send have message to all neighbors
                break;

            default:
                // throw error or something idk - not a valid message type therefore not a valid message
                break;
        }


    }


}
