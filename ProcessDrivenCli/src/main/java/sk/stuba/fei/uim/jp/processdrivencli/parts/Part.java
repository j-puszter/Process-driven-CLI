package sk.stuba.fei.uim.jp.processdrivencli.parts;

@SuppressWarnings("unused")
public abstract class Part {

    protected int id;

    public Part(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}