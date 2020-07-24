package org.backend.Configuration;

import org.backend.DTOs.HikeMasterUserErrorDTO;
import org.backend.DTOs.ResponseDTO;
import org.backend.Model.HikeMasterUser;
import org.backend.Service.UserService;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
//                .formLogin()
//                .loginPage("/login_page")

//                .permitAll()
//                .and()
                .authorizeRequests()
                .antMatchers("/csrf","/hike_route","/registration","/login", "/hike_route/{route_Id}","/{hikerouteId}", "/hike_route/{route_Id}/messages").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login();
//               .and()
//               .logout()
//               .invalidateHttpSession(true)
//               .clearAuthentication(true)

//               .deleteCookies("JSESSIONID")
//               .permitAll()
//                .and()
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        DozerBeanMapper dozer = new DozerBeanMapper();
        dozer.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(ResponseDTO.class, HikeMasterUserErrorDTO.class, TypeMappingOptions.mapNull(false));
            }
        });
        return dozer;
    }

    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator(new WhitespaceRule(), new UsernameRule(), new LengthRule(8, 16));
    }
    @Bean
    public UserDetails userDetailsService(HikeMasterUser hikeMasterUser) {
        UserService userService = new UserService();
        return userService.loadUserByUsername(hikeMasterUser.getUsername());
    }

}
