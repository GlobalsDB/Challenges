<#import "macros/common.ftl" as common>

<#macro logsFilter>
<div class="logs-filter">
    <form action="" method="post">
        <input type="hidden" name="action" value="applyFilter"/>
        <span style="font-weight: bold;">{{Show}}</span>:
        <span style="margin-left: -1em;">
            <#if showLogsConfig.showDebug>
                <input type="checkbox" name="showDebug" checked/>{{Debug}}
                <#else>
                    <input type="checkbox" name="showDebug"/>{{Debug}}
            </#if>
            <#if showLogsConfig.showInfo>
                <input type="checkbox" name="showInfo" checked/>{{Info}}
                <#else>
                    <input type="checkbox" name="showInfo"/>{{Info}}
            </#if>
            <#if showLogsConfig.showWarn>
                <input type="checkbox" name="showWarn" checked/>{{Warning}}
                <#else>
                    <input type="checkbox" name="showWarn"/>{{Warning}}
            </#if>
            <#if showLogsConfig.showError>
                <input type="checkbox" name="showError" checked/>{{Error}}
                <#else>
                    <input type="checkbox" name="showError"/>{{Error}}
            </#if>
            <#if showLogsConfig.showFatal>
                <input type="checkbox" name="showFatal" checked/>{{Fatal}}
                <#else>
                    <input type="checkbox" name="showFatal"/>{{Fatal}}
            </#if>
        </span>

    <#--<input type="submit" value="<@caption>Display</@caption>"/>-->
    </form>
</div>
</#macro>

<@common.page>

<@logsFilter/>

<table cellpadding="0" cellspacing="0" class="logs list-table">
    <thead>
    <tr>
        <th>{{Date}}</th>
        <th>{{Severity}}</th>
        <th>{{Message}}</th>
    </tr>
    </thead>
    <tbody>
        <#if logs?? && (logs?size > 0)>
            <#list logs as log>
                <#if log.severity == "DEBUG">
                <tr class="debug">
                    <#elseif log.severity == "INFO">
                    <tr class="info">
                    <#elseif log.severity == "WARN">
                    <tr class="warning">
                    <#elseif log.severity == "ERROR">
                    <tr class="error">
                    <#elseif log.severity == "FATAL">
                    <tr class="error">
                    <#else>
                    <tr>
                </#if>
                <td>${log.date?string("yyyy-MM-dd HH:mm:ss.SSS")}</td>
                <td>${log.severity}</td>
                <td style="text-align: left;">${log.message}</td>
            </tr>
            </#list>
            <#else>
            <tr>
                <td colspan="3" style="text-align: left;">{{Empty}}.</td>
            </tr>
        </#if>
    </tbody>
</table>
    <#if logs?? && (logs?size > 25)>
    <@logsFilter/>
    </#if>
</@common.page>
