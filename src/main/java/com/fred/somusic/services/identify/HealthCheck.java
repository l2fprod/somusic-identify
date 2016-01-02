package com.fred.somusic.services.identify;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {

  @RequestMapping("/services/identify/healthcheck")
  public String healthcheck() {
    return "OK";
  }

}
