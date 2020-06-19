package net.chrisrichardson.bankingexample.apigateway.apigateway;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapUtilTest {

  @Test
  public void shouldMergeMaps() {
    Map<String, Object> m1 = map("a", 1);
    Map<String, Object> m2 = map("b", 2);
    MapUtil.mergeIntoMap(m1, m2);
    assertEquals(map("a", 1, "b", 2), m1);
  }

  @Test
  public void shouldNotOverwriteScalar() {
    Map<String, Object> m1 = map("a", 1);
    Map<String, Object> m2 = map("a", 2);
    MapUtil.mergeIntoMap(m1, m2);
    assertEquals(map("a", 1), m1);
  }

  @Test
  public void shouldAppendLists() {
    Map<String, Object> m1 = map("a", list(1));
    Map<String, Object> m2 = map("a", list(2));
    MapUtil.mergeIntoMap(m1, m2);
    assertEquals(map("a", list(1, 2)), m1);
  }

  @Test
  public void shouldMergeNestedMaps() {
    Map<String, Object> m1 = map("a", map("c", 2));
    Map<String, Object> m2 = map("a", map("d", 3));
    MapUtil.mergeIntoMap(m1, m2);
    assertEquals(map("a", map("c", 2, "d", 3)), m1);
  }

  private Object list(Object... args) {
    return new ArrayList<>(Arrays.asList(args));
  }

  private Map<String, Object> map(String k, Object v) {
    Map<String, Object> m = new HashMap<>();
    m.put(k, v);
    return m;
  }

  private Map<String, Object> map(String k1, Object v1, String k2, Object v2) {
    Map<String, Object> m = new HashMap<>();
    m.put(k1, v1);
    m.put(k2, v2);
    return m;
  }

}