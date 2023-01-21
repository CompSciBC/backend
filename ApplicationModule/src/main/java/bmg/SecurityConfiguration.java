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

        // cross site request forgery
        // all methods resulting in data change (POST, DELETE, etc.) are blocked by default
        // by spring security; ignore for selected paths
        http.csrf().ignoringRequestMatchers("/api/**");

        return http.build();
    }
}
