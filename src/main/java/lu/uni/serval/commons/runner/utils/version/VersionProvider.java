package lu.uni.serval.commons.runner.utils.version;

import java.io.IOException;

public interface VersionProvider extends Iterable<Version> {
    void clean() throws IOException;
}
