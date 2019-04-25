package com.matiasnicoletti.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Value("${threading.config.sc.corePoolSize:10}")
  private int scCorePoolSize;
  @Value("${threading.config.sc.maxPoolSize:200}")
  private int scMaxPoolSize;
  @Value("${threading.config.sc.queueCapacity:500}")
  private int scQueueCapacity;

  /**
   * Create the pool executor for the threads in swagger.
   *
   * @return Task executor.
   */
  @Bean(name = "threadPoolSwaggerExecutor")
  public TaskExecutor threadPoolSwaggerExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(scCorePoolSize);
    executor.setMaxPoolSize(scMaxPoolSize);
    executor.setQueueCapacity(scQueueCapacity);
    executor.setThreadNamePrefix("swagger-pool-");
    executor.afterPropertiesSet();
    return executor;
  }
}
