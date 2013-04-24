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

public class Decorator {

    private final PathFinder pathFinder;
    //private final String from;
    //private final String to;
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

    public Decorator(PathFinder pathFinder) {
        this("UTF-8", pathFinder);
    }

    public Decorator(String charsetName, PathFinder pathFinder) {
        this.charsetName = charsetName;
        this.pathFinder = pathFinder;
    }

    public String getPage(DecorationOverrides overrides, String file, String... insertionVars) throws FileNotFoundException {
        return getPage(overrides, new ArrayList<String>(), file, makeInsertions(insertionVars));
    }

    private Map<String, String> makeInsertions(String[] insertionVars) {
        Map<String, String> insertions = new HashMap<String, String>();
        for (String insertionVar : insertionVars) {
            insertions.put(insertionVar, "");
        }
        return insertions;
    }

    public String getPage(DecorationOverrides overrides, String file, Map<String, String> insertions) throws FileNotFoundException {
        return getPage(overrides, new ArrayList<String>(), file, insertions);
    }

    public String getPage(DecorationOverrides overrides, List<String> previousDecorators, String file, Map<String, String> insertions) throws FileNotFoundException {
        String content = getRawContent(pathFinder, file);
        content = performInsertions(insertions, content);
        Pattern decorateWithPattern = Pattern.compile(DECORATE_WITH_REGEX);
        Matcher matcher = decorateWithPattern.matcher(content);
        previousDecorators.add(file);
        String decorateWith = DecorationOverrides.NO_DECORATOR_SPECIFIED;
        if (matcher.find()) {
            decorateWith = matcher.group(1);
        }
        String decorator = overrides.override(decorateWith, previousDecorators);
        if (!decorator.equals(DecorationOverrides.NO_MORE_DECORATION)) {
            HashMap<String, String> newInsertions = extractInserts(content, insertions);
            return getPage(overrides, previousDecorators, decorator, newInsertions);
        } else {
            return removeDecdnoratorMarks(content);
        }
    }

    private String removeDecdnoratorMarks(String content) {
        for (Type type : TYPES) {
            content = content.replaceAll(type.before("(\\w*)"), "");
            content = content.replaceAll(type.after("(\\w*)"), "");
        }
        return content.replaceAll(DECORATE_WITH_REGEX, "");
    }

    protected String getRawContent(PathFinder pathFinder, String file) throws FileNotFoundException {
        String path = pathFinder.getBasePath();
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
