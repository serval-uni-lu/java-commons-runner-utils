package lu.uni.serval.commons.runner.utils.helpers;

public class Observer {
    private volatile boolean touched;

    public Observer(){
        touched = false;
    }

    public void touch(){
        touched = true;
    }

    public boolean isTouched(){
        return touched;
    }
}
