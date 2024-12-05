package com.moneda.back.services.Impl;

import com.moneda.back.dto.*;
import com.moneda.back.entities.BankAccount;
import com.moneda.back.entities.User;
import com.moneda.back.mappers.BankAccountMapper;
import com.moneda.back.mappers.UserMapper;
import com.moneda.back.repositories.BankAccountRepository;
import com.moneda.back.repositories.UserRepository;
import com.moneda.back.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BankAccountMapper bankAccountMapper;
    private final BankAccountRepository bankAccountRepository;
    @Override
    public ResponseEntity<Map<String, Object>> listUsers() {
        Map<String, Object> response = new HashMap<>();
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
        if(users.isEmpty()){
            response.put("message", "No hay datos");
            response.put("Users", Collections.emptyList());
        }else {
            response.put("message", "Listas Usuarios");
            response.put("Users", users);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> saveUser(CreateUserDto createUserDto, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(err->"El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("message", "Error en la validación");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }
        try{
            List<BankAccount> bankAccounts = bankAccountRepository.findAllById(createUserDto.getBankAccountIds());
            if (bankAccounts.isEmpty()) {
                throw new RuntimeException("No se encontraron cuentas bancarias para los IDs proporcionados");
            }
            User user = new User();
            user.setFirstName(createUserDto.getFirstName());
            user.setLastName_p(createUserDto.getLastName_p());
            user.setLastName_m(createUserDto.getLastName_m());
            user.setAddress(createUserDto.getAddress());
            user.setBirthDate(createUserDto.getBirthDate());
            user.setCuil(createUserDto.getCuil());
            user.setDni(createUserDto.getDni());
            user.setEmail(createUserDto.getEmail());
            user.setPassword(createUserDto.getPassword());
            user.setBankAccounts(bankAccounts);
            user.setIsActive(true);
            user.setCreatedAt(new Date());
            userRepository.save(user);

            UserDto userDto = userMapper.toUserDto(user);
            response.put("message", "Se ha creado exitosamente");
            response.put("User", userDto);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("message", "Error al registrar el usuario");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateUser(Integer id, UpdateUserDto updateUserDto, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(err->"El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("message", "Error en la validación");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            response.put("message", "Usuario con ID '" + id + "' no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        try {
            User user = existingUser.get();
            user.setFirstName(updateUserDto.getFirstName());
            user.setLastName_p(updateUserDto.getLastName_p());
            user.setLastName_m(updateUserDto.getLastName_m());
            user.setAddress(updateUserDto.getAddress());
            user.setBirthDate(updateUserDto.getBirthDate());
            user.setEmail(updateUserDto.getEmail());
            user.setPhone(updateUserDto.getPhone());
            user.setPhoto_url(updateUserDto.getPhoto_url());
            user.setLastModified(new Date()); //colocará la fecha actual
            userRepository.save(user);

            UserDto userDto = userMapper.toUserDto(user);
            response.put("message", "El usuario se ha actualizado correctamente");
            response.put("User", userDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error al actualizar el Usuario");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getUserById(Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> UserOptional = userRepository.findById(id);
            if (UserOptional.isEmpty()) {
                response.put("message", "El usuario no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("message", "Usuario no encontrado");
            response.put("User", UserOptional.get());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error al buscar al Usuario");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteUser(Integer id) {
        Map<String, Object> response = new HashMap<>();
        if(!userRepository.existsById(id)){
            response.put("message", "El usuario no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        try{
            Optional<User> existingUser = userRepository.findById(id);
            if (existingUser.isPresent()) {
                User userEntity = existingUser.get();
                userEntity.setIsActive(false);
                userRepository.save(userEntity);
                response.put("message", "El usuario se eliminó exitosamente");
                response.put("bankAccount", userEntity);
                return ResponseEntity.ok(response);
            }
        }
        catch (Exception e){
            response.put("message", "Error al eliminar el Usuario");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "Error inesperado");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getUserByBankAccount(String bankAccount) {
        return null;
    }
}
