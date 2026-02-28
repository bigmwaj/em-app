package ca.bigmwaj.emapp.ig.route;

import ca.bigmwaj.emapp.ig.transform.DeadLetterMapper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterSenderRoute extends RouteBuilder {

    @Autowired
    private DeadLetterMapper deadLetterMapper;

    @Override
    public void configure() throws Exception {
        restConfiguration().host("localhost").port(8080);

        from("kafka:user-created?groupId=ig-dead-letter-sender-group")
                .routeId("ig-dead-letter-sender-route")
                .log("Initial message: ${body}")
                .setHeader("Authorization").constant("Basic dGVzdDp0ZXN0")
                .bean(deadLetterMapper)
                .log("The dead letter is : ${body}")
                .choice()
                    .when(simple("${body.id} != null"))
                        .to("direct:update-dead-letter")
                    .otherwise()
                        .to("direct:create-dead-letter")
                .end();

        from("direct:create-dead-letter")
                .routeId("ig-create-dead-letter-route")
                .log("Create the dead letter is : ${body}")
                .marshal().json(JsonLibrary.Jackson)
                .to("rest:post:/api/v1/platform/dead-letters")
                .end();

        from("direct:update-dead-letter")
                .routeId("ig-update-dead-letter-route")
                .log("Update the dead letter is : ${body}")
                .marshal().json(JsonLibrary.Jackson)
                .to("rest:patch:/api/v1/platform/dead-letters")
                .end();

    }
}
