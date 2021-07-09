package lu.uni.serval.commons.runner.utils.helpers;

public class SimpleLaunchableClass {
    public static void main(String[] args) {
        System.out.printf(
                "Hello from process with arguments: [%s]%n",
                String.join(",", args)
        );
    }
}
