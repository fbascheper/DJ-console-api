package com.github.fbascheper.dj.console;

import com.github.fbascheper.dj.console.config.CrowdVoteProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CrowdVoteProperties.class)
public class DiscJockeyConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscJockeyConsoleApplication.class, args);
    }

}
