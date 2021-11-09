package com.joydeep.poc.repositories;

import com.joydeep.poc.models.Author;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CassandraRepository<Author, String> {
}
