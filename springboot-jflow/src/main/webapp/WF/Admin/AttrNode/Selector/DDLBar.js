﻿
var optionKey = 0;
function InitBar(key) {

    optionKey = key;

    var nodeID = GetQueryString("FK_Node");
    var str = nodeID.substr(nodeID.length - 2);
    var isSatrtNode = false;
    if (str == "01")
        isSatrtNode = true;

    // var html = "<div style='background-color:Silver' > 请选择访问规则: ";
    var html = "<div style='padding:5px' >接受人范围限定: ";

    html += "<select id='changBar' onchange='changeOption()'>";

    html += "<option value=null  disabled='disabled'>+按组织结构限定范围</option>";

    html += "<option value=" + SelectorModel.Station + ">&nbsp;&nbsp;&nbsp;&nbsp;按照岗位</option>";
    html += "<option value=" + SelectorModel.Dept + " >&nbsp;&nbsp;&nbsp;&nbsp;按部门计算</option>";
    html += "<option value=" + SelectorModel.Emp + " >&nbsp;&nbsp;&nbsp;&nbsp;按人员计算</option>";
    html += "<option value=" + SelectorModel.SQL + " >&nbsp;&nbsp;&nbsp;&nbsp;按SQL计算</option>";
    html += "<option value=" + SelectorModel.SQLTemplate + " >&nbsp;&nbsp;&nbsp;&nbsp;按SQL模板计算</option>";
    html += "<option value=" + SelectorModel.GenerUserSelecter + " >&nbsp;&nbsp;&nbsp;&nbsp;使用通用人员选择器</option>";
    html += "<option value=" + SelectorModel.DeptAndStation + ">&nbsp;&nbsp;&nbsp;&nbsp;按部门与岗位的交集</option>";

    html += "<option value=null  disabled='disabled'>+其他</option>";
    html += "<option value=" + SelectorModel.Url + ">&nbsp;&nbsp;&nbsp;&nbsp;自定义URL</option>";
    html += "<option value=" + SelectorModel.AccepterOfDeptStationEmp + ">&nbsp;&nbsp;&nbsp;&nbsp;使用通用部门岗位人员选择器（开发中）</option>";
    html += "<option value=" + SelectorModel.AccepterOfDeptStationEmp + ">&nbsp;&nbsp;&nbsp;&nbsp;按岗位智能计算(操作员所在部门)（开发中）</option>";

    html += "</select >";

    html += "<input  id='Btn_Save' type=button onclick='Save()' value='保存' />";
//    html += "<input type=button onclick='AdvSetting()' value='高级设置' />";
    html += "<input type=button onclick='Help()' value='我需要帮助' />";
    html += "</div>";

    document.getElementById("bar").innerHTML = html;

    $("#changBar option[value='" + optionKey + "']").attr("selected", "selected");


}

function OldVer() {

    var nodeID = GetQueryString("FK_Node");
    var flowNo = GetQueryString("FK_Flow");

    var url = '../NodeAccepterRole.aspx?FK_Flow=' + flowNo + '&FK_Node=' + nodeID;
    window.location.href = url;
}
function Help() {

    var url = "";
    switch (optionKey) {
        case SelectorModel.Station:
            url = 'http://bbs.ccflow.org/showtopic-131376.aspx';
            break;
        case SelectorModel.Dept:
            url = 'http://bbs.ccflow.org/showtopic-131376.aspx';
            break;
        default:
            url = "http://ccbpm.mydoc.io/?v=5404&t=17906";
            break;
    }

    window.open(url);
}

//通用的设置岗位的方法。for admin.

function OpenDot2DotStations() {

    var nodeID = GetQueryString("FK_Node");

    var url = "../../../Comm/RefFunc/Dot2Dot.htm?EnName=BP.WF.Template.NodeSheet&Dot2DotEnsName=BP.WF.Template.NodeStations";
    url += "&AttrOfOneInMM=FK_Node&AttrOfMInMM=FK_Station&EnsOfM=BP.WF.Port.Stations";
    url += "&DefaultGroupAttrKey=FK_StationType&NodeID=" + nodeID + "&PKVal=" + nodeID;

    OpenEasyUiDialogExt(url, '设置岗位', 800, 500, true);
}

function changeOption() {
    var nodeID = GetQueryString("FK_Node");
    var obj = document.getElementById("changBar");
    var sele = obj.options;
    var index = obj.selectedIndex;
    var optionKey = 0;
    if (index > 1) {
        optionKey = sele[index].value
    }

    var roleName = "";
    switch (parseInt(optionKey)) {
        case SelectorModel.Station:
            roleName = "0.Station.htm";
            break;
        case SelectorModel.Dept:
            roleName = "1.Dept.htm";
            break;
        case SelectorModel.Emp:
            roleName = "2.Emp.htm";
            break;
        case SelectorModel.SQL:
            roleName = "3.SQL.htm";
            break;
        case SelectorModel.SQLTemplate:
            roleName = "4.SQLTemplate.htm";
            break;
        case SelectorModel.GenerUserSelecter:
            roleName = "5.GenerUserSelecter.htm";
            break;
        case SelectorModel.DeptAndStation:
            roleName = "6.DeptAndStation.htm";
            break;
        case SelectorModel.Url:
            roleName = "7.Url.htm";
            break;
        case SelectorModel.BySpecNodeEmp:
            roleName = "8.AccepterOfDeptStationEmp.htm";
            break;
        case SelectorModel.DeptAndStation:
            roleName = "9.AccepterOfDeptStationOfCurrentOper.htm";
            break;
        default:

            roleName = "0.Station.htm";
            break;
    }

    window.location.href = roleName + "?FK_Node=" + nodeID;
}
function SaveAndClose() {
    Save();
    window.close();
}

//打开窗体.
function OpenEasyUiDialogExt(url, title, w, h, isReload) {

    OpenEasyUiDialog(url, "eudlgframe", title, w, h, "icon-property", true, null, null, null, function () {
        if (isReload == true) {
            window.location.href = window.location.href;
        }
    });
}

//高级设置.
function AdvSetting() {

    var nodeID = GetQueryString("FK_Node");
    var url = "AdvSetting.htm?FK_Node=" + nodeID + "&M=" + Math.random();
    OpenEasyUiDialogExt(url, "高级设置", 600, 500, false);
}