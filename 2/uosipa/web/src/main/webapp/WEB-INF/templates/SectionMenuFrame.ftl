<div class="menu-items">
    <ul>
    <#list links as link>
        <#if link.selected>
            <li class="selected">
                <a href="${link.address}">${link.text}</a>
            </li>
            <#else>
                <li class="unselected">
                    <a href="${link.address}">${link.text}</a>
                </li>
        </#if>
    </#list>
    </ul>
</div>
