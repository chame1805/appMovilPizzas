package com.chame.myapplication.core.di

import com.chame.myapplication.core.network.PizzeriaApi
import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.feacture.administrador.data.datasource.AdminOrdersApi
import com.chame.myapplication.feacture.auth.data.datasource.AuthApi
import com.chame.myapplication.feacturecocina.data.datasource.CocineroApi
import com.chame.myapplication.feacture.register.data.datasource.RegisterApi
import com.chame.myapplication.feactures.Admin.data.datasource.AdminApi
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.remote.WaiterOrderApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = sessionManager.token
                val request = if (token.isNotEmpty()) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://44.212.148.188:8000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePizzeriaApi(retrofit: Retrofit): PizzeriaApi = retrofit.create(PizzeriaApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideRegisterApi(retrofit: Retrofit): RegisterApi = retrofit.create(RegisterApi::class.java)

    @Provides
    @Singleton
    fun provideAdminApi(retrofit: Retrofit): AdminApi = retrofit.create(AdminApi::class.java)

    @Provides
    @Singleton
    fun provideCocineroApi(retrofit: Retrofit): CocineroApi = retrofit.create(CocineroApi::class.java)

    @Provides
    @Singleton
    fun provideAdminOrdersApi(retrofit: Retrofit): AdminOrdersApi = retrofit.create(AdminOrdersApi::class.java)

    @Provides
    @Singleton
    fun provideWaiterOrderApi(retrofit: Retrofit): WaiterOrderApi = retrofit.create(WaiterOrderApi::class.java)
}
