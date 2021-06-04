package sk.stuba.fei.uim.jp.processdrivencli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sk.stuba.fei.uim.jp.processdrivencli.petriNetEnvironment.PetriNet;

@SpringBootApplication
public class ProcessDrivenCliApplication{

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ProcessDrivenCliApplication.class, args);
        PetriNet petriNet = context.getBean(PetriNet.class);
        petriNet.run();
    }
}