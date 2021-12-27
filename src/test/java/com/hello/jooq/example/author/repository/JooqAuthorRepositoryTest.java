package com.hello.jooq.example.author.repository;

import com.hello.jooq.example.author.dto.AuthorDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@JooqTest // 이거 config 어디로 되는지 확인 필요
@Import(JooqAuthorRepository.class)
class JooqAuthorRepositoryTest {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private JooqAuthorRepository jooqAuthorRepository;

    @Test
    void insertAndSelectOne() {
        AuthorDto dto = AuthorDto.builder()
                .firstName("first")
                .lastName("last")
                .build();
        AuthorDto authorDto = jooqAuthorRepository.insertVALUES_RETURNING(dto);

        AuthorDto select = jooqAuthorRepository.select(authorDto.getId());
        log.info("select : {}", select);

        assertThat(select.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(select.getLastName()).isEqualTo(dto.getLastName());
    }

    @Test
    void batchInsertAndSelect() {
        jooqAuthorRepository.batchInsert();
        List<AuthorDto> authorDtos = jooqAuthorRepository.selectAll();
        log.info("list : {}", authorDtos);
    }

    @Test
    void insert10000() {
        AuthorDto dto = AuthorDto.builder()
                .firstName("first")
                .lastName("last")
                .build();
        int insertCount = 0;

        for (int i = 0; i < 10000; i++) {
            insertCount += jooqAuthorRepository.insertVALUES(dto);
        }

        assertThat(insertCount).isEqualTo(10000);
    }

    @Test
    void bulkInsert10000() {
        jooqAuthorRepository.bulkInsert();
    }

    @Test
    void bulkInsertSingle() {
        jooqAuthorRepository.bulkInsertSingle();
//        List<AuthorDto> authorDtos = jooqAuthorRepository.selectAll();
//        log.info("size : {}", authorDtos.size());
    }



}