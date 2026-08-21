package com.felipefreitas.FourBank.exceptions;


import com.felipefreitas.FourBank.enums.ErrorEnum;
import lombok.Getter;

@Getter
public class BaseExceptions extends RuntimeException {
    private final ErrorEnum errorEnum;

    public BaseExceptions(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMessage());

        this.errorEnum = errorEnum;
    }

}
