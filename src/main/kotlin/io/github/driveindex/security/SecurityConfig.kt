package io.github.driveindex.security

import io.github.driveindex.core.ConfigManager
import io.github.driveindex.security.handler.IAccessDeniedHandler
import io.github.driveindex.security.filter.JwtTokenAuthenticationFilter
import io.github.driveindex.security.filter.PasswordOnlyAuthenticationProcessingFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * @author sgpublic
 * @Date 2022/8/3 19:44
 */
@Configuration
class SecurityConfig(
        private val password: PasswordOnlyAuthenticationProcessingFilter,
        private val jwt: JwtTokenAuthenticationFilter,
        private val accessDeniedHandler: IAccessDeniedHandler,
        private val entryPoint: IAuthenticationEntryPoint
) {
    @Bean
    fun filterChain(http: HttpSecurity, environment: Environment): SecurityFilterChain {
        if (ConfigManager.Debug) {
            if (environment.getProperty("spring.h2.console.enabled", Boolean::class.java, false)) {
                // 若开启 h2-console 则允许 iframe
                http.headers().frameOptions().disable()
            }
            registerCorsConfiguration("/api/**", CorsConfiguration().apply {
                allowedHeaders = listOf(CorsConfiguration.ALL)
                allowedMethods = listOf(CorsConfiguration.ALL)
                allowedOrigins = listOf(CorsConfiguration.ALL)
            })
        } else {
            registerCorsConfiguration("/api/admin/**", CorsConfiguration().apply {
                allowedHeaders = listOf(Header)
                allowedMethods = listOf(HttpMethod.GET.name(), HttpMethod.POST.name())
            })
            updateDownloadCors()
        }
        http.cors().configurationSource(corsConfigurationSource)
        http.csrf().disable()
        http.httpBasic().disable()
        http.addFilterBefore(password, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(jwt, PasswordOnlyAuthenticationProcessingFilter::class.java)
        http.authorizeHttpRequests()
            .requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)
            .anyRequest().permitAll()
        http.exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(entryPoint)
        return http.build()
    }

    companion object {
        const val JWT_TAG = "tag"
        const val Header = "Authorization"

        const val ROLE_ADMIN = "ADMIN"
        val AUTH_ADMIN = listOf(SimpleGrantedAuthority("ROLE_$ROLE_ADMIN"))

        private val corsConfigurationSource: UrlBasedCorsConfigurationSource by lazy {
            UrlBasedCorsConfigurationSource()
        }
        private fun registerCorsConfiguration(pattern: String, conf: CorsConfiguration): Companion {
            corsConfigurationSource.registerCorsConfiguration(pattern, conf)
            return this
        }

        fun updateDownloadCors() {
            registerCorsConfiguration("/api/download", CorsConfiguration().apply {
                allowedMethods = listOf(HttpMethod.GET.name())
                allowedOrigins = ConfigManager.getCorsOrigins()
            })
        }
    }
}