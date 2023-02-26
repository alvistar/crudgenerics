package com.thealvistar.crudgenerics.autoconfigure

import com.thealvistar.crudgenerics.utils.CustomOperationCustomizer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.context.annotation.Bean

// Not used today, but it can be used to customize the router function mapping order
// @AutoConfiguration
// @AutoConfigureBefore(WebMvcAutoConfiguration::class)
// class CrudAutoconfiguration : WebMvcConfigurationSupport() {
//    override fun routerFunctionMapping(
//        conversionService: FormattingConversionService,
//        resourceUrlProvider: ResourceUrlProvider,
//    ): RouterFunctionMapping {
//        return super.routerFunctionMapping(conversionService, resourceUrlProvider)
//            .apply { order = -1 }
//    }
// }

@AutoConfiguration(before = [WebMvcAutoConfiguration::class])
class CrudAutoconfiguration {
    @Bean
    fun operationCustomizer() = CustomOperationCustomizer()
}
