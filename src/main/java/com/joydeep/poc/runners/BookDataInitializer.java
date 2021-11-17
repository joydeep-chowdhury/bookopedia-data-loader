package com.joydeep.poc.runners;

import com.joydeep.poc.configurations.DataDumpConfiguration;
import com.joydeep.poc.models.Author;
import com.joydeep.poc.models.Book;
import com.joydeep.poc.models.Book.BookBuilder;
import com.joydeep.poc.repositories.AuthorRepository;
import com.joydeep.poc.repositories.BookRepository;
import org.json.JSONArray;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Order(2)
public class BookDataInitializer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(BookDataInitializer.class);

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final DataDumpConfiguration dataDumpConfiguration;

    public BookDataInitializer(BookRepository bookRepository, AuthorRepository authorRepository, DataDumpConfiguration dataDumpConfiguration) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.dataDumpConfiguration = dataDumpConfiguration;
    }

    @Override
    public void run(String... args) throws Exception {
        Path path = Paths.get(ClassLoader.getSystemResource(dataDumpConfiguration.getWorks())
                                         .toURI());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                try {
                    String jsonString = line.substring(line.indexOf("{"))
                                            .trim();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    String id = jsonObject.optString("key")
                                          .replace("/works/", "")
                                          .trim();
                    String name = jsonObject.optString("title")
                                            .trim();
                    JSONObject descriptionJsonObject = jsonObject.optJSONObject("description");
                    JSONObject publishedJsonObject = jsonObject.optJSONObject("created");
                    JSONArray coversArray = jsonObject.optJSONArray("covers");
                    JSONArray authorArray = jsonObject.optJSONArray("authors");
                    BookBuilder bookBuilder = new BookBuilder(id, name);
                    if (descriptionJsonObject != null) {
                        bookBuilder.description(descriptionJsonObject.optString("value"));
                    }

                    if (publishedJsonObject != null) {
                        bookBuilder.publishedDate(LocalDate.parse(publishedJsonObject.optString("value"), dateTimeFormatter));
                    }

                    if (coversArray != null) {
                        List<String> coverIds = IntStream.range(0, coversArray.length())
                                                         .mapToObj(i -> coversArray.optString(i))
                                                         .collect(Collectors.toList());
                        bookBuilder.coverIds(coverIds);
                    }

                    if (authorArray != null) {
                        List<String> authorIds = IntStream.range(0, authorArray.length())
                                                          .mapToObj(i -> authorArray.optJSONObject(i))
                                                          .map(object -> object.optJSONObject("author")
                                                                               .optString("key")
                                                                               .replace("/authors/", "")
                                                                               .trim())
                                                          .collect(Collectors.toList());
                        List<String> authorNames = authorIds.stream()
                                                            .map(authorId -> {
                                                                Optional<Author> optionalAuthor = authorRepository.findById(authorId);
                                                                if (!optionalAuthor.isPresent()) {
                                                                    return "Unknown author";
                                                                }
                                                                return optionalAuthor.get()
                                                                                     .getName();
                                                            })
                                                            .collect(Collectors.toList());
                        bookBuilder.authorIds(authorIds);
                        bookBuilder.authorNames(authorNames);
                    }

                    Book book = bookBuilder.build();
                    bookRepository.save(book);
                    logger.info("Persisted book {}", book);

                } catch (JSONException jsonException) {
                    logger.error("Error parsing json line ", jsonException);
                }
            });
        } catch (IOException ioException) {
            logger.error("Exception while reading file from path {}", dataDumpConfiguration.getWorks());
        }
    }
}
