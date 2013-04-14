package com.paulhammant.decdnorator;

import java.util.List;

public interface DecorationOverrides {

    public static final String NO_DECORATION = "No_More_Decoration";

    String override(String decorator, List<String> previousDecorators);

    public static DecorationOverrides NULL = new DecorationOverrides() {

        public String override(String decorator, List<String> done) {
            return decorator;
        }
    };

}
