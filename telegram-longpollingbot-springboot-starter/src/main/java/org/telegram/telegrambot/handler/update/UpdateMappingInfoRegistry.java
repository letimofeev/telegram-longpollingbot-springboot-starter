package org.telegram.telegrambot.handler.update;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class UpdateMappingInfoRegistry {

    private final List<UpdateMappingMethodInfo> mappingMethodInfos = new ArrayList<>();

    public void addMappingInfo(UpdateMappingMethodInfo mappingMethodInfo) {
        mappingMethodInfos.add(mappingMethodInfo);
    }

    public List<UpdateMappingMethodInfo> getMappingInfos() {
        return Collections.unmodifiableList(mappingMethodInfos);
    }

    public List<UpdateMappingMethodInfo> getFilteredMappingInfos(UpdateMappingMethodInfoFilterChain filterChain) {
        List<Predicate<UpdateMappingMethodInfo>> filters = filterChain.getFilters();
        return mappingMethodInfos.stream()
                .filter(filters.stream().reduce(i -> true, Predicate::and))
                .collect(Collectors.toList());
    }
}
