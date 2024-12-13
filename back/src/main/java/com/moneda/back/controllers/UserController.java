package com.moneda.back.controllers;

import com.moneda.back.dto.*;
import com.moneda.back.services.BankAccountService;
import com.moneda.back.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @Operation(summary = "Obtiene todos los Usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios activos",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/selectCombo")
    public ResponseEntity<Map<String, Object>> listUsers(){
        return userService.listUsers();
    }
    @Operation(summary = "Registra un nuevo Usuario")
    @ApiResponse(responseCode = "200", description = "Usuario registrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody CreateUserDto createUserDto, BindingResult result){
        return userService.saveUser(createUserDto, result);
    }
    @Operation(summary = "Actualiza una Cuenta Banco por Id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Integer id, @RequestBody UpdateUserDto updateUserDto, BindingResult result){
        return userService.updateUser(id, updateUserDto, result);
    }
    @Operation(summary = "Elimina un Usuario por Id (Eliminación Logica)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id){
        return userService.deleteUser(id);
    }
    @Operation(summary = "Obtiene un Usuario por Id")
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Integer id){
        return userService.getUserById(id);
    }
}
