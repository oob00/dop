package com.example.dop.demo;

import com.github.underscore.U;

import java.util.*;

public class UI {

    public static Map<String, Object> set(Map<String, Object> original, String path, Object value) {
        return set(original, U.stringToPath(path), value);
    }

    public static Map<String, Object> set(Map<String, Object> original, List<String> paths, Object value) {
        return structuralSet(original, paths, value, 0);
    }

    private static Map<String, Object> structuralSet(Map<String, Object> current, List<String> path, Object value, int index) {
        String key = path.get(index);

        if (index == path.size() - 1) {
            // 최종 값이 변경되는 경우
            Object oldValue = current.get(key);
            if (Objects.equals(oldValue, value)) {
                return current;
            }

            // 변경된 값만 새로운 맵에 설정
            Map<String, Object> newMap = new HashMap<>(current);
            newMap.put(key, value);
            return newMap;
        } else {
            // 중간 경로 처리
            Object next = current.get(key);
            Map<String, Object> nextMap;
            
            if (next instanceof Map) {
                nextMap = (Map<String, Object>) next;
            } else {
                nextMap = new HashMap<>();
            }

            // 하위 경로 업데이트
            Map<String, Object> updatedChild = structuralSet(nextMap, path, value, index + 1);

            // 하위 맵이 변경되지 않았다면 현재 맵 반환 (참조 공유)
            if (nextMap == updatedChild) {
                return current;
            }

            // 하위 맵이 변경된 경우에만 새로운 맵 생성
            Map<String, Object> newMap = new HashMap<>(current);
            newMap.put(key, updatedChild);
            return newMap;
        }
    }

    // 깊은 복사
    @SuppressWarnings("unchecked")
    private static Map<String, Object> deepCopy(Map<String, Object> original) {
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof Map) {
                copy.put(entry.getKey(), deepCopy((Map<String, Object>) val));
            } else if (val instanceof List) {
                copy.put(entry.getKey(), deepCopyList((List<Object>) val));
            } else {
                copy.put(entry.getKey(), val);
            }
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> deepCopyList(List<Object> original) {
        List<Object> copy = new ArrayList<>();
        for (Object val : original) {
            if (val instanceof Map) {
                copy.add(deepCopy((Map<String, Object>) val));
            } else if (val instanceof List) {
                copy.add(deepCopyList((List<Object>) val));
            } else {
                copy.add(val);
            }
        }
        return copy;
    }

}
