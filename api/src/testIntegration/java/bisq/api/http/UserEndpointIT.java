/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.api.http;

import bisq.api.http.model.ChangePassword;

import bisq.common.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;



import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.arquillian.cube.docker.impl.client.containerobject.dsl.Container;
import org.arquillian.cube.docker.impl.client.containerobject.dsl.DockerContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;

@RunWith(Arquillian.class)
public class UserEndpointIT {

    private static String validPassword = new Faker().internet().password();
    private static String invalidPassword = getRandomPasswordDifferentThan(validPassword);
    @DockerContainer
    Container alice = ContainerFactory.createApiContainer("alice", "8081->8080", 3333, false, false);

    private static String getRandomPasswordDifferentThan(String otherPassword) {
        String newPassword;
        do {
            newPassword = new Faker().internet().password();
        } while (otherPassword.equals(newPassword));
        return newPassword;
    }

    @InSequence
    @Test
    public void waitForAllServicesToBeReady() throws InterruptedException {
        ApiTestHelper.waitForAllServicesToBeReady();
        verifyThatAuthenticationIsDisabled();
    }

    @InSequence(2)
    @Test
    public void changePassword_missingPayload_returns422() {
        int alicePort = getAlicePort();
        given().
                port(alicePort).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
//
        when().
                post("/api/v1/user/password").
//
        then().
                statusCode(422).
                and().body("errors.size", equalTo(1)).
                and().body("errors[0]", equalTo("Request payload is required"));

        verifyThatAuthenticationIsDisabled();
    }

    @InSequence(3)
    @Test
    public void changePassword_settingFirstPassword_enablesAuthentication() {
        given().
                port(getAlicePort()).
                body(new ChangePassword(validPassword, null)).
                contentType(ContentType.JSON).
//
        when().
                post("/api/v1/user/password").
//
        then().
                statusCode(204).
                and().body(isEmptyString());

        verifyThatAuthenticationIsEnabled();
        verifyThatPasswordIsValid(validPassword);
    }

    @InSequence(4)
    @Test
    public void changePassword_invalidOldPassword_returns401() {
        String newPassword = getRandomPasswordDifferentThan(validPassword);
        given().
                port(getAlicePort()).
                body(new ChangePassword(newPassword, invalidPassword)).
                contentType(ContentType.JSON).
//
        when().
                post("/api/v1/user/password").
//
        then().
                statusCode(401);

        verifyThatAuthenticationIsEnabled();
        verifyThatPasswordIsValid(validPassword);
        verifyThatPasswordIsInvalid(newPassword);
    }

    @InSequence(4)
    @Test
    public void changePassword_emptyOldPassword_returns401() {
        String newPassword = getRandomPasswordDifferentThan(validPassword);
        given().
                port(getAlicePort()).
                body(new ChangePassword(newPassword, null)).
                contentType(ContentType.JSON).
//
        when().
                post("/api/v1/user/password").
//
        then().
                statusCode(401);

        verifyThatAuthenticationIsEnabled();
        verifyThatPasswordIsValid(validPassword);
        verifyThatPasswordIsInvalid(newPassword);
    }

    @InSequence(5)
    @Test
    public void changePassword_settingAnotherPassword_keepsAuthenticationEnabled() {
        String oldPassword = validPassword;
        String newPassword = getRandomPasswordDifferentThan(validPassword);
        validPassword = newPassword;
        invalidPassword = getRandomPasswordDifferentThan(validPassword);
        given().
                port(getAlicePort()).
                body(new ChangePassword(newPassword, oldPassword)).
                contentType(ContentType.JSON).
//
        when().
                post("/api/v1/user/password").
//
        then().
                statusCode(204);

        verifyThatPasswordIsInvalid(oldPassword);
        verifyThatPasswordIsValid(newPassword);
        verifyThatAuthenticationIsEnabled();
    }

    @InSequence(6)
    @Test
    public void changePassword_validOldPasswordAndNoNewPassword_disablesAuthentication() {
        given().
                port(getAlicePort()).
                body(new ChangePassword(null, validPassword)).
                contentType(ContentType.JSON).
//
        when().
                post("/api/v1/user/password").
//
        then().
                statusCode(204);

        verifyThatAuthenticationIsDisabled();
    }

    private void verifyThatAuthenticationIsDisabled() {
        authenticationVerificationRequest().then().statusCode(200);
    }

    private void verifyThatAuthenticationIsEnabled() {
        authenticationVerificationRequest().then().statusCode(401);
    }

    private void verifyThatPasswordIsInvalid(String password) {
        passwordVerificationRequest(password).then().statusCode(401);
    }

    private void verifyThatPasswordIsValid(String password) {
        passwordVerificationRequest(password).then().statusCode(200);
    }

    private Response authenticationVerificationRequest() {
        return given().port(getAlicePort()).when().get("/api/v1/version");
    }

    private Response passwordVerificationRequest(String password) {
        String authHeader = "Basic " + Base64.encode((":" + password).getBytes());
        return given().port(getAlicePort()).
//
        when().
                        header("authorization", authHeader).
                        get("/api/v1/version");
    }

    private int getAlicePort() {
        return alice.getBindPort(8080);
    }

}
