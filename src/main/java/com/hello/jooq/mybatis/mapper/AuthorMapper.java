package com.hello.jooq.mybatis.mapper;

import com.hello.jooq.example.author.dto.AuthorDto;
import com.hello.jooq.mybatis.vo.AuthorVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuthorMapper {
    @Insert("insert into author (first_name, last_name) values (#{firstName}, #{lastName})")
    public int insertAuthor(AuthorVO authorVO);

    @Select("select id, first_name, last_name from author")
    public List<AuthorVO> selectAllAuthor();

    public int bulkInsertAuthor(@Param("authorVOs") List<AuthorVO> authorDtos);
}
