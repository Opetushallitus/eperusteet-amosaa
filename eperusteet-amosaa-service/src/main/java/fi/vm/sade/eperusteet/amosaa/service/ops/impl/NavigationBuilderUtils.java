package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.utils.CollectionUtil;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class NavigationBuilderUtils {

    public static NavigationNodeDto reOrderTuvaKoulutuksenOsat(NavigationNodeDto navigationNodeDto) {
        Optional<NavigationNodeDto> koulutuksenosatNode = CollectionUtil.treeToStream(navigationNodeDto, NavigationNodeDto::getChildren)
                .filter(node -> node.getType().equals(NavigationType.koulutuksenosat)).findFirst();
        koulutuksenosatNode.ifPresent(nodeDto -> nodeDto.setChildren(CollectionUtil.treeToStream(navigationNodeDto, NavigationNodeDto::getChildren)
                .filter(node -> node.getType().equals(NavigationType.koulutuksenosa))
                .collect(Collectors.toList())));

        return navigationNodeDto;

    }
}
