package lu.uni.serval.commons.runner.utils.helpers;

import java.util.concurrent.TimeUnit;

public class InfiniteClass {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println("Still alive");
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }
}
