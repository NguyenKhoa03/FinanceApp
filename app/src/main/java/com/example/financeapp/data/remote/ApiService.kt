package com.example.financeapp.data.remote

import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("auth/login.php")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("auth/register.php")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("fullname") fullname: String
    ): Map<String, Any>

    @GET("transaction/get.php")
    suspend fun getTransactions(
        @Query("user_id") userId: Int
    ): List<TransactionResponse>

    // 🛠️ SỬA: Thêm đầy đủ title, category, date
    @FormUrlEncoded
    @POST("transaction/add.php")
    suspend fun addTransaction(
        @Field("user_id") userId: Int,
        @Field("title") title: String,
        @Field("amount") amount: Double,
        @Field("category") category: String,
        @Field("type") type: String,
        @Field("date") date: String
    ): Map<String, Any>

    // 🛠️ SỬA: Thêm đầy đủ title, category, date
    @FormUrlEncoded
    @POST("transaction/update.php")
    suspend fun updateTransaction(
        @Field("transaction_id") id: Int,
        @Field("title") title: String,
        @Field("amount") amount: Double,
        @Field("category") category: String,
        @Field("type") type: String,
        @Field("date") date: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("transaction/delete.php")
    suspend fun deleteTransaction(
        @Field("transaction_id") id: Int
    ): Map<String, Any>
}