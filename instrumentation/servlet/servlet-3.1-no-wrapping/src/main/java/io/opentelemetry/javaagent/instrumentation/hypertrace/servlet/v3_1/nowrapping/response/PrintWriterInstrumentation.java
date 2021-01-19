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

package io.opentelemetry.javaagent.instrumentation.hypertrace.servlet.v3_1.nowrapping.response;

import static io.opentelemetry.javaagent.tooling.bytebuddy.matcher.AgentElementMatchers.safeHasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.javaagent.instrumentation.api.CallDepthThreadLocalMap;
import io.opentelemetry.javaagent.instrumentation.api.InstrumentationContext;
import io.opentelemetry.javaagent.tooling.TypeInstrumentation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import org.hypertrace.agent.core.instrumentation.buffer.BoundedCharArrayWriter;

public class PrintWriterInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<? super TypeDescription> typeMatcher() {
    return safeHasSuperType(named("java.io.PrintWriter")).or(named("java.io.PrintWriter"));
  }

  @Override
  public Map<? extends ElementMatcher<? super MethodDescription>, String> transformers() {
    Map<Junction<MethodDescription>, String> transformers = new HashMap<>();
    transformers.put(
        named("write").and(takesArguments(1)).and(takesArgument(0, is(int.class))).and(isPublic()),
        PrintWriterInstrumentation.class.getName() + "$Writer_writeChar");
    transformers.put(
        named("write")
            .and(takesArguments(1))
            .and(takesArgument(0, is(char[].class)))
            .and(isPublic()),
        PrintWriterInstrumentation.class.getName() + "$Writer_writeArr");
    transformers.put(
        named("write")
            .and(takesArguments(3))
            .and(takesArgument(0, is(char[].class)))
            .and(takesArgument(1, is(int.class)))
            .and(takesArgument(2, is(int.class)))
            .and(isPublic()),
        PrintWriterInstrumentation.class.getName() + "$Writer_writeOffset");
    transformers.put(
        named("print")
            .and(takesArguments(1))
            .and(takesArgument(0, is(String.class)))
            .and(isPublic()),
        PrintWriterInstrumentation.class.getName() + "$PrintWriter_print");
    transformers.put(
        named("println").and(takesArguments(0)).and(isPublic()),
        PrintWriterInstrumentation.class.getName() + "$PrintWriter_println");
    transformers.put(
        named("println")
            .and(takesArguments(1))
            .and(takesArgument(0, is(String.class)))
            .and(isPublic()),
        PrintWriterInstrumentation.class.getName() + "$PrintWriter_printlnStr");
    return transformers;
  }

  static class Writer_writeChar {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void enter(@Advice.This PrintWriter thizz, @Advice.Argument(0) int ch) {

      int callDepth = CallDepthThreadLocalMap.incrementCallDepth(PrintWriter.class);
      if (callDepth > 0) {
        return;
      }
      BoundedCharArrayWriter buffer =
          InstrumentationContext.get(PrintWriter.class, BoundedCharArrayWriter.class).get(thizz);
      if (buffer != null) {
        System.out.printf("Writing character %s \n", ch);
        buffer.write(ch);
      }
    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    public static void exit() {
      CallDepthThreadLocalMap.decrementCallDepth(PrintWriter.class);
    }
  }

  static class Writer_writeArr {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void enter(@Advice.This PrintWriter thizz, @Advice.Argument(0) char[] buf)
        throws IOException {

      int callDepth = CallDepthThreadLocalMap.incrementCallDepth(PrintWriter.class);
      if (callDepth > 0) {
        return;
      }
      BoundedCharArrayWriter buffer =
          InstrumentationContext.get(PrintWriter.class, BoundedCharArrayWriter.class).get(thizz);
      if (buffer != null) {
        buffer.write(buf);
      }
    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    public static void exit() {
      CallDepthThreadLocalMap.decrementCallDepth(PrintWriter.class);
    }
  }

  static class Writer_writeOffset {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void enter(
        @Advice.This PrintWriter thizz,
        @Advice.Argument(0) char[] buf,
        @Advice.Argument(1) int offset,
        @Advice.Argument(2) int len) {

      int callDepth = CallDepthThreadLocalMap.incrementCallDepth(PrintWriter.class);
      if (callDepth > 0) {
        return;
      }
      BoundedCharArrayWriter buffer =
          InstrumentationContext.get(PrintWriter.class, BoundedCharArrayWriter.class).get(thizz);
      if (buffer != null) {
        buffer.write(buf, offset, len);
      }
    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    public static void exit() {
      CallDepthThreadLocalMap.decrementCallDepth(PrintWriter.class);
    }
  }

  static class PrintWriter_print {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void enter(@Advice.This PrintWriter thizz, @Advice.Argument(0) String str)
        throws IOException {
      int callDepth = CallDepthThreadLocalMap.incrementCallDepth(PrintWriter.class);
      if (callDepth > 0) {
        return;
      }
      BoundedCharArrayWriter buffer =
          InstrumentationContext.get(PrintWriter.class, BoundedCharArrayWriter.class).get(thizz);
      if (buffer != null) {
        buffer.write(str);
      }
    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    public static void exit() {
      CallDepthThreadLocalMap.decrementCallDepth(PrintWriter.class);
    }
  }

  static class PrintWriter_println {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void enter(@Advice.This PrintWriter thizz) throws IOException {
      int callDepth = CallDepthThreadLocalMap.incrementCallDepth(PrintWriter.class);
      if (callDepth > 0) {
        return;
      }
      BoundedCharArrayWriter buffer =
          InstrumentationContext.get(PrintWriter.class, BoundedCharArrayWriter.class).get(thizz);
      if (buffer != null) {
        buffer.append('\n');
      }
    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    public static void exit() {
      CallDepthThreadLocalMap.decrementCallDepth(PrintWriter.class);
    }
  }

  static class PrintWriter_printlnStr {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void enter(@Advice.This PrintWriter thizz, @Advice.Argument(0) String str)
        throws IOException {
      int callDepth = CallDepthThreadLocalMap.incrementCallDepth(PrintWriter.class);
      if (callDepth > 0) {
        return;
      }
      BoundedCharArrayWriter buffer =
          InstrumentationContext.get(PrintWriter.class, BoundedCharArrayWriter.class).get(thizz);
      if (buffer != null) {
        buffer.write(str);
        buffer.append('\n');
      }
    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    public static void exit() {
      CallDepthThreadLocalMap.decrementCallDepth(PrintWriter.class);
    }
  }
}
