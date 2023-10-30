package org.pipeData.config.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class OpenBrowserCondition  extends AnyNestedCondition {
    public OpenBrowserCondition(ConfigurationPhase configurationPhase) {
        super(configurationPhase);
    }

    @ConditionalOnProperty(value = "open.browser.enabled", havingValue = "true")
    static class EnableOpenBrowser {}

    @ConditionalOnProperty(value = "open.browser.enabled", havingValue = "false", matchIfMissing = true)
    static class DisableOpenBrowser {}

}
