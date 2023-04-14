// package com.ddalggak.finalproject.global.nginx;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.client.RestTemplate;
//
// @Configuration
// public class NginxTestConfig {
//
// 	@Value("${local.server.port}")
// 	private int port;
//
// 	@Bean
// 	public RestTemplate restTemplate() {
// 		return new RestTemplate();
// 	}
//
// 	@Bean
// 	public TestRestTemplate testRestTemplate() {
// 		return new TestRestTemplate(restTemplate());
// 	}
//
// 	@Bean
// 	public EmbeddedServletContainerFactory servletContainer() {
// 		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
// 		factory.setPort(0);
// 		return factory;
// 	}
//
// 	@Bean
// 	public EmbeddedServletContainerCustomizer containerCustomizer() {
// 		return container -> {
// 			if (container instanceof TomcatEmbeddedServletContainerFactory) {
// 				((TomcatEmbeddedServletContainerFactory) container)
// 					.addConnectorCustomizers(connector -> {
// 						connector.setPort(8080);
// 						connector.setRedirectPort(port);
// 					});
// 			}
// 		};
// 	}
// }
