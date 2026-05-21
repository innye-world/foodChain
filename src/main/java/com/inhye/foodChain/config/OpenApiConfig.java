package com.inhye.foodChain.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI foodChainOpenApi(
			@Value("${foodchain.openapi.server-url:}") String serverUrl) {
		OpenAPI openAPI =
				new OpenAPI()
						.info(
								new Info()
										.title("FoodChain API")
										.description("식품 유통 관리 API")
										.version("v1"));

		if (StringUtils.hasText(serverUrl)) {
			openAPI.addServersItem(new Server().url(serverUrl));
		}
		return openAPI;
	}
}
