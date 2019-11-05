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

package bisq.api.http.model;

import bisq.core.exceptions.ValidationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.theInstance;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PayloadValidatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void validateRequiredRequestPayload_nonNull_doesNotThrow() {
//        Given
        PayloadValidator payloadValidator = new PayloadValidator();
        Validatable data = mock(Validatable.class);
//        When
        payloadValidator.validateRequiredRequestPayload(data);
//        Then
        verify(data, times(1)).validate();
    }

    @Test
    public void validateRequiredRequestPayload_nonNull_callsValidateOnData() {
//        Given
        PayloadValidator payloadValidator = new PayloadValidator();
        Validatable data = mock(Validatable.class);
//        When
        payloadValidator.validateRequiredRequestPayload(data);
//        Then
        verify(data, times(1)).validate();
    }

    @Test
    public void validateRequiredRequestPayload_validateThrows_propagatesTheException() {
//        Given
        PayloadValidator payloadValidator = new PayloadValidator();
        Validatable data = mock(Validatable.class);
        RuntimeException someRandomError = new RuntimeException("Some random error");
        doThrow(someRandomError).when(data).validate();
        expectedException.expect(theInstance(someRandomError));
//        When
        payloadValidator.validateRequiredRequestPayload(data);
    }

    @Test
    public void validateRequiredRequestPayload_null_throwsException() {
//        Given
        PayloadValidator payloadValidator = new PayloadValidator();
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Request payload is required");
//        When
        //noinspection ConstantConditions
        payloadValidator.validateRequiredRequestPayload(null);
    }
}
