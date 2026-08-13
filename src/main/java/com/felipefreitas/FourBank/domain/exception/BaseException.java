package com.felipefreitas.FourBank.domain.exception;

import com.felipefreitas.FourBank.domain.enums.ErrorEnum;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public BaseException(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMessage());

        this.errorEnum = errorEnum;
    }

}
