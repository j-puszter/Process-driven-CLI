package sk.stuba.fei.uim.jp.processdrivencli.parts;

public class Place extends Node{

    private int tokens;

    public Place(int id, String label, int tokens) {
        super(id, label);
        this.tokens = tokens;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }
}