package com.paulhammant.decdnorator;

import java.io.File;

public abstract class PathFinder {

    public abstract String getBasePath();

    public static FromFile fromFile(File path) {
        return new FromFile(path);
    }

    public static class FromFile extends PathFinder {
        private File path;
        private String from = "";
        private String to = "";

        public FromFile(File path) {
            this.path = path;
        }
        public String getBasePath() {
            return path.getAbsolutePath().replace(from, to);
        }
        PathFinder replace(String from, String to) {
            this.from = from;
            this.to = to;
            return this;
        }

    }

    public static FromClass fromClass(Class clazz) {
        return new FromClass(clazz);
    }

    public static class FromClass extends PathFinder {
        private Class clazz;
        private String from = "";
        private String to = "";

        public FromClass(Class clazz) {
            this.clazz = clazz;
        }

        public PathFinder replace(String from, String to) {
            this.from = from;
            this.to = to;
            return this;
        }

        public String getBasePath() {
            String path = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
            return path.replace(from, to);
        }
    }
}
