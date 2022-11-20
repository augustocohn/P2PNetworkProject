package parsers;

import messages.Message;

public class MessageParser {

    public static void ParseMessage(Message message, int peerID, int connectedPeer) {

        // parse the message depending on the corresponding message type and forward it to the message action
        switch(message.getMessageType()) {
            case 1:

                break;

            case 2:

                break;

            case 3:

                break;

            case 4:

                break;

            case 5:

                break;

            case 6:

                break;

            case 7:

                break;

            default:
                // throw error or something idk - not a valid message type therefore not a valid message
                break;
        }


    }


}
