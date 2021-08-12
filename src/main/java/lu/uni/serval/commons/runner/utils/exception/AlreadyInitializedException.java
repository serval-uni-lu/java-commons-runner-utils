package lu.uni.serval.commons.runner.utils.exception;

public class AlreadyInitializedException extends BrokerException {
    public AlreadyInitializedException(String message){
        super(message);
    }
}
