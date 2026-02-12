package ca.bigmwaj.emapp.as.converter.shared;

import lombok.Getter;

public class ClausePatternsConversionException extends RuntimeException{

    @Getter
    private final String message;
    public ClausePatternsConversionException(String message){
        this.message = message;
    }
}
