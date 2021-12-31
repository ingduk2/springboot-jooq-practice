package com.hello.jooq.example.author.dto;

import lombok.*;

import java.util.Arrays;
import java.util.Map;

import static com.hello.jooq.jooqgen.tables.Author.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorEntity {

    private int id;
    private String firstName;
    private String lastName;
    private int readCount;

    @Builder
    public AuthorEntity(int id, String firstName, String lastName, int readCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.readCount = readCount;
    }


    public Map<String, Object> bindMap(AuthorEntity entity) {
        return Map.of(AUTHOR.ID.getName(), entity.getId(),
                AUTHOR.FIRST_NAME.getName(), entity.getFirstName(),
                AUTHOR.LAST_NAME.getName(), entity.getLastName(),
                AUTHOR.READ_COUNT.getName(), entity.getReadCount());
    }

    public Object[] getBulkUpsertBatchParams() {
        return Arrays.asList(this.id,
                this.firstName,
                this.lastName,
                this.readCount,
                this.readCount)
                .toArray();
    }

}
