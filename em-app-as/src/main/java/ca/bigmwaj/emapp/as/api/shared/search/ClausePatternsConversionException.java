package ca.bigmwaj.emapp.as.api.shared.search;

import lombok.Getter;

public class ClausePatternsConversionException extends RuntimeException{

    @Getter
    private final String message;
    public ClausePatternsConversionException(String message){
        this.message = message;
    }
}
