package lu.uni.serval.commons.runner.utils.helpers;

import lu.uni.serval.commons.runner.utils.exception.ResponseException;
import lu.uni.serval.commons.runner.utils.messaging.frame.ErrorFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import lu.uni.serval.commons.runner.utils.messaging.frame.RequestFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.TextFrame;
import lu.uni.serval.commons.runner.utils.process.ManagedProcess;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ResponseClass extends ManagedProcess {
    private boolean isError = false;

    public static void main(String[] args) {
        new ResponseClass().doMain(args);
    }

    @Override
    protected Set<Option> getOptions() {
        final Set<Option> options = new HashSet<>();
        options.add(new Option("e", "isError", true, "Whether the answer is an error"));
        return options;
    }

    @Override
    protected void doWork(CommandLine cmd) {
        final String errorCmd = cmd.getOptionValue("isError");
        this.isError = errorCmd != null && errorCmd.equalsIgnoreCase("true");

        while (isWorking()){}
    }

    @Override
    protected Frame onRequest(RequestFrame<?> requestFrame){
        if(this.isError){
            return new ErrorFrame(ResponseException.class, "Forced exception");
        }

        final Class<?> target = requestFrame.getTarget();

        if(target == TextFrame.class){
            final Optional<String> text = requestFrame.getOption("text");

            if(text.isPresent()){
                final String reverse = new StringBuffer(text.get()).reverse().toString();
                return new TextFrame(reverse);
            }
        }

        return super.onRequest(requestFrame);
    }

    @Override
    protected void stop() {
        setWorking(false);
    }
}
