package com.noxus.draven.flow;

import com.noxus.draven.flow.config.AppDispatcherServletConfiguration;
import com.noxus.draven.flow.config.ApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@Import({
        ApplicationConfiguration.class,
        AppDispatcherServletConfiguration.class
})
@SpringBootApplication(
        //scanBasePackages = {"com.noxus.draven.flow.config"},
        exclude = {SecurityAutoConfiguration.class})
public class FlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowApplication.class, args);
    }

}
