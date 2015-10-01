addFunction("{{serviceFuncNameForWire}}", new Function2<TProtocol, Integer, Future<byte[]>>() {
  public Future<byte[]> apply(TProtocol iprot, final Integer seqid) {
    try {
      {{funcObjectName}}.Args args = {{funcObjectName}}.Args.decode(iprot);
      iprot.readMessageEnd();
      CompletableFuture<{{typeName}}> result;
      try {
        result = iface.{{serviceFuncNameForCompile}}({{argNames}});
      } catch (Throwable t) {
	result = new CompletableFuture<{{typeName}}>();
        result.completeExceptionally(t);
      }
      return result.handle(new BiFunction<{{typeName}}, Throwable,Future<byte[]>>() {
        public Future<byte[]> apply({{typeName}} value,Throwable t){
	  if (t != null) {
{{#exceptions}}
          	if (t instanceof {{exceptionType}}) {
            		return reply("{{ServiceName}}", seqid, new {{funcObjectName}}.Result.Builder().{{fieldName}}(({{exceptionType}}) t).build());
          	}
{{/exceptions}}
		return Future.exception(t);
	  }
	  else {
          	return reply("{{serviceFuncNameForWire}}", seqid, new {{funcObjectName}}.Result.Builder(){{^isVoid}}.success(value){{/isVoid}}.build());
	  }
        }
      }).get();
      /*return new CFTF(result.thenCompose(new Function<{{typeName}},CompletableFuture<byte[]>>() {
		public CompletableFuture<byte[]> apply({{typeName}} value) {
		      return reply("{{serviceFuncNameForWire}}", seqid, new {{funcObjectName}}.Result.Builder(){{^isVoid}}.success(value){{/isVoid}}.build());
		}
		})
		.exceptionally(new Function<Throwable,CompletableFuture<{{typeName}}>() {
                	CompletableFuture<{{typeName}}> apply(Throwable t) {
      			CompletableFuture<byte[]> future = new CompletableFuture<byte[]>();
      			return future.completeExceptionally(e);
                }}))
		;
*/
    } catch (TProtocolException e) {
      try {
        iprot.readMessageEnd();
        return exception("{{serviceFuncNameForWire}}", seqid, TApplicationException.PROTOCOL_ERROR, e.getMessage());
      } catch (Exception unrecoverable) {
        return Future.exception(unrecoverable);
      }
    } catch (Throwable t) {
      return Future.exception(t);
    }
  }
});
