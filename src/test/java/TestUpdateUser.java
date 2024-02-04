import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestUpdateUser {
    UserSteps userSteps;
    String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
    String email2 = RandomStringUtils.randomAlphabetic(10) + "@mail.ru";
    String password = RandomStringUtils.randomNumeric(8);
    String name = RandomStringUtils.randomAlphabetic(7);
    private String accessToken;

    @Before
    public void setUp() {
        userSteps = new UserSteps(new UserClient());
    }

    @Test
    @DisplayName("Изменение пользователя")
    @Description("Проверка возможности изменения пользователя")
    public void testUpdateAuthorisedUser() {
        ValidatableResponse responsea = userSteps.create(email, password, name);
        ValidatableResponse responseb = userSteps.login(email, password);
        accessToken = userSteps.getAccessToken(responseb);
        userSteps.update(accessToken, email2, 1 + password, "new" + name)
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение пользователя с дублированным emeil")
    @Description("Проверка невозможности изменения пользователя с дублированным emeil")
    public void testUpdateUserDuplicateEmail() {
        ValidatableResponse responsea = userSteps.create(email, password, name);
        ValidatableResponse responseb = userSteps.login(email, password);
        accessToken = userSteps.getAccessToken(responseb);
        userSteps.update(accessToken, "test@mail.ru", password, name)
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User with such email already exists"));
    }

    @Test
    @DisplayName("Изменение пользователя без авторизации")
    @Description("Проверка невозможности изменения пользователя без авторизации токеном")
    public void testUpdateNoAuthorisedUser() {
        ValidatableResponse response = userSteps.create(email, password, name);
        userSteps.login(email, password);
        accessToken = userSteps.getAccessToken(response);
        userSteps.updateWithoutToken("q" + email, name, password)
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        userSteps.delete();
    }
}
