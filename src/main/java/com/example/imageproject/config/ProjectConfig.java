package hu.progmasters.moovsmart.config;

import com.cloudinary.Cloudinary;
import com.paypal.base.rest.APIContext;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


import java.util.*;

@Configuration
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "basicAuth",
        scheme = "basic")
public class ProjectConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Estate trade APP")
                        .version("1.0.1")
                        .description("This is the backand of an Estate trade application."));
    }


    @Bean
    public Cloudinary getCloudinary() {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dvuarfxuu");
        config.put("api_key", "978623321346782");
        config.put("api_secret", "qSu7IhNadONu8C27hVObBJKKuOk");
        config.put("secure", true);
        return new Cloudinary(config);
    }


    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value("${spring.openweathermap.api.key}")
    private String openWeatherMapApiKey;

    public String getOpenWeatherMapApiKey() {
        return openWeatherMapApiKey;
    }

    @Value("${spring.exchangerates.api.key}")
    private String exchangeratesApiKey;


    public String getExchangeratesApiKey() {
        return exchangeratesApiKey;
    }

}
