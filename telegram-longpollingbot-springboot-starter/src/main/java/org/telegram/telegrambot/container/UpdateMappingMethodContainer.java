package org.telegram.telegrambot.container;

import org.springframework.stereotype.Component;
import org.telegram.telegrambot.dto.MethodTargetPair;

import java.util.List;

@Component
public class UpdateMappingMethodContainer extends AbstractContainer<String, List<MethodTargetPair>> {
}
