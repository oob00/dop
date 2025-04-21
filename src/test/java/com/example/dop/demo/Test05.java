package com.example.dop.demo;

import com.github.underscore.U;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Test05 {

    @Test
    public void test() {
        Map<String, Object> data1 = U.fromJson("""
        { 
            "a": {
                "x": 1,
                "y": [2,3],
                "z": 4
            }
        }
        """);

        Map<String, Object> data2 = U.fromJson("""
        { 
            "a": {
                "x": 2,
                "y": [2,4],
                "z": 4
            }
        }
        """);

        Map<String, Object> result = diff(data1, data2);

        System.out.println(result);
    }

    @Test
    public void test5_7() {
        Map<String, Object> library = U.fromJson("""
        {
          "catalog": {
            "booksByIsbn": {
              "978-1779501127": {
                "isbn": "978-1779501127",
                "title": "Watchmen",
                "publicactionYear": 1987,
                "authorIds": ["alan-moore", "dave-gibbons"]
              }
            },
            "authorsById": {
              "alan-moore": {
                "name": "Alan Moore",
                "bookIsbns": ["978-1779501127"]
              },
              "dave-gibbons": {
                "name": "Dave Gibbons",
                "bookIsbns": ["978-1779501127"]
              }
            }
          }
        }
        """);

        Map<String, Object> previous = library;

        ImmutableDataStore store = new ImmutableDataStore(library);
        Map<String, Object> next = store.set("catalog.booksByIsbn.978-1779501127.publicactionYear", 1986).getData();

        Map<String, Object> librayWithUpdatedTitle = store.set("catalog.booksByIsbn.978-1779501127.title", "The Watchmen").getData();

        ImmutableDataStore newStore = new ImmutableDataStore(librayWithUpdatedTitle);
        Map<String, Object> current = newStore.set("catalog.authorsById.dave-gibbons.name", "David Chester Gibbons").getData();

        Map<String, Object> result = diff(previous, next);

        System.out.println("Result 1:");
        System.out.println(U.toJson(result));

        Map<String, Object> result2 = diff(previous, current);

        System.out.println("\nResult 2:");
        System.out.println(U.toJson(result2));
    }

    private Map<String, Object> diffObjects(Map<String, Object> data1, Map<String, Object> data2){
        Map<String, Object> emptyObject = new HashMap<>();
        if(data1 == data2){
            return emptyObject;
        }

        List<String> keys = U.union(new ArrayList<>(U.keys(data1)), new ArrayList<>(U.keys(data2)));

        return U.reduce(keys, (acc,k)->{
            Object value1 = U.get(data1, k);
            Object value2 = U.get(data2, k);

            // 두 값이 모두 Map인 경우에만 재귀적으로 비교
            if (value1 instanceof Map && value2 instanceof Map) {
                Map<String, Object> res = diff((Map<String, Object>)value1, (Map<String, Object>)value2);

                if (res == null || (U.isObject(res) && U.isEmpty(res))) {
                    return acc;
                }
                ImmutableDataStore store = new ImmutableDataStore(acc);
                return store.set(k, res).getData();
            }
            // 두 값이 모두 List인 경우
            else if (value1 instanceof List && value2 instanceof List) {
                List<Object> list1 = (List<Object>)value1;
                List<Object> list2 = (List<Object>)value2;
                List<Object> diffList = new ArrayList<>();
                
                // 더 긴 리스트의 길이만큼 비교
                int maxLength = Math.max(list1.size(), list2.size());
                for (int i = 0; i < maxLength; i++) {
                    if (i >= list1.size() || i >= list2.size()) {
                        // 한쪽 리스트가 더 긴 경우
                        diffList.add(i < list2.size() ? list2.get(i) : null);
                    } else if (!Objects.equals(list1.get(i), list2.get(i))) {
                        // 값이 다른 경우
                        diffList.add(list2.get(i));
                    } else {
                        // 값이 같은 경우
                        diffList.add(null);
                    }
                }
                
                // null이 아닌 값이 하나라도 있는 경우에만 결과에 포함
                if (diffList.stream().anyMatch(Objects::nonNull)) {
                    ImmutableDataStore store = new ImmutableDataStore(acc);
                    return store.set(k, diffList).getData();
                }
                return acc;
            }
            // 두 값이 다르면 새로운 값으로 업데이트
            else if (!Objects.equals(value1, value2)) {
                ImmutableDataStore store = new ImmutableDataStore(acc);
                return store.set(k, value2).getData();
            }
            // 값이 같으면 변경 없음
            return acc;
        }, emptyObject);
    }

    private Map<String, Object> diff(Map<String, Object> data1, Map<String, Object> data2){
        if(U.isObject(data1) && U.isObject(data2)){
            return diffObjects(data1, data2);
        }
        if (!Objects.equals(data1, data2)){
            return data2;
        }
        return null;
    }

    private Map<String, Object> mergeMaps(Map<String, Object> map1, Map<String, Object> map2) {
        Map<String, Object> result = new HashMap<>(map1);
        
        for (Map.Entry<String, Object> entry : map2.entrySet()) {
            String key = entry.getKey();
            Object value2 = entry.getValue();
            Object value1 = result.get(key);
            
            if (value1 instanceof Map && value2 instanceof Map) {
                // 두 값이 모두 Map인 경우 재귀적으로 병합
                result.put(key, mergeMaps((Map<String, Object>)value1, (Map<String, Object>)value2));
            } else if (value1 instanceof List && value2 instanceof List) {
                // 두 값이 모두 List인 경우
                List<Object> list1 = (List<Object>)value1;
                List<Object> list2 = (List<Object>)value2;
                List<Object> mergedList = new ArrayList<>(list1);
                
                // list2에서 list1에 없는 값만 추가
                for (Object item : list2) {
                    if (!mergedList.contains(item)) {
                        mergedList.add(item);
                    }
                }
                result.put(key, mergedList);
            } else {
                // 기본적인 값인 경우 map2의 값으로 덮어쓰기
                result.put(key, value2);
            }
        }
        
        return result;
    }

}
