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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;



import javax.ws.rs.core.Response;
import org.eclipse.jetty.io.EofException;

public class EofExceptionMapperTest {

    @Test
    public void toResponse_always_statusIs400() {
        //        Given
        ExceptionMappers.EofExceptionMapper mapper = new ExceptionMappers.EofExceptionMapper();

        //        When
        Response response = mapper.toResponse(new EofException());

        //        Then
        assertEquals(400, response.getStatus());
    }

    @Test
    public void toResponse_always_noBody() {
        //        Given
        ExceptionMappers.EofExceptionMapper mapper = new ExceptionMappers.EofExceptionMapper();

        //        When
        Response response = mapper.toResponse(new EofException());

        //        Then
        Object entity = response.getEntity();
        assertNull(entity);
    }
}
