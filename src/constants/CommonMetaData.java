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

    public int get_num_of_pref_neighbors() {
        return this.num_of_pref_neighbors;
    }

    public int get_unchoking_interval() {
        return this.unchoking_interval;
    }

    public int get_optim_unchoking_interval() {
        return this.optim_unchoking_interval;
    }

    public String get_file_name() {
        return this.file_name;
    }

    public int get_file_size() {
        return this.file_size;
    }

    public int get_piece_size() {
        return this.piece_size;
    }


}
