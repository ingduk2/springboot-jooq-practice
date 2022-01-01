package com.hello.jooq.mybatis.vo;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorVO {

    private int id;
    private String firstName;
    private String lastName;

    @Builder
    public AuthorVO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
