package lu.uni.serval.commons.runner.utils.helpers;

public class InfiniteLaunchableClass {
    public static void main(String[] args) throws InterruptedException {
        while (true){
            Thread.sleep(100);
        }
    }
}
