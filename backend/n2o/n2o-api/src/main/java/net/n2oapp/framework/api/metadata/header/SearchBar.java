package net.n2oapp.framework.api.metadata.header;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.meta.widget.WidgetDataProvider;

/**
 * Клиентская модель панели поиска
 */
@Getter
@Setter
public class SearchBar implements Compiled {
    @JsonProperty
    private String urlFieldId;
    @JsonProperty
    private String labelFieldId;
    @JsonProperty
    private String iconFieldId;
    @JsonProperty
    private String descrFieldId;
    @JsonProperty
    private SearchPageLocation searchPageLocation;
    @JsonProperty
    private WidgetDataProvider dataProvider;

    @Setter
    @Getter
    public static class SearchPageLocation implements Compiled {
        @JsonProperty
        private String url;
        @JsonProperty
        private String searchQueryName;
        @JsonProperty
        private LinkType linkType;
    }

    public enum LinkType {
        inner, outer
    }
}
