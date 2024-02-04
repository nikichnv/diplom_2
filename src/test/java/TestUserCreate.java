import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestUserCreate {
    UserSteps userSteps;
    String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
    String password = RandomStringUtils.randomNumeric(8);
    String name = RandomStringUtils.randomAlphabetic(7);
    private String accessToken;

    @Before
    public void setUp() {
        userSteps = new UserSteps(new UserClient());
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание пользователя с валидным телом запроса")
    public void canUserCreate() {
        ValidatableResponse response = userSteps.create(email, password, name);
        response.assertThat().statusCode(SC_OK).and().body("success", equalTo(true));
        accessToken = userSteps.getAccessToken(response);
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Проверка невозможности создания пользователя, который уже зарегестрирован")
    public void duplicateUserCreate() {
        ValidatableResponse responsea = userSteps.create(email, password, name);
        accessToken = userSteps.getAccessToken(responsea);
        ValidatableResponse responseb = userSteps.create(email, password, name);
        responseb.assertThat().statusCode(SC_FORBIDDEN).and().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка невозможности создания пользователя без обязательного поля")
    public void userCreateNoRequiredFieldPassword() {
        ValidatableResponse response = userSteps.create(email, null, name);
        response.assertThat().statusCode(SC_FORBIDDEN).and().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без эмейла")
    @Description("Проверка невозможности создания пользователя без обязательного поля")
    public void userCreateNoRequiredFieldEmail() {
        ValidatableResponse response = userSteps.create(null, password, name);
        response.assertThat().statusCode(SC_FORBIDDEN).and().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка невозможности создания пользователя без обязательного поля")
    public void userCreateNoRequiredFieldName() {
        ValidatableResponse response = userSteps.create(email, password, null);
        response.assertThat().statusCode(SC_FORBIDDEN).and().body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        userSteps.delete();
    }
}


