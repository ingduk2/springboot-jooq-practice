package com.hello.jooq.example.author.dto;

import lombok.*;

@Getter
//@Setter //아 불편하다..? 어라 없어도 되는건가..?
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorDto {

    private int id;
    private String firstName;
    private String lastName;

    @Builder
    public AuthorDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
