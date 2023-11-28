package com.example.customer.config;


import com.example.library.repository.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class CustomerConfiguration {

    private final CustomerRepository customerRepository;

    public CustomerConfiguration(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Bean
    public UserDetailsService userDetailsService(){

        return new CustomerServiceConfig(customerRepository);
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http    .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/css/**","/imgs/**","/js/**","/fonts/**","/sass/**").permitAll()
                                .requestMatchers("/**","/byCategory/**").permitAll()
                                .requestMatchers("/customer/**").hasAuthority("CUSTOMER")

                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/do-login")
                                .defaultSuccessUrl("/index", true)
                                .failureUrl("/login?error")
                                .permitAll()
                )
                .logout(
                        logout -> logout
                                .clearAuthentication(true)
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .invalidateHttpSession(true)
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                );
        return http.build();
}
}
