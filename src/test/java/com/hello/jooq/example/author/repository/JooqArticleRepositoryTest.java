package com.hello.jooq.example.author.repository;

import com.hello.jooq.example.author.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@JooqTest
@Import({JooqAuthorRepository.class, JooqArticleRepository.class})
class JooqArticleRepositoryTest {

    @Autowired
    JooqAuthorRepository authorRepository;

    @Autowired
    JooqArticleRepository articleRepository;

    @Test
    void insertSelectTest() {
        //author Save
        AuthorDto authorDto = authorRepository
                .insertVALUES_RETURNING(AuthorDto.builder()
                        .firstName("aa")
                        .lastName("bb")
                        .build());

        //article Save
        ArticleEntity savedArticle = articleRepository.insert(ArticleEntity.builder()
                .title("title")
                .description("description")
                .authorId(authorDto.getId())
                .build());

        log.info("savedArticle : {}", savedArticle);

        //select
        AuthorArticleJoinDto joinDto = articleRepository.findByArticleId(savedArticle.getId());
        log.info("joinDto : {}", joinDto);
    }

    @Test
    void insertSelectTest2() {
        //author Save
        AuthorDto authorDto = authorRepository
                .insertVALUES_RETURNING(AuthorDto.builder()
                        .firstName("aa")
                        .lastName("bb")
                        .build());

        //article Save
        int savedArticleId = articleRepository.insertNewRecord(ArticleEntity.builder()
                .title("title")
                .description("description")
                .authorId(authorDto.getId())
                .build());
         articleRepository.insertNewRecord(ArticleEntity.builder()
                .title("title")
                .description("description")
                .authorId(authorDto.getId())
                .build());
        log.info("savedArticleId : {}", savedArticleId);

        //select
        AuthorArticleJoinDto joinDto = articleRepository.findByArticleId(savedArticleId);
        log.info("joinDto : {}", joinDto);
    }

    @Test
    void insertSelectTest3() {
        //author Save
        AuthorDto authorDto = authorRepository
                .insertVALUES_RETURNING(AuthorDto.builder()
                        .firstName("aa")
                        .lastName("bb")
                        .build());

        //article Save
        int savedArticleId = articleRepository.insertNewRecordNewId(ArticleEntityNoId.builder()
                .title("title")
                .description("description")
                .authorId(authorDto.getId())
                .build());

        int savedArticleId2 = articleRepository.insertNewRecordNewId(ArticleEntityNoId.builder()
                .title("title")
                .description("description")
                .authorId(authorDto.getId())
                .build());
        log.info("savedArticleId : {}", savedArticleId);
        log.info("savedArticleId2 : {}", savedArticleId2);

        //select
        AuthorArticleJoinDto joinDto = articleRepository.findByArticleId(savedArticleId);
        log.info("joinDto : {}", joinDto);
    }
}