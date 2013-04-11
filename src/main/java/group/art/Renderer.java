package group.art;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Renderer {

    private final DecoratorOverrides overides;
    private String from;
    private String to;

    private class Type {
        private Type(String startEsc, String endEsc, String start, String end) {
            this.startEsc = startEsc;
            this.endEsc = endEsc;
            this.start = start;
            this.end = end;
        }
        String startEsc;
        String endEsc;
        String start;
        String end;
    }

    private Type[] types = new Type[] {
      new Type("<!--", "-->", "<!--", "-->"),  // HTML comments
      new Type("\\/\\*", "\\*\\/", "/*", "*/") // JavaScript multi-line comments
    };

    public Renderer(DecoratorOverrides overides, String from, String to) {
        this.overides = overides;
        this.from = from;
        this.to = to;
    }
    public Renderer(String from, String to) {
        this(DecoratorOverrides.NULL, from, to);
    }

    public String getPage(String file, Map<String, String> replacements) throws FileNotFoundException {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        path = path.replace(from, to);
        String content = new Scanner(new File(path, file), "UTF-8").useDelimiter("\\A").next();
        content = performReplacements(replacements, content);
        Pattern decorateWith = Pattern.compile("<!--decorateWith:(.*\\w)-->");
        Matcher matcher = decorateWith.matcher(content);
        if (matcher.find()) {
            String decorator = matcher.group(1);
            decorator = overides.override(decorator);
            return new Renderer(from, to).getPage(decorator, newReplacements(replacements, content));
        }
        return content;
    }

    private String performReplacements(Map<String, String> replacements, String content) {
        for (String from : replacements.keySet()) {
            String to = replacements.get(from);
            for (Type type : types) {
                content = content.replace(type.start + "insert:" + from + type.end, to);
            }
        }
        return content;
    }

    private HashMap<String, String> newReplacements(Map<String, String> replacements, String content) {
        HashMap<String, String> newReplacements = new HashMap<String, String>(replacements);
        for (Type type : types) {
            for (String from : replacements.keySet()) {
                String regex = type.startEsc + "block:" + from + type.endEsc + "(.*)" + type.startEsc + "endblock:" + from + type.endEsc;
                Matcher matcher2 = Pattern.compile(regex, Pattern.DOTALL).matcher(content);
                if (matcher2.find()) {
                    String group = matcher2.group(1);
                    group = group.replaceAll("\\s+$", "");
                    group = group.replaceAll("^\\n", "");
                    newReplacements.put(from, group);
                }
            }
        }
        return newReplacements;
    }
}
