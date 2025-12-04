package com.markoala.tomoandroid.data.api

import com.google.gson.annotations.SerializedName
import com.markoala.tomoandroid.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapGeocodeApi {
    @GET("map-geocode/v2/geocode")
    suspend fun geocode(
        @Query("query") query: String,
        @Query("coordinate") coordinate: String? = null,
        @Query("filter") filter: String? = null,
        @Query("language") language: String? = "kor",
        @Query("page") page: Int? = 1,
        @Query("count") count: Int? = 10
    ): GeocodeResponse

    @GET("map-place/v1/search")
    suspend fun localSearch(
        @Query("query") query: String,
        @Query("coordinate") coordinate: String? = null,
        @Query("page") page: Int? = 1,
        @Query("count") count: Int? = 20,
        @Query("lang") language: String? = "ko"
    ): LocalSearchResponse
}

object NaverMapGeocodeClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("x-ncp-apigw-api-key-id", BuildConfig.NAVER_MAP_CLIENT_ID)
            .addHeader("x-ncp-apigw-api-key", BuildConfig.NAVER_MAP_CLIENT_SECRET)
            .addHeader("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://maps.apigw.ntruss.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: NaverMapGeocodeApi = retrofit.create(NaverMapGeocodeApi::class.java)
}

interface NaverLocalSearchApi {
    @GET("v1/search/local.json")
    suspend fun searchPlaces(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int = 5
    ): LocalSearchResult
}

object NaverLocalSearchClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Naver-Client-Id", BuildConfig.NAVER_MAP_CLIENT_ID)
            .addHeader("X-Naver-Client-Secret", BuildConfig.NAVER_MAP_CLIENT_SECRET)
            .build()
        chain.proceed(request)
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://openapi.naver.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: NaverLocalSearchApi = retrofit.create(NaverLocalSearchApi::class.java)
}

data class GeocodeResponse(
    val status: String?,
    val meta: GeocodeMeta?,
    val addresses: List<GeocodeAddress>?,
    @SerializedName("errorMessage") val errorMessage: String?
)

data class GeocodeMeta(
    val totalCount: Int?,
    val page: Int?,
    val count: Int?
)

data class GeocodeAddress(
    val name: String? = null,
    val roadAddress: String?,
    val jibunAddress: String?,
    val englishAddress: String?,
    val addressElements: List<GeocodeAddressElement>?,
    val x: String?,
    val y: String?,
    val distance: Double?
)

data class GeocodeAddressElement(
    @SerializedName("types") val types: List<String>?,
    val longName: String?,
    val shortName: String?,
    val code: String?
)

data class LocalSearchResponse(
    val status: String?,
    val meta: GeocodeMeta?,
    val places: List<LocalPlace>?,
    @SerializedName("errorMessage") val errorMessage: String?
)

data class LocalPlace(
    val name: String?,
    @SerializedName("road_address") val roadAddress: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("jibun_address") val jibunAddress: String?,
    @SerializedName("english_address") val englishAddress: String?,
    val category: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    val x: String?,
    val y: String?,
    val distance: Double?
) {
    fun toGeocodeAddress(): GeocodeAddress = GeocodeAddress(
        name = name,
        roadAddress = roadAddress ?: name,
        jibunAddress = jibunAddress ?: address ?: roadAddress ?: name,
        englishAddress = englishAddress ?: name,
        addressElements = null,
        x = x,
        y = y,
        distance = distance
    )
}

data class LocalSearchResult(
    val lastBuildDate: String?,
    val total: Int?,
    val start: Int?,
    val display: Int?,
    val items: List<LocalSearchItem>?
)

data class LocalSearchItem(
    val title: String?,
    val link: String?,
    val category: String?,
    val description: String?,
    val telephone: String?,
    val address: String?,
    @SerializedName("roadAddress") val roadAddress: String?,
    val mapx: String?,
    val mapy: String?
) {
    fun toGeocodeAddress(): GeocodeAddress = GeocodeAddress(
        name = title?.stripHtmlTags(),
        roadAddress = roadAddress ?: title?.stripHtmlTags(),
        jibunAddress = address ?: roadAddress ?: title?.stripHtmlTags(),
        englishAddress = null,
        addressElements = null,
        x = mapx,
        y = mapy,
        distance = null
    )
}

private fun String.stripHtmlTags(): String = this
    .replace("<b>", "", ignoreCase = true)
    .replace("</b>", "", ignoreCase = true)
