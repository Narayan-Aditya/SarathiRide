package com.example.sarathi.model

import android.telecom.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers("Content-Type:application/json",
        "Authorization:AAAAHordFhc:APA91bHCMgk4EKhjTK_RZH2oFM749VcpnWsYLo3GFbXHVIyOngMcyQPlkF2zOXbER02-ofPg9J6PXeiUFzvtsj6WP2_b1-TkaMWPAbTzXELEah4RqCWI4EDuqHp4t0xww3MYx5802bRo")

    @POST("fcm/send")
    fun sendNotification(@Body notification: PushNotification): retrofit2.Call<PushNotification>
}