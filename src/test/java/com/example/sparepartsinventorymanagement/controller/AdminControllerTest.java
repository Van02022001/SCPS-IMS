package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AdminController.class)
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    public void whenCreateAccount_thenReturnOkStatus() throws Exception {
        CreateAccountForm form = new CreateAccountForm();
        form.setEmail("quangvanpham02022001@gmail.com");
        form.setPhone("0935182029");
        form.setFirstName("Van");
        form.setMiddleName("Quang");
        form.setLastName("Pham");
        form.setRoleName("INVENTORY_STAFF");

        when(userService.createAccount(any(CreateAccountForm.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(post("/api/v1/admin/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(form)))
                .andExpect(status().isOk());
    }





    @Test
    public void whenUsePostMethodOnGetAllUsers_thenReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }


}