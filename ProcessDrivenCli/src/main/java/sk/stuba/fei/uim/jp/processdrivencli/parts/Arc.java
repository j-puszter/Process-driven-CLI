package sk.stuba.fei.uim.jp.processdrivencli.parts;

@SuppressWarnings("unused")
public class Arc extends Part {

    private int multiplicity;
    private Place place;

    public Arc(int id, int multiplicity, Place place) {
        super(id);
        this.multiplicity = multiplicity;
        this.place = place;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}