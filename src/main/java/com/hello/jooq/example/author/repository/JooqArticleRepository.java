package com.hello.jooq.example.author.repository;

import com.hello.jooq.example.author.dto.ArticleEntity;
import com.hello.jooq.example.author.dto.ArticleEntityNoId;
import com.hello.jooq.example.author.dto.AuthorArticleJoinDto;
import com.hello.jooq.example.author.dto.AuthorEntity;
import com.hello.jooq.jooqgen.tables.daos.ArticleDao;
import com.hello.jooq.jooqgen.tables.records.ArticleRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hello.jooq.jooqgen.tables.Article.ARTICLE;
import static com.hello.jooq.jooqgen.tables.Author.AUTHOR;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JooqArticleRepository {

    private final DSLContext q;

    public ArticleEntity insert(ArticleEntity entity) {
        return q.insertInto(ARTICLE,
                        ARTICLE.TITLE,
                        ARTICLE.DESCRIPTION,
                        ARTICLE.AUTHOR_ID)
                .values(entity.getTitle(),
                        entity.getDescription(),
                        entity.getAuthorId())
                .returning()
                .fetchOne()
                .into(ArticleEntity.class);
    }

    public int insertNewRecord(ArticleEntity entity) {
        //이렇게 하면 auto increment 가 안된..
//        ArticleRecord articleRecord = q.newRecord(ARTICLE, entity);

        //set을 해줘야 되넹... 흠
        ArticleRecord articleRecord = q.newRecord(ARTICLE);
        articleRecord.setAuthorId(entity.getAuthorId());
        articleRecord.setTitle(entity.getTitle());
        articleRecord.setDescription(entity.getDescription());

        if (articleRecord.store() < 1) throw new RuntimeException("DB save Exeption");

        return articleRecord.getId();
    }

    public int insertNewRecordNewId(ArticleEntityNoId entityNoId) {
        //pojo 에 id를 빼야함
        ArticleRecord articleRecord = q.newRecord(ARTICLE, entityNoId);
        articleRecord.store();
        return articleRecord.getId();
    }

    public void bulkInsert(List<ArticleEntity> entities) {
        Query query = q.insertInto(ARTICLE,
                ARTICLE.TITLE,
                ARTICLE.DESCRIPTION,
                ARTICLE.AUTHOR_ID)
                .values((String) null, null, null);

        BatchBindStep batch = q.batch(query)
                .bind(entities.stream()
                        .map(ArticleEntity::getBulkInsertParams)
                        .toArray(Object[][]::new));

        int[] results = batch.execute();
        log.info("{}", results);
    }

    public List<AuthorEntity> findAll() {
        return q.select()
                .from(ARTICLE)
                .fetchInto(AuthorEntity.class);
    }

    public List<AuthorArticleJoinDto> findAllJoinArticleAndAuthor() {
         return selectAuthorJoinArticle()
                .fetchInto(AuthorArticleJoinDto.class);
    }

    public AuthorArticleJoinDto findByArticleId(int articleId) {
        return selectAuthorJoinArticle()
                .where(ARTICLE.ID.eq(articleId))
                .fetchOne()
                .into(AuthorArticleJoinDto.class);
    }

    public SelectOnConditionStep<Record6<Integer, Integer, String, String, String, String>> selectAuthorJoinArticle() {
        return q.select(AUTHOR.ID.as("authorId"),
                        ARTICLE.ID.as("articleId"),
                        AUTHOR.FIRST_NAME,
                        AUTHOR.LAST_NAME,
                        ARTICLE.TITLE,
                        ARTICLE.DESCRIPTION)
                .from(AUTHOR)
                .join(ARTICLE)
                .on(AUTHOR.ID.eq(ARTICLE.AUTHOR_ID));
    }

}
