package com.zencare.network.interceptor

import com.zencare.network.ApiResponse
import com.zencare.model.dto.ChatHistoryResponse
import com.zencare.model.dto.ChatMessage
import com.zencare.model.dto.ChatSession
import com.zencare.model.dto.ChatSessionListResponse
import com.zencare.model.dto.HealthMetricType
import com.zencare.model.dto.HealthRecord
import com.zencare.model.dto.HealthRecordListResponse
import com.zencare.model.dto.HealthStats
import com.zencare.model.dto.MessageRole
import com.zencare.model.dto.MessageType
import com.zencare.model.dto.Product
import com.zencare.model.dto.ProductCategory
import com.zencare.model.dto.ProductListResponse
import com.zencare.model.dto.SendMessageRequest
import com.zencare.model.dto.SendMessageResponse
import com.zencare.model.dto.TrendDirection
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.time.Instant
import java.util.UUID

class MockInterceptor : Interceptor {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val mediaType = "application/json".toMediaType()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val responseJson = when {
            // Consultation
            path.contains("/chat/sessions") && method == "GET" -> mockSessionList()
            path.contains("/chat/send") && method == "POST" -> mockSendMessage(request)
            path.contains("/chat/history") && method == "GET" -> mockChatHistory()
            // Health
            path.contains("/health/records") && method == "GET" -> mockHealthRecords()
            path.contains("/health/stats") && method == "GET" -> mockHealthStats()
            // Shop
            path.contains("/shop/products") && method == "GET" -> mockProductList()
            // Default empty success
            else -> json.encodeToString(ApiResponse(code = 0, message = "mock success", data = null))
        }

        return Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(request)
            .body(responseJson.toResponseBody(mediaType))
            .build()
    }

    private fun mockSessionList(): String {
        val sessions = listOf(
            ChatSession("s1", "健康咨询", "你好，有什么可以帮您的？", Instant.now().toString()),
            ChatSession("s2", "用药指导", "请详细描述您的症状", Instant.now().minusSeconds(3600).toString())
        )
        return json.encodeToString(ApiResponse(data = ChatSessionListResponse(sessions, false)))
    }

    private fun mockSendMessage(request: okhttp3.Request): String {
        val now = Instant.now().toString()
        val response = SendMessageResponse(UUID.randomUUID().toString(), "s1")
        return json.encodeToString(ApiResponse(data = response))
    }

    private fun mockChatHistory(): String {
        val now = Instant.now()
        val messages = listOf(
            ChatMessage("m1", "s1", MessageRole.USER, MessageType.TEXT, "我最近经常头痛，怎么办？", null, now.minusSeconds(300).toString()),
            ChatMessage("m2", "s1", MessageRole.ASSISTANT, MessageType.TEXT, "头痛的原因有很多，请问您头痛的频率和持续时间是怎样的？", null, now.minusSeconds(280).toString()),
            ChatMessage("m3", "s1", MessageRole.USER, MessageType.TEXT, "大概一周两三次，每次持续两三个小时", null, now.minusSeconds(260).toString()),
            ChatMessage("m4", "s1", MessageRole.ASSISTANT, MessageType.TEXT, "根据您的描述，建议您注意休息，保持规律作息。如果症状持续，建议到医院做进一步检查。", null, now.minusSeconds(240).toString())
        )
        return json.encodeToString(ApiResponse(data = ChatHistoryResponse(messages, false)))
    }

    private fun mockHealthRecords(): String {
        val now = Instant.now()
        val records = listOf(
            HealthRecord("r1", HealthMetricType.BLOOD_PRESSURE_SYSTOLIC, 120.0, "mmHg", now.minusSeconds(86400).toString()),
            HealthRecord("r2", HealthMetricType.BLOOD_PRESSURE_DIASTOLIC, 80.0, "mmHg", now.minusSeconds(86400).toString()),
            HealthRecord("r3", HealthMetricType.BLOOD_SUGAR, 5.6, "mmol/L", now.minusSeconds(86400).toString(), "空腹"),
            HealthRecord("r4", HealthMetricType.WEIGHT, 68.5, "kg", now.minusSeconds(172800).toString())
        )
        return json.encodeToString(ApiResponse(data = HealthRecordListResponse(records, false)))
    }

    private fun mockHealthStats(): String {
        val stats = HealthStats(
            HealthMetricType.BLOOD_SUGAR, 5.6, "mmol/L", TrendDirection.STABLE, 4.8, 6.2, 5.4
        )
        return json.encodeToString(ApiResponse(data = stats))
    }

    private fun mockProductList(): String {
        val products = listOf(
            Product("p1", "阿莫西林胶囊", "适用于敏感菌所致的感染", 15.80, "", ProductCategory.MEDICINE, 100, true),
            Product("p2", "电子血压计", "智能语音播报，大屏显示", 299.00, "", ProductCategory.MEDICAL_DEVICE, 50),
            Product("p3", "维生素C片", "增强免疫力，美白抗氧化", 45.00, "", ProductCategory.HEALTH_PRODUCT, 200),
            Product("p4", "感冒灵颗粒", "缓解感冒引起的头痛、发热", 22.50, "", ProductCategory.MEDICINE, 150)
        )
        return json.encodeToString(ApiResponse(data = ProductListResponse(products, false, 4)))
    }
}
