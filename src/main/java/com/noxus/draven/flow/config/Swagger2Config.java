package com.noxus.draven.flow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 描述:swagger配置类
 *
 * @author draven
 * @date 2020/06/27
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //自行修改为自己的包路径
                .apis(RequestHandlerSelectors.basePackage("com.noxus.draven.flow.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("工作流微服务API文档")
                .description("draven服务项目")
                //服务条款网址
                .termsOfServiceUrl("*")
                .version("1.0")
                .contact(new Contact("*", "*", "*"))
                .build();
    }
}

