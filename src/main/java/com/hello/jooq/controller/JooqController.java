package com.hello.jooq.controller;

import com.hello.jooq.example.author.dto.AuthorDto;
import com.hello.jooq.example.author.dto.AuthorEntity;
import com.hello.jooq.example.author.repository.JooqAuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class JooqController {

    private final JooqAuthorRepository jooqAuthorRepository;

    @GetMapping("/jooq/upsert")
    public List<AuthorEntity> jooqUpsert() {
        List<AuthorEntity> entities = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            entities.add(AuthorEntity.builder()
                    .id(i)
                    .firstName("first")
                    .lastName("last")
                    .readCount(i)
                    .build());
        }
        jooqAuthorRepository.bulkInsertOnDuplicate(entities);
        return jooqAuthorRepository.selectReturnEntities();
    }
}
