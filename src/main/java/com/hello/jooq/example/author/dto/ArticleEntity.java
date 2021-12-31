package com.hello.jooq.example.author.dto;

import lombok.*;

import java.util.Arrays;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleEntity {

    private int id;
    private String title;
    private String description;
    private int authorId;

    @Builder
    public ArticleEntity(int id, String title, String description, int authorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
    }

    public Object[] getBulkInsertParams() {
        return Arrays.asList(
                this.title,
                this.description,
                this.authorId)
                .toArray();
    }
}
