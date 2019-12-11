public enum Colour{
    BLACK("B"),
    WHITE("W"),
    NONE(".");

    private String name;

    private Colour(String name){
        this.name = name;
    }

    public Colour getOppositeColour() {
        if (name.equals("B"))
            return WHITE;
        if (name.equals("W"))
            return BLACK;
        return NONE;
    }

    public String toString(){
        return name;
    }

}