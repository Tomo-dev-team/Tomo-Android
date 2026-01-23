package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MoimsApi {

    @GET("/public/moims/{moimId}")
    fun getMoimDetails(
        @Path("moimId") moimId: Int
    ): Call<BaseResponse<MoimDetails>>

    @GET("/public/moims/my")
    fun getMyMoims(): Call<BaseResponse<List<MoimListDTO>>>

    @GET("/public/moims/all")
    fun getAllPublicMoims(): Call<BaseResponse<List<MoimListDTO>>>

    @POST("/public/moims")
    fun postMoim(
        @Body body: CreateMoimDTO
    ): Call<BaseResponse<CreateMoimDTO>>

    @DELETE("/public/moims/{moim_id}")
    fun deleteMoim(
        @Path("moim_id") moimId: Int
    ): Call<BaseResponse<Unit>>

    @GET("/public/promises/moim")
    fun getMoimPromises(
        @Query("moimTitle") moimTitle: String
    ): Call<BaseResponse<List<MoimPromiseDTO>>>
}


val MoimsApiService: MoimsApi by lazy {
    ApiClient.create(MoimsApi::class.java)
}
