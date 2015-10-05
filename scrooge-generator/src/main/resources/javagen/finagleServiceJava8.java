package {{package}};

import com.twitter.finagle.Service;
import com.twitter.finagle.SourcedException;
import com.twitter.finagle.stats.Counter;
import com.twitter.finagle.stats.NullStatsReceiver;
import com.twitter.finagle.stats.StatsReceiver;
import com.twitter.finagle.thrift.ThriftClientRequest;
import com.twitter.util.Function2;
import com.twitter.util.Future;
//import com.twitter.util.FutureEventListener;
import com.twitter.util.Promise;
import com.twitter.scrooge.Option;
import com.twitter.scrooge.ThriftStruct;
import com.twitter.scrooge.ThriftStructCodec;
import com.twitter.scrooge.ThriftStructCodec3;
import com.twitter.scrooge.Utilities;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.transport.TTransport;

import com.twitter.util.Awaitable;
import com.twitter.util.Duration;
import com.twitter.util.TimeoutException;
import com.twitter.util.Try;

import scala.Function1;
import scala.runtime.BoxedUnit;

import java.util.function.Function;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import {{package}}.{{ServiceName}}.*;

{{docstring}}
public class {{ServiceName}}$FinagleService extends {{finagleServiceParent}} {
  final private {{ServiceName}}.FutureIface iface;
  final private TProtocolFactory protocolFactory;

  public {{ServiceName}}$FinagleService(final {{ServiceName}}.FutureIface iface, final TProtocolFactory protocolFactory) {
{{#hasParent}}
    super(iface, protocolFactory);
{{/hasParent}}
    this.iface = iface;
    this.protocolFactory = protocolFactory;

{{#functions}}
    {{>function}}
{{/function}}
  }

{{^hasParent}}
  // ----- boilerplate that should eventually be moved into finagle:

  protected Map<String, Function2<TProtocol, Integer, Future<byte[]>>> functionMap =
    new HashMap<String, Function2<TProtocol, Integer, Future<byte[]>>>();

  protected void addFunction(String name, Function2<TProtocol, Integer, Future<byte[]>> fn) {
    functionMap.put(name, fn);
  }

  protected Function2<TProtocol, Integer, Future<byte[]>> getFunction(String name) {
    return functionMap.get(name);
  }

  protected Future<byte[]> exception(String name, int seqid, int code, String message) {
    try {
      TApplicationException x = new TApplicationException(code, message);
      TMemoryBuffer memoryBuffer = new TMemoryBuffer(512);
      TProtocol oprot = protocolFactory.getProtocol(memoryBuffer);

      oprot.writeMessageBegin(new TMessage(name, TMessageType.EXCEPTION, seqid));
      x.write(oprot);
      oprot.writeMessageEnd();
      oprot.getTransport().flush();
      return Future.value(Arrays.copyOfRange(memoryBuffer.getArray(), 0, memoryBuffer.length()));
    } catch (Exception e) {
      return Future.exception(e);
    }
  }

  protected CompletableFuture<byte[]> reply(String name, int seqid, ThriftStruct result) {
    try {
      TMemoryBuffer memoryBuffer = new TMemoryBuffer(512);
      TProtocol oprot = protocolFactory.getProtocol(memoryBuffer);

      oprot.writeMessageBegin(new TMessage(name, TMessageType.REPLY, seqid));
      result.write(oprot);
      oprot.writeMessageEnd();

      return CompletableFuture.completedFuture(Arrays.copyOfRange(memoryBuffer.getArray(), 0, memoryBuffer.length()));
    } catch (Exception e) {
      CompletableFuture<byte[]> future = new CompletableFuture<byte[]>();
      future.completeExceptionally(e);
      return future;
    }
  }

  public final Future<byte[]> apply(byte[] request) {
    TTransport inputTransport = new TMemoryInputTransport(request);
    TProtocol iprot = protocolFactory.getProtocol(inputTransport);

    try {
      TMessage msg = iprot.readMessageBegin();
      Function2<TProtocol, Integer, Future<byte[]>> f = functionMap.get(msg.name);
      if (f != null) {
        return f.apply(iprot, msg.seqid);
      } else {
        TProtocolUtil.skip(iprot, TType.STRUCT);
        return exception(msg.name, msg.seqid, TApplicationException.UNKNOWN_METHOD, "Invalid method name: '" + msg.name + "'");
      }
    } catch (Exception e) {
      return Future.exception(e);
    }
  }

  // ---- end boilerplate.
{{/hasServer}}

public class CFTF<T> extends com.twitter.util.Future<T> {
        private Promise<T> promise = new Promise<>();

        public CFTF(CompletableFuture<T> javaFuture) {
                javaFuture.whenComplete((ok, ex) -> {
                        if(ok != null) {
                                promise.setValue(ok);
                        } else {
                                promise.setException(ex);
                        }
                });
        }
        @Override
        public boolean isReady(com.twitter.util.Awaitable.CanAwait arg0) {
                return promise.isReady(arg0);
        }

        @Override
        public Awaitable<T> ready(Duration arg0,
                        com.twitter.util.Awaitable.CanAwait arg1) throws TimeoutException,
                        InterruptedException {
                return promise.ready(arg0, arg1);
        }

        @Override
        public T result(Duration arg0, com.twitter.util.Awaitable.CanAwait arg1)
                        throws Exception {
                return promise.result(arg0, arg1);
        }

        @Override
        public scala.Option<Try<T>> poll() {
                return promise.poll();
        }

        @Override
        public void raise(Throwable arg0) {
                promise.raise(arg0);
        }

        @Override
        public Future<T> respond(Function1<Try<T>, BoxedUnit> arg0) {
                return promise.respond(arg0);
        }

        @Override
        public <B> Future<B> transform(Function1<Try<T>, Future<B>> arg0) {
                return promise.transform(arg0);
        }


}

}
