package com.paulhammant.decdnorator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Renderer {

    private final Class clazz;
    private final String from;
    private final String to;
    private final String charsetName;

    private static final String DECORATE_WITH_REGEX = "<!--decorateWith:(.*\\w)-->";
    private static final Type[] TYPES = new Type[] {
            new Type("<!--", "-->", "<!--", "-->"),  // HTML comments
            new Type("\\/\\*", "\\*\\/", "/*", "*/") // JavaScript multi-line comments
    };

    private static class Type {
        private Type(String startEsc, String endEsc, String start, String end) {
            this.startEsc = startEsc;
            this.endEsc = endEsc;
            this.start = start;
            this.end = end;
        }
        private String startEsc;
        private String endEsc;
        private String start;
        private String end;
        private String before(String from) {
            return startEsc + "block:" + from + endEsc;
        }
        private String after(String from) {
            return startEsc + "endblock:" + from + endEsc;
        }
    }

    public Renderer(Class clazz, String from, String to) {
        this("UTF-8", clazz, from, to);
    }

    public Renderer(String charsetName, Class clazz, String from, String to) {
        this.charsetName = charsetName;
        this.clazz = clazz;
        this.from = from;
        this.to = to;
    }

    public String getPage(DecoratorOverrides overrides, String file, String... insertionVars) throws FileNotFoundException {
        return getPage(overrides, new ArrayList<String>(), file, makeInsertions(insertionVars));
    }

    private Map<String, String> makeInsertions(String[] insertionVars) {
        Map<String, String> insertions = new HashMap<String, String>();
        for (String insertionVar : insertionVars) {
            insertions.put(insertionVar, "");
        }
        return insertions;
    }

    public String getPage(DecoratorOverrides overrides, String file, Map<String, String> insertions) throws FileNotFoundException {
        return getPage(overrides, new ArrayList<String>(), file, insertions);
    }

    public String getPage(DecoratorOverrides overrides, List<String> previousDecorators, String file, Map<String, String> insertions) throws FileNotFoundException {
        String content = getRawContent(clazz, file);
        content = performInsertions(insertions, content);
        Pattern decorateWith = Pattern.compile(DECORATE_WITH_REGEX);
        Matcher matcher = decorateWith.matcher(content);
        previousDecorators.add(file);
        if (matcher.find()) {
            String decorator = overrides.override(matcher.group(1), previousDecorators);
            if (!decorator.equals(DecoratorOverrides.NO_DECORATION)) {
                HashMap<String, String> newInsertions = extractInserts(content, insertions);
                return getPage(overrides, previousDecorators, decorator, newInsertions);
            }
        }
        return removeDecorationMarks(content);
    }

    private String removeDecorationMarks(String content) {
        for (Type type : TYPES) {
            content = content.replaceAll(type.before("(\\w*)"), "");
            content = content.replaceAll(type.after("(\\w*)"), "");
        }
        return content.replaceAll(DECORATE_WITH_REGEX, "");
    }

    protected String getRawContent(Class clazz, String file) throws FileNotFoundException {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = path.replace(from, to);
        return new Scanner(new File(path, file), charsetName).useDelimiter("\\A").next();
    }

    private String performInsertions(Map<String, String> inserts, String content) {
        for (String from : inserts.keySet()) {
            content = performInsert(content, from, inserts.get(from));
        }
        return content;
    }

    private String performInsert(String content, String from, String to) {
        for (Type type : TYPES) {
            content = content.replace(type.start + "insert:" + from + type.end, to);
        }
        return content;
    }

    public HashMap<String, String> extractInserts(String content, String... insertionVars) {
        return extractInserts(content, makeInsertions(insertionVars));
    }

    public HashMap<String, String> extractInserts(String content, Map<String, String> inserts) {
        HashMap<String, String> newInserts = new HashMap<String, String>(inserts);
        for (Type type : TYPES) {
            for (String from : inserts.keySet()) {
                String regex = type.before(from) + "(.*)" + type.after(from);
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
