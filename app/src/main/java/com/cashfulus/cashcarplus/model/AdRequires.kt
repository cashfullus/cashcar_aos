package com.cashfulus.cashcarplus.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MyAdResponseWrapper(
    val status: Int,
    val data: MyAdResponse,
)

data class MyAdResponse(
        @SerializedName("data") val data: MyAdResponseData
)
data class MyAdResponseData(
        @SerializedName("ad_information") val adInformation: AdInformation,
        @SerializedName("vehicle_information") val vehicleInformation: List<VehicleInformation>,
        @SerializedName("message") val message: AdMessage,
        @SerializedName("is_delete") val isDeletable: Boolean,
        @SerializedName("is_read_alarm") val isReadAlarm: Boolean
)

data class AdMessage(
        @SerializedName("is_read") val isRead: Int, //기본값 -1
        @SerializedName("message_type") val message_type: String, //기본값 ""
        @SerializedName("reason") val reason: String, //기본값 ""
        @SerializedName("reason_id") val reasonId: Int, //기본값 0
        @SerializedName("title") val title: String //기본값 ""
)
data class AdInformation(
        @SerializedName("activity_end_date") val activityEndDate: String,
        @SerializedName("mission_end_date") val missionEndDate: String,
        @SerializedName("activity_start_date") val activityStartDate: String,
        @SerializedName("ad_id") val adId: Int,
        @SerializedName("ad_mission_card_id") val adMissionCardId: Int,
        @SerializedName("ad_mission_card_user_id") val adMissionCardUserId: Int,
        @SerializedName("ad_user_apply_id") val adUserApplyId: Int,
        @SerializedName("additional_mission_success_count") val additionalMissionSuccessCount: Int,
        @SerializedName("apply_register_time") val applyRegisterTime: String,
        @SerializedName("apply_status") val applyStatus: String,
        @SerializedName("default_mission_success_count") val defaultMissionSuccessCount: Int,
        @SerializedName("thumbnail_image") val logoImage: String,
        @SerializedName("mission_status") val missionStatus: String,
        @SerializedName("mission_type") val missionType: Int,
        @SerializedName("mission_name") val missionName: String,
        @SerializedName("title") val title: String,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("ongoing_days") val ongoingDays: Int,
        @SerializedName("ongoing_day_percent") val ongoingDayPercent: Int,
        @SerializedName("point") val point: Int,
        @SerializedName("order") val order: Int
)
data class VehicleInformation(
        @SerializedName("brand") val brand: String,
        @SerializedName("car_number") val carNumber: String,
        @SerializedName("is_foreign_car") val isForeignCar: Int,
        @SerializedName("owner_relationship") val ownerRelationship: String,
        @SerializedName("register_time") val registerTime: String,
        @SerializedName("remove_time") val removeTime: String,
        @SerializedName("removed") val removed: Int,
        @SerializedName("supporters") val supporters: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("vehicle_id") val vehicleId: Int,
        @SerializedName("vehicle_model_name") val vehicleModelName: String,
        @SerializedName("year") val year: Int
)

data class AdResponseData(
    @SerializedName("data") val data: AdResponseList,
    @SerializedName("status") val status: Boolean
)
class AdResponseList: ArrayList<AdResponse>()
class AdResponse() : Parcelable {
    @SerializedName("ad_id") var adId: Int? = null
    @SerializedName("area") var area: String? = null
    @SerializedName("max_recruiting_count") var maxRecruitingCount: Int? = null
    @SerializedName("recruit_end_date") var recruitEndDate: String? = null //"2021-05-22 23:59:59",
    @SerializedName("recruit_start_date") var recruitStartDate: String? = null //"2021-04-20 00:00:00",
    @SerializedName("recruiting_count") var recruitingCount: Int? = null
    @SerializedName("title") var title: String? = null
    @SerializedName("thumbnail_image") var image: String? = null
    @SerializedName("total_point") var totalPoint: Int? = null
    @SerializedName("time_diff") var timeDiff:Int? = null

    constructor(parcel: Parcel) : this() {
        parcel.run {
            adId = readInt()
            area = readString()
            maxRecruitingCount = readInt()
            recruitEndDate = readString()
            recruitStartDate = readString()
            recruitingCount = readInt()
            title = readString()
            image = readString()
            totalPoint = readInt()
            timeDiff = readInt()
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.run {
            writeInt(this@AdResponse.adId!!)
            writeString(this@AdResponse.area)
            writeInt(this@AdResponse.maxRecruitingCount!!)
            writeString(this@AdResponse.recruitEndDate)
            writeString(this@AdResponse.recruitStartDate)
            writeInt(this@AdResponse.recruitingCount!!)
            writeString(this@AdResponse.title)
            writeString(this@AdResponse.image!!)
            writeInt(this@AdResponse.totalPoint!!)
            writeInt(this@AdResponse.timeDiff!!)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdResponse> {
        override fun createFromParcel(parcel: Parcel): AdResponse {
            return AdResponse(parcel)
        }

        override fun newArray(size: Int): Array<AdResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class AdInfoResponseData(
    @SerializedName("data") val data: AdInfoResponse,
    @SerializedName("status") val status: Boolean
)
data class AdInfoResponse(
        @SerializedName("activity_period") val activity_period: Int,
        @SerializedName("ad_id") val ad_id: Int,
        @SerializedName("area") val area: String,
        @SerializedName("back_image") val back_image: String,
        @SerializedName("back_length") val back_length: Int,
        @SerializedName("back_width") val back_width: Int,
        @SerializedName("description") val description: String,
        @SerializedName("gender") val gender: Int, //0: 무관, 1: 남자, 2: 여자
        @SerializedName("max_age_group") val max_age_group: Int, //0: 무관
        @SerializedName("min_age_group") val min_age_group: Int, //0: 무관
        @SerializedName("images") val images: List<ImageUrl>,
        @SerializedName("max_recruiting_count") val max_recruiting_count: Int,
        @SerializedName("min_distance") val min_distance: Int,
        @SerializedName("recruit_end_date") val recruit_end_date: String,
        @SerializedName("recruit_start_date") val recruit_start_date: String,
        @SerializedName("recruiting_count") val recruiting_count: Int,
        @SerializedName("side_image") val side_image: String,
        @SerializedName("side_length") val side_length: Int,
        @SerializedName("side_width") val side_width: Int,
        @SerializedName("thumbnail_image") val thumbnail_image: String,
        @SerializedName("title") val title: String,
        @SerializedName("total_point") val total_point: Int
)
data class ImageUrl(
        @SerializedName("image") val image: String
)

data class ApplyRequest(
    @SerializedName("main_address") val mainAddress: String,
    @SerializedName("detail_address") val detailAddress: String,
    @SerializedName("call_number") val callNumber: String,
    @SerializedName("name") val name: String,
)
data class ApplyResponseData(
    @SerializedName("data") val data: ApplyResponse,
    @SerializedName("status") val status: Boolean
)

data class ApplyResponse(
    @SerializedName("ad_information") val adInformation: Boolean,
    @SerializedName("already_apply") val alreadyApply: Boolean,
    @SerializedName("area") val area: Boolean,
    @SerializedName("user_information") val userInformation: Boolean,
    @SerializedName("vehicle") val vehicle: Boolean,
    @SerializedName("reject_apply") val rejectApply: Boolean
)

data class AdApplyInfoResponseData(
    @SerializedName("data") val data: AdApplyInfoResponse,
    @SerializedName("status") val status: Boolean
)
data class AdApplyInfoResponse(
    @SerializedName("ad_information") val ad_information: AdInformationShort,
    @SerializedName("user_information") val user_information: UserInformation,
    @SerializedName("vehicle_information") val vehicle_information: List<VehicleInformation>
)
data class AdInformationShort(
    @SerializedName("ad_id") val ad_id: Int,
    @SerializedName("thumbnail_image") val thumbnail_image: String,
    @SerializedName("title") val title: String,
    @SerializedName("total_point") val total_point: Int
)
data class UserInformation(
    @SerializedName("call_number") val call_number: String,
    @SerializedName("detail_address") val detail_address: String,
    @SerializedName("main_address") val main_address: String,
    @SerializedName("name") val name: String
)

data class AdMissionResponseData(
    @SerializedName("data") val data: AdMissionResponse,
    @SerializedName("status") val status: Boolean
)
data class AdMissionResponse(
    @SerializedName("ad_user_information") val adUserInformation: AdUserInformation,
    @SerializedName("day_diffs") val dayPercent: Int,
    @SerializedName("images") val images: List<ImageUrl>,
    @SerializedName("mission_information") val missionInformation: ArrayList<MissionInformation>,
)
data class AdUserInformation(
    @SerializedName("activity_end_date") val activityEndDate: String,
    @SerializedName("activity_start_date") val activityStartDate: String,
    @SerializedName("thumbnail_image") val thumbnailImage: String,
    @SerializedName("title") val title: String,
    @SerializedName("total_point") val totalPoint: Int,
    @SerializedName("day_diff") val dayDiff: Int,
    @SerializedName("cumulative_point") val cumulativePoint: Int
)
data class MissionInformation(
    @SerializedName("ad_mission_card_id") val adMissionCardId: Int,
    @SerializedName("ad_mission_card_user_id") val adMissionCardUserId: Int,
    @SerializedName("ad_user_apply_id") val adUserApplyId: Int,
    @SerializedName("additional_point") val additionalPoint: Int,
    @SerializedName("mission_end_date") val missionEndDate: String,
    @SerializedName("mission_name") val missionName: String,
    @SerializedName("mission_start_date") val missionStartDate: String,
    @SerializedName("mission_type") val missionType: Int,
    @SerializedName("order") val order: Int,
    @SerializedName("status") val status: String
)

/** View에 제공해야 하는 추가 데이터 목록 */
data class AdMissionResponseForView(
    val isMissionStart: Boolean,
    val dayPercent: Int,
    val adUserInformation: AdUserInformation,
    val images: List<ImageUrl>,
    val importantMissions: ArrayList<MissionImportant>,
    val additionalMissions: ArrayList<MissionAdditional>,
    val drivings: ArrayList<Driving>
)

/**  */
data class AdMissionPostData(
    @SerializedName("data") val data: AdMissionPost,
    @SerializedName("status") val status: Boolean,
)
data class AdMissionPost(
    @SerializedName("data_not_null") val dataNotNull: Boolean,
    @SerializedName("image_allowed") val imageAllowed: Boolean,
    @SerializedName("image_data") val imageData: Boolean,
    @SerializedName("mission_data") val missionData: Boolean,
    @SerializedName("mission_type") val missionType: Boolean,
)