package app.web.souvikportfolio.compiler.service;

import app.web.souvikportfolio.compiler.model.CompilerModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Service
public class CompileServices {

    public String compileCode( CompilerModel compilerModel) throws IOException {
        String output;
        String filename = UUID.randomUUID().toString();
        String fileExtension;
        String compileCommand;
        String runCommand;

        switch (compilerModel.getLanguage().toLowerCase()) {
            case "python":
                fileExtension = ".py";
                compileCommand = "python3";
                runCommand = "python3";
                break;
            case "java":
                fileExtension = ".java";
                compileCommand = "javac";
                runCommand = "java";
                break;
            case "c":
                fileExtension = ".c";
                compileCommand = "gcc";
                runCommand = "./a.out";
                break;
            case "cpp":
                fileExtension = ".cpp";
                compileCommand = "g++";
                runCommand = "./a.out";
                break;
            default:
                return "Unsupported language";
        }

        File tempFile = File.createTempFile(filename, fileExtension);
        String filePath = tempFile.getAbsolutePath();

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(compilerModel.getCodeText());
        } catch (IOException e) {
            return "Error writing to file "+e.getMessage();
        }

        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCommand + " " + filePath);
            compileProcess.waitFor();

            Process runProcess = Runtime.getRuntime().exec(runCommand + " " + filePath);
            runProcess.waitFor();

            output = new String(runProcess.getInputStream().readAllBytes());

        } catch (IOException | InterruptedException e) {
            return "Error during compilation or execution "+e.getMessage();
        }
        tempFile.deleteOnExit();

        return output;
    }
}
