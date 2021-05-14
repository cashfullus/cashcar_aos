package com.cashfulus.cashcarplus.model

/** ApiResponse : API 응답을 저장하는 객체.
 * isSucceed가 true인 경우 contents에는 객체로 변환된 API의 응답값이 들어오면서 error는 null이 되고,
 * isSucceed가 false인 경우 contents에는 null이 되고, 대신 error에 ErrorResponse 객체가 들어온다.
 *
 * API 응답은 우선 String으로 받은 뒤, Repository에서 ApiResponse로 변환해서 처리한다.
 * isSucceed 값은 API의 statusCode가 200인지의 여부를 가지고 판단한다.
 */
data class ApiResponse<T>(
    val isSucceed: Boolean,
    val contents: T?,
    val error: ErrorResponse?
)