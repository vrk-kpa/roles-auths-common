package fi.vm.kapa.rova.rest.exception;

public enum ExceptionType implements ErrorCode {

    MISSING_PARAMETER(101),
    MATCHING_SERVICE_NOT_FOUND(102),
    DUPLICATE_SERVICE_IDENTIFIER(103),
    USER_UNKNOWN(104),
    DUPLICATE_RULESET_TYPE(105),
    NOT_AUTHORIZED(106),
    MATCHING_RULESET_NOT_FOUND(107),
    OTHER_EXCEPTION(199);

    ExceptionType(int number) {
        this.number = number;
    }

    int number;

    @Override
    public int getCodeNumber() {
        return number;
    }
}
