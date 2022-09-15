package com.example.di

import com.example.repository.HeroRepository
import com.example.repository.HeroRepositoryImp
import org.koin.dsl.module

// single -> Singletone type
// <Herorepository> -> what type of object
// class implementation
val koinModule = module {
    single <HeroRepository>{
        HeroRepositoryImp()
    }
}