package com.paulhammant.decdnorator;

public interface PathFinder {

    String getBasePath();

    public static class FromClass implements PathFinder {
        private Class clazz;

        public FromClass(Class clazz) {
            this.clazz = clazz;
        }
        public String getBasePath() {
            return clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        }
    }
}
