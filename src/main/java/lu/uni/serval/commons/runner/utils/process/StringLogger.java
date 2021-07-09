package lu.uni.serval.commons.runner.utils.process;

public class StringLogger extends Listener {
    private StringBuilder out;
    private StringBuilder err;

    @Override
    protected void onStartListening() {
        this.out = new StringBuilder();
        this.err = new StringBuilder();
    }

    @Override
    protected boolean onMessageReceived(String line) {
        this.out.append(line);
        this.out.append("\n");

        return true;
    }

    @Override
    protected void onEndListening() { }

    @Override
    protected void onExceptionRaised(Exception e) {
        this.err.append(e.getMessage());
        this.err.append("\n");
    }

    public String getOut(){
        return this.out.toString();
    }

    public String getErr(){
        return this.err.toString();
    }
}
