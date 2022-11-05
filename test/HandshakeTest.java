import messages.HandshakeMessage;
import constants.GlobalConstants;

import java.nio.ByteBuffer;

public class HandshakeTest {


    public static void main(String[] args){

        HandshakeMessage mes = new HandshakeMessage(1001);

        String test = mes.getStringMessage();

        System.out.print(test);

    }
}
