package org.sandhya.airtasker.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @RequestMapping("/hello")
    public ResponseEntity<String> getPerson(@RequestParam(value="name", defaultValue="World") String name) {
        return ResponseEntity.status(HttpStatus.OK).
                body("Hello " + name + " " + System.currentTimeMillis() );
    }


}
