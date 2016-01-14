package spikes.javafx;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;


/**
 * Created by rconaway on 1/1/16.
 */
public class ShowPointerInfo {

    public static void main(String[] args) {
        while(true) {
            PointerInfo inf = MouseInfo.getPointerInfo();
            Point p = inf.getLocation();
            System.out.println(p);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
