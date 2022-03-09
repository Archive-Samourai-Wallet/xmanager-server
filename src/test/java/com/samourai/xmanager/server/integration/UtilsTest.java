package com.samourai.xmanager.server.integration;

import com.samourai.xmanager.server.utils.Utils;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {
  private Utils utils = Utils.getInstance();

  @Test
  public void runOrTimeout() throws Exception {
    final int EXPECTED = 5;

    // no delay
    Integer result =
        utils.runOrTimeout(
            () -> {
              return EXPECTED;
            },
            1000);
    Assertions.assertEquals(EXPECTED, result);

    // wait less than timeout delay
    result =
        utils.runOrTimeout(
            () -> {
              Thread.sleep(1000);
              return EXPECTED;
            },
            1050);
    Assertions.assertEquals(EXPECTED, result);

    // wait more than timeout delay
    try {
      result =
          utils.runOrTimeout(
              () -> {
                Thread.sleep(1000);
                return EXPECTED;
              },
              900);
      Assertions.assertTrue(false); // should timeout
    } catch (TimeoutException e) {
      // expected
      e.printStackTrace();
    }
  }
}
