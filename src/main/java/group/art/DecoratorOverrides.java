package group.art;

public interface DecoratorOverrides {

    String override(String decorator);

    public static DecoratorOverrides NULL = new DecoratorOverrides() {

        public String override(String decorator) {
            return decorator;
        }
    };

}
