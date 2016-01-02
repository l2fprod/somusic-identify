package com.fred.somusic.services.identify;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.fred.somusic.common.BaseTask;

@Component
public class HealthCheck extends BaseTask implements HealthIndicator {

  @Override
  public Health health() {    
    try {
      getSongDb();
      return Health.up().build();
    } catch (Exception e) {
      return Health.down(e).build();
    }
  }

}
