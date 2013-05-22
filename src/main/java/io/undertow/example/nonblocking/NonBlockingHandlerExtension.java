package io.undertow.example.nonblocking;

import io.undertow.predicate.Predicates;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.spec.ServletContextImpl;

/**
 * An example extension that registers some non-blocking handlers,
 *
 * and delegates to the servlet handler chain if none of the handlers are matched.
 *
 *
 * @author Stuart Douglas
 */
public class NonBlockingHandlerExtension implements ServletExtension {
    @Override
    public void handleDeployment(final DeploymentInfo deploymentInfo, final ServletContextImpl servletContext) {
        deploymentInfo.addInitialHandlerChainWrapper(new HandlerWrapper() {
            @Override
            public HttpHandler wrap(final HttpHandler handler) {

                //we use a path handler to either delegate to our non-blocking server
                //or forward through to the default servlet handler
                final PathHandler pathHandler = new PathHandler();
                pathHandler.addPath("/", handler); //if nothing matches just forward to the servlet chain
                pathHandler.addPath("/hello", new HelloWorldHandler());

                final ResourceHandler resourceHandler = new ResourceHandler()
                        .setResourceManager(deploymentInfo.getResourceManager());

                PredicateHandler predicateHandler = new PredicateHandler(Predicates.suffix(".css"), resourceHandler, pathHandler);

                return predicateHandler;
            }
        });
    }
}
