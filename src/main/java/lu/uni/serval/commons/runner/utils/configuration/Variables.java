package lu.uni.serval.commons.runner.utils.configuration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variables {
    public static final String BUILD_FOLDER = "BUILD_FOLDER";
    public static final String CONFIGURATION_FOLDER = "CONFIGURATION_FOLDER";

    private static final Pattern pattern = Pattern.compile("\\{[^{}]*\\}");

    private final Map<String, String> environment = new HashMap<>(System.getenv());

    public void register(String key, String value){
        environment.put(key, value);
    }

    public String resolve(String text){
        final Set<String> found = new HashSet<>();
        final Matcher matcher = pattern.matcher(text);

        while (matcher.find()){
            found.add(matcher.group());
        }

        String resolved = text;
        for(String variable: found){
            resolved = resolved.replace(variable, resolveVariable(variable));
        }

        return resolved;
    }

    private String resolveVariable(String variable){
        final String resolved = environment.get(variable.substring(1, variable.length() - 1));

        if(resolved == null){
            throw new IllegalArgumentException(String.format("Variable %s cannot be resolved", variable));
        }

        return resolved;
    }
}
