package com.webClipBoard.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig: WebMvcConfigurer {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        super.addViewControllers(registry)
        registry.addViewController("/login").setViewName("login")
        registry.addViewController("/signup").setViewName("signup")
        registry.addViewController("/admin").setViewName("admin")
        registry.addViewController("/").setViewName("main")
        registry.addViewController("/file-upload").setViewName("file-upload")
        registry.addViewController("/hello").setViewName("hello")
    }
}