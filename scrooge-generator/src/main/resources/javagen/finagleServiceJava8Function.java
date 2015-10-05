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
      return new CFTF(result.thenCompose(new Function<{{typeName}},CompletableFuture<byte[]>>() {
		public CompletableFuture<byte[]> apply({{typeName}} value) {
		      return reply("{{serviceFuncNameForWire}}", seqid, new {{funcObjectName}}.Result.Builder(){{^isVoid}}.success(value){{/isVoid}}.build());
		}
		}))
		.exceptionally(new Function<Throwable,CompletableFuture<byte[]>>() {
                	public CompletableFuture<byte[]> apply(Throwable t) {
      			CompletableFuture<byte[]> future = new CompletableFuture<byte[]>();
      			future.completeExceptionally(t);
      			return future;
                }}))
		;
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
