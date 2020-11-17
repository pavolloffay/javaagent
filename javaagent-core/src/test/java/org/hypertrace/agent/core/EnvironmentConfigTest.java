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

package org.hypertrace.agent.core;

import com.google.protobuf.StringValue;
import java.util.Arrays;
import org.hypertrace.agent.config.Config.AgentConfig;
import org.hypertrace.agent.config.Config.PropagationFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;

class EnvironmentConfigTest {

  @Test
  @ClearSystemProperty(key = EnvironmentConfig.REPORTING_ADDRESS)
  @ClearSystemProperty(key = EnvironmentConfig.REPORTING_SECURE)
  @ClearSystemProperty(key = EnvironmentConfig.OPA_ADDRESS)
  @ClearSystemProperty(key = EnvironmentConfig.OPA_POLL_PERIOD)
  @ClearSystemProperty(key = EnvironmentConfig.PROPAGATION_FORMATS)
  @ClearSystemProperty(key = EnvironmentConfig.CAPTURE_HTTP_BODY_PREFIX + "request")
  public void systemProperties() {
    // when tests are run in parallel the env vars/sys props set it junit-pioneer are visible to
    // parallel tests
    System.setProperty(EnvironmentConfig.REPORTING_ADDRESS, "http://:-)");
    System.setProperty(EnvironmentConfig.REPORTING_SECURE, "true");
    System.setProperty(EnvironmentConfig.CAPTURE_HTTP_BODY_PREFIX + "request", "true");
    System.setProperty(EnvironmentConfig.OPA_ADDRESS, "http://azkaban:9090");
    System.setProperty(EnvironmentConfig.OPA_POLL_PERIOD, "10");
    System.setProperty(EnvironmentConfig.PROPAGATION_FORMATS, "B3,TRACE_CONTEXT");

    AgentConfig.Builder configBuilder = AgentConfig.newBuilder();
    configBuilder.setServiceName(StringValue.newBuilder().setValue("foo"));

    AgentConfig agentConfig = EnvironmentConfig.applyPropertiesAndEnvVars(configBuilder).build();
    Assertions.assertEquals("foo", agentConfig.getServiceName().getValue());
    Assertions.assertEquals(
        Arrays.asList(PropagationFormat.B3, PropagationFormat.TRACE_CONTEXT),
        agentConfig.getPropagationFormatsList());
    Assertions.assertEquals("http://:-)", agentConfig.getReporting().getAddress().getValue());
    Assertions.assertEquals(
        "http://azkaban:9090", agentConfig.getReporting().getOpa().getAddress().getValue());
    Assertions.assertEquals(10, agentConfig.getReporting().getOpa().getPollPeriod().getValue());
    Assertions.assertEquals(true, agentConfig.getReporting().getSecure().getValue());
    Assertions.assertEquals(
        true, agentConfig.getDataCapture().getHttpBody().getRequest().getValue());
  }
}
