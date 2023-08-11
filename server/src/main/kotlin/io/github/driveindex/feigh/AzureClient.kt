package io.github.driveindex.feigh

import feign.Headers
import io.github.driveindex.dto.feign.AzureGraphDtoV2_Me
import io.github.driveindex.dto.feign.AzureGraphDtoV2_Me_Drive_Root_Delta
import io.github.driveindex.dto.feign.AzurePortalDtoV1_Token
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*


/**
 * @author sgpublic
 * @Date 2022/8/8 15:46
 */
interface AzureAuthClient {
    @PostMapping("/{tenant}/oauth2/v2.0/token", consumes = [
        MediaType.APPLICATION_FORM_URLENCODED_VALUE
    ])
    fun getToken(
        @PathVariable("tenant") tenant: String,
        @RequestBody body: TokenGetDto,
    ): AzurePortalDtoV1_Token

    data class TokenGetDto(
        @RequestParam("code")
        val code: String,
        @RequestParam("client_secret")
        val clientSecret: String,
        @RequestParam("scope")
        val scope: String = Scope.joinToString("%20"),
        @RequestParam("grant_type")
        val grantType: String = "authorization_code",
    )

    @PostMapping("/{tenant}/oauth2/v2.0/token", consumes = [
        MediaType.APPLICATION_FORM_URLENCODED_VALUE
    ])
    fun refreshToken(
        @PathVariable("tenant") tenant: String,
        @RequestBody body: TokenRefreshDto,
    ): AzurePortalDtoV1_Token

    data class TokenRefreshDto(
        @RequestParam("client_id")
        val clientId: String,
        @RequestParam("client_secret")
        val clientSecret: String,
        @RequestParam("refresh_token")
        val refreshToken: String,
        @RequestParam("grant_type")
        val grantType: String = "refresh_token",
    )

    companion object {
        @JvmStatic
        val Scope: Array<String> = arrayOf(
            "User.Read",
            "Files.ReadWrite",
            "Files.ReadWrite.All",
            "offline_access",
            "Sites.ReadWrite.All",
        )
    }
}

fun AzureAuthClient.getToken(
    tenant: String,
    code: String,
    clientSecret: String,
): AzurePortalDtoV1_Token {
    return getToken(tenant, AzureAuthClient.TokenGetDto(code, clientSecret))
}
fun AzureAuthClient.refreshToken(
    tenant: String,
    clientId: String,
    clientSecret: String,
    refreshToken: String,
): AzurePortalDtoV1_Token {
    return refreshToken(tenant, AzureAuthClient.TokenRefreshDto(clientId, clientSecret, refreshToken))
}

interface AzureGraphClient {
    @PostMapping("/v1.0/me")
    fun Me(@RequestHeader("Authorization") token: String): AzureGraphDtoV2_Me

    @GetMapping("/v1.0/me/drive/root/delta")
    fun Me_Drive_Root_Delta(
        @RequestHeader("Authorization") token: String,
        @RequestParam("token") deltaToken: String,
    ): AzureGraphDtoV2_Me_Drive_Root_Delta
}