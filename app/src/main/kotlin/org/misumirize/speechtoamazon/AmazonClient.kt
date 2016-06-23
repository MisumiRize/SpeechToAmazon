package org.misumirize.speechtoamazon

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

class AmazonClient {

    companion object {

        val service = buildService()

        private fun buildService(): AmazonService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()

            return Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(AmazonService::class.java)
        }

        fun createCart(cart: AmazonCart): Observable<Response<ServerResponse>> {
            return service.createCart(cart)
        }
    }

    interface AmazonService {
        @POST("cart")
        fun createCart(@Body cart: AmazonCart): Observable<Response<ServerResponse>>
    }

    data class ServerResponse (
            val code: Int,
            val message: String
    )
}

data class AmazonCart (
        val item: String
)

