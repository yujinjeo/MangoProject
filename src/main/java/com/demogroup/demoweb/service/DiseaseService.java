package com.demogroup.demoweb.service;

import com.demogroup.demoweb.config.S3Config;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiseaseService {
    private final S3Config s3Config;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String localLocation="C:\\Users\\yujin\\Downloads\\temp";

    //S3에 이미지를 저장하는 메소드 입니다.
    public String saveToS3(MultipartFile mangoImage) {
        //mangoImage를 일시적으로 로컬에 저장
        //확장자를 추출한다.
        String originalFilename = mangoImage.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.indexOf("."));

        //이미지 이름을 생성한다.
        String imageName = UUID.randomUUID() + ext;

        //일시적으로 저장할 로컬 저장소를 생성한다.

        return "";
    }

    public List diagnosis(String s3Url) {
        List resultList=new ArrayList<String>();

        return resultList;
    }


}
