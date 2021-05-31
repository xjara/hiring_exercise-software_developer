package com.cyansecurity.vlasak.rsstopicanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.cyansecurity.vlasak.rsstopicanalyzer"})
@EntityScan(basePackages="model")
public class RssTopicAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssTopicAnalyzerApplication.class, args);
    }

}
