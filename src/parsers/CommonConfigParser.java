package parsers;

import constants.CommonMetaData;

import java.io.BufferedReader;
import java.io.FileReader;

public class CommonConfigParser {

    public static CommonMetaData getCommonMetaData() {
        return CommonConfigParser.commonMetaData;
    }

    public static void loadCommonMetaData(){
        parse("cfg\\Common.cfg");
    }

    private static CommonMetaData commonMetaData;

    private static void parse(String filename) {
        try{
            BufferedReader in = new BufferedReader(new FileReader(filename));

            int num_of_pref_neighbors;
            int unchoking_interval;
            int optim_unchoking_interval;
            String file_name;
            int file_size;
            int piece_size;

            String line1 = in.readLine();
            String[] tokens1 = line1.split(" ");
            num_of_pref_neighbors = Integer.parseInt(tokens1[1]);

            String line2 = in.readLine();
            String[] tokens2 = line2.split(" ");
            unchoking_interval = Integer.parseInt(tokens2[1]);

            String line3 = in.readLine();
            String[] tokens3 = line3.split(" ");
            optim_unchoking_interval = Integer.parseInt(tokens3[1]);

            String line4 = in.readLine();
            String[] tokens4 = line4.split(" ");
            file_name = tokens4[1];

            String line5 = in.readLine();
            String[] tokens5 = line5.split(" ");
            file_size = Integer.parseInt(tokens5[1]);

            String line6 = in.readLine();
            String[] tokens6 = line6.split(" ");
            piece_size = Integer.parseInt(tokens6[1]);

            CommonConfigParser.commonMetaData = new CommonMetaData(num_of_pref_neighbors, unchoking_interval, optim_unchoking_interval,
                    file_name, file_size, piece_size);


        } catch(Exception e){
            System.out.println("Failed to open file");
        }
    }


}
