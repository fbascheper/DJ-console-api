package com.github.fbascheper.dj.console.controller;

import com.github.fbascheper.dj.console.exception.DJConsoleException;
import com.github.fbascheper.dj.console.exception.MixSessionException;
import com.github.fbascheper.dj.console.exception.MixSessionNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
@Slf4j
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {

        var errorType = ErrorType.BAD_REQUEST; // Default error type
        var extensions = new LinkedHashMap<String, Object>();

        if (ex instanceof DJConsoleException consoleEx) {

            extensions.put("errorCode", consoleEx.getErrorCode());

            // Optionally add more metadata based on subtype
            if (consoleEx instanceof MixSessionException mixEx) {
                if (mixEx instanceof MixSessionNotFoundException notFound) {
                    extensions.put("sessionId", notFound.getSessionId());

                    errorType = ErrorType.NOT_FOUND;
                }
            }
        }

        var logMessage = String.format("Resolved exception %s to %s during data fetching: %s",
                ex.getClass().getSimpleName(), errorType, ex.getMessage());
        log.error(logMessage, ex);

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(errorType)
                .extensions(extensions)
                .build();

    }
}