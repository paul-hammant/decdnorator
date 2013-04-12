package group.art;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Renderer {

    private final Class clazz;
    private final DecoratorOverrides overrides;
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

    public Renderer(Class clazz, DecoratorOverrides overides, String from, String to) {
        this.clazz = clazz;
        this.overrides = overides;
        this.from = from;
        this.to = to;
    }
    public Renderer(Class clazz, String from, String to) {
        this(clazz, DecoratorOverrides.NULL, from, to);
    }

    public String getPage(String file, String... insertionVars) throws FileNotFoundException {
        return getPage(file, makeInsertions(insertionVars));
    }

    private Map<String, String> makeInsertions(String[] insertionVars) {
        Map<String, String> insertions = new HashMap<String, String>();
        for (String insertionVar : insertionVars) {
            insertions.put(insertionVar, "");
        }
        return insertions;
    }

    public String getPage(String file, Map<String, String> insertions) throws FileNotFoundException {
        String content = getRawContent(clazz, file);
        content = performInsertions(insertions, content);
        Pattern decorateWith = Pattern.compile("<!--decorateWith:(.*\\w)-->");
        Matcher matcher = decorateWith.matcher(content);
        if (matcher.find()) {
            String decorator = matcher.group(1);
            decorator = overrides.override(decorator);
            return new Renderer(clazz, from, to).getPage(decorator, extractInserts(content, insertions));
        }
        return content;
    }

    protected String getRawContent(Class clazz, String file) throws FileNotFoundException {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = path.replace(from, to);
        return new Scanner(new File(path, file), "UTF-8").useDelimiter("\\A").next();
    }

    private String performInsertions(Map<String, String> inserts, String content) {
        for (String from : inserts.keySet()) {
            content = performInsert(content, from, inserts.get(from));
        }
        return content;
    }

    private String performInsert(String content, String from, String to) {
        for (Type type : types) {
            content = content.replace(type.start + "insert:" + from + type.end, to);
        }
        return content;
    }

    public HashMap<String, String> extractInserts(String content, String... insertionVars) {
        return extractInserts(content, makeInsertions(insertionVars));
    }

    public HashMap<String, String> extractInserts(String content, Map<String, String> inserts) {
        HashMap<String, String> newInserts = new HashMap<String, String>(inserts);
        for (Type type : types) {
            for (String from : inserts.keySet()) {
                String regex = type.startEsc + "block:" + from + type.endEsc + "(.*)" + type.startEsc + "endblock:" + from + type.endEsc;
                Matcher matcher2 = Pattern.compile(regex, Pattern.DOTALL).matcher(content);
                if (matcher2.find()) {
                    String group = matcher2.group(1);
                    group = group.replaceAll("\\s+$", "");
                    group = group.replaceAll("^\\n", "");
                    newInserts.put(from, group);
                }
            }
        }
        return newInserts;
    }
}
