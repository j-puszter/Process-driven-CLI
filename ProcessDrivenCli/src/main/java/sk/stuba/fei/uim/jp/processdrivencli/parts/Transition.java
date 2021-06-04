package sk.stuba.fei.uim.jp.processdrivencli.parts;

import java.util.List;

@SuppressWarnings("unused")
public class Transition extends Node{

    private String action;
    private List<Arc> arcsIn;
    private List<Arc> arcsOut;

    public Transition(int id, String label, String action, List<Arc> arcsIn, List<Arc> arcsOut) {
        super(id, label);
        this.action = action;
        this.arcsIn = arcsIn;
        this.arcsOut = arcsOut;
    }

    public boolean executable(){
        for (Arc arc : arcsIn) {
            if(arc.getMultiplicity() > arc.getPlace().getTokens()) {
                return false;
            }
        }
        return true;
    }

    public void execute() throws IllegalStateException {
        if (executable()) {
            for (Arc arcIn : arcsIn) {
                arcIn.getPlace().setTokens(arcIn.getPlace().getTokens() - arcIn.getMultiplicity());
            }

            for (Arc arcOut : arcsOut) {
                arcOut.getPlace().setTokens(arcOut.getPlace().getTokens() + arcOut.getMultiplicity());
            }
        }
        else {
            throw new IllegalStateException("Transition can't be executed");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Arc> getArcsIn() {
        return arcsIn;
    }

    public void setArcsIn(List<Arc> arcsIn) {
        this.arcsIn = arcsIn;
    }

    public List<Arc> getArcsOut() {
        return arcsOut;
    }

    public void setArcsOut(List<Arc> arcsOut) {
        this.arcsOut = arcsOut;
    }
}