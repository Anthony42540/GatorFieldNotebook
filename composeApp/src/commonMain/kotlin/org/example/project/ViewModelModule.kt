package org.example.project

import org.example.project.viewModels.CollectionViewModel
import org.example.project.viewModels.FormViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::FormViewModel)
    viewModelOf(::CollectionViewModel)
}