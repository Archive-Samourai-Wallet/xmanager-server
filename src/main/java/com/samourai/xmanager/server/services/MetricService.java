package com.samourai.xmanager.server.services;

import com.samourai.xmanager.server.beans.ManagedService;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.lang.invoke.MethodHandles;
import java8.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MetricService {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String COUNTER_HIT_SUCCESS_TOTAL = "xm_hit_success_total";
  private static final String COUNTER_HIT_FAIL_TOTAL = "xm_hit_fail_total";

  private static final String GAUGE_LAST_HIT_ERROR_SECONDS = "xm_last_hit_error_seconds";
  private static final String GAUGE_LAST_HIT_SUCCESS_SECONDS = "xm_last_hit_success_seconds";
  private static final String GAUGE_LAST_INDEX = "xm_last_index";

  private static final String TIMER_HIT_LATENCY = "xm_hit_latency";

  public MetricService() {}

  public void manage(ManagedService managedService) {
    Iterable<Tag> tags = Lists.of(Tag.of("xmService", managedService.getId()));

    // last error
    Metrics.gauge(
        GAUGE_LAST_HIT_ERROR_SECONDS,
        tags,
        managedService,
        ms -> (ms.getLastError() != null ? ms.getLastError() / 1000 : 0));

    // last success
    Metrics.gauge(
        GAUGE_LAST_HIT_SUCCESS_SECONDS,
        tags,
        managedService,
        ms -> (ms.getLastSuccess() != null ? ms.getLastSuccess() / 1000 : 0));

    // last index
    Metrics.gauge(
        GAUGE_LAST_INDEX,
        tags,
        managedService,
        ms -> (ms.getLastResponse() != null ? ms.getLastResponse().getIndex() : 0));
  }

  public void onHitSuccess(ManagedService managedService) {
    Metrics.counter(COUNTER_HIT_SUCCESS_TOTAL, "xmService", managedService.getId()).increment();
  }

  public void onHitFail(ManagedService managedService) {
    Metrics.counter(COUNTER_HIT_FAIL_TOTAL, "xmService", managedService.getId()).increment();
  }

  public Timer hitTimer(ManagedService managedService) {
    return Metrics.timer(TIMER_HIT_LATENCY, "xmService", managedService.getId());
  }
}
