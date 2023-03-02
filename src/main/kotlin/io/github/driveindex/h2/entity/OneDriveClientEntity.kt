package io.github.driveindex.h2.entity

import com.google.gson.annotations.SerializedName
import feign.Feign
import io.github.driveindex.Application
import io.github.driveindex.feigh.AzureGraphClient
import io.github.driveindex.feigh.AzurePortalClient
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

    @Column(name = "create_by")
    val createBy: UUID,

    @Column(name = "create_at")
    val createAt: Long,

    @Column(name = "modify_at")
    val modifyAt: Long? = null,
) {
    enum class EndPoint(
        val LoginHosts: String,
        private val portal: String,
        private val graph: String,
    ) {
        @SerializedName("global")
        Global(
            "https://login.microsoftonline.com",
            "https://portal.azure.com",
            "https://graph.microsoft.com",
        ),
        @SerializedName("us_l4")
        US_L4(
            "https://login.microsoftonline.us",
            "https://portal.azure.us",
            "https://graph.microsoft.us",
        ),
        @SerializedName("us_l5")
        US_L5(
            "https://login.microsoftonline.us",
            "https://portal.azure.us",
            "https://dod-graph.microsoft.us",
        ),
        @SerializedName("cn")
        CN(
            "https://login.chinacloudapi.cn",
            "https://portal.azure.cn",
            "https://microsoftgraph.chinacloudapi.cn",
        );

        val Portal: AzurePortalClient by lazy {
            Application.getBean<Feign.Builder>()
                .target(AzurePortalClient::class.java, portal)
        }

        val Graph: AzureGraphClient by lazy {
            Application.getBean<Feign.Builder>()
                .target(AzureGraphClient::class.java, graph)
        }
    }
}