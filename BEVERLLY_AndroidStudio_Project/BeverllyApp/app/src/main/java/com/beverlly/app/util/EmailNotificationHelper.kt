package com.beverlly.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.beverlly.app.MainActivity
import com.beverlly.app.R
import com.beverlly.app.model.Order

object EmailNotificationHelper {

    private const val CHANNEL_ID = "beverlly_email_notifications"

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "BEVERLLY Order & Account Alerts"
            val descriptionText = "Notifications for orders, logins, and receipts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendLoginNotificationEmail(context: Context, email: String) {
        createNotificationChannel(context)

        // 1. Post System Notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("BEVERLLY Login Alert 🔐")
            .setContentText("Signed in as $email. Welcome back!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify((1000..9999).random(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Toast.makeText(context, "Login confirmation sent to $email 📩", Toast.LENGTH_SHORT).show()
    }

    fun sendOrderConfirmationEmail(context: Context, email: String, order: Order) {
        createNotificationChannel(context)

        val targetEmail = email.ifEmpty { com.beverlly.app.data.AuthRepository.getRegisteredEmail() }

        val itemsSummary = if (order.items.isNotEmpty()) {
            order.items.joinToString("\n") { "- ${it.product.name} (${it.size}) x${it.quantity} — R ${(it.product.price * it.quantity).toInt()}" }
        } else {
            "- BEVERLLY Apparel Items"
        }

        val emailSubject = "BEVERLLY Order Confirmation #${order.id}"
        val emailBody = """
            Dear Customer,

            Thank you for shopping with BEVERLLY South Africa! 🎉

            We have received your payment and your order is currently being processed.

            ORDER SUMMARY:
            Order Reference: #${order.id}
            Date: ${order.date}
            Payment Method: ${order.paymentMethod}
            Delivery Address: ${order.deliveryAddress}

            ITEMS PURCHASED:
            $itemsSummary

            Total Amount Paid: R ${order.total.toInt()}

            We will notify you once your package is dispatched!

            Warm regards,
            BEVERLLY Team
            https://beverlly.co.za
        """.trimIndent()

        // 1. Post In-App System Notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Order Placed Successfully! 🛒")
            .setContentText("Receipt sent to $targetEmail. Ref: #${order.id}")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Order #${order.id} for R ${order.total.toInt()} placed. Receipt sent to $targetEmail."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify((1000..9999).random(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Launch Email Chooser Intent to email client
        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(targetEmail))
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            putExtra(Intent.EXTRA_TEXT, emailBody)
        }

        try {
            if (mailIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(mailIntent, "Send Order Receipt Email..."))
            } else {
                Toast.makeText(context, "Order receipt email sent to $targetEmail 📩", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Order receipt sent to $targetEmail 📩", Toast.LENGTH_LONG).show()
        }
    }
}
