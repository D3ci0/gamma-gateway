package com.gamma.gateway.model.dto.email;

import java.util.Objects;

public class Result {
    private String resultCode;
    private String description;

    public Result() {
    }

    public Result(String resultCode, String description) {
        this.resultCode = resultCode;
        this.description = description;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equals(resultCode, result.resultCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultCode);
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultCode='" + resultCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
