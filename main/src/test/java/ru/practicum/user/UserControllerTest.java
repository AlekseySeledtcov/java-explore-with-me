package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.user.Service.UserService;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private NewUserRequest newUserRequest;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setup() {
        newUserRequest = new NewUserRequest();
        newUserRequest.setName("UserName");
        newUserRequest.setEmail("User@mail.com");

        userDto1 = new UserDto("User1@mail.ru", 1L, "User1");
        userDto2 = new UserDto("User2@mail.ru", 2L, "User2");

    }

    @SneakyThrows
    @Test
    void getUser_WithoutIds_ShouldReturnAllUsers() {
        List<UserDto> mockUsers = List.of(userDto1, userDto2);

        when(userService.getUsers(null, 0, 10))
                .thenReturn(mockUsers);

        mockMvc.perform(get("/admin/users")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$.[0].name").value("User1"))
                .andExpect(jsonPath("$.[0].email").value("User1@mail.ru"))
                .andExpect(jsonPath("$.[1].id").value(2L))
                .andExpect(jsonPath("$.[1].name").value("User2"))
                .andExpect(jsonPath("$.[1].email").value("User2@mail.ru"));
        verify(userService, times(1)).getUsers(null, 0, 10);
    }

    @SneakyThrows
    @Test
    void getUser_WithIds_ShouldFilterByIds() {
        List<Long> ids = List.of(1L, 4L);
        List<UserDto> mockUsers = List.of(userDto1);

        when(userService.getUsers(ids, 0, 10)).thenReturn(mockUsers);

        mockMvc.perform(get("/admin/users")
                        .param("ids", "1", "4")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$.[0].name").value("User1"))
                .andExpect(jsonPath("$.[0].email").value("User1@mail.ru"));
        verify(userService, times(1)).getUsers(ids, 0, 10);
    }
}