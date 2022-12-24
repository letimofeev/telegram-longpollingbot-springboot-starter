package org.telegram.telegrambot.handler.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class UpdateMappingMethodInfoFilterChain {

    private final List<Predicate<UpdateMappingMethodInfo>> filters = new ArrayList<>();

    public UpdateMappingMethodInfoFilterChain() {
    }

    public void addFilter(Predicate<UpdateMappingMethodInfo> filterPredicate) {
        filters.add(filterPredicate);
    }

    public List<Predicate<UpdateMappingMethodInfo>> getFilters() {
        return Collections.unmodifiableList(filters);
    }
}
