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

    //private final PathFinder pathFinder;
    private String basePath;
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

    public Decorator(String basePath) {
        this("UTF-8", basePath);
    }

    public Decorator(String charsetName, PathFinder pathFinder) {
        this.charsetName = charsetName;
        basePath = pathFinder.getBasePath();
    }

    public Decorator(String charsetName, String basePath) {
        this.charsetName = charsetName;
        this.basePath = basePath;
    }

    public String getPage(DecorationOverrides overrides, String file, String... insertionVars) throws FileNotFoundException {
        return getPage(overrides, new ArrayList<String>(), file, makeInsertionKeys(insertionVars, new HashMap<String, String>()));
    }

    private Map<String, String> makeInsertionKeys(String[] insertionVars, Map<String, String> insertions) {
        for (String insertionVar : insertionVars) {
            insertions.put(insertionVar, "");
        }
        return insertions;
    }

    public String getPage(String file, Map<String, String> insertions) throws FileNotFoundException {
        return getPage(DecorationOverrides.NO_OVERRIDES, new ArrayList<String>(), file, insertions);
    }

    public String getPage(DecorationOverrides overrides, String file, Map<String, String> insertions) throws FileNotFoundException {
        return getPage(overrides, new ArrayList<String>(), file, insertions);
    }

    public String getPage(DecorationOverrides overrides, List<String> previousDecorators, String file, Map<String, String> insertions) throws FileNotFoundException {
        String content = getPageAndPerformInsertions(file, insertions);
        Pattern decorateWithPattern = Pattern.compile(DECORATE_WITH_REGEX);
        Matcher matcher = decorateWithPattern.matcher(content);
        previousDecorators.add(file);
        String decorateWith = DecorationOverrides.NO_DECORATOR_SPECIFIED;
        if (matcher.find()) {
            decorateWith = matcher.group(1);
        }
        String decorator = overrides.override(decorateWith, previousDecorators);
        if (!decorator.equals(DecorationOverrides.NO_MORE_DECORATION)) {
            Map<String, String> newInsertions = extractInserts(content, insertions);
            return getPage(overrides, previousDecorators, decorator, newInsertions);
        } else {
            return removeDecdnoratorMarks(content);
        }
    }

    public String getPageAndPerformInsertions(String file, Map<String, String> insertions) throws FileNotFoundException {
        return performInsertions(insertions, getRawContent(file));
    }

    private String removeDecdnoratorMarks(String content) {
        for (Type type : TYPES) {
            content = content.replaceAll(type.before("(\\w*)"), "");
            content = content.replaceAll(type.after("(\\w*)"), "");
        }
        return content.replaceAll(DECORATE_WITH_REGEX, "");
    }

    public String getRawContent(String file) throws FileNotFoundException {
        return new Scanner(new File(basePath, file), charsetName).useDelimiter("\\A").next();
    }

    public String performInsertions(Map<String, String> inserts, String content) {
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

    public Map<String, String> extractInserts(String content, String... insertionVars) {
        return extractInserts(content, makeInsertionKeys(insertionVars, new HashMap<String, String>()));
    }

    public Map<String, String> extractInserts(String content, Map<String, String> inserts, String... insertionVars) {
        makeInsertionKeys(insertionVars, inserts);
        return extractInserts(content, inserts);
    }

    public Map<String, String> extractInserts(String content, Map<String, String> inserts) {
        for (Type type : TYPES) {
            for (String from : inserts.keySet()) {
                String regex = type.before(from) + "(.*)" + type.after(from);
                Matcher matcher2 = Pattern.compile(regex, Pattern.DOTALL).matcher(content);
                if (matcher2.find()) {
                    String group = matcher2.group(1);
                    group = group.replaceAll("\\s+$", "");
                    group = group.replaceAll("^\\n", "");
                    inserts.put(from, group);
                }
            }
        }
        return inserts;
    }
}
