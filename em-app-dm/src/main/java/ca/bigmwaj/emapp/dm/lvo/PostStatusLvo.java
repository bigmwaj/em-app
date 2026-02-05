package ca.bigmwaj.emapp.dm.lvo;

import lombok.Getter;

public enum PostStatusLvo {

    DRAFT("Brouillon"),
    APPROVED("Approuvé"),
    PUBLISHED("Publié");

    @Getter
    private final String description;

    PostStatusLvo(String description) {
        this.description = description;
    }
}
