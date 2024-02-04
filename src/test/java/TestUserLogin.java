import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestUserLogin {
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
    @DisplayName("Авторизация пользователем")
    @Description("Проверка корректной авторизации пользователя")
    public void canLoginUser() {
        ValidatableResponse response = userSteps.create(email, password, name).log().all();
        ValidatableResponse responsea = userSteps.login(email, password).statusCode(SC_OK).and().body("success", equalTo(true)).log().all();
        accessToken = userSteps.getAccessToken(responsea);
    }

    @Test
    @DisplayName("Авторизация пользователем без логина")
    @Description("Проверка невозможности авторизации пользователя без логина")
    public void loginUserWithoutEmail() {
        ValidatableResponse response = userSteps.login(null, password).statusCode(SC_UNAUTHORIZED).and().body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация пользователем без пароля")
    @Description("Проверка невозможности авторизации пользователя без пароля")
    public void loginUserWithoutPassword() {
        ValidatableResponse response = userSteps.login(null, password).statusCode(SC_UNAUTHORIZED).and().body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        userSteps.delete();
    }
}
