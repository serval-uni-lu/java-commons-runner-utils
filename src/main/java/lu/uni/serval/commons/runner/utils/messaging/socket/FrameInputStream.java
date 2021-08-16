package lu.uni.serval.commons.runner.utils.messaging.socket;

import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

public class FrameInputStream extends ObjectInputStream {
    private final Set<String> allowedClasses;

    public FrameInputStream(InputStream in, Set<Class<? extends Frame>> allowedClasses) throws IOException {
        super(in);
        this.allowedClasses = allowedClasses.stream().map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
        if (!allowedClasses.contains(osc.getName())) {
            throw new InvalidClassException("Unauthorized deserialization", osc.getName());
        }

        return super.resolveClass(osc);
    }
}
