package com.joydeep.poc.runners;

import com.joydeep.poc.configurations.DataDumpConfiguration;
import com.joydeep.poc.repositories.AuthorRepository;
import com.joydeep.poc.repositories.BookRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorDataInitializerTest {

    private AuthorDataInitializer authorDataInitializer;
    private AuthorRepository authorRepository;
    private DataDumpConfiguration dataDumpConfiguration;

    @BeforeEach
    public void init(){
        authorRepository= Mockito.mock(AuthorRepository.class);
        dataDumpConfiguration = Mockito.mock(DataDumpConfiguration.class);
        authorDataInitializer = new AuthorDataInitializer(authorRepository,dataDumpConfiguration);
    }

    @Test
    void run() throws Exception {
        when(dataDumpConfiguration.getAuthors()).thenReturn("authors-test.txt");
        authorDataInitializer.run();
        verify(authorRepository,times(3)).save(Mockito.any());
    }
}