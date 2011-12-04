<#setting url_escaping_charset='UTF-8'>
<#macro page>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="-1"/>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>

    <title>${pageTitle}</title>

    <link href="${home}/image/favicon.ico" rel="icon" type="image/x-icon"/>
    <link rel="stylesheet" href="${home}/css/reset.css" type="text/css"/>
    <link rel="stylesheet" href="${home}/css/style.css" type="text/css"/>
    <link rel="stylesheet" href="${home}/css/header.css" type="text/css"/>
    <link rel="stylesheet" href="${home}/css/footer.css" type="text/css"/>
    <#list css as file>
        <link rel="stylesheet" href="${home}/${file}" type="text/css"/>
    </#list>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <#list js as file>
        <script type="text/javascript" src="${home}/${file}"></script>
    </#list>
</head>

<body>
<div class="mainbody">
    <div class="header">
    <@frame name="topMenuFrame"/>
    </div>

    <#if user??>
    <#--<@frame name="menuFrame"/>-->
    <@frame name="sectionMenuFrame"/>
    </#if>

    <div class="content">
        <#nested>
    </div>

    <div class="footer">
        <div>Globals Challenge #2 - Managing Data</div>
        <br/>

        <div>Globals version: ${globalsVersion}</div>
    </div>
</div>

</body>

</html>
</#macro>

<#macro errorLabel text = "">
    <#if text?? && (text?length > 0)>
    <div class="field-error">${text!?html}</div>
    </#if>
</#macro>

<#macro subscript error = "" hint = "" clazz = "under">
    <#if (error?? && (error?length > 0)) || (hint?? && (hint?length > 0))>
    <tr>
        <td>&nbsp;</td>
        <td>
            <div class="${clazz}">
                <#if error?? && (error?length > 0)>
            <@errorLabel text=error/>
            <#else>
                ${hint}
                </#if>
            </div>
        </td>
    </tr>
    </#if>
</#macro>

<#macro onLoad>
<script type="text/javascript">
    $(document).ready(function() {
        <#nested/>
    });
</script>
</#macro>
