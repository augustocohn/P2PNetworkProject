import parsers.CommonConfigParser;
import constants.CommonMetaData;

public class CommonConfigParserTest {

    public static boolean compare(CommonMetaData commonMetaData1, CommonMetaData commonMetaData2) {

        return (commonMetaData1.getNumOfPrefNeighbors() == commonMetaData2.getNumOfPrefNeighbors() &&
                commonMetaData1.getUnchokingInterval() == commonMetaData2.getUnchokingInterval() &&
                commonMetaData1.getOptimUnchokingInterval() == commonMetaData2.getOptimUnchokingInterval() &&
                commonMetaData1.getFileName().equals(commonMetaData2.getFileName()) &&
                commonMetaData1.getFileSize() == commonMetaData2.getFileSize() &&
                commonMetaData1.getPieceSize() == commonMetaData2.getPieceSize());

    }

    public static boolean testCommonCreatedProperly(CommonMetaData commonMetaData1) {

        CommonMetaData commonMetaData2 = new CommonMetaData(2, 5, 15,
                "TheFile.dat", 10000232, 32768);

        return compare(commonMetaData1, commonMetaData2);

    }


    public static void main(String[] args){

        CommonConfigParser.loadCommonMetaData();

        boolean result = testCommonCreatedProperly(CommonConfigParser.getCommonMetaData());

        System.out.println(result);
    }

}
