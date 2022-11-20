package constants;

public class CommonMetaData {

    private int num_of_pref_neighbors;

    private int unchoking_interval;

    private int optim_unchoking_interval;

    private String file_name;

    private int file_size;

    private int piece_size;

    public CommonMetaData(int num_of_pref_neighbors, int unchoking_interval, int optim_unchoking_interval,
                          String file_name, int file_size, int piece_size)
    {
        this.num_of_pref_neighbors = num_of_pref_neighbors;
        this.unchoking_interval = unchoking_interval;
        this.optim_unchoking_interval = optim_unchoking_interval;
        this.file_name = file_name;
        this.file_size = file_size;
        this.piece_size = piece_size;
    }

    public int getNumOfPrefNeighbors() {
        return this.num_of_pref_neighbors;
    }

    public int getUnchokingInterval() {
        return this.unchoking_interval;
    }

    public int getOptimUnchokingInterval() {
        return this.optim_unchoking_interval;
    }

    public String getFileName() {
        return this.file_name;
    }

    public int getFileSize() {
        return this.file_size;
    }

    public int getPieceSize() {
        return this.piece_size;
    }


}
