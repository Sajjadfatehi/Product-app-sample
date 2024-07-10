package com.example.gameapp.di

import com.example.gameapp.data.remotedatasource.ProductRemoteDataSource
import com.example.gameapp.data.remotedatasource.ProductRemoteDataSourceImpl
import com.example.gameapp.data.repository.ProductRepositoryImpl
import com.example.gameapp.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {

    @Binds
    abstract fun provideProductRemoteDataSource(dataSourceImpl: ProductRemoteDataSourceImpl): ProductRemoteDataSource

    @Binds
    abstract fun provideProductRepository(repositoryImpl: ProductRepositoryImpl): ProductRepository

    companion object {

    }
}