package sk.stuba.fei.uim.jp.processdrivencli.parts;

@SuppressWarnings("unused")
public abstract class Node extends Part {

    protected String label;

    public Node(int id, String label) {
        super(id);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}