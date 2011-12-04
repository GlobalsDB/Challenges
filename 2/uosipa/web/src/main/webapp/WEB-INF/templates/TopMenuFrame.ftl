<#import "macros/common.ftl" as common>

<div class="header-right">
    <div class="top-menu">
    <#if user??>
    ${user.login} |
    </#if>

    <#list links as link>
        <a href="${link.address}">${link.text}</a>
        <#if link_has_next>|</#if>
    </#list>
    </div>
</div>
