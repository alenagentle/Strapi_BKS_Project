package ru.bcs.creditmarkt.strapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

    public ApiError(String message) {
        this.message = message;
    }
}
