package com.ticketbox.analytics.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TicketBox Analytics Service API",
                version = "1.0.0",
                description = """
                        ## 📊 TicketBox Analytics Service
                        
                        ### 📈 Real-time analytics and reporting for ticket sales
                        
                        **Features:**
                        - Revenue analytics (system, organizer, event levels)
                        - Top events ranking
                        - KPI dashboards
                        - Ticket type comparison
                        
                        **Authentication:**
                        - Use Basic Auth for demo: `admin:password`
                        - Or use organizer/viewer accounts
                        """,
                contact = @Contact(
                        name = "TicketBox Team",
                        email = "support@ticketbox.com",
                        url = "https://ticketbox.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081",
                        description = "Local Development Server"
                ),
                @Server(
                        url = "https://api.ticketbox.com",
                        description = "Production Server"
                )
        },
        security = @SecurityRequirement(name = "basicAuth")
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = "Basic Authentication (use: admin/password for demo)"
)
public class OpenApiConfig {
    // Configuration will be picked up by SpringDoc
}