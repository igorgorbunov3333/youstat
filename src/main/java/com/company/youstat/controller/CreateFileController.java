package com.company.youstat.controller;

import com.company.youstat.domain.ApiCreds;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class CreateFileController {

    @Value("${CLIENT_ID}")
    private String clientId;
    @Value("${CLIENT_SECRET}")
    private String clientSecret;
    @Value("{HOME}")
    private String homePath;

    private static Logger logger = LoggerFactory.getLogger(CreateFileController.class);

    @GetMapping(path = "/createFile")
    public void createCredsFile() {
        run();
    }

    public void run() {
        ApiCreds apiCreds = new ApiCreds();

        ApiCreds.Installed installed = apiCreds.new Installed();

        String clientIdNotnull = clientId != null ? "not null" : "null";
        logger.info("client id: " + clientIdNotnull);

        installed.setClientId(clientId);

        String clientSecretNotnull = clientSecret != null ? "not null" : "null";
        logger.info("client secret: " + clientSecretNotnull);

        installed.setClientSecret(clientSecret);
        apiCreds.setInstalled(installed);

        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File( homePath + "/client_secrets.json");
        try {
            if (file.createNewFile()) {
                logger.info("client_secrets.json file is created!");
            } else {
                logger.info("client_secrets.json file not created!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectMapper.writeValue(file, apiCreds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
