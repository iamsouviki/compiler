package app.web.souvikportfolio.compiler.controller;


import app.web.souvikportfolio.compiler.model.CompileandTest;
import app.web.souvikportfolio.compiler.model.CompilerModel;
import app.web.souvikportfolio.compiler.service.CompileServices;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    CompileServices compileServices;

    @PostMapping(value = "/compile")
    public ResponseEntity<String> compileCode(@RequestBody CompilerModel compilerModel) throws IOException {
        try{
            String output = compileServices.compileCode(compilerModel);
            return new ResponseEntity<>(output, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/compile-and-test")
    public ResponseEntity<String> compileAndTestCode(@RequestBody CompileandTest compileandTest) throws IOException {
        CompilerModel compilerModel = new CompilerModel();
            compilerModel.setLanguage(compileandTest.getLanguage());
            compilerModel.setCodeText(compileandTest.getCodeText());
            String output = compileServices.compileCode(compilerModel);
            if(compileandTest.getExpectedOutput().equals(output)){
                return new ResponseEntity<>("passed", HttpStatus.OK);
            }else{
               return new ResponseEntity<>("failed", HttpStatus.NOT_ACCEPTABLE);
            }
    }
}
