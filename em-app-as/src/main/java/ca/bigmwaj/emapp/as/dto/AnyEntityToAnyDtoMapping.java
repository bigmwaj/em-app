package ca.bigmwaj.emapp.as.dto;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "new", constant = "false")
@Mapping(target = "editAction", constant = "NONE")
@Mapping(target = "key", expression = "java(entity.getDefaultKey())")
public @interface AnyEntityToAnyDtoMapping {}
