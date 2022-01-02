package com.hello.jooq.example.author.repository;

import com.hello.jooq.example.author.dto.AuthorDto;
import com.hello.jooq.example.author.dto.AuthorEntity;
import com.hello.jooq.example.bind.JooqBind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hello.jooq.jooqgen.tables.Author.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JooqAuthorRepository {

    private final DSLContext dslContext;

    /**
     * insert into `author` (`first_name`, `last_name`) values (?, ?)
     * @param authorDto
     * @return
     */
    public int insertVALUES(AuthorDto authorDto) {
        return dslContext.insertInto(AUTHOR,
                        AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .values(authorDto.getFirstName(), authorDto.getLastName())
                .execute();
    }

    /**
     * insert into `author` (`first_name`, `last_name`) values (?, ?)
     * and Return inserted value
     * @param authorDto
     * @return
     */
    public AuthorDto insertVALUES_RETURNING(AuthorDto authorDto) {
        return dslContext.insertInto(AUTHOR,
                        AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .values(authorDto.getFirstName(), authorDto.getLastName())
                .returning()
                .fetchOne()
                .into(AuthorDto.class);
    }

    /**
     * select * from author where id = ?
     * @param id
     * @return
     */
    public AuthorDto select(int id) {
        return dslContext.select()
                .from(AUTHOR)
                .where(AUTHOR.ID.eq(id))
                .fetchOneInto(AuthorDto.class);
    }

    /**
     * batch query multiple
     *
     * Executing batch query    : insert into `author` (`first_name`, `last_name`) values ('F1', 'L1')
     * Executing batch query    : insert into `author` (`first_name`, `last_name`) values ('F2', 'L2')
     * Executing batch query    : insert into `author` (`first_name`, `last_name`) values ('F3', 'L3')
     * Executing batch query    : insert into `author` (`first_name`, `last_name`) values ('F4', 'L4')
     * Executing batch query    : insert into `author` (`first_name`, `last_name`) values ('F5', 'L5')
     */
    public void batchInsert() {
        dslContext.batch(
                dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                        .values("F1", "L1"),
                dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                        .values("F2", "L2"),
                dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                        .values("F3", "L3"),
                dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                        .values("F4", "L4"),
                dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                        .values("F5", "L5")
        ).execute();
    }

    /**
     * batch query multiple
     * 쿼리 여러번 날아감
     * batch execute return 이 int[] 를 줌.
     * [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, ....]
     */
    public int bulkInsert() {

        List<Query> queries = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            queries.add(dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                    .values("F5", "L5"));
        }

        int[] execute = dslContext.batch(queries).execute();
        return Arrays.stream(execute).sum();
    }

    /**
     * batch query single
     * 한방쿼리, mybatis foreach 같이 만들어주는듯
     * @return
     */
    public int bulkInsertSingle() {
        BatchBindStep batch = dslContext.batch(
                dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                        .values((String) null, null));
        for (int i = 0; i < 10000; i++) {
            batch.bind("F" + i, "L" + i);
        }
        return Arrays.stream(batch.execute()).sum();
    }

    /**
     * select `author`.`id`, `author`.`first_name`, `author`.`last_name`, `author`.`read_count` from `author`
     * @return AuthorDto type
     */
    public List<AuthorDto> selectAll() {
        return dslContext.select()
                .from(AUTHOR)
                .fetchInto(AuthorDto.class);
    }

    /**
     * mysql insertOnDuplicate
     * insert into `author`
     *      (`id`, `first_name`, `last_name`, `read_count`)
     *      values (?, ?, ?, ?)
     *      on duplicate key update
     *      `author`.`read_count` = (`author`.`read_count` + ?)
     * @param entity
     * @return
     */
    public int insertOnDuplicate(AuthorEntity entity) {
        return dslContext.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.LAST_NAME, AUTHOR.READ_COUNT)
                .values(entity.getId(), entity.getLastName(), entity.getReadCount())
                .onDuplicateKeyUpdate()
                .set(AUTHOR.READ_COUNT, AUTHOR.READ_COUNT.plus(entity.getReadCount()))
                .execute();
    }

    /**
     * mysql bulk insertOnDuplicate bind
     * insert into `author`
     *      (`id`, `first_name`, `last_name`, `read_count`)
     *      values (?, ?, ?, ?)
     *      on duplicate key update
     *      `author`.`read_count` = (`author`.`read_count` + ?)
     * @param entities
     */
    public void bulkInsertOnDuplicate(List<AuthorEntity> entities) {
        BatchBindStep batch = dslContext.batch(
                dslContext.insertInto(AUTHOR,
                                AUTHOR.ID,
                                AUTHOR.FIRST_NAME,
                                AUTHOR.LAST_NAME,
                                AUTHOR.READ_COUNT)
                        .values(JooqBind.intVal,
                                JooqBind.strVal,
                                JooqBind.strVal,
                                JooqBind.intVal)
                        .onDuplicateKeyUpdate()
                        .set(AUTHOR.READ_COUNT, AUTHOR.READ_COUNT.plus(JooqBind.intVal))
        );

        entities.forEach(e -> batch.bind(e.getBulkUpsertBatchParams()));

        // fail
        // map으로 하는거는 안되지...?
//        entities.forEach(e -> {
//            log.info("e {}", e);
//            log.info("e.bindMap : {}", e.bindMap(e));
//            batch.bind(e.bindMap(e));
//        });

        // success
//        for (AuthorEntity entity : entities) {
//            log.info("entity : {}", entity);
//            batch.bind(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getReadCount(), entity.getReadCount());
//        }

        batch.execute();
    }

    /**
     * mysql bulkInsert with stream bind
     * insert into `author`
     *      (`id`, `first_name`, `last_name`, `read_count`)
     *      values (?, ?, ?, ?)
     *      on duplicate key update
     *      `author`.`read_count` = (`author`.`read_count` + ?)
     * @param entities
     */
    public void bulkInsertOnDuplicateStreamParam(List<AuthorEntity> entities) {
        BatchBindStep batch = dslContext.batch(
                dslContext.insertInto(AUTHOR,
                                AUTHOR.ID,
                                AUTHOR.FIRST_NAME,
                                AUTHOR.LAST_NAME,
                                AUTHOR.READ_COUNT)
                        .values(JooqBind.intVal,
                                JooqBind.strVal,
                                JooqBind.strVal,
                                JooqBind.intVal)
                        .onDuplicateKeyUpdate()
                        .set(AUTHOR.READ_COUNT, AUTHOR.READ_COUNT.plus(JooqBind.intVal))
                )
                .bind(entities.stream()
                        .map(AuthorEntity::getBulkUpsertBatchParams)
                        .toArray(Object[][]::new));

        batch.execute();
    }

    /**
     * select All
     * select `author`.`id`, `author`.`first_name`, `author`.`last_name`, `author`.`read_count` from `author`
     * @return AuthorEntity type
     */
    public List<AuthorEntity> selectReturnEntities() {
        return dslContext.select()
                .from(AUTHOR)
                    .fetchInto(AuthorEntity.class);
    }

}
