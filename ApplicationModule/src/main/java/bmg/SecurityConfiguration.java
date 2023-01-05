package bmg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO: add authorization
        // specify authorization requirements for each request path
        http.authorizeHttpRequests().anyRequest().permitAll();

        return http.build();
    }
}
