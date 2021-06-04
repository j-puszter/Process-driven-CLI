package sk.stuba.fei.uim.jp.processdrivencli.cli;

import org.springframework.stereotype.Component;

import java.io.*;

@SuppressWarnings("unused")
@Component
public class CliExecutor {

    private final boolean isWindows;
    private String charset;
    private final ProcessBuilder processBuilder;
    private File workingDir;

    public CliExecutor() {
        processBuilder = new ProcessBuilder();
        isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        workingDir = new File(System.getProperty("user.home"));
        processBuilder.directory(workingDir);
        charset = "UTF-8";

        if(isWindows) {
            charset = ("CP" + executeCommand("chcp").getResult().split(": ")[1]).trim();
        }
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class CliResult {
        private final int exitCode;
        private final String result;

        public CliResult(int exitCode, String result) {
            this.exitCode = exitCode;
            this.result = result;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getResult() {
            return result;
        }

        public void printCliResult() {
            if (exitCode == 0) {
                System.out.println("Operation execution: SUCCESS");
            } else {
                System.out.println("Operation execution: FAILURE");
            }
            System.out.println();
            System.out.println(result);

        }
    }

    public CliResult executeCommand(String command) {
        if (isWindows) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("sh", "-c", command);
        }

        int exitCode = -1;
        StringBuilder result = new StringBuilder();

        try {
            Process process = processBuilder.redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));

            exitCode = process.waitFor();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new CliResult(exitCode, result.toString());
    }

    public boolean isWindows() {
        return isWindows;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public ProcessBuilder getProcessBuilder() {
        return processBuilder;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(File workingDir) throws FileNotFoundException {
        if(!workingDir.isDirectory()) {
            throw new FileNotFoundException("Adresar s danym nazvom neexistuje");
        }
        this.workingDir = workingDir;
        processBuilder.directory(workingDir);
    }
}
