package messages;

import messages.Message;
import messages.MessageAction;
import messages.MessageResponse;
import peer.Peer;
import utils.BitFieldUtility;

import java.nio.ByteBuffer;

public class MessageParser {

    public static void ParseMessage(Message message, int peerID, int connectedPeer) {

        MessageAction ma = new MessageAction();
        MessageResponse mr = new MessageResponse();
        int index = 0;

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
                index = (ByteBuffer.wrap(message.getMessagePayload())).getInt();
                ma.updateNeighborBitField(peerID, connectedPeer, index);
                break;

            case 5:
                ma.updateNeighborBitFields(peerID, connectedPeer, message.getMessagePayload());
                break;

            case 6:
                //request message | kick off sending piece message
                index = (ByteBuffer.wrap(message.getMessagePayload())).getInt();
                mr.sendPieceMessage(peerID, connectedPeer, index);
                //Message Action to send message containing literal file contents
                break;

            case 7:
                //piece message | update bitfield
                //Add content to file
                index = (ByteBuffer.wrap(message.getMessagePayload())).getInt();
                ma.placePiece(peerID, index, message.getMessagePayload());
                //Update bitfield
                ma.updateBitField(peerID, index);
                //
                //send have message to all neighbors
                break;

            default:
                // throw error or something idk - not a valid message type therefore not a valid message
                break;
        }


    }


}
