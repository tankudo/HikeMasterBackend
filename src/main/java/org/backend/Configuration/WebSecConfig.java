package org.backend.Configuration;

import org.dozer.DozerBeanMapper;
import org.passay.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;



@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/rest/login")
                .defaultSuccessUrl("https://www.google.hu/?hl=hu")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/csrf", "/hike_route", "/registration","/rest/login").permitAll()
                .anyRequest().authenticated()
                .and().logout().invalidateHttpSession(true)
                .clearAuthentication(true).logoutSuccessUrl("/login").deleteCookies("JSESSIONID").permitAll().and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DozerBeanMapper dozerBeanMapper () {
        return new DozerBeanMapper();
    }

    @Bean
    public PasswordValidator passwordValidator () {
        return new PasswordValidator(new WhitespaceRule(), new UsernameRule(), new LengthRule(8,16));
    }

}
