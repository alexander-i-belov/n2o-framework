package net.n2oapp.framework.config.metadata.compile.context;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.global.view.page.N2oDialog;
import net.n2oapp.framework.api.metadata.meta.page.Dialog;

/**
 * Контекст компиляции диалога подтверждения действия
 */
@Getter
@Setter
public class DialogContext extends BaseCompileContext<Dialog, N2oDialog> {

    /**
     * Идентификатор родительской страницы
     */
    private String parentPageId;
    /**
     * Идентификатор родительского источника данных
     */
    private String parentSourceDatasourceId;

    /**
     * Идентификатор родительского виджета
     */
    private String parentClientWidgetId;

    /**
     * Идентификатор клиентского виджета
     */
    @Deprecated
    private String clientWidgetId;

    /**
     * Идентификатор объекта, в котором находится операция
     */
    private String objectId;

    public DialogContext(String route, String sourceId) {
        super(route, sourceId, N2oDialog.class, Dialog.class);
    }
}
