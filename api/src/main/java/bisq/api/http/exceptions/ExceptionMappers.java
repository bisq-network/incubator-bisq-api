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

import com.fasterxml.jackson.core.JsonParseException;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;



import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.eclipse.jetty.io.EofException;
import org.glassfish.jersey.server.ResourceConfig;

@Slf4j
public final class ExceptionMappers {

    private ExceptionMappers() {
    }

    public static void register(ResourceConfig environment) {
        environment.register(new ExceptionMappers.EofExceptionMapper(), 1);
        environment.register(new ExceptionMappers.JsonParseExceptionMapper(), 1);
        environment.register(new ExceptionMappers.UnauthorizedExceptionMapper());
        environment.register(new ExceptionMappers.ValidationExceptionMapper());
    }

    public static class EofExceptionMapper implements ExceptionMapper<EofException> {
        @Override
        public Response toResponse(EofException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public static class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
        @Override
        public Response toResponse(JsonParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public static class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {
        @Override
        public Response toResponse(UnauthorizedException exception) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    public static class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
        @Override
        public Response toResponse(ValidationException exception) {
            Response.ResponseBuilder responseBuilder = Response.status(422);
            String message = exception.getMessage();
            if (exception instanceof ConstraintViolationException) {
                List<String> messages = ((ConstraintViolationException) exception).getConstraintViolations().stream().map(constraintViolation -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    String propertyPath = constraintViolation.getPropertyPath();
                    if (propertyPath != null) {
                        stringBuilder.append(propertyPath).append(" ");
                    }
                    return stringBuilder.append(constraintViolation.getMessage()).toString();
                }).collect(Collectors.toList());
                responseBuilder.entity(new ValidationErrorMessage(ImmutableList.copyOf(messages)));
            } else if (message != null) {
                responseBuilder.entity(new ValidationErrorMessage(ImmutableList.of(message)));
            } else {
                responseBuilder.entity(new ValidationErrorMessage(ImmutableList.of()));
            }
            return responseBuilder.build();
        }
    }
}
