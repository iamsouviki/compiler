package app.web.souvikportfolio.compiler.controller;


import app.web.souvikportfolio.compiler.model.CompilerModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@RestController
public class CompilerController {
    @PostMapping(value = "/compile")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> compileCode(@RequestBody CompilerModel compilerModel) throws IOException {
        String output;
        String filename = UUID.randomUUID().toString();
        String fileExtension;
        String compileCommand;
        String runCommand;

        switch (compilerModel.getLanguage().toLowerCase()) {
            case "python":
                fileExtension = ".py";
                compileCommand = "python";
                runCommand = "python";
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
                return new ResponseEntity<>("Unsupported language", HttpStatus.BAD_REQUEST);
        }

        File tempFile = File.createTempFile(filename, fileExtension);
        String filePath = tempFile.getAbsolutePath();

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(compilerModel.getCodeText());
        } catch (IOException e) {
            return new ResponseEntity<>("Error writing to file ", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCommand + " " + filePath);
            compileProcess.waitFor();

            Process runProcess = Runtime.getRuntime().exec(runCommand + " " + filePath);
            runProcess.waitFor();

            output = new String(runProcess.getInputStream().readAllBytes());

        } catch (IOException | InterruptedException e) {
            return new ResponseEntity<>("Error during compilation or execution "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        tempFile.deleteOnExit();

        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
