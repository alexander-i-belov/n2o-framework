package net.n2oapp.framework.boot.graphql;

import lombok.Setter;
import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.data.MapInvocationEngine;
import net.n2oapp.framework.api.metadata.dataprovider.N2oGraphqlDataProvider;
import net.n2oapp.framework.engine.data.QueryUtil;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.n2oapp.framework.engine.data.QueryUtil.replaceListPlaceholder;
import static net.n2oapp.framework.engine.data.QueryUtil.replacePlaceholder;

/**
 * GraphQL провайдер данных
 */
@Setter
public class GraphqlDataProviderEngine implements MapInvocationEngine<N2oGraphqlDataProvider> {

    private String endpoint;
    private RestTemplate restTemplate;
    private Map<String, Object> payload = new HashMap<>();

    @Override
    public Class<? extends N2oGraphqlDataProvider> getType() {
        return N2oGraphqlDataProvider.class;
    }

    @Override
    public Object invoke(N2oGraphqlDataProvider invocation, Map<String, Object> data) {
        return execute(prepareQuery(invocation, data), initEndpoint(invocation.getEndpoint()), data);
    }

    private String prepareQuery(N2oGraphqlDataProvider invocation, Map<String, Object> data) {
        if (invocation.getQuery() == null)
            throw new N2oGraphqlException("Запрос не найден");
        return resolvePlaceHolders(invocation, data);
    }

    private String resolvePlaceHolders(N2oGraphqlDataProvider invocation, Map<String, Object> data) {
        String query = invocation.getQuery();
        Map<String, Object> args = new HashMap<>(data);
        query = replaceListPlaceholder(query, "{{select}}", args.remove("select"), "", QueryUtil::reduceSpace);
        query = resolveFilters(query, args, invocation.getFilterSeparator());
        for (String key : data.keySet()) {
            Object value = args.get(key);
            query = replacePlaceholder(query, "{{" + key + "}}", value instanceof String ? "\"" + value + "\"" : value, "");
        }
        return query;
    }

    private String resolveFilters(String query, Map<String, Object> args, String filterSeparator) {
        if (((List<Object>) args.get("filters")).size() > 1 && filterSeparator == null)
            throw new N2oGraphqlException("Не задан сепаратор для фильтров");
        query = replaceListPlaceholder(query, "{{filters}}", args.remove("filters"),
                "", (a, b) -> QueryUtil.reduceSeparator(a, b, filterSeparator));
        return query;
    }

    private Object execute(String query, String endpoint, Map<String, Object> data) {
        initPayload(query, data);
        return restTemplate.postForObject(endpoint, payload, DataSet.class);
    }

    private void initPayload(String query, Map<String, Object> data) {
        payload.put("query", query);
        payload.put("variables", initVariables(query, data));
    }

    private Object initVariables(String query, Map<String, Object> data) {
        Set<String> variables = extractVariables(query);
        DataSet result = new DataSet();
        for (String variable : variables) {
            if (!data.containsKey(variable))
                throw new N2oGraphqlException("Переменная запроса не найдена");
            result.add(variable, data.get(variable));
        }
        return result;
    }

    private Set<String> extractVariables(String query) {
        Set<String> variables = new HashSet<>();
        Pattern pattern = Pattern.compile("\\$\\w+");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find())
            variables.add(query.substring(matcher.start(), matcher.end()).replace("$", ""));
        return variables;
    }

    private String initEndpoint(String invocationEndpoint) {
        if (invocationEndpoint != null)
            return invocationEndpoint;
        return endpoint;
    }
}
