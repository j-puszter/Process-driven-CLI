package sk.stuba.fei.uim.jp.processdrivencli.transformer;

import sk.stuba.fei.uim.jp.processdrivencli.parts.Arc;
import sk.stuba.fei.uim.jp.processdrivencli.parts.Place;
import sk.stuba.fei.uim.jp.processdrivencli.parts.Transition;

import java.util.List;

public class PartsWrapper {
    private List<Transition> transitions;
    private List<Place> places;
    private List<Arc> arcs;

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Arc> getArcs() {
        return arcs;
    }

    public void setArcs(List<Arc> arcs) {
        this.arcs = arcs;
    }
}
