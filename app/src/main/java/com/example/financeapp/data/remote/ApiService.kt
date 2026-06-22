package com.example.financeapp.data.remote

import retrofit2.http.*

interface ApiService {

    // ================= AUTH =================

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
        @Field("full_name") fullName: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("auth/change_password.php")
    suspend fun changePassword(
        @Field("user_id") userId: Int,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String
    ): Map<String, Any>


    // ================= ACCOUNTS =================

    @FormUrlEncoded
    @POST("account/add.php")
    suspend fun addAccount(
        @Field("user_id") userId: Int,
        @Field("account_name") accountName: String,
        @Field("account_type") accountType: String, // Đã đồng bộ cột account_type ENUM của MySQL
        @Field("balance") balance: Long
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("account/update.php")
    suspend fun updateAccount(
        @Field("account_id") accountId: Int,
        @Field("account_name") accountName: String,
        @Field("account_type") accountType: String, // Đã đồng bộ cập nhật loại tài khoản
        @Field("balance") balance: Long
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("account/delete.php")
    suspend fun deleteAccount(
        @Field("account_id") accountId: Int
    ): Map<String, Any>


    // ================= CATEGORIES =================

    @FormUrlEncoded
    @POST("category/add.php")
    suspend fun addCategory(
        @Field("user_id") userId: Int,
        @Field("category_name") categoryName: String,
        @Field("type") type: String,
        @Field("color") color: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("category/delete.php")
    suspend fun deleteCategory(
        @Field("category_id") categoryId: Int
    ): Map<String, Any>


    // ================= TRANSACTIONS =================

    @GET("transaction/get.php")
    suspend fun getTransactions(
        @Query("user_id") userId: Int
    ): List<Map<String, Any>> // Hoặc thay bằng List<TransactionResponse> nếu bạn đã định nghĩa class

    @FormUrlEncoded
    @POST("transaction/add.php")
    suspend fun addTransaction(
        @Field("user_id") userId: Int,
        @Field("account_id") accountId: Int,
        @Field("category_id") categoryId: Int,
        @Field("note") note: String?,
        @Field("amount") amount: Long,
        @Field("type") type: String,
        @Field("transaction_date") transactionDate: String // Đồng bộ định dạng chuỗi ngày yyyy-MM-dd
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("transaction/delete.php")
    suspend fun deleteTransaction(
        @Field("transaction_id") transactionId: Int
    ): Map<String, Any>


    // ================= BUDGET =================

    @FormUrlEncoded
    @POST("budget/add.php")
    suspend fun addBudget(
        @Field("user_id") userId: Int,
        @Field("category_id") categoryId: Int,
        @Field("limit_amount") limitAmount: Long,
        @Field("month_year") monthYear: String
    ): Map<String, Any>


    // ================= ADMIN MANAGEMENT =================

    // 🛠️ ĐÃ THÊM: Lấy toàn bộ danh sách thành viên trên hệ thống MySQL
    @GET("admin/users.php?action=list")
    suspend fun getAllUsers(): Map<String, Any>

    // 🛠️ ĐÃ THÊM: Admin chủ động tạo tài khoản mới cho người dùng
    @FormUrlEncoded
    @POST("admin/users.php?action=add")
    suspend fun adminAddUser(
        @Field("username") username: String,
        @Field("full_name") fullName: String,
        @Field("role") role: String
    ): Map<String, Any>

    // 🛠️ ĐÃ THÊM: Admin sửa đổi thông tin hoặc nâng/hạ quyền (USER/ADMIN) của một thành viên
    @FormUrlEncoded
    @POST("admin/users.php?action=update")
    suspend fun adminUpdateUser(
        @Field("user_id") userId: Int,
        @Field("full_name") fullName: String,
        @Field("role") role: String
    ): Map<String, Any>

    // 🛠️ ĐÃ THÊM: Admin xóa vĩnh viễn tài khoản (Dọn sạch cả các bảng liên quan)
    @FormUrlEncoded
    @POST("admin/users.php?action=delete")
    suspend fun adminDeleteUser(
        @Field("user_id") userId: Int
    ): Map<String, Any>
}