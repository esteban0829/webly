package com.webClipBoard.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.OAS_30)
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.webClipBoard.controller"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())

    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("web clip board")
            .description("swagger config")
            .version("1.0")
            .build()
    }
}