package com.joydeep.poc.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "datadump.location")
public class DataDumpConfiguration {

    private String authors;
    private String works;

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getWorks() {
        return works;
    }

    public void setWorks(String works) {
        this.works = works;
    }
}
