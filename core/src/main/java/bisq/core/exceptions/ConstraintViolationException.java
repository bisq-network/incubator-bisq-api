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

package bisq.core.exceptions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConstraintViolationException extends ValidationException {
    private Set<ConstraintViolation> constraintViolations;


    public ConstraintViolationException(Set<ConstraintViolation> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    public Set<ConstraintViolation> getConstraintViolations() {
        return null == constraintViolations ? Collections.emptySet() : constraintViolations;
    }


    public static class ConstraintViolation {
        private String propertyPath;
        private String message;

        private ConstraintViolation(String propertyPath, String message) {
            this.propertyPath = propertyPath;
            this.message = message;
        }

        public String getPropertyPath() {
            return propertyPath;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class Builder {
        private Set<ConstraintViolation> constraintViolations;

        public Builder addViolation(String propertyPath, String message) {
            if (null == constraintViolations)
                constraintViolations = new HashSet<>();
            constraintViolations.add(new ConstraintViolation(propertyPath, message));
            return this;
        }

        public ConstraintViolationException build() {
            return new ConstraintViolationException(constraintViolations);
        }

        public void throwIfAnyValidation() {
            if (null != constraintViolations && !constraintViolations.isEmpty())
                throw build();
        }
    }
}
