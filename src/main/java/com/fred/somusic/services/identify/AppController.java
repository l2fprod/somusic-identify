package com.fred.somusic.services.identify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
public class AppController {
  
  public static void main(String[] args) {
    SpringApplication.run(AppController.class, args);
  }
}
