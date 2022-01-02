package com.hello.jooq.example.author.repository;

import com.hello.jooq.example.author.dto.*;
import com.hello.jooq.example.bind.JooqBind;
import com.hello.jooq.jooqgen.tables.Article;
import com.hello.jooq.jooqgen.tables.Author;
import com.hello.jooq.jooqgen.tables.records.ArticleRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hello.jooq.jooqgen.tables.Article.ARTICLE;
import static com.hello.jooq.jooqgen.tables.Author.AUTHOR;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JooqArticleRepository {

    private final DSLContext q;

    /**
     * insert into article(title, description, author_id) values(?, ?, ?)
     * returning (insert 한 것 autoincrement 채워서 return)
     * @param entity
     * @return
     */
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

    /**
     * insert into article(title, description, author_id) values(?, ?, ?)
     * id 만 returning (insert 한 것 autoincrement 채워서 return)
     * @param entity
     * @return
     */
    public int insertReturnId(ArticleEntity entity) {
        return q.insertInto(ARTICLE,
                        ARTICLE.TITLE,
                        ARTICLE.DESCRIPTION,
                        ARTICLE.AUTHOR_ID)
                .values(entity.getTitle(),
                        entity.getDescription(),
                        entity.getAuthorId())
                .returning()
                .fetchOne()
                .getId();
    }

    /**
     * insert with store(Wrapper class Id), jpa save 같이 사용 하는듯
     *
     * Dto, Vo, Entity int 형 id 일 경우 0으로 채워지기 때문에
     * insert 할 때마다 아래의 쿼리가 실행.
     * insert into `article` (`id`, `title`, `description`, `author_id`) values (0, 'title', 'description', 1)
     * insert into `article` (`id`, `title`, `description`, `author_id`) values (0, 'title', 'description', 1)
     * 그 결과 Unique index or primary key violation: "PRIMARY KEY ON public.article(id) 발생.
     *
     * Wrapper 클래스를 Id로 사용해야함(Integer, Long)
     * @param entity
     * @return
     */
    public int insertNewRecord(ArticleEntity entity) {
        ArticleRecord articleRecord = q.newRecord(ARTICLE, entity);

        if (articleRecord.store() < 1) throw new RuntimeException("DB save Exception");

        return articleRecord.getId();
    }

    /**
     * insert with store(no id dto), jpa save 같이 사용 하는듯
     *
     * Id (AutoIncrement) 값을 채워서 insert 하려면
     * id 를 wrapper Class로 사용하거나, dto 로 id 없이 사용
     * @param entityNoId
     * @return
     */
    public int insertNewRecordNoId(ArticleDtoNoId entityNoId) {
        //pojo 에 id를 빼야함
        ArticleRecord articleRecord = q.newRecord(ARTICLE, entityNoId);
        articleRecord.store();
        return articleRecord.getId();
    }

    /**
     * once bulk query with stream bind
     * @param entities
     */
    public void bulkInsert(List<ArticleEntity> entities) {
        BatchBindStep batch = q.batch(
                q.insertInto(ARTICLE,
                                ARTICLE.TITLE,
                                ARTICLE.DESCRIPTION,
                                ARTICLE.AUTHOR_ID)
                        .values(JooqBind.strVal,
                                JooqBind.strVal,
                                JooqBind.intVal)
                )
                .bind(entities.stream()
                        .map(ArticleEntity::getBulkInsertParams)
                        .toArray(Object[][]::new));

        int[] results = batch.execute();
        log.info("{}", results);
    }

    /**
     * select * from article
     * @return
     */
    public List<AuthorEntity> findAll() {
        return q.select()
                .from(ARTICLE)
                .fetchInto(AuthorEntity.class);
    }

    /**
     * select 부분 메서드로 분리
     * selectAuthorJoinArticle()
     * select `author`.`id` as `authorId`,
     *        `article`.`id` as `articleId`,
     *        `author`.`first_name`,
     *        `author`.`last_name`,
     *        `article`.`title`,
     *        `article`.`description`
     * from `author`
     * join `article`
     * on `author`.`id` = `article`.`author_id`
     * @return
     */
    public List<AuthorArticleJoinDto> findAllJoinArticleAndAuthor() {
         return selectAuthorJoinArticle()
                .fetchInto(AuthorArticleJoinDto.class);
    }

    /**
     * + where `article`.`id` = ?
     * @param articleId
     * @return
     */
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

    /**
     * Dynamic Query with if condition
     * select `author`.`id` as `authorId`,
     *        `article`.`id` as `articleId`,
     *        `author`.`first_name`,
     *        `author`.`last_name`,
     *        `article`.`title`,
     *        `article`.`description`
     * from `author`
     *      join `article`
     *      on `author`.`id` = `article`.`author_id`
     * where
     *      (true
     *      and `author`.`first_name` like ?
     *      and `author`.`last_name` like ?)
     *
     * searchDto 값이 null 이 아니면 and 가 붙는다.
     * @param searchDto
     * @return
     */
    public List<AuthorArticleJoinDto> searchDynamicQuery(SearchDto searchDto) {
        return selectAuthorJoinArticle()
                .where(searchCondition(searchDto))
                .fetchInto(AuthorArticleJoinDto.class);
    }

    public Condition searchCondition(SearchDto searchDto) {
        Condition condition = DSL.trueCondition();

        if (searchDto.hasFirstNameSearch()) {
            condition = condition.and(AUTHOR.FIRST_NAME.like("%" + searchDto.getFirstName() + "%"));
        }
        if (searchDto.hasLastNameSearch()) {
            condition = condition.and(AUTHOR.LAST_NAME.like("%" + searchDto.getLastName() + "%"));
        }
        if (searchDto.hasTitleSearch()) {
            condition = condition.and(ARTICLE.TITLE.like("%" + searchDto.getTitle() + "%"));
        }
        return condition;
    }

    /**
     * dynamic query with function
     * select `author`.`id` as `authorId`,
     *        `article`.`id` as `articleId`,
     *        `author`.`first_name`,
     *        `author`.`last_name`,
     *        `article`.`title`,
     *        `article`.`description`
     * from `author`
     * join `article`
     * on `author`.`id` = `article`.`author_id`
     * where (true and true and true)
     *
     * 다 null 인 경우는 true 로 나오게..
     * @param searchDto
     * @return
     */
    public List<AuthorArticleJoinDto> searchDynamicFunction(SearchDto searchDto) {
        return selectAuthorJoinArticle()
                //여기서 위에처럼 한번에 다 null인 경우에 처리 할 수 없나..
                //열라 기네... 여기서 일단 끝..
                .where(
                        Arrays.asList(
                                whereAuthor((Author author) -> searchDto.hasFirstNameSearch()
                                                ? author.FIRST_NAME.like("%" + searchDto.getFirstName() + "%")
                                                : DSL.trueCondition()),
                                whereAuthor((Author author) -> searchDto.hasLastNameSearch()
                                                ? author.LAST_NAME.like("%" + searchDto.getLastName() + "%")
                                                : DSL.trueCondition()),
                                whereArticle((Article article) -> searchDto.hasTitleSearch()
                                                ? article.TITLE.like("%" + searchDto.getTitle() + "%")
                                                : DSL.trueCondition()))
//                        searchCondition(
//                                (Author author) -> searchDto.hasFirstNameSearch() ? author.FIRST_NAME.like("%" + searchDto.getFirstName() + "%") : DSL.trueCondition(),
//                                (Author author) -> searchDto.hasLastNameSearch() ? author.LAST_NAME.like("%" + searchDto.getLastName() + "%") : DSL.trueCondition()
//                        )
                )
                .fetchInto(AuthorArticleJoinDto.class);
    }

    private Condition whereAuthor(Function<Author, Condition> where) {
        return where.apply(AUTHOR);
    }

    private Condition whereArticle(Function<Article, Condition> where) {
        return where.apply(ARTICLE);
    }

    public List<Condition> searchCondition(Function<Author, Condition> ... where) {
        return Arrays.stream(where)
                .map(f -> f.apply(AUTHOR))
                .collect(Collectors.toList());
    }

    /**
     * 아 T로 Author, Article 다 받으려했는데 잘 안됨 ㅜㅜ 실력이 없다..
     * function... T...
     */
//    public <T> Condition searchWhere(Function<T, Condition> where) {
//        return Arrays.stream(where)
//                .map(f -> f.apply())
//                .collect(Collectors.toList());
//    }

}
