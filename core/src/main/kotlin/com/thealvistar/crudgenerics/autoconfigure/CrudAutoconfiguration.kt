package com.thealvistar.crudgenerics.autoconfigure

import org.springframework.format.support.FormattingConversionService
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.function.support.RouterFunctionMapping
import org.springframework.web.servlet.resource.ResourceUrlProvider

// @AutoConfiguration
// @AutoConfigureBefore(WebMvcAutoConfiguration::class)
class CrudAutoconfiguration : WebMvcConfigurationSupport() {
    override fun routerFunctionMapping(
        conversionService: FormattingConversionService,
        resourceUrlProvider: ResourceUrlProvider
    ): RouterFunctionMapping {
        return super.routerFunctionMapping(conversionService, resourceUrlProvider)
            .apply { order = -1 }
    }
}
