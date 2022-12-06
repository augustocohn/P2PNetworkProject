package messages;

import utils.BitFieldUtility;

import java.nio.ByteBuffer;

public class MessageParser {

    public static void ParseMessage(Message message, int peerID, int connectedPeer) {

        MessageAction ma = new MessageAction();
        MessageResponse mr = new MessageResponse();
        BitFieldUtility bitUtil = new BitFieldUtility();
        int index = 0;

        // parse the message depending on the corresponding message type and forward it to the message action
        switch(message.getMessageType()) {
            case 0:
                //choke
                ma.addToChokedBy(peerID, connectedPeer);
                break;
            case 1:
                //unchoke
                ma.removeFromChokedBy(peerID, connectedPeer);
                mr.sendRequestMessage(peerID, connectedPeer);
                break;

            case 2:
                //interested
                ma.addToInterestedNeighbors(peerID, connectedPeer);
                break;

            case 3:
                //not interested
                ma.removeFromInterestedNeighbors(peerID, connectedPeer);
                break;

            case 4:
                //have
                index = (ByteBuffer.wrap(message.getMessagePayload())).getInt();
                ma.updateNeighborBitField(peerID, connectedPeer, index);
                mr.updateBitField(peerID, connectedPeer);
                break;

            case 5:
                //bitfield
                if(connectedPeer == 1006) {
                    int x = 0;
                }
                ma.updateNeighborBitFields(peerID, connectedPeer, message.getMessagePayload());
                mr.updateBitField(peerID, connectedPeer);
                break;

            case 6:
                //request message | kick off sending piece message
                index = (ByteBuffer.wrap(message.getMessagePayload())).getInt();
                mr.sendPieceMessage(peerID, connectedPeer, index);
                break;

            case 7:
                //piece message | update bitfield

                //get index
                index = bitUtil.getIndex(peerID, connectedPeer);
                //Add content to file
                ma.placePiece(peerID, connectedPeer, index, message.getMessagePayload());
                //Update bitfield
                ma.updateBitField(peerID, index);
                //cascade interested/non-interested changes
                mr.updateBitFields(peerID);
                //send have message to all neighbors
                mr.sendHaveMessage(peerID, index);
                //update Downloads priority queue values
                ma.updateDownloadSpeed(peerID, connectedPeer);
                //send another request message if the correct conditions are met
                mr.sendAnotherRequest(peerID, connectedPeer);

                break;

            default:
                // throw error or something idk - not a valid message type therefore not a valid message
                break;
        }


    }


}
