package com.example.transferwatch;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TeamSearchViewModelFactory
        implements ViewModelProvider.Factory {

    private final FootballRepository repository;

    public TeamSearchViewModelFactory(
            FootballRepository repository
    ) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(
            @NonNull Class<T> modelClass
    ) {
        if (!modelClass.isAssignableFrom(
                TeamSearchViewModel.class
        )) {
            throw new IllegalArgumentException(
                    "Unknown ViewModel class"
            );
        }

        return (T) new TeamSearchViewModel(repository);
    }
}