package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.data.service.API_CONNECT_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import java.text.SimpleDateFormat
import java.util.*

/**
 * 실패 시 : STATUS CODE != 200, 아래와 같은 JSON이 넘어옴.
{
"timestamp": "2021-02-17T05:31:45.125+00:00",
"status": 409,
"error": "Conflict",
"message": "",
"path": "/register"
}
 */

data class ErrorResponse(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("status") val status: Int,
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("path") val path: String
)

/** 웹에서 받은 ErrorResponse의 경우, message가 영어로 간단하게 넘어오므로 해당 message를 그대로 Toast에서 쓸 수 없는 문제가 있다.
 *  따라서 이를 statusCode를 기반으로 한 번 변환하는 함수를 추가했다. (statusCode와 message가 1:1 대응됨.)
 *
 *  409의 경우 원래 Conflict 오류이지만, 여기서는 register 시 이미 등록된 유저일 경우에만 409 오류가 발생하기 때문에 Already Registered로 간주했다.
 */
fun makeErrorResponseFromStatusCode(status: Int, path: String): ErrorResponse {
    val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    return when(status) {
        204 -> ErrorResponse(currentDate, status, "No Content", App()
            .context().getString(R.string.err_msg_204), path)
        400 -> ErrorResponse(currentDate, status, "Bad Request", App()
            .context().getString(R.string.err_msg_400), path)
        401 -> ErrorResponse(currentDate, status, "Unauthorized", App()
            .context().getString(R.string.err_msg_401), path)
        403 -> ErrorResponse(currentDate, status, "Forbidden", App()
            .context().getString(R.string.err_msg_403), path)
        404 -> ErrorResponse(currentDate, status, "Not Found", App()
            .context().getString(R.string.err_msg_404), path)
        406 -> ErrorResponse(currentDate, status, "Not Acceptable", App()
            .context().getString(R.string.err_msg_406), path)
        408 -> ErrorResponse(currentDate, status, "Request Timeout", App()
            .context().getString(R.string.err_msg_408), path)
        409 -> ErrorResponse(currentDate, status, "Already Registered", App()
            .context().getString(R.string.err_msg_409), path)
        410 -> ErrorResponse(currentDate, status, "Gone", App()
            .context().getString(R.string.err_msg_410), path)
        500 -> ErrorResponse(currentDate, status, "Internal Server Error", App()
            .context().getString(R.string.err_msg_500), path)
        502 -> ErrorResponse(currentDate, status, "Bad Gateway", App()
            .context().getString(R.string.err_msg_502), path)
        503 -> ErrorResponse(currentDate, status, "Service Temporarily Unavailable", App()
            .context().getString(R.string.err_msg_503), path)
        504 -> ErrorResponse(currentDate, status, "Gateway Timeout", App()
            .context().getString(R.string.err_msg_504), path)
        NO_INTERNET_ERROR_CODE -> ErrorResponse(currentDate, status, "No Internet", App()
            .context().getString(R.string.err_msg_NO_INTERNET_ERROR_CODE), path)
        API_CONNECT_ERROR_CODE -> ErrorResponse(currentDate, status, "No Internet", App()
            .context().getString(R.string.err_msg_API_CONNECT_ERROR_CODE), path)
        else -> ErrorResponse(currentDate, status, "Undefined Error", App()
            .context().getString(R.string.err_msg_else), path)
    }
}

fun makeErrorResponseFromMessage(msg: String?, path: String): ErrorResponse {
    val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    return if(msg != null)
        ErrorResponse(currentDate, API_CONNECT_ERROR_CODE, msg!!, msg!!, path)
    else
        ErrorResponse(currentDate, API_CONNECT_ERROR_CODE, "Undefined Error", App()
            .context().getString(R.string.err_msg_else), path)
}

fun makeCustomErrorResponse(status: Int, msg: String, path: String): ErrorResponse {
    return ErrorResponse(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()), status, msg, msg, path)
}