package io.github.driveindex.configuration

import feign.Contract
import feign.Feign
import feign.codec.Decoder
import feign.codec.Encoder
import io.github.driveindex.feigh.AzureErrorDecoder

import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@Import(FeignClientsConfiguration::class, AzureErrorDecoder::class)
@Configuration
class FeignClientConfig(
    private val encoder: Encoder,
    private val decoder: Decoder,
    private val contract: Contract,
    private val errorDecoder: AzureErrorDecoder,
) {
    @Bean
    fun feignBuilder(): Feign.Builder {
        return Feign.builder().encoder(encoder).decoder(decoder)
            .contract(contract).errorDecoder(errorDecoder)
    }
}