package com.company.youstat.controller;

import com.company.youstat.apiservice.UploadVideosAnalytic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private UploadVideosAnalytic uploadVideosAnalytic;

    @GetMapping(path = "/")
    public void calculate() {
        uploadVideosAnalytic.calculateStatistic();
    }
}
