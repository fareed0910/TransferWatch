package com.example.transferwatch;

public interface RepositoryCallback<T> {

    void onSuccess(T result);

    void onError(Throwable throwable);
}