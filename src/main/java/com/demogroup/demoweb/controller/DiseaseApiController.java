package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.service.DiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/disease")
@RequiredArgsConstructor
public class DiseaseApiController {
    private final DiseaseService diseaseService;

    @PostMapping("/diagnosis")
    public ResponseEntity<List> mangoDiagnosis(@RequestParam("mangoImage")MultipartFile mangoImage,
                                                 @RequestParam("location") String location){
        System.out.println("DiseaseApiController.mangoDiagnosis");
        String s3Url = diseaseService.saveToS3(mangoImage);
        List resultList = diseaseService.diagnosis(s3Url);


        return ResponseEntity.ok().body(resultList);
    }
}
