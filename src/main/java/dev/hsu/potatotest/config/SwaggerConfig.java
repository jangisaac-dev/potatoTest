package dev.hsu.potatotest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;


/** swagger url
http://localhost:8080/swagger-ui/index.html
*/
@OpenAPIDefinition(
        info = @Info(title = "Potato Restful api",
                description = "potato test",
                version = "v1"))
@Configuration
public class SwaggerConfig {


}
