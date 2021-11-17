package com.joydeep.poc;

import com.joydeep.poc.configurations.BookopediaDataLoaderCommonConfiguration;
import com.joydeep.poc.configurations.DataDumpConfiguration;
import com.joydeep.poc.configurations.DataStaxAstraConnectionConfiguration;
import com.joydeep.poc.models.Author;
import com.joydeep.poc.models.Book;
import com.joydeep.poc.repositories.AuthorRepository;
import com.joydeep.poc.repositories.BookRepository;
import com.joydeep.poc.runners.AuthorDataInitializer;
import com.joydeep.poc.runners.BookDataInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@TestPropertySource(locations = { "classpath:applicationtest.properties" })
public class BookopediaDataLoaderApplicationTests {

    private static final int AUTHOR_COUNT = 3;
    private static final int BOOK_COUNT = 3;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void contextLoads() throws InterruptedException {
        assertEquals(AUTHOR_COUNT, authorRepository.count());
        assertEquals(BOOK_COUNT, bookRepository.count());
        assertThrows(NoSuchBeanDefinitionException.class,()->applicationContext.getBean(Author.class));
        assertNotNull(applicationContext.getBean(BookopediaDataLoaderCommonConfiguration.class));
        assertNotNull(applicationContext.getBean(CqlSessionBuilderCustomizer.class));
        assertNotNull(applicationContext.getBean(DataDumpConfiguration.class));
        assertNotNull(applicationContext.getBean(DataStaxAstraConnectionConfiguration.class));
        assertNotNull(applicationContext.getBean(AuthorRepository.class));
        assertNotNull(applicationContext.getBean(BookRepository.class));
        assertNotNull(applicationContext.getBean(AuthorDataInitializer.class));
        assertNotNull(applicationContext.getBean(BookDataInitializer.class));
        List<Author> authorList=authorRepository.findAll();
        authorList.forEach(author -> authorRepository.delete(author));
        List<Book> bookList=bookRepository.findAll();
        bookList.forEach(book -> bookRepository.delete(book));
        assertEquals(0, authorRepository.count());
        assertEquals(0, bookRepository.count());
    }

}
