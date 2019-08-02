package com.dataexo.zblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * com.dataexo.zblog.Application.java
 *
 * @desc 不积跬步无以至千里, 不积小流无以至千里
 * @author:EumJi
 * @year: 2017
 * @month: 04
 * @day: 02
 * @time: 2017/4/2
 */

@SpringBootApplication
@EnableScheduling


public class Application  extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}

