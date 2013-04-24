package com.paulhammant.decdnorator;

import java.util.List;

public interface DecorationOverrides {

    public static final String NO_MORE_DECORATION = "No_More_Decoration";
    public static final String NO_DECORATOR_SPECIFIED = "No_Decorator_Specified";

    String override(String decorator, List<String> previousDecorators);

    public static class Single implements DecorationOverrides {

        private String from;
        private String to;

        public Single(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String override(String decorator, List<String> previousDecorators) {
            return NO_OVERRIDES.override(decorator.replace(from, to), null);
        }
    }

    public static class LimitRecursion implements DecorationOverrides {

        private int limit;

        public LimitRecursion(int limit) {
            this.limit = limit;
        }

        public String override(String decorator, List<String> previousDecorators) {
            if (previousDecorators.size() >= limit) {
                return NO_MORE_DECORATION;
            }
            return decorator;
        }
    }

    public static DecorationOverrides NO_OVERRIDES = new DecorationOverrides() {

        public String override(String decorator, List<String> done) {
            if (decorator.equals(NO_DECORATOR_SPECIFIED)) {
                return NO_MORE_DECORATION;
            }
            return decorator;
        }
    };

}
