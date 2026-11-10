
package com.beverlly.app.util

import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object OrderEmailService {

    private const val SCRIPT_URL =
        "https://script.google.com/macros/s/AKfycbz9PxmRcLUAewuqYxk6KIYa2cXh9lI1Lk1kBxglHnsQ1zQir9y5MPIdR02RrYIemwVv/exec"

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun sendOrderConfirmation(
        orderId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            onResult(false, "Please sign in before requesting your receipt.")
            return
        }

        user.getIdToken(true)
            .addOnSuccessListener { tokenResult ->

                val idToken = tokenResult.token

                if (idToken.isNullOrBlank()) {
                    onResult(false, "Could not verify your account.")
                    return@addOnSuccessListener
                }

                executor.execute {

                    var connection: HttpURLConnection? = null

                    try {
                        connection = URL(SCRIPT_URL)
                            .openConnection() as HttpURLConnection

                        connection.requestMethod = "POST"
                        connection.connectTimeout = 20000
                        connection.readTimeout = 30000
                        connection.doOutput = true
                        connection.instanceFollowRedirects = false

                        connection.setRequestProperty(
                            "Content-Type",
                            "application/json; charset=UTF-8"
                        )

                        val payload = JSONObject().apply {
                            put("idToken", idToken)
                            put("orderId", orderId)
                        }

                        connection.outputStream.use {
                            it.write(payload.toString().toByteArray(Charsets.UTF_8))
                        }

                        var responseCode = connection.responseCode

                        // Handle Google Apps Script redirect
                        if (responseCode in 300..399) {

                            val redirectUrl =
                                connection.getHeaderField("Location")
                                    ?: throw Exception(
                                        "Email service redirect was missing."
                                    )

                            connection.disconnect()

                            connection = URL(redirectUrl)
                                .openConnection() as HttpURLConnection

                            connection.requestMethod = "GET"
                            connection.connectTimeout = 20000
                            connection.readTimeout = 30000

                            responseCode = connection.responseCode
                        }

                        val responseStream =
                            if (responseCode in 200..299) {
                                connection.inputStream
                            } else {
                                connection.errorStream
                            }

                        val responseText = responseStream
                            ?.bufferedReader()
                            ?.use { it.readText() }
                            ?: throw Exception(
                                "Email service returned an empty response."
                            )

                        val response = JSONObject(responseText)

                        val success = response.optBoolean("success", false)

                        val message = response.optString(
                            "message",
                            "Email service did not confirm sending."
                        )

                        mainHandler.post {
                            onResult(success, message)
                        }

                    } catch (e: Exception) {

                        mainHandler.post {
                            onResult(
                                false,
                                e.message ?: "Could not send the order email."
                            )
                        }

                    } finally {
                        connection?.disconnect()
                    }
                }
            }
            .addOnFailureListener { error ->

                onResult(
                    false,
                    error.message ?: "Could not verify your Firebase account."
                )
            }
    }
}
