build:
    javac -d ./objects -cp ./objects ./src/constants/CommonMetaData.java
    javac -d ./objects -cp ./objects ./src/constants/GlobalConstants.java
    javac -d ./objects -cp ./objects ./src/logger/Logger.java
    javac -d ./objects -cp ./objects ./src/messages/HandshakeMessage.java
    javac -d ./objects -cp ./objects ./src/messages/Message.java
    javac -d ./objects -cp ./objects ./src/messages/MessageAction.java
    javac -d ./objects -cp ./objects ./src/messages/MessageParser.java
    javac -d ./objects -cp ./objects ./src/messages/MessageResponse.java
    javac -d ./objects -cp ./objects ./src/parsers/CommonConfigParser.java
    javac -d ./objects -cp ./objects ./src/parsers/PeerConfigParser.java
    javac -d ./objects -cp ./objects ./src/peer/IncomingConnection.java
    javac -d ./objects -cp ./objects ./src/peer/OutgoingConnection.java
    javac -d ./objects -cp ./objects ./src/peer/PeetMetaData.java
    javac -d ./objects -cp ./objects ./src/peer/PeerProcess.java
    javac -d ./objects -cp ./objects ./src/startup/Begin.java
    javac -d ./objects -cp ./objects ./src/startup/StartUp.java
    javac -d ./objects -cp ./objects ./src/utils/BitFieldUtility.java
    javac -d ./objects -cp ./objects ./src/utils/FileUtility.java

clean:
    rm ./objects/*.class