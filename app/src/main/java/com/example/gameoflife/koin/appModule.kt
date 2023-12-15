package com.example.gameoflife.koin

import com.example.gameoflife.viewmodel.MainScreenViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        MainScreenViewModel(
            defaultDispatcher = Dispatchers.Default
        )
    }
}