package com.matiasnicoletti.config;

import com.netflix.appinfo.AmazonInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EurekaInstanceConfig {

  @Value("${server.port}")
  private int serverPort;

  /**
   * Eureka aws config.
   * 
   * @return EurekaInstanceConfigBean.
   */
  @Bean
  @Profile("aws")
  public EurekaInstanceConfigBean eurekaInstanceConfigBean() {
    EurekaInstanceConfigBean config = new EurekaInstanceConfigBean();
    AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
    config.setDataCenterInfo(info);
    config.setNonSecurePort(serverPort);
    config.setPreferIpAddress(true);
    config.setInstanceId(config.getIpAddress() + ":" + serverPort);
    return config;
  }
}
