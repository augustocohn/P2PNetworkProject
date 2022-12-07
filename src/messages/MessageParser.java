package messages;

import peer.Peer;
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
                //remove any potentially requested pieces so it can be re-requested
                ma.removeFromRequested(peerID, connectedPeer); //TODO not sure if this does anything, but trying to fix null index problem
                Peer.getPeerByID(peerID).getLogger().chokeByLog(connectedPeer);
                break;
            case 1:
                //unchoke
                ma.removeFromChokedBy(peerID, connectedPeer);
                mr.sendRequestMessage(peerID, connectedPeer);
                Peer.getPeerByID(peerID).getLogger().unchokeByLog(connectedPeer);
                break;

            case 2:
                //interested
                ma.addToInterestedNeighbors(peerID, connectedPeer);
                Peer.getPeerByID(peerID).getLogger().receiveInterestedLog(connectedPeer);
                break;

            case 3:
                //not interested
                ma.removeFromInterestedNeighbors(peerID, connectedPeer);
                Peer.getPeerByID(peerID).getLogger().receiveNotInterestedLog(connectedPeer);
                break;

            case 4:
                //have
                index = (ByteBuffer.wrap(message.getMessagePayload())).getInt();
                ma.updateNeighborBitField(peerID, connectedPeer, index);
                mr.updateBitField(peerID, connectedPeer);
                Peer.getPeerByID(peerID).getLogger().receiveHaveLog(connectedPeer, index);
                break;

            case 5:
                //bitfield
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
                Integer tempInd;
                tempInd = bitUtil.getIndex(peerID, connectedPeer);
                if(tempInd == null) {
                    break;
                }
                index = tempInd;
                //Add content to file
                ma.placePiece(peerID, connectedPeer, index, message.getMessagePayload());

                Peer.getPeerByID(peerID).getLogger().downloadPieceFromLog(connectedPeer, index, Peer.getPeerByID(peerID).getPieceCount());

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
