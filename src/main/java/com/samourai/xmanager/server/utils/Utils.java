package com.samourai.xmanager.server.utils;

import com.samourai.javaserver.utils.ServerUtils;
import java.util.concurrent.*;

public class Utils {
  private static Utils instance;
  private static final ServerUtils serverUtils = ServerUtils.getInstance();

  public static final int CHAIN_RECEIVE = 0;

  public static Utils getInstance() {
    if (instance == null) {
      instance = new Utils();
    }
    return instance;
  }

  public void setLoggerDebug() {
    serverUtils.setLoggerDebug("com.samourai.xmanager");
    serverUtils.setLoggerDebug("com.samourai.wallet");
  }

  public <T> T runOrTimeout(Callable<T> task, long delayMs) throws Exception {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    Future<T> future = executor.submit(task);
    executor.schedule(
        () -> {
          future.cancel(true); // throws CancellationException (should be silent)
        },
        delayMs + 100, // silently interrupt job after throwing future's TimeoutException
        TimeUnit.MILLISECONDS);
    executor.shutdown();
    return future.get(delayMs, TimeUnit.MILLISECONDS); // throws TimeoutException
  }
}
