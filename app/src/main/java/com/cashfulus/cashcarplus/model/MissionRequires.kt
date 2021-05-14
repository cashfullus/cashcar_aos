package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

class MissionResponseList :ArrayList<MissionResponse>()
data class MissionResponse(
    @SerializedName("ad_id") val ad_id: Int,
    @SerializedName("area") val area: String,
    @SerializedName("max_recruiting_count") val max_recruiting_count: Int,
    @SerializedName("recruit_end_date") val recruit_end_date: String,
    @SerializedName("recruit_start_date") val recruit_start_date: String,
    @SerializedName("recruiting_count") val recruiting_count: Int,
    @SerializedName("title") val title: String,
    @SerializedName("title_image") val title_image: Int,
    @SerializedName("total_point") val total_point: Int
)

data class MyMissionResponse(
    @SerializedName("activity_period") val activity_period: Int,
    @SerializedName("ad_id") val ad_id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("title_image") val title_image: String,
    @SerializedName("total_point") val total_point: Int,
    @SerializedName("images") val images: List<ImageUrl>,

    @SerializedName("recruit_end_date") val recruit_end_date: String,
    @SerializedName("recruit_start_date") val recruit_start_date: String,
    @SerializedName("important_mission_list") val missionImportantList: ArrayList<MissionImportant>,
    @SerializedName("additional_mission_list") val missionAdditionalList: ArrayList<MissionAdditional>,
    @SerializedName("driving_list") val drivingList: ArrayList<Driving>,
    @SerializedName("all_point") val allPoint: Int,
)
data class MissionImportant(
    val status: String,
    val endDate: String,
    val missionId: Int,
    val order: Int
)
data class MissionAdditional(
    val status: String,
    val endDate: String,
    val title: String,
    val point: Int,
    val missionId: Int
)
data class Driving(
    val status: String,
    val endDate: String,
    val title: String,
    val point: Int,
    val missionId: Int
)