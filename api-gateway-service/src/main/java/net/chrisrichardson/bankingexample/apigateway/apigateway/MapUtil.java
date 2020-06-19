package net.chrisrichardson.bankingexample.apigateway.apigateway;

import java.util.List;
import java.util.Map;

public class MapUtil {
  static void mergeIntoMap(Map<String, Object> destination, Map<String, Object> source) {
    source.forEach((k, v) -> {
      destination.merge(k, v, MapUtil::combineValues);
    });
  }

  private static Object combineValues(Object originalValue, Object newValue) {
    if (originalValue instanceof List) {
      ((List) originalValue).addAll((List) newValue);
    } else if (originalValue instanceof Map) {
      mergeIntoMap((Map<String, Object>) originalValue, (Map<String, Object>) newValue);
    }
    return originalValue;
  }

}
