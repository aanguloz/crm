package com.prototype.crm.controller;

import com.prototype.crm.model.dto.AuthRespDTO;
import com.prototype.crm.model.dto.LoginReqDTO;
import com.prototype.crm.model.dto.request.UserReqDTO;
import com.prototype.crm.service.AuthenticationService;
import com.prototype.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    public final AuthenticationService authenticationService;
    public final UserService userService;

    @PostMapping("/login")
    public AuthRespDTO login(@RequestBody LoginReqDTO loginReq) {
        return authenticationService.login(loginReq.getUserName(), loginReq.getPassword());
    }

    @PostMapping("/")
    ResponseEntity<?> addUser(
            @RequestBody UserReqDTO userReqDTO
    ) {
        try {
            return new ResponseEntity<>(userService.addUser(userReqDTO), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/perfil")
    public String getPerfil() { return "¡Hola, has pasado el filtro JWT!"; }

}
