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

package bisq.api.http.exceptions;

import bisq.api.http.model.ValidationErrorMessage;

import bisq.core.exceptions.ConstraintViolationException;
import bisq.core.exceptions.ValidationException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



import com.github.javafaker.Faker;
import javax.ws.rs.core.Response;

public class ValidationExceptionMapperTest {


    private static void assertInstanceOfValidationErrorMessage(Object object) {
        String message = "Response payload is an instance of " + ValidationErrorMessage.class.getSimpleName();
        assertTrue(message, object instanceof ValidationErrorMessage);
    }

    @Test
    public void toResponse_constraintViolationException_statusIs422() {
        //        Given
        ExceptionMappers.ValidationExceptionMapper mapper = new ExceptionMappers.ValidationExceptionMapper();

        //        When
        Response response = mapper.toResponse(new ConstraintViolationException(null));

        //        Then
        assertEquals(422, response.getStatus());
    }

    @Test
    public void toResponse_constraintViolationExceptionWithoutViolations_bodyIsViolationErrorMessageWithEmptyErrorList() {
        //        Given
        ExceptionMappers.ValidationExceptionMapper mapper = new ExceptionMappers.ValidationExceptionMapper();

        //        When
        Response response = mapper.toResponse(new ConstraintViolationException(null));

        //        Then
        Object entity = response.getEntity();
        assertNotNull(entity);
        assertInstanceOfValidationErrorMessage(entity);
        ValidationErrorMessage payload = (ValidationErrorMessage) entity;
        assertEquals(Collections.emptyList(), payload.getErrors());
    }

    @Test
    public void toResponse_constraintViolationException_bodyIsViolationErrorMessageWithNonEmptyErrorList() {
        //        Given
        ExceptionMappers.ValidationExceptionMapper mapper = new ExceptionMappers.ValidationExceptionMapper();
        ConstraintViolationException.Builder builder = new ConstraintViolationException.Builder();
        Faker faker = Faker.instance();
        String propertyPathA = faker.app().name();
        String messageA = faker.app().version();
        builder.addViolation(propertyPathA, messageA);
        String messageB = faker.app().version();
        builder.addViolation(null, messageB);

        //        When
        Response response = mapper.toResponse(builder.build());

        //        Then
        Object entity = response.getEntity();
        assertNotNull(entity);
        assertInstanceOfValidationErrorMessage(entity);
        ValidationErrorMessage payload = (ValidationErrorMessage) entity;
        assertEquals(new HashSet<>(List.of(propertyPathA + " " + messageA, messageB)), new HashSet<>(payload.getErrors()));
    }

    @Test
    public void toResponse_ValidationException_statusIs422() {
        //        Given
        ExceptionMappers.ValidationExceptionMapper mapper = new ExceptionMappers.ValidationExceptionMapper();
        String message = Faker.instance().chuckNorris().fact();

        //        When
        Response response = mapper.toResponse(new ValidationException(message));

        //        Then
        assertEquals(422, response.getStatus());
    }

    @Test
    public void toResponse_ValidationException_bodyIsViolationErrorMessageWithNonEmptyErrorList() {
        //        Given
        ExceptionMappers.ValidationExceptionMapper mapper = new ExceptionMappers.ValidationExceptionMapper();
        String message = Faker.instance().chuckNorris().fact();

        //        When
        Response response = mapper.toResponse(new ValidationException(message));

        //        Then
        Object entity = response.getEntity();
        assertNotNull(entity);
        assertInstanceOfValidationErrorMessage(entity);
        ValidationErrorMessage payload = (ValidationErrorMessage) entity;
        assertEquals(List.of(message), payload.getErrors());
    }

    @Test
    public void toResponse_ValidationExceptionWithoutMessage_bodyIsViolationErrorMessageWithEmptyErrorList() {
        //        Given
        ExceptionMappers.ValidationExceptionMapper mapper = new ExceptionMappers.ValidationExceptionMapper();

        //        When
        Response response = mapper.toResponse(new ValidationException());

        //        Then
        Object entity = response.getEntity();
        assertNotNull(entity);
        assertInstanceOfValidationErrorMessage(entity);
        ValidationErrorMessage payload = (ValidationErrorMessage) entity;
        assertEquals(Collections.emptyList(), payload.getErrors());
    }
}
