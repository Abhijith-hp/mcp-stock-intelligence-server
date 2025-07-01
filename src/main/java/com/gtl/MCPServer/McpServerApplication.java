package com.gtl.MCPServer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.gtl.MCPServer.tools.AuthenticationTools;
import com.gtl.MCPServer.tools.DataTools;
import com.gtl.MCPServer.tools.PortfoliosTools;



@SpringBootApplication
public class McpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}
@Bean
public List<ToolCallback> toolCallbacks(
        PortfoliosTools portfoliosTools,
        DataTools dataTools,
        AuthenticationTools authenticationTools) {

    return Stream.of(
                Arrays.stream(ToolCallbacks.from(portfoliosTools)), 
                Arrays.stream(ToolCallbacks.from(dataTools)),
                Arrays.stream(ToolCallbacks.from(authenticationTools))
               
            )
            .flatMap(s -> s)
            .collect(Collectors.toList());
}


}
