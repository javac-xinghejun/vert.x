package io.vertx.core.file;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import io.vertx.core.spi.json.JsonDecoder;

/**
 * Converter and Codec for {@link io.vertx.core.file.CopyOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.core.file.CopyOptions} original class using Vert.x codegen.
 */
public class CopyOptionsConverter implements JsonDecoder<CopyOptions, JsonObject> {

  public static final CopyOptionsConverter INSTANCE = new CopyOptionsConverter();

  @Override public CopyOptions decode(JsonObject value) { return (value != null) ? new CopyOptions(value) : null; }

  @Override public Class<CopyOptions> getTargetClass() { return CopyOptions.class; }

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CopyOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "atomicMove":
          if (member.getValue() instanceof Boolean) {
            obj.setAtomicMove((Boolean)member.getValue());
          }
          break;
        case "copyAttributes":
          if (member.getValue() instanceof Boolean) {
            obj.setCopyAttributes((Boolean)member.getValue());
          }
          break;
        case "nofollowLinks":
          if (member.getValue() instanceof Boolean) {
            obj.setNofollowLinks((Boolean)member.getValue());
          }
          break;
        case "replaceExisting":
          if (member.getValue() instanceof Boolean) {
            obj.setReplaceExisting((Boolean)member.getValue());
          }
          break;
      }
    }
  }

   static void toJson(CopyOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(CopyOptions obj, java.util.Map<String, Object> json) {
    json.put("atomicMove", obj.isAtomicMove());
    json.put("copyAttributes", obj.isCopyAttributes());
    json.put("nofollowLinks", obj.isNofollowLinks());
    json.put("replaceExisting", obj.isReplaceExisting());
  }
}
