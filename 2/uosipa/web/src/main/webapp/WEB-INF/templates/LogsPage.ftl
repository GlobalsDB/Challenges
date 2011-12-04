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
            <#if showLogsConfig.showUnknown>
                <input type="checkbox" name="showUnknown" checked/>{{Unknown}}
                <#else>
                    <input type="checkbox" name="showUnknown"/>{{Unknown}}
            </#if>
        </span>
    </form>
</div>
</#macro>

<#macro timePicker>
<div class="timepicker">
    <label for="fromDate">From date</label>
    <input class="date" type="text" id="fromDate" name="fromDate"/>
    <label for="fromTime">time</label>
    <input class="time" type="text" id="fromTime" name="fromTime"/>
    <label for="toDate">to date</label>
    <input class="date" type="text" id="toDate" name="toDate"/>
    <label for="toTime">time</label>
    <input class="time" type="text" id="toTime" name="toTime"/>
</div>
</#macro>

<#macro logsPaginate>
<div class="logs-filter" style="text-align: right;">
    <span class="logs-paginate">
        <#if prevId != -1>
        <span><a
                href="?action=applyFilter&showDebug=${showLogsConfig.showDebug?string("on", "off")}&showInfo=${showLogsConfig.showInfo?string("on", "off")}&showWarning=${showLogsConfig.showWarning?string("on", "off")}&showError=${showLogsConfig.showError?string("on", "off")}&limit=${showLogsConfig.onPage}&serverId=${showLogsConfig.serverId!}&threadId=${showLogsConfig.threadId!}&startFrom=${prevId?c}">‹
            {{Prev}} ${showLogsConfig.onPage}</a></span>
        <#else>
        <span class="disabled">‹ {{Prev}} ${showLogsConfig.onPage}</span>
        </#if>
        <#if logsFirstId??>
        <strong>${logsFirstId?c}-${logsLastId?c}</strong>
        </#if>
        <#if nextId != -1>
        <span><a
                href="?action=applyFilter&showDebug=${showLogsConfig.showDebug?string("on", "off")}&showInfo=${showLogsConfig.showInfo?string("on", "off")}&showWarning=${showLogsConfig.showWarning?string("on", "off")}&showError=${showLogsConfig.showError?string("on", "off")}&limit=${showLogsConfig.onPage}&serverId=${showLogsConfig.serverId!}&threadId=${showLogsConfig.threadId!}&startFrom=${nextId?c}">{{Next}} ${showLogsConfig.onPage}
            ›</a></span>
        <#else>
        <span class="disabled">{{Next}} ${showLogsConfig.onPage} ›</span>
        </#if>
    </span>
</div>
</#macro>

<#macro uploadLogFile>
<div class="upload-log-file">
    <form enctype="multipart/form-data" action="" method="post">
        <span>{{Upload log file}}:</span>

        <input type="hidden" name="action" value="uploadLogFile"/>
        <input type="file" name="logFile"/>
        <input type="submit" class="button" value="{{Upload}}"/>
    </form>
</div>
</#macro>

<@common.page>

<@uploadLogFile/>
<@logsFilter/>
<#--<@timePicker/>-->

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
                    <#elseif log.severity == "UNKNOWN">
                    <tr class="unknown">
                    <#else>
                    <tr>
                </#if>
                <#if log.date??>
                    <td>${log.date?string("yyyy-MM-dd HH:mm:ss.SSS")}</td>
                    <#else>
                        <td></td>
                </#if>
                <td>${log.severity}</td>
                <#if log.message??>
                    <td style="text-align: left;">${log.message}</td>
                    <#else>
                        <td style="text-align: left;"></td>
                </#if>
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
