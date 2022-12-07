package logger;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.FileHandler;

import java.util.logging.Logger;
import java.util.logging.Level;

public class LoggerFuncTest {
    public static void main(String[] args) throws IOException {
//        Logger logger = Logger.getLogger(LoggerFuncTest.class.getName());
//
//        // Create an instance of FileHandler that write log to a file called
//        // app.log. Each new message will be appended at the at of the log file.
//        FileHandler fileHandler = new FileHandler("app.log", true);
//        logger.addHandler(fileHandler);
//
//        if (logger.isLoggable(Level.INFO)) {
//            logger.info("Information message");
//        }
//
//        if (logger.isLoggable(Level.WARNING)) {
//            logger.warning("Warning message");
//        }


//        File myObj = new File("filename.txt");
//        myObj.createNewFile();

        FileWriter myWriter = new FileWriter("filename.log");
        myWriter.write("Files in Java might be tricky, but it is fun enough!\n");
        myWriter.write("UGGGGGHHHHHHHHUHHHH!");
        myWriter.close();

    }
}