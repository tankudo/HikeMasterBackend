package org.backend.Controllers;

import org.backend.DTOs.ErrorDTO;
import org.backend.DTOs.RegisterDTO;
import org.backend.DTOs.ResponseDTO;
import org.backend.DTOs.SuccessDTO;
import org.backend.Model.HikeMasterUser;
import org.backend.Service.UserService;
import org.backend.Service.ValidationService;
import org.dozer.DozerBeanMapper;
import org.passay.PasswordData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {
    
    private DozerBeanMapper mapper;
    private UserService service = new UserService();
    private PasswordEncoder encoder;
    private ValidationService validationService;
    
    @Autowired
    public UserController(DozerBeanMapper mapper, ValidationService validationService, PasswordEncoder enc,UserService service) {
        this.mapper = mapper;
        this.encoder = enc;
        this.validationService = validationService;
        this.service=service;
    }
    @PostMapping(value = "/registration")
    public ResponseDTO registration(@Valid RegisterDTO newUser, BindingResult bindingResult) {
        PasswordData passwordData = mapper.map(newUser,PasswordData.class);
        ResponseDTO passwordValidation = validationService.validatePassword(passwordData);
        boolean usernameValid = validationService.validateUsername(passwordData);
        ResponseDTO springValidation = validationService.validateSpringResults(bindingResult);
        
        if (passwordValidation instanceof SuccessDTO 
                && springValidation instanceof  SuccessDTO
                && usernameValid){
            HikeMasterUser validHikeMasterUser = mapper.map(newUser, HikeMasterUser.class);
            validHikeMasterUser.setPassword(encoder.encode(validHikeMasterUser.getPassword()));
            service.addUserToDatabase(validHikeMasterUser);
            return new SuccessDTO();
        }else {
            ErrorDTO errorDTO = new ErrorDTO();
            mapper.map(springValidation, errorDTO);
            mapper.map(passwordValidation, errorDTO);
            return errorDTO;
        }
    }

    @RequestMapping (value = "/rest/login",method = RequestMethod.POST)
    public HikeMasterUser userLogin(@RequestBody HikeMasterUser hikeMasterUser){
      return service.loginUser(hikeMasterUser.getUsername(),hikeMasterUser.getPassword());
    }
}
