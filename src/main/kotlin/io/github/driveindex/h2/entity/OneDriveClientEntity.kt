package io.github.driveindex.h2.entity

import com.google.gson.annotations.SerializedName
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "client_onedrive")
data class OneDriveClientEntity(
    @Id
    @Column(name = "client_id")
    val id: UUID,

    @Column(name = "azure_client_id")
    val clientId: String,

    @Column(name = "azure_client_secret")
    var clientSecret: String,

    @Column(name = "azure_client_tenant")
    val tenantId: String = "common",

    @Column(name = "azure_client_endpoint")
    val endPoint: EndPoint = EndPoint.Global,
) {
    enum class EndPoint(
        val Portal: String,
        val Login: String,
    ) {
        @SerializedName("global")
        Global(
            "https://portal.azure.com",
            "https://login.microsoftonline.com"
        ),
        @SerializedName("us")
        US(
            "https://portal.azure.us",
            "https://login.microsoftonline.us"
        ),
        @SerializedName("cn")
        CN(
            "https://portal.azure.cn",
            "https://login.chinacloudapi.cn"
        );
    }
}