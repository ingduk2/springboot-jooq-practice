package com.hello.jooq.example.author.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorArticleJoinDto {

    private int authorId;
    private int articleId;
    private String firstName;
    private String lastName;
    private String title;
    private String description;

    @Builder
    public AuthorArticleJoinDto(int authorId, int articleId, String firstName, String lastName, String title, String description) {
        this.authorId = authorId;
        this.articleId = articleId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.description = description;
    }
}
