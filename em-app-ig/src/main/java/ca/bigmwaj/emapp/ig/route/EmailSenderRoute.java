package ca.bigmwaj.emapp.ig.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//@Component
public class EmailSenderRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("kafka:{{ig.kafka.topic.dead-letter-sender}}?brokers={{ig.kafka.bootstrap-servers}}")
                .routeId("email-sender-route")
                .log("Received message in Email Sender Route: ${body}")
                // Add any additional processing or error handling logic here
                .end();
    }
}
