package com.hello.jooq.example.author.repository;

import com.hello.jooq.example.author.dto.AuthorDto;
import com.hello.jooq.example.author.dto.AuthorEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.hello.jooq.jooqgen.tables.Author.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JooqAuthorRepository {

    private final DSLContext dslContext;

    public int insertVALUES(AuthorDto authorDto) {
        return dslContext.insertInto(AUTHOR,
                        AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .values(authorDto.getFirstName(), authorDto.getLastName())
                .execute();
    }

    public AuthorDto insertVALUES_RETURNING(AuthorDto authorDto) {
        return dslContext.insertInto(AUTHOR,
                        AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .values(authorDto.getFirstName(), authorDto.getLastName())
                .returning()
                .fetchOne()
                .into(AuthorDto.class);
    }

    public AuthorDto select(int id) {
        return dslContext.select()
                .from(AUTHOR)
                .where(AUTHOR.ID.eq(id))
                .fetchOneInto(AuthorDto.class);
    }

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
     * 쿼리 여러번 날아감. preparedStatement
     */
    public void bulkInsert() {

        List<Query> queries = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            queries.add(dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                    .values("F5", "L5"));
        }

        dslContext.batch(queries).execute();
    }

    /**
     * 한방 쿼리. mybatis foreach 같이 만들어주는듯
     */
    public void bulkInsertSingle() {
        BatchBindStep batch = dslContext.batch(
                dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                        .values((String) null, null));
        for (int i = 0; i < 10000; i++) {
            batch.bind("F" + i, "L" + i);
        }
        batch.execute();
//        dslContext.batch(dslContext.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME).values((String) null, null))
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .bind("F2", "F3")
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .bind("F1", "F2")
//                .execute();
    }

    public List<AuthorDto> selectAll() {
        return dslContext.select()
                .from(AUTHOR)
                .fetchInto(AuthorDto.class);
    }

    public int insertOnDuplicate(AuthorEntity entity) {
        return dslContext.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.LAST_NAME, AUTHOR.READ_COUNT)
                .values(entity.getId(), entity.getLastName(), entity.getReadCount())
                .onDuplicateKeyUpdate()
                .set(AUTHOR.READ_COUNT, AUTHOR.READ_COUNT.plus(entity.getReadCount()))
                .execute();
    }

    public void bulkInsertOnDuplicate(List<AuthorEntity> entities) {
        BatchBindStep batch = dslContext.batch(
                dslContext.insertInto(AUTHOR,
                                AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.READ_COUNT)
                        .values((Integer) null, null, null, null)
                        .onDuplicateKeyUpdate()
                        .set(AUTHOR.READ_COUNT, AUTHOR.READ_COUNT.plus((Number) null))
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

    public void bulkInsertOnDuplicateStreamParam(List<AuthorEntity> entities) {
        Query query = dslContext.insertInto(AUTHOR,
                        AUTHOR.ID,
                        AUTHOR.FIRST_NAME,
                        AUTHOR.LAST_NAME,
                        AUTHOR.READ_COUNT)
                .values((Integer) null,
                        null,
                        null,
                        null)
                .onDuplicateKeyUpdate()
                .set(AUTHOR.READ_COUNT, AUTHOR.READ_COUNT.plus((Number) null));

        BatchBindStep batch = dslContext
                .batch(query)
                .bind(entities.stream()
                        .map(AuthorEntity::getBulkUpsertBatchParams)
                        .toArray(Object[][]::new));

        batch.execute();
    }

    public List<AuthorEntity> selectReturnEntities() {
        return dslContext.select()
                .from(AUTHOR)
                .fetchInto(AuthorEntity.class);
    }

}
