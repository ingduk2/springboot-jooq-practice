package com.hello.jooq.example.author.repository;

import com.hello.jooq.example.author.dto.*;
import com.hello.jooq.jooqgen.tables.Article;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@JooqTest
@Import({JooqAuthorRepository.class, JooqArticleRepository.class})
class JooqArticleRepositoryTest {

    @Autowired
    JooqAuthorRepository authorRepository;

    @Autowired
    JooqArticleRepository articleRepository;

    private AuthorDto getAuthorDto(String firstName, String lastName) {
        return AuthorDto.builder()
                .firstName("aa")
                .lastName("bb")
                .build();
    }

    private ArticleEntity getArticleEntity(String title, String description, int authorId) {
        return ArticleEntity.builder()
                .title(title)
                .description(description)
                .authorId(authorId)
                .build();
    }

    private ArticleDtoNoId getArticleNoId(String title, String description, int authorId) {
        return ArticleDtoNoId.builder()
                .title(title)
                .description(description)
                .authorId(authorId)
                .build();
    }

    @Test
    @DisplayName("author insert Returning, article insert, findJoinByArticleId")
    void insertSelectTest() {
        //given
        //author Save
        AuthorDto authorDto = authorRepository
                .insertVALUES_RETURNING(getAuthorDto("aa", "bb"));

        //article Save
        ArticleEntity savedArticle = articleRepository
                .insert(getArticleEntity("title", "desc", authorDto.getId()));
        log.info("savedArticle : {}", savedArticle);

        //when
        //select
        AuthorArticleJoinDto joinDto = articleRepository.findByArticleId(savedArticle.getId());
        log.info("joinDto : {}", joinDto);

        //then
        assertThat(joinDto.getArticleId()).isEqualTo(savedArticle.getId());
    }

    @Test
    @DisplayName("author insert Returning, article insertNewRecord Entity, findJoinByArticleId")
    void insertSelectTest2() {
        //given
        //author Save
        AuthorDto authorDto = authorRepository
                .insertVALUES_RETURNING(getAuthorDto("aa", "bb"));

        //article Save Entity Wrapper
        int savedArticleId = articleRepository
                .insertNewRecord(getArticleEntity("title1", "desc1", authorDto.getId()));
        log.info("savedArticleId : {}", savedArticleId);
        int savedArticleId2 = articleRepository
                .insertNewRecord(getArticleEntity("title2", "desc2", authorDto.getId()));
        log.info("savedArticleId2 : {}", savedArticleId2);

        //when
        //select
        AuthorArticleJoinDto joinDto = articleRepository.findByArticleId(savedArticleId);
        log.info("joinDto : {}", joinDto);

        //then
        assertThat(joinDto.getArticleId()).isEqualTo(savedArticleId);
        assertThat(joinDto.getTitle()).isEqualTo("title1");
    }

    @Test
    @DisplayName("author insert Returning, article insertNewRecord NoId, findJoinByArticleId")
    void insertSelectTest3() {
        //given
        //author Save
        AuthorDto authorDto = authorRepository
                .insertVALUES_RETURNING(getAuthorDto("aa", "bb"));

        //article Save NoId
        int savedArticleId = articleRepository
                .insertNewRecordNoId(getArticleNoId("title1", "desc1", authorDto.getId()));
        log.info("savedArticleId : {}", savedArticleId);
        int savedArticleId2 = articleRepository
                .insertNewRecordNoId(getArticleNoId("title1", "desc1", authorDto.getId()));
        log.info("savedArticleId2 : {}", savedArticleId2);

        //when
        //select
        AuthorArticleJoinDto joinDto = articleRepository.findByArticleId(savedArticleId);
        log.info("joinDto : {}", joinDto);

        //then
        assertThat(joinDto.getArticleId()).isEqualTo(savedArticleId);
    }

    @Test
    @DisplayName("author insert Returning, article insertReturnId, findSearchDynamic")
    void searchDynamicQuery() {
        //given
        //author Save
        AuthorDto authorDto = authorRepository
                .insertVALUES_RETURNING(getAuthorDto("aa", "bb"));

        //article Save
        int savedArticleId = articleRepository.
                insertReturnId(getArticleEntity("title1", "desc1", authorDto.getId()));
        log.info("savedArticleId : {}", savedArticleId);

        int savedArticleId2 = articleRepository.
                insertReturnId(getArticleEntity("title2", "desc2", authorDto.getId()));
        log.info("savedArticleId2 : {}", savedArticleId2);

        //when
        //select
        List<AuthorArticleJoinDto> joinDtos = articleRepository
                .searchDynamicQuery(SearchDto.builder()
                        .firstName("aa")
                        .lastName("b")
                        .build());
        log.info("joinDto : {}", joinDtos);

        //then
        assertThat(joinDtos.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("author insert Returning, article insertReturnId, findSearchDynamicFunction")
    void searchDynamicFunction() {
        //given
        //author Save
        AuthorDto authorDto = authorRepository
                .insertVALUES_RETURNING(AuthorDto.builder()
                        .firstName("aaa")
                        .lastName("bbb")
                        .build());

        //article Save
        for (int i = 1; i <= 3; i++) {
            int savedArticleId = articleRepository.
                    insertReturnId(getArticleEntity("title" + i, "desc" + i, authorDto.getId()));
            log.info("savedArticleId : {}", savedArticleId);
        }

        //when
        //select
        List<AuthorArticleJoinDto> joinDtos = articleRepository.searchDynamicFunction(
                SearchDto.builder()
//                        .firstName("aa")
//                        .lastName("bcc")
//                        .title("title")
                        .build());
        log.info("joinDto : {}", joinDtos);

        //then
        assertThat(joinDtos.size()).isEqualTo(3);
    }


}