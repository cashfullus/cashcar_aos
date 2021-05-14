package com.cashfulus.cashcarplus.util

const val GENDER_MALE = "1"
const val GENDER_FEMALE = "2"

object UserManager {
    /** 최초 회원가입 시 받는 정보들 */
    var isLogined: Boolean = false
    var email: String? = null
    var jwtToken: String? = null
    var userId: Int? = null

    /** 이후 추가로 받는 정보들 */
    var alarm: Int? = null
    var callNumber: String? = null
    var dateBirth: String? = null
    var gender: String? = null
    var marketing: Int? = null
    var nickName: String? = null
    var name: String? = null
    var profileImage: String? = null

    /** 미션 상태 저장 */
    var hasMission: Boolean = false
}