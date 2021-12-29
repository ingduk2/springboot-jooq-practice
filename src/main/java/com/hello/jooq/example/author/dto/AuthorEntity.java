package com.hello.jooq.example.author.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
//        return Map.of(Author.AUTHOR.ID.getName(), entity.getId(),
//                Author.AUTHOR.FIRST_NAME.getName(), entity.getFirstName(),
//                Author.AUTHOR.LAST_NAME.getName(), entity.getLastName(),
//                Author.AUTHOR.READ_COUNT.getName(), entity.getReadCount());

                return Map.of(AUTHOR.ID.getName(), this.id,
                AUTHOR.FIRST_NAME.getName(), this.firstName,
                AUTHOR.LAST_NAME.getName(), this.lastName,
                AUTHOR.READ_COUNT.getName(), this.readCount);
    }

    public Object[] batchOnDuplicate() {
        return Arrays.asList(this.id,
                this.firstName,
                this.lastName,
                this.readCount,
                this.readCount)
                .toArray();
    }
}
