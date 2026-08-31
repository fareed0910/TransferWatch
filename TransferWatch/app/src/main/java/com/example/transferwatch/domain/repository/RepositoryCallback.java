package com.example.transferwatch.domain.repository;

public interface RepositoryCallback<T> {

    void onSuccess(T result);

    void onError(Throwable throwable);
}