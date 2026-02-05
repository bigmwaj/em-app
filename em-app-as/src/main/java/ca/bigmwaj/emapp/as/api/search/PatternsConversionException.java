package ca.bigmwaj.emapp.as.api.search;

import lombok.Getter;

public class PatternsConversionException extends RuntimeException{

    @Getter
    private final String message;
    public PatternsConversionException(String message){
        this.message = message;
    }
}
