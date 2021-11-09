package com.joydeep.poc.runners;

import com.joydeep.poc.configurations.DataDumpConfiguration;
import com.joydeep.poc.models.Author;
import com.joydeep.poc.repositories.AuthorRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
@Order(1)
public class AuthorDataInitializer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(AuthorDataInitializer.class);

    private final AuthorRepository authorRepository;
    private final String authorDumpFilePath;

    public AuthorDataInitializer(AuthorRepository authorRepository, DataDumpConfiguration dataDumpConfiguration) {
        this.authorRepository = authorRepository;
        authorDumpFilePath = dataDumpConfiguration.getAuthors();
    }


    @Override
    public void run(String... args) throws Exception {
        Path path= Paths.get(ClassLoader.getSystemResource(authorDumpFilePath).toURI());
        try(Stream<String> lines = Files.lines(path)){
             lines.forEach(line-> {
                 try {
                     String jsonString = line.substring(line.indexOf("{")).trim();
                     JSONObject jsonObject = new JSONObject(jsonString);
                     String id = jsonObject.optString("key").replace("/authors/","").trim();
                     String name = jsonObject.optString("name");
                     String personalName = jsonObject.optString("personal_name");
                     Author author = new Author.AuthorBuilder(id,name).personalName(personalName).build();
                     authorRepository.save(author);
                     logger.info("Persisted author {}",author);
                 }
                 catch (JSONException jsonException){
                     logger.error("Error parsing json line ",jsonException);
                 }
             });
        }
        catch (IOException ioException){
            logger.error("Exception while reading file from path {}", authorDumpFilePath);
        }
    }
}
