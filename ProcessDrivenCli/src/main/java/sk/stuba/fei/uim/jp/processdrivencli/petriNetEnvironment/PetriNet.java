package sk.stuba.fei.uim.jp.processdrivencli.petriNetEnvironment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;
import sk.stuba.fei.uim.jp.processdrivencli.parts.*;
import sk.stuba.fei.uim.jp.processdrivencli.cli.CliExecutor;
import sk.stuba.fei.uim.jp.processdrivencli.transformer.PartsWrapper;
import sk.stuba.fei.uim.jp.processdrivencli.transformer.Transformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Service
@SuppressWarnings({"unchecked", "rawtypes", "ResultOfMethodCallIgnored", "unused", "SpringJavaAutowiredFieldsWarningInspection"})
public class PetriNet {

    private List<Transition> transitions;
    private List<Place> places;
    private List<Arc> arcs;
    private final Transformer transformer;
    private String sourceName;
    private final Scanner scanner;
    private Mode mode;
    @Autowired
    private CliExecutor cliExecutor;

    @Autowired
    public PetriNet(Transformer transformer) {
        this.transformer = transformer;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        //inicializacia PetriNet zo zvoleneho suboru
        initializePetriNet();

        //kontrola spustitelnosti programu
        boolean running = runnable();
        if(!running) {
            System.out.println("V petriho sieti neexistuju ziadne spustitelne prechody");
            return;
        }
        //volba pracovneho adresara
        userSetWorkingDir();
        //volba rezimu chodu programu
        userSetMode();

        //cyklus ktory vykonava chod programu
        while (running) {
            //vypise a nacita spustitelne prechody
            List<String> executableTransitions = getPrintExecutableTransitions();
            //nacitanie volby spustitelneho prechodu
            int transitionNumber = userGetTransitionNumber(executableTransitions);
            //spustenie prechodu
            executeTransition(executableTransitions, transitionNumber);
            //ulozenie progressu do suboru
            try {
                transformer.updateSourcePlacesToFile(places, sourceName);
            } catch (FileNotFoundException e) {
                System.out.println("Nieje mozne zalohovat source, source sa nenasiel");
            }
            //kontrola ukoncenia programu
            running = runnable();
        }
    }

    private boolean runnable() {
        for (Transition transition : transitions) {
            if (transition.executable()) {
                return true;
            }
        }
        return false;
    }

    private void executeTransition(List<String> executableTransitions, int transitionNumber) {
        for (Transition transition : transitions) {
            if (transition.getLabel().equals(executableTransitions.get(transitionNumber - 1))) {
                if (transition.executable()) {
                    CliExecutor.CliResult cmdResult = cliExecutor.executeCommand(transition.getAction());
                    cmdResult.printCliResult();
                    if(cmdResult.getExitCode() == 0)
                        transition.execute();
                    break;
                }
            }
        }
    }

    private void initializePetriNet() {
        System.out.println();
        System.out.print("Zadajte nazov suboru ktory chcete otvorit: ");
        //Spracovanie vstupnych dat
        String sourceName;
        while(true) {
            sourceName = scanner.nextLine();
            try {
                PartsWrapper partsWrapper = transformer.transformFromSource(transformer.unmarshalSource(sourceName));
                this.transitions = partsWrapper.getTransitions();
                this.places = partsWrapper.getPlaces();
                this.arcs = partsWrapper.getArcs();
                break;
            } catch (FileNotFoundException fnfe) {
                System.out.print("Subor s danym nazvom neexistuje. Zadajte iny nazov: ");
            } catch (UnmarshallingFailureException | IllegalStateException ufe) {
                System.out.print("Chyba pri spracovani suboru. Skontrolujte spravnost dat. Zadajte nazov suboru: ");
            }
        }
        this.sourceName = sourceName;
    }

    private void userSetWorkingDir() {
        File newWorkingDir;
        System.out.print("Zadajte nazov adresara v ktorom chcete pracovat, alebo nezadajte nic pre defaultnu hodnotu '"
                + cliExecutor.getWorkingDir().getAbsolutePath() + "': ");
        do {
            String path = scanner.nextLine();

            if (path.equals("")) {   //ak nieje nic zadane, zostava defaultna hodnota
                break;
            }

            newWorkingDir = new File(path);
            try {
                cliExecutor.setWorkingDir(newWorkingDir);
            } catch (FileNotFoundException e) {
                System.out.print(e.getMessage() + ". Zadajte iny nazov adresara: ");
            }

        } while (!newWorkingDir.isDirectory());
    }

    private void userSetMode() {
        System.out.print("Vyberte rezim chodu programu, zadajte cislo 1. Manualny, 2. Semiautomaticky: ");
        int choice = 0;
        do {
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Nezadali ste cislo, zadajte znovu: ");
                scanner.nextLine();
                continue;
            }

            for(Mode mode2 : Mode.values()) {
                if(mode2.getNumber() == choice) {
                    mode = mode2;
                    choice = -1;
                    break;
                }
            }
            if(!(choice == -1)) {
                System.out.print("Nespravne cislo, zadajte znovu: ");
                choice = 0;
            }
        } while (choice == 0);
    }

    private List<String> getPrintExecutableTransitions() {
        List<String> executableTransitions = new ArrayList<>();
        System.out.println("Spustitelne prechody: ");
        int i = 0;
        for (Transition transition : transitions) {
            if (transition.executable()) {
                i++;
                executableTransitions.add(transition.getLabel());
                System.out.println(i + ". " + transition.getLabel());
            }
        }
        return executableTransitions;
    }

    private int userGetTransitionNumber(List<String> executableTransitions) {
        int transitionNumber;
        if(mode == Mode.MANUAL || executableTransitions.size() > 1) {
            System.out.println();
            System.out.print("Zadajte cislo prechodu ktory chcete spustit: ");
            while (true) {
                try {
                    transitionNumber = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.print("Nezadali ste cislo, zadajte znovu: ");
                    continue;
                } finally {
                    scanner.nextLine();
                }
                try {
                    executableTransitions.get(transitionNumber - 1);
                } catch (IndexOutOfBoundsException e) {
                    System.out.print("Nespravne cislo, zadajte znovu: ");
                    continue;
                }
                break;
            }
        } else {
            transitionNumber = 1;
        }
        return transitionNumber;
    }

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

    public Transformer getTransformer() {
        return transformer;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public CliExecutor getCliExecutor() {
        return cliExecutor;
    }

    public void setCliExecutor(CliExecutor cliExecutor) {
        this.cliExecutor = cliExecutor;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}