package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MoimsApi {

    @GET("/public/moims/{moimId}")
    fun getMoimDetails(
        @Path("moimId") moimId: Int
    ): Call<BaseResponse<MoimDetails>>

    @GET("/public/moims/my")
    fun getMyMoims(): Call<BaseResponse<List<MoimListDTO>>>

    @POST("/public/moims")
    fun postMoim(
        @Body body: CreateMoimDTO
    ): Call<BaseResponse<CreateMoimDTO>>

    @DELETE("/public/moims/{moim_id}")
    fun deleteMoim(
        @Path("moim_id") moimId: Int
    ): Call<BaseResponse<Unit>>
}


val MoimsApiService: MoimsApi by lazy {
    ApiClient.create(MoimsApi::class.java)
}
