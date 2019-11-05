package bisq.core.exceptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ConstraintViolationExceptionTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void builderThrowIfAnyViolation_noViolations_doesNotThrow() {
        //        Given
        ConstraintViolationException.Builder builder = new ConstraintViolationException.Builder();

        //        When
        builder.throwIfAnyValidation();
    }

    @Test
    public void builderThrowIfAnyViolation_violationAdded_doesNotThrow() {
        //        Given
        ConstraintViolationException.Builder builder = new ConstraintViolationException.Builder();
        String must_not_be_null = "must not be null";
        String username = "username";
        builder.addViolation(username, must_not_be_null);
        String should_be_an_email = "should be an email";
        String address = "address";
        builder.addViolation(address, should_be_an_email);
        //        When
        try {
            builder.throwIfAnyValidation();
            fail("Expected ConstraintViolationException");
        } catch (ConstraintViolationException e) {
            //        Then
            assertNull(e.getMessage());
            Set<ConstraintViolationException.ConstraintViolation> constraintViolations = e.getConstraintViolations();
            assertNotNull(constraintViolations);
            assertEquals(2, constraintViolations.size());
            Set<String> resultMessageSet = constraintViolations.stream().map(ConstraintViolationException.ConstraintViolation::getMessage).collect(Collectors.toSet());
            Set<String> resultPropertyPathSet = constraintViolations.stream().map(ConstraintViolationException.ConstraintViolation::getPropertyPath).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of(must_not_be_null, should_be_an_email)), resultMessageSet);
            assertEquals(new HashSet<>(List.of(username, address)), resultPropertyPathSet);
        }
    }
}
