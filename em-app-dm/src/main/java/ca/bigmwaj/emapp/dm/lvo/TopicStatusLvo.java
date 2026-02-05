package ca.bigmwaj.emapp.dm.lvo;

import lombok.Getter;

public enum TopicStatusLvo {
    DRAFT("Brouillon"),
    APPROVED("Approuvé"),
    SCENARIOS_READY("Prompt IA des scénarios pret");

    @Getter
    private final String description;

    TopicStatusLvo(String description) {
        this.description = description;
    }
}
