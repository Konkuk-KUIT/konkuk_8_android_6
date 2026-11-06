package com.kuit.week6.di

import com.kuit.week6.data.auth.AuthRepositoryImpl
import com.kuit.week6.data.product.ProductRepositoryImpl
import com.kuit.week6.domain.auth.AuthRepository
import com.kuit.week6.domain.product.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository
}
