package fi.vm.kapa.rova.rest.exception;

public enum ExceptionType implements ErrorCode {

    MISSING_PARAMETER(101),
    MATCHING_SERVICE_NOT_FOUND(102),
    DUPLICATE_SERVICE_IDENTIFIER(103),
    USER_UNKNOWN(104),
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
