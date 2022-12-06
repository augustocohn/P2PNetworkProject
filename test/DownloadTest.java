import utils.Download;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class DownloadTest {

    public static void main(String[] args) {
        ensureCorrectPoll();
    }

    public static void ensureCorrectPoll() {

        Download d1 = new Download(1001, 5);
        Download d2 = new Download(1002, 3);
        Download d3 = new Download(1003, 2);
        Download d4 = new Download(1004, 2);
        Download d5 = new Download(1005, 9);
        Download d6 = new Download(1006, 1);

        ArrayList<Download> dList = new ArrayList<>();
        dList.add(d1);
        dList.add(d3);
        dList.add(d2);
        dList.add(d4);
        dList.add(d5);
        dList.add(d6);

        PriorityQueue<Download> dPQ = new PriorityQueue<>(dList);

        System.out.println(dPQ.poll().getPeerID());
        System.out.println(dPQ.poll().getPeerID());
        System.out.println(dPQ.poll().getPeerID());
        System.out.println(dPQ.poll().getPeerID());
        System.out.println(dPQ.poll().getPeerID());
        System.out.println(dPQ.poll().getPeerID());

    }



}
