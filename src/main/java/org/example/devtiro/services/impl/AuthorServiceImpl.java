package org.example.devtiro.services.impl;



import org.example.devtiro.domain.entities.AuthorEntity;
import org.example.devtiro.services.AuthorService;
import org.example.devtiro.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

    private AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorEntity save(AuthorEntity authorEntity) {
        return authorRepository.save(authorEntity);
    }
}