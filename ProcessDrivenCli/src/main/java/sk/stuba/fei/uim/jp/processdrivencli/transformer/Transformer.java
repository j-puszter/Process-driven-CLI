package sk.stuba.fei.uim.jp.processdrivencli.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import sk.stuba.fei.uim.jp.processdrivencli.parts.*;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SuppressWarnings({"unused", "SpringJavaAutowiredFieldsWarningInspection"})
@Component
public abstract class Transformer<T> {
    @Autowired
    protected Jaxb2Marshaller marshaller;

    abstract public PartsWrapper transformFromSource(T source) throws IllegalStateException;
    abstract public void updateSourcePlacesToFile(List<Place> places, String sourceName) throws FileNotFoundException;

    abstract protected void validateSource(T source) throws IllegalStateException;

    @SuppressWarnings("unchecked")
    public T unmarshalSource(String sourceName) throws FileNotFoundException, UnmarshallingFailureException{
        File file = new File(System.getProperty("user.dir") + File.separator + sourceName + ".xml");
        if(!file.isFile()) {
            throw new FileNotFoundException("Subor s danym nazvom neexistuje");
        }
            return (T) marshaller.unmarshal(new StreamSource(file));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void marshalSource(T source) {
        File file = new File(System.getProperty("user.dir") + File.separator + "PetriNetDataProgressBackup" + ".xml");
        try {
            file.createNewFile();
            marshaller.createMarshaller().marshal(source, file);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    public Jaxb2Marshaller getMarshaller() {
        return marshaller;
    }

    public void setMarshaller(Jaxb2Marshaller marshaller) {
        this.marshaller = marshaller;
    }
}
