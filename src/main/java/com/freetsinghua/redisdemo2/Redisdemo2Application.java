package com.freetsinghua.redisdemo2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/** @author z.tsinghua */
@SpringBootApplication
@EnableAsync
public class Redisdemo2Application {

    private static Logger logger = LoggerFactory.getLogger(Redisdemo2Application.class);

    public static void main(String[] args) {

        SpringApplication.run(Redisdemo2Application.class, args);
    }
}
