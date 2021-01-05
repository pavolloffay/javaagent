/*
 * Copyright The Hypertrace Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opentelemetry.javaagent.instrumentation.hypertrace.vertx;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.javaagent.instrumentation.vertx.VertxTracer;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import java.util.Map;
import org.hypertrace.agent.core.HypertraceSemanticAttributes;

public class BodyCaptureRoutingContext implements Handler<RoutingContext> {

  private final Handler<RoutingContext> wrapped;

  public BodyCaptureRoutingContext(Handler<RoutingContext> wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public void handle(RoutingContext event) {
    System.out.println("--> Route handler advice enter");
    Span currentServerSpan = VertxTracer.tracer().getCurrentServerSpan();
    System.out.println(currentServerSpan);

    // capture request body
    //    if (event.getBody() != null) {
    //      System.out.println(new String(event.getBody().getBytes()));
    //      System.out.println(new String(event.getBody().getBytes()));
    //    }

    MultiMap headers = event.request().headers();
    for (Map.Entry<String, String> entry : headers.entries()) {
      currentServerSpan.setAttribute(
          HypertraceSemanticAttributes.httpRequestHeader(entry.getKey()), entry.getValue());
    }

    wrapped.handle(event);
  }
}
