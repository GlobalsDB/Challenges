<#import "macros/common.ftl" as common>

<div class="menu-sections">
    <ul>
    <#list links as link>
        <li>
            <#if link.selected>
                <a class="selected" href="${link.address}">${link.text}</a>
                <#else>
                    <a href="${link.address}">${link.text}</a>
            </#if>
        </li>
    </#list>
    </ul>
</div>
