package com.hello.jooq.example.author.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchDto {

    private String firstName;
    private String lastName;
    private String title;

    @Builder
    public SearchDto(String firstName, String lastName, String title) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
    }

    public boolean hasFirstNameSearch() {
        return StringUtils.hasLength(this.firstName);
    }

    public boolean hasLastNameSearch() {
        return StringUtils.hasLength(this.lastName);
    }

    public boolean hasTitleSearch() {
        return StringUtils.hasLength(this.title);
    }
}
