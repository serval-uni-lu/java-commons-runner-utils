package lu.uni.serval.commons.runner.utils.helpers;

import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import lu.uni.serval.commons.runner.utils.messaging.frame.RequestFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.TextFrame;
import lu.uni.serval.commons.runner.utils.process.ManagedProcess;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class ResponseClass extends ManagedProcess {
    public static void main(String[] args) {
        new ResponseClass().doMain(args);
    }

    @Override
    protected Set<Option> getOptions() {
        return Collections.emptySet();
    }

    @Override
    protected void doWork(CommandLine cmd) {
        while (isWorking()){}
    }

    @Override
    protected Frame onRequest(RequestFrame<?> requestFrame){
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
