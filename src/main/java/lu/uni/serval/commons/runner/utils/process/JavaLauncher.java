package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;

import java.io.File;
import java.util.Map;

public abstract class JavaLauncher extends ProcessLauncher {
    private File javaHome;
    private final Entries javaParameters = new Entries();

    public JavaLauncher(String name) {
        super(name);
    }

    protected void setJavaHome(File javaHome){
        this.javaHome = javaHome;
    }

    protected void addJavaParameter(Entry entry){
        javaParameters.add(entry);
    }

    protected void addJavaParameters(Entries entries){
        javaParameters.putAll(entries);
    }

    protected Entries getJavaParameters(){
        return javaParameters;
    }

    @Override
    protected Map<String, String> getEnvironment(){
        Map<String, String> localEnv = super.getEnvironment();

        if(javaHome != null && javaHome.exists()){
            localEnv.put("JAVA_HOME", javaHome.getAbsolutePath());
        }

        return localEnv;
    }
}
