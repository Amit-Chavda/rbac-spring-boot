package com.springsecurity.rbac.springsecurityrbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springsecurity.rbac.springsecurityrbac.annotation.WithMockCustomUser;
import com.springsecurity.rbac.springsecurityrbac.controller.*;
import com.springsecurity.rbac.springsecurityrbac.dto.*;
import com.springsecurity.rbac.springsecurityrbac.entity.JwtUserRequest;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PAGE;
import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class SpringSecurityRbacApplicationTests {


    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions resultActions;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final String paramName = "pageCode";

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Nested
    class JwtUserControllerTests {

        private JwtUserRequest jwtUserRequest;

        @BeforeEach
        void setup() {
            jwtUserRequest = new JwtUserRequest();
            jwtUserRequest.setEmail("admin@test.com");
            jwtUserRequest.setPassword("admin");
        }

        /**
         * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
         */
        @Test
        void testGenerateToken() throws Exception {
            // Arrange
            requestBuilder = get("/token")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(jwtUserRequest));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            //Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", notNullValue()))
                    .andExpect(jsonPath("$.expiry", notNullValue()));

            //save token for other tests
                /*.andDo(result -> {
                    String jsonResponseString = result.getResponse().getContentAsString();
                    SpringSecurityRbacApplicationTests.jwtToken += ((JwtUserResponse) mapper.readValue(jsonResponseString, JwtUserResponse.class)).getToken();
                });*/
        }

        /**
         * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
         */
        @Test
        void testGenerateToken2() throws Exception {
            // Arrange
            jwtUserRequest.setPassword("123");
            requestBuilder = get("/token")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(jwtUserRequest));


            // Act
            resultActions = mockMvc.perform(requestBuilder);

            //Assert
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(notNullValue()))
                    .andExpect(content().string(containsString("Bad credentials")));
        }


        /**
         * Method under test: {@link JwtUserController#generateToken(JwtUserRequest)}
         */
        @Test
        void testGenerateToken3() throws Exception {
            // Arrange
            jwtUserRequest.setEmail("admin@testing.com");
            requestBuilder = get("/token")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(jwtUserRequest));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            //Assert
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
                    .andExpect(result -> assertEquals("Bad credentials", Objects.requireNonNull(result.getResolvedException()).getMessage() + ""));
        }

    }
    //Class under test: PageController

    @Nested
    @WithMockCustomUser
    class PageControllerTests {

        private PageDto pageDto;

        @BeforeEach
        void setup() {
            pageDto = new PageDto();
            pageDto.setName("Some Page Name");
        }

        /**
         * Method under test: {@link PageController#createPage(PageDto)}
         */
        @Test
        @Order(1)
        void testCreatePage() throws Exception {
            // Arrange
            requestBuilder = post("/page/create").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(pageDto));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(pageDto)));

        }

        /**
         * Method under test: {@link PageController#findAllPages()}
         */
        @Test
        void testFindAllPages() throws Exception {
            // Arrange
            requestBuilder = get("/page/findAll").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", greaterThan(0)));
        }

        /**
         * Method under test: {@link PageController#findPageByName(String)}
         */
        @Test
        void testFindPageByName() throws Exception {
            // Arrange
            String name = "ROLE";
            pageDto.setName(name);
            requestBuilder = get("/page/findByName").param("name", name)
                    .param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(pageDto)));

        }

        /**
         * Method under test: {@link PageController#findPageByName(String)}
         */
        @Test
        void testFindPageByName2() throws Exception {
            // Arrange
            String name = "SOME PAGE";
            pageDto.setName(name);
            requestBuilder = get("/page/findByName").param("name", name)
                    .param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found")));

        }

        /**
         * Method under test: {@link PageController#removePage(PageDto)}
         */
        @Test
        void testRemovePage() throws Exception {
            // Arrange
            pageDto.setName("SOME ROLE");

            //insert some page before to delete it later
            requestBuilder = post("/page/create").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(pageDto));
            mockMvc.perform(requestBuilder);

            requestBuilder = delete("/page/remove").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(pageDto));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(pageDto)));
        }

        /**
         * Method under test: {@link PageController#removePage(PageDto)}
         */
        @Test
        void testRemovePage2() throws Exception {
            // Arrange
            pageDto.setName("SOME ROLE");

            requestBuilder = delete("/page/remove").param(paramName, PAGE.ROLE)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(pageDto));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }

    }

    /*
        Class under test: PrivilegeController
     */

    @Nested
    @WithMockCustomUser
    class PrivilegeControllerTests {

        private PrivilegeDto privilegeDto;

        @BeforeEach
        void setup() {
            privilegeDto = new PrivilegeDto();
            privilegeDto.setName("Some Privilege Name");
        }

        /**
         * Method under test: {@link PrivilegeController#createPrivilege(PrivilegeDto)}
         */

        @Test
        void testCreatePrivilege1() throws Exception {
            // Arrange
            requestBuilder = post("/privilege/create").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(privilegeDto));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(privilegeDto)));
        }

        /**
         * Method under test: {@link PrivilegeController#findAllPrivileges()}
         */

        @Test
        void testFindAllPrivileges() throws Exception {
            // Arrange
            requestBuilder = get("/privilege/findAll").param(paramName, PAGE.ROLE);

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", greaterThan(0)))
                    .andExpect(jsonPath("$[0].name", notNullValue()));
        }


        /**
         * Method under test: {@link PrivilegeController#findPrivilegeByName(String)}
         */

        @Test
        void testFindPrivilegeByName() throws Exception {
            // Arrange
            String name = "READ";
            PrivilegeDto privilegeDto1 = new PrivilegeDto(name);
            requestBuilder = get("/privilege/findByName").param(paramName, PAGE.ROLE).param("name", name);
            // Act
            resultActions = mockMvc.perform(requestBuilder);
            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", notNullValue()))
                    .andExpect(content().string(mapper.writeValueAsString(privilegeDto1)));

        }


        /**
         * Method under test: {@link PrivilegeController#findPrivilegeByName(String)}
         */

        @Test
        void testFindPrivilegeByName2() throws Exception {
            // Arrange
            String name = "UNKNOWN";
            requestBuilder = get("/privilege/findByName").param(paramName, PAGE.ROLE).param("name", name);
            // Act
            resultActions = mockMvc.perform(requestBuilder);
            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }


        /**
         * Method under test: {@link PrivilegeController#removePrivilege(PrivilegeDto)}
         */

        @Test
        void testRemovePrivilege_ExistingPrivilege() throws Exception {
            // Arrange
            requestBuilder = delete("/privilege/remove").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(privilegeDto));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(privilegeDto)));

        }

        /**
         * Method under test: {@link PrivilegeController#removePrivilege(PrivilegeDto)}
         */

        @Test
        void testRemovePrivilege2() throws Exception {
            // Arrange
            privilegeDto.setName("SOME NAME");
            requestBuilder = delete("/privilege/remove").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(privilegeDto));
            // Act
            resultActions = mockMvc.perform(requestBuilder);
            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found")));

        }
    }


    @Nested
    @WithMockCustomUser
    class RoleControllerTests {

        private RoleDto roleDto;
        private final String baseUrl = "/role";

        @BeforeEach
        void setup() {
            roleDto = new RoleDto();
            roleDto.setName("SOME ROLE NAME");
            roleDto.setPagePrivilegeMap(Collections.emptyMap());
        }


        /**
         * Method under test: {@link RoleController#createRole(RoleDto)}
         */
        @Test
        void testCreateRole() throws Exception {
            // Arrange
            requestBuilder = post(baseUrl + "/create").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(roleDto));

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(roleDto)));
        }

        /**
         * Method under test: {@link RoleController#createRole(RoleDto)}
         */
        @Test
        void testCreateRole2() throws Exception {
            // Arrange
            roleDto.setName("ADMIN");
            requestBuilder = post(baseUrl + "/create").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(roleDto));

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("already exist")));
        }

        /**
         * Method under test: {@link RoleController#findAllRoles()}
         */
        @Test
        void testFindAllRoles() throws Exception {
            // Arrange
            requestBuilder = get(baseUrl + "/findAll").param(paramName, PAGE.ROLE);

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", greaterThan(0)));
        }

        /**
         * Method under test: {@link RoleController#findRoleByName(String)}
         */
        @Test
        void testFindRoleByName() throws Exception {
            // Arrange
            String name = "ADMIN";
            requestBuilder = get(baseUrl + "/findByName").param("name", name).param(paramName, PAGE.ROLE);
            // Act
            resultActions = mockMvc.perform(requestBuilder);
            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", notNullValue()))
                    .andExpect(jsonPath("$.pagePrivilegeMap", notNullValue()));
        }

        /**
         * Method under test: {@link RoleController#findRoleByName(String)}
         */
        @Test
        void testFindRoleByName2() throws Exception {
            // Arrange
            String name = roleDto.getName();
            requestBuilder = get(baseUrl + "/findByName").param("name", name).param(paramName, PAGE.ROLE);

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }

        /**
         * Method under test: {@link RoleController#updateRole(RoleDto)}
         */
        @Test
        void testUpdateRole_UpdateExistingRole() throws Exception {
            // Arrange
            requestBuilder = put(baseUrl + "/update").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(roleDto));

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(roleDto)));
        }

        /**
         * Method under test: {@link RoleController#updateRole(RoleDto)}
         */
        @Test
        void testUpdateRole2() throws Exception {
            // Arrange
            requestBuilder = put("/role/update").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(roleDto));

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }

        /**
         * Method under test: {@link RoleController#updateRole(RoleDto)}
         */
        @Test
        void testUpdateRole3() throws Exception {
            // Arrange
            requestBuilder = put("/role/update").param(paramName, PAGE.ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(roleDto));

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }
    }


    @Nested
    @WithMockCustomUser
    class UserControllerTests {
        private UserDto userDto;
        private final String baserUrl = "/user";

        @BeforeEach
        void setup() {
            userDto = new UserDto();
            userDto.setFirstName("tony");
            userDto.setLastName("stark");
            userDto.setEnabled(true);
            userDto.setPassword("12345");
            userDto.setEmail("tony@test.com");
            userDto.setRoles(Collections.emptyList());
            userDto.setSpecialPagesPrivileges(Collections.emptyList());
        }


        /**
         * Method under test: {@link UserController#createUser(UserDto)}
         */
        @Test
        void testCreateUser1() throws Exception {
            // Arrange
            requestBuilder = post(baserUrl + "/create").param(paramName, PAGE.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(userDto));

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.password", emptyString()));

        }

        /**
         * Method under test: {@link UserController#createUser(UserDto)}
         */
        @Test
        void testCreateUser2() throws Exception {
            // Arrange
            requestBuilder = post(baserUrl + "/create").param(paramName, PAGE.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(userDto));

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("already exist")));

        }

        /**
         * Method under test: {@link UserController#deleteByEmail(String)}
         */
        @Test
        void testDeleteByEmail() throws Exception {
            // Arrange
            String email = userDto.getEmail();
            userDto.setPassword("");
            requestBuilder = delete("/user/deleteByEmail").param("email", email).param(paramName, PAGE.USER);

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(userDto)));
        }

        /**
         * Method under test: {@link UserController#deleteByEmail(String)}
         */
        @Test
        void testDeleteByEmail2() throws Exception {
            // Arrange
            String email = userDto.getEmail();
            requestBuilder = delete("/user/deleteByEmail").param("email", email).param(paramName, PAGE.USER);

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }

        /**
         * Method under test: {@link UserController#getAllUsers()}
         */
        @Test
        void testGetAllUsers() throws Exception {
            // Arrange
            requestBuilder = get("/user/findAll").param(paramName, PAGE.USER);

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", greaterThan(0)));
        }
    }

    @Nested
    @WithMockCustomUser
    @TestMethodOrder(MethodOrderer.MethodName.class)
    class UserRoleControllerTests {
        private UserDto userDto;
        private final String baserUrl = "/userRole";

        @BeforeEach
        void setup() {
            userDto = new UserDto();
            userDto.setFirstName("user");
            userDto.setLastName("user");
            userDto.setEnabled(true);
            userDto.setPassword("");
            userDto.setEmail("user@test.com");
            userDto.setRoles(Collections.emptyList());
            userDto.setSpecialPagesPrivileges(Collections.emptyList());
        }


        /**
         * Method under test: {@link UserRoleController#assignRole(AssignRole)}
         */
        @Test
        void testAssignRole1() throws Exception {
            // Arrange
            AssignRole assignRole = new AssignRole();
            assignRole.setRoleNames(List.of("USER"));
            assignRole.setUsername(userDto.getEmail());

            userDto.setRoles(List.of(new RoleDto("USER", Collections.emptyMap())));
            requestBuilder = post(baserUrl + "/assignRole").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(assignRole));

            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", notNullValue()))
                    .andExpect(jsonPath("$.firstName", notNullValue()))
                    .andExpect(jsonPath("$.lastName", notNullValue()))
                    .andExpect(jsonPath("$.password", emptyString()))
                    .andExpect(jsonPath("$.roles.size()", greaterThan(0)));
        }

        /**
         * Method under test: {@link UserRoleController#assignRole(AssignRole)}
         */
        @Test
        void testAssignRole2() throws Exception {
            // Arrange
            AssignRole assignRole = new AssignRole();
            assignRole.setRoleNames(Collections.emptyList());
            assignRole.setUsername("SOME USER");

            requestBuilder = post(baserUrl + "/assignRole").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(assignRole));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }

        /**
         * Method under test: {@link UserRoleController#assignRole(AssignRole)}
         */
        @Test
        void testAssignRole3() throws Exception {
            // Arrange
            String roleName = "SOME ROLE";
            AssignRole assignRole = new AssignRole();
            assignRole.setRoleNames(List.of(roleName));
            assignRole.setUsername(userDto.getEmail());
            requestBuilder = post(baserUrl + "/assignRole").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(assignRole));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }

        /**
         * Method under test: {@link UserRoleController#extendRole(ExtendRole)}
         */
        @Test
        void testExtendRole() throws Exception {
            // Arrange
            ExtendRole extendRole = new ExtendRole();
            extendRole.setUsername(userDto.getEmail());
            List<PagesPrivilegesDto> pagesPrivilegesDtos = List.of(
                    new PagesPrivilegesDto(
                            new PageDto(PAGE.USER),
                            new PrivilegeDto(PRIVILEGE.READ)
                    ));
            extendRole.setPagesPrivilegesDtos(pagesPrivilegesDtos);

            requestBuilder = post(baserUrl + "/extendRole").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(extendRole));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", notNullValue()))
                    .andExpect(jsonPath("$.firstName", notNullValue()))
                    .andExpect(jsonPath("$.lastName", notNullValue()))
                    .andExpect(jsonPath("$.password", emptyString()))
                    .andExpect(jsonPath("$.roles.size()", greaterThan(0)))
                    .andExpect(jsonPath("$.specialPagesPrivileges.size()", greaterThan(0)));
        }


        /**
         * Method under test: {@link UserRoleController#extendRole(ExtendRole)}
         */
        @Test
        void testExtendRole2() throws Exception {
            // Arrange
            ExtendRole extendRole = new ExtendRole();
            extendRole.setUsername("Unknown User");
            extendRole.setPagesPrivilegesDtos(Collections.emptyList());
            requestBuilder = post(baserUrl + "/extendRole").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(extendRole));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }


        /**
         * Method under test: {@link UserRoleController#revokeExtendedPrivileges(RevokeExtendPrivilege)}
         */
        @Test
        void testRevokeExtendedPrivileges() throws Exception {
            // Arrange
            RevokeExtendPrivilege revokeExtendPrivilege = new RevokeExtendPrivilege();
            revokeExtendPrivilege.setSpecialPrivilegesMap(Collections.emptyMap());
            revokeExtendPrivilege.setUsername(userDto.getEmail());

            requestBuilder = put(baserUrl + "/revokeExtendedPrivileges").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(revokeExtendPrivilege));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", notNullValue()))
                    .andExpect(jsonPath("$.firstName", notNullValue()))
                    .andExpect(jsonPath("$.lastName", notNullValue()))
                    .andExpect(jsonPath("$.password", emptyString()))
                    .andExpect(jsonPath("$.roles.size()", greaterThan(0)))
                    .andExpect(jsonPath("$.specialPagesPrivileges", empty()));

        }

        /**
         * Method under test: {@link UserRoleController#revokeRole(RevokeRole)}
         */
        @Test
        void testRevokeRole1() throws Exception {
            // Arrange
            String roleName = "USER";
            RevokeRole revokeRole = new RevokeRole();
            revokeRole.setRoleNames(List.of(roleName));
            revokeRole.setUsername(userDto.getEmail());

            requestBuilder = put(baserUrl + "/revokeRole").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(revokeRole));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", notNullValue()))
                    .andExpect(jsonPath("$.firstName", notNullValue()))
                    .andExpect(jsonPath("$.lastName", notNullValue()))
                    .andExpect(jsonPath("$.password", emptyString()))
                    .andExpect(jsonPath("$.roles", empty()))
                    .andExpect(jsonPath("$.specialPagesPrivileges", empty()));
        }

        /**
         * Method under test: {@link UserRoleController#revokeRole(RevokeRole)}
         */
        @Test
        void testRevokeRole2() throws Exception {
            // Arrange
            String roleName = "SOME ROLE";
            RevokeRole revokeRole = new RevokeRole();
            revokeRole.setRoleNames(List.of(roleName));
            revokeRole.setUsername(userDto.getEmail());

            requestBuilder = put(baserUrl + "/revokeRole").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(revokeRole));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }

        /**
         * Method under test: {@link UserRoleController#revokeRole(RevokeRole)}
         */
        @Test
        void testRevokeRole3() throws Exception {
            // Arrange
            String roleName = "SOME ROLE";
            RevokeRole revokeRole = new RevokeRole();
            revokeRole.setRoleNames(List.of(roleName));
            revokeRole.setUsername("Unknown User");

            requestBuilder = put(baserUrl + "/revokeRole").param(paramName, PAGE.USER_ROLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(revokeRole));
            // Act
            resultActions = mockMvc.perform(requestBuilder);

            // Assert
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(requireNonNull(result.getResolvedException()).getMessage().contains("not found")));
        }
    }
}

