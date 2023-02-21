package com.webClipBoard.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig: WebMvcConfigurer {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        super.addViewControllers(registry)
        //registry.addViewController("/login").setViewName("pages/login")
        registry.addViewController("/signup").setViewName("pages/signup")
        registry.addViewController("/admin").setViewName("pages/admin")
        registry.addViewController("/").setViewName("pages/main")
        registry.addViewController("/file-upload").setViewName("pages/file-upload")
        registry.addViewController("/hello").setViewName("pages/hello")
        registry.addViewController("/new-project").setViewName("pages/projects/new-project")
        registry.addViewController("/project-page").setViewName("pages/projects/project-page")
    }
}