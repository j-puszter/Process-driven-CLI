package sk.stuba.fei.uim.jp.processdrivencli.transformer;

import generated.Document;
import org.springframework.stereotype.Component;
import sk.stuba.fei.uim.jp.processdrivencli.parts.*;
import sk.stuba.fei.uim.jp.processdrivencli.parts.Transition;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentTransformer extends Transformer<Document> {

    @Override
    public PartsWrapper transformFromSource(Document source) throws IllegalStateException{
        //doplnkova validacia k xsd
        validateSource(source);

        PartsWrapper result = new PartsWrapper();

        //vytvorenie miest pre neskorsie pridanie referencii k hranam
        List<Place> places = new ArrayList<>();
        for (Document.Place p : source.getPlace()) {
            places.add(new Place(p.getId().intValue(), p.getLabel(), p.getTokens().intValue()));
        }

        List<Transition> transitions = new ArrayList<>();
        List<Arc> arcs = new ArrayList<>();

        for (Document.Transition t : source.getTransition()) {
            Transition transition = new Transition(t.getId().intValue(), t.getLabel(), t.getAction(), new ArrayList<>(), new ArrayList<>());
            for (Document.Arc arc : source.getArc()) {
                if (transition.getId() == arc.getSourceId().intValue()) {   //ak je prechod zdroj hrany, najde sa ciel(miesto) danej hrany,
                    for (Place place : places) {                            //vytvori sa hrana s referenciou miesta a priradi sa prechodu ako referencia do zoznamu vystupnych hran daneho prechodu
                        if (place.getId() == arc.getDestinationId().intValue()) {
                            Arc newArc = new Arc(arc.getId().intValue(), arc.getMultiplicity().intValue(), place);
                            transition.getArcsOut().add(newArc);
                            arcs.add(newArc);
                            break;
                        }
                    }
                } else if (transition.getId() == arc.getDestinationId().intValue()) {   //ak je prechod ciel hrany, najde sa zdroj(miesto) danej hrany,
                for (Place place : places) {                                            //vytvori sa hrana s referenciou miesta a priradi sa prechodu ako referencia do zoznamu vstupnych hran daneho prechodu
                        if (place.getId() == arc.getSourceId().intValue()) {
                            Arc newArc = new Arc(arc.getId().intValue(), arc.getMultiplicity().intValue(), place);
                            transition.getArcsIn().add(newArc);
                            arcs.add(newArc);
                            break;
                        }
                    }
                }
            }
            transitions.add(transition);
        }
        result.setTransitions(transitions);
        result.setPlaces(places);
        result.setArcs(arcs);
        return result;
    }

    @Override
    public void updateSourcePlacesToFile(List<Place> places, String sourceName) throws FileNotFoundException {
        Document source = unmarshalSource(sourceName);

        for(Document.Place documentPlace : source.getPlace()) {
            for (Place place : places) {
                if(place.getId() == documentPlace.getId().intValue()) {
                    documentPlace.setTokens(BigInteger.valueOf(place.getTokens()));
                }
            }
        }
        marshalSource(source);  //marshal source do suboru
    }

    @Override
    protected void validateSource(Document source) throws IllegalStateException {
        validateArc(source);
    }

    private void validateArc(Document source) throws IllegalStateException{
        for (Document.Arc arc : source.getArc()) {
            boolean valid = false;
            for (Document.Transition transition: source.getTransition()) {
                if (transition.getId().equals(arc.getSourceId())) {
                    for (Document.Place place : source.getPlace()) {
                        if (place.getId().equals(arc.getDestinationId())) {
                            valid = true;
                            break;
                        }
                    }
                } else if (transition.getId().equals(arc.getDestinationId())) {
                    for (Document.Place place : source.getPlace()) {
                        if (place.getId().equals(arc.getSourceId())) {
                            valid = true;
                            break;
                        }
                    }
                }
                if(valid) {
                    break;
                }
            }
            if(!valid) {
                throw new IllegalStateException("Nespravna stavba siete");
            }
        }
    }
}
