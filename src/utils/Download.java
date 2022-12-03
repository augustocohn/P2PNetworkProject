package utils;

public class Download implements Comparable<Download>{

    private int peerID;
    private int count;

    public Download(int _peerID, int _count){
        peerID = _peerID;
        count = _count;
    }

    public int getPeerID(){
        return this.peerID;
    }

    public int getCount(){
        return this.count;
    }

    public void incrementCount() {
        this.count++;
    }

    @Override
    public int compareTo(Download other){
        return Integer.compare(other.getCount(), this.getCount());
    }

}
