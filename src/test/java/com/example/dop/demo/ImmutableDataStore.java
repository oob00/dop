package com.example.dop.demo;

import java.util.*;

public class ImmutableDataStore {
    private final Map<String, Object> data;

    public ImmutableDataStore(Map<String, Object> data) {
        this.data = data;
    }

    public ImmutableDataStore set(List<String> path, Object value) {
        return new ImmutableDataStore(structuralSet(this.data, path, value, 0));
    }

    public ImmutableDataStore set(String dottedPath, Object value) {
        return set(Arrays.asList(dottedPath.split("\\.")), value);
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    private Map<String, Object> structuralSet(Map<String, Object> current, List<String> path, Object value, int index) {
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

            // 다음 키가 숫자인지 확인 (배열 인덱스)
            String nextKey = path.get(index + 1);
            boolean isNextKeyArrayIndex = nextKey.matches("\\d+");

            if (isNextKeyArrayIndex) {
                // 배열/리스트 처리
                if (!(next instanceof List)) {
                    next = new ArrayList<>();
                }
                List<Object> nextList = (List<Object>) next;
                int arrayIndex = Integer.parseInt(nextKey);

                // 리스트 크기가 충분한지 확인
                while (nextList.size() <= arrayIndex) {
                    nextList.add(new HashMap<>());
                }

                Object element = nextList.get(arrayIndex);
                if (!(element instanceof Map)) {
                    element = new HashMap<>();
                }

                // 재귀 호출로 하위 요소 업데이트
                Map<String, Object> updatedElement = structuralSet(
                        (Map<String, Object>) element,
                        path.subList(index + 1, path.size()),
                        value,
                        1
                );

                // 요소가 변경되지 않았다면 현재 맵 반환
                if (element == updatedElement) {
                    return current;
                }

                // 새로운 리스트 생성 및 요소 업데이트
                List<Object> newList = new ArrayList<>(nextList);
                newList.set(arrayIndex, updatedElement);

                // 새로운 맵 생성 및 업데이트된 리스트 설정
                Map<String, Object> newMap = new HashMap<>(current);
                newMap.put(key, newList);
                return newMap;
            } else {
                // 일반 맵 처리
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
    }
}
