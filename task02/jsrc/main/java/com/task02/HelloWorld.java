package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	@Override
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
		// Extract the raw path (e.g. "/hello" or "/student_id")
		String rawPath = event.getRawPath();
		// Extract HTTP method (e.g. "GET", "POST")
		String httpMethod = event.getRequestContext().getHttp().getMethod();

		// If exactly GET /hello → return 200 + {"message":"Hello from Lambda!"}
		if ("GET".equalsIgnoreCase(httpMethod) && "/hello".equals(rawPath)) {
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");

			String body = "{\"message\":\"Hello from Lambda!\"}";

			return APIGatewayV2HTTPResponse.builder()
					.withStatusCode(200)
					.withHeaders(headers)
					.withBody(body)
					.build();
		}

		// Otherwise → 400 Bad Request with path & method in JSON
		Map<String, String> errorHeaders = new HashMap<>();
		errorHeaders.put("Content-Type", "application/json");

		String errorBody = String.format(
				"{\"error\":\"Bad Request: endpoint '%s' with method '%s' is not supported\"}",
				rawPath, httpMethod
		);

		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(400)
				.withHeaders(errorHeaders)
				.withBody(errorBody)
				.build();
	}
}
