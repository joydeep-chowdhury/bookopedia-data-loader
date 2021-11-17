package com.joydeep.poc.runners;

import com.joydeep.poc.configurations.DataDumpConfiguration;
import com.joydeep.poc.repositories.AuthorRepository;
import com.joydeep.poc.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookDataInitializerTest {

    private  BookRepository bookRepository;
    private  AuthorRepository authorRepository;
    private  DataDumpConfiguration dataDumpConfiguration;
    private  BookDataInitializer bookDataInitializer;

    @BeforeEach
    public void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        authorRepository = Mockito.mock(AuthorRepository.class);
        dataDumpConfiguration = Mockito.mock(DataDumpConfiguration.class);
        bookDataInitializer = new BookDataInitializer(bookRepository,authorRepository,dataDumpConfiguration);
    }

    @Test
    public void run() throws Exception {
        when(dataDumpConfiguration.getWorks()).thenReturn("works-test.txt");
        bookDataInitializer.run();
        verify(authorRepository,times(3)).findById(anyString());
        verify(bookRepository,times(3)).save(Mockito.any());
    }
}