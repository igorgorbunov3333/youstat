//package com.company.youstat.init;
//
//import com.company.youstat.domain.ApiCreds;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//
//import java.io.File;
//
//public class AppStartupRunner implements ApplicationRunner {
//
//    @Value("${CLIENT_ID}")
//    private String clientId;
//    @Value("${CLIENT_SECRET}")
//    private String clientSecret;
//    @Value("{HOME}")
//    private String homePath;
//
//    private static Logger logger = LoggerFactory.getLogger(AppStartupRunner.class);
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        ApiCreds apiCreds = new ApiCreds();
//
//        ApiCreds.Installed installed = apiCreds.new Installed();
//
//        String clientIdNotnull = clientId != null ? "not null" : "null";
//        logger.info("client id: " + clientIdNotnull);
//
//        installed.setClientId(clientId);
//
//        String clientSecretNotnull = clientSecret != null ? "not null" : "null";
//        logger.info("client secret: " + clientSecretNotnull);
//
//        installed.setClientSecret(clientSecret);
//        apiCreds.setInstalled(installed);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        File file = new File( homePath + "/test.xml");
//        if (file.createNewFile()) {
//            logger.info("client_secrets.json file is created!");
//        } else {
//            logger.info("client_secrets.json file not created!");
//        }
//        objectMapper.writeValue(file, apiCreds);
//    }
//}
