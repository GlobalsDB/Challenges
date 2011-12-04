<#import "macros/common.ftl" as common>

<@common.page>
<div class="login-form">
    <form action="" method="post">
        <div><input type="hidden" name="action" value="register"/></div>
        <table>
            <caption class="caption">{{Registration}}</caption>

            <tr>
                <td class="field-names">
                    {{Login}}:
                </td>
                <td>
                    <input class="textbox" name="login" value="${login!}"/>
                </td>
            </tr>
        <@common.subscript error="${error__login!}"/>
            <tr>
                <td class="field-names">
                    {{Password}}:
                </td>
                <td>
                    <input class="textbox" type="password" name="password"/>
                </td>
            </tr>
        <@common.subscript error="${error__password!}"/>
            <tr>
                <td class="field-names">
                    {{Password confirmation}}:
                </td>
                <td>
                    <input class="textbox" type="password" name="passwordConfirmation"/>
                </td>
            </tr>
        <@common.subscript error="${error__passwordConfirmation!}"/>
            <tr>
                <td colspan="2" class="button">
                    <input type="submit" value="{{Register}}"/>
                </td>
            </tr>
        </table>
    </form>
</div>
</@common.page>

<@common.onLoad>
$(".textbox:first").focus();
</@common.onLoad>
