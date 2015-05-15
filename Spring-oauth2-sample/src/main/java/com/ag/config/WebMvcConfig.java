package com.ag.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * The Class WebMvcConfig.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.ag.mvc")
public class WebMvcConfig {

}
