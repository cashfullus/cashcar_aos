package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class DonationListResponseData(
    @SerializedName("data") val data: ArrayList<DonationListResponse>,
    @SerializedName("ongoing") val ongoing: String
)
data class DonationListResponse(
    @SerializedName("donation_organization_id") val id: Int,
    @SerializedName("donation_organization_name") val name: String,
    @SerializedName("logo_image") val logo: String,
    @SerializedName("register_time") val registerTime: String,
    @SerializedName("image_information")val imageInformation: ArrayList<DonationImagesInfo>
)
data class DonationImagesInfo(
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String
)

data class DonationRequest(
    @SerializedName("withdrawal_point") val point: Int,
    @SerializedName("is_receipt") val isReceipt: Int,
    @SerializedName("name_of_donor") val name: String
)

data class DonationResponse(
    @SerializedName("deposit") val deposit: Boolean,
    @SerializedName("ongoing") val ongoing: Boolean
)