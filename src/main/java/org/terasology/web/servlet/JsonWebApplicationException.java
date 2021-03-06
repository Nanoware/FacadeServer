/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.web.servlet;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This exception class is thrown when a JSON session has wrong data.
 */
public class JsonWebApplicationException extends WebApplicationException {

    private static class ErrorResponse {
        String message;
        ErrorResponse(String message) {
            this.message = message;
        }
    }

    public JsonWebApplicationException(String message, Response.Status status) {
        super(Response.status(status).type(MediaType.APPLICATION_JSON).entity(new ErrorResponse(message)).build());
    }
}
