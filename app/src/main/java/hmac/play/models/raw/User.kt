package hmac.play.models.raw

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("address") val address: Address?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("website") val website: String?,
    @SerializedName("company") val company: Company?
)

data class GeoCoords(
    @SerializedName("lat") val lat: String?,
    @SerializedName("lng") val lng: String?
)

data class Address(
    @SerializedName("street") val street: String?,
    @SerializedName("suite") val suite: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("zipcode") val zipcode: String?,
    @SerializedName("geo") val geo: GeoCoords?
)

data class Company(
    @SerializedName("name") val name: String?,
    @SerializedName("catchPhrase") val catchPhrase: String?,
    @SerializedName("bs") val bs: String?
)

