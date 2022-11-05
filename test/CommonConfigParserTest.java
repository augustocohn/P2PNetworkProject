import parsers.CommonConfigParser;
import constants.CommonMetaData;

public class CommonConfigParserTest {

    public static boolean compare(CommonMetaData commonMetaData1, CommonMetaData commonMetaData2) {

        return (commonMetaData1.get_num_of_pref_neighbors() == commonMetaData2.get_num_of_pref_neighbors() &&
                commonMetaData1.get_unchoking_interval() == commonMetaData2.get_unchoking_interval() &&
                commonMetaData1.get_optim_unchoking_interval() == commonMetaData2.get_optim_unchoking_interval() &&
                commonMetaData1.get_file_name().equals(commonMetaData2.get_file_name()) &&
                commonMetaData1.get_file_size() == commonMetaData2.get_file_size() &&
                commonMetaData1.get_piece_size() == commonMetaData2.get_piece_size());

    }

    public static boolean testCommonCreatedProperly(CommonMetaData commonMetaData1) {

        CommonMetaData commonMetaData2 = new CommonMetaData(2, 5, 15,
                "TheFile.dat", 10000232, 32768);

        return compare(commonMetaData1, commonMetaData2);

    }


    public static void main(String[] args){

        CommonConfigParser.loadCommonMetaData();

        boolean result = testCommonCreatedProperly(CommonConfigParser.get_common_meta_data());

        System.out.println(result);
    }

}
