package com.enn.ionic.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UtilsBean(
    @SerializedName("hqwModelVOList", alternate = ["models"])
    var hqwModelVOList: List<HqwModelVO>,
    val iconUrl1: String,
    val iconUrl2: Any,
    val iconUrl3: Any,
    val linkUrl: Any,
    val modelId: Int,
    val modelName: String,
    val sort: Int,
    val modelCount: Int
)


data class HqwModelVO(
    val iconUrl1: String?,
    val iconUrl2: String?,
    val iconUrl3: String?,
    var linkUrl: String?,
    val modelId: Int,
    val modelName: String?,
    val sort: Int
) : Parcelable {
    var isEdit: Boolean = false

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(iconUrl1)
        parcel.writeString(iconUrl2)
        parcel.writeString(iconUrl3)
        parcel.writeString(linkUrl)
        parcel.writeInt(modelId)
        parcel.writeString(modelName)
        parcel.writeInt(sort)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HqwModelVO> {
        override fun createFromParcel(parcel: Parcel): HqwModelVO {
            return HqwModelVO(parcel)
        }

        override fun newArray(size: Int): Array<HqwModelVO?> {
            return arrayOfNulls(size)
        }
    }
}
