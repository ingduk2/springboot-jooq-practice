package com.hello.jooq.mybatis.mapper;

import com.hello.jooq.mybatis.vo.AuthorVO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthorMapperTest {

    @Autowired
    AuthorMapper authorMapper;

    @Test
    void insertSelectTest() {
        AuthorVO authorVO = AuthorVO.builder()
                .firstName("first")
                .lastName("last")
                .build();
        int i = authorMapper.insertAuthor(authorVO);
        List<AuthorVO> authorVOList = authorMapper.selectAllAuthor();
        log.info("{}", authorVOList);
    }

    @Test
    void bulkInsertTest() {
        AuthorVO authorVO = AuthorVO.builder()
                .firstName("first")
                .lastName("last")
                .build();

        List<AuthorVO> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(authorVO);
        }

        authorMapper.bulkInsertAuthor(list);
        List<AuthorVO> authorVOS = authorMapper.selectAllAuthor();
        Assertions.assertThat(authorVOS.size()).isEqualTo(10);
    }

}