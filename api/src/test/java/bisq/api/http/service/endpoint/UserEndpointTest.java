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

package bisq.api.http.service.endpoint;

import bisq.api.http.model.ChangePassword;
import bisq.api.http.model.PayloadValidator;
import bisq.api.http.service.auth.ApiPasswordManager;

import bisq.core.exceptions.ValidationException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;



import com.github.javafaker.Faker;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import org.mockito.ArgumentCaptor;

public class UserEndpointTest {

    private Faker faker;

    @Before
    public void setUp() {
        faker = Faker.instance();
    }

    @Test
    public void changePassword_nullData_resumesWithValidationException() {
//        Given
        ArgumentCaptor<ValidationException> argument = ArgumentCaptor.forClass(ValidationException.class);
        UserEndpoint endpoint = new UserEndpoint(null, new PayloadValidator());
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
//        When
        endpoint.changePassword(asyncResponse, null);
//        Then
        verify(asyncResponse).resume(argument.capture());
        assertEquals(ValidationException.class, argument.getValue().getClass());
        assertEquals("Request payload is required", argument.getValue().getMessage());
    }

    @Test
    public void changePassword_apiPasswordManagerThrows_resumesWithThrownException() {
//        Given
        ArgumentCaptor<RuntimeException> argument = ArgumentCaptor.forClass(RuntimeException.class);
        ApiPasswordManager apiPasswordManager = mock(ApiPasswordManager.class);
        UserEndpoint endpoint = new UserEndpoint(apiPasswordManager, new PayloadValidator());
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
        String newPassword = faker.internet().password();
        String oldPassword = faker.internet().password();
        ChangePassword data = new ChangePassword(newPassword, oldPassword);
        RuntimeException exception = new RuntimeException(faker.lorem().sentence());
        doThrow(exception)
                .when(apiPasswordManager).changePassword(oldPassword, newPassword);
//        When
        endpoint.changePassword(asyncResponse, data);
//        Then
        verify(asyncResponse).resume(argument.capture());
        assertEquals(exception.getClass(), argument.getValue().getClass());
        assertEquals(exception.getMessage(), argument.getValue().getMessage());
    }

    @Test
    public void changePassword_everythingOK_resumesNoContentResponse() {
//        Given
        ArgumentCaptor<Response> argument = ArgumentCaptor.forClass(Response.class);
        ApiPasswordManager apiPasswordManager = mock(ApiPasswordManager.class);
        UserEndpoint endpoint = new UserEndpoint(apiPasswordManager, new PayloadValidator());
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
        ChangePassword data = new ChangePassword();
//        When
        endpoint.changePassword(asyncResponse, data);
//        Then
        verify(asyncResponse).resume(argument.capture());
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), argument.getValue().getStatus());
        assertEquals(Response.Status.NO_CONTENT, argument.getValue().getStatusInfo());
    }
}
