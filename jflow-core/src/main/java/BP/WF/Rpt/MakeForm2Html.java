package BP.WF.Rpt;

import BP.DA.DBAccess;
import BP.DA.DataRow;
import BP.DA.DataTable;
import BP.DA.DataType;
import BP.Difference.ContextHolderUtils;
import BP.En.*;
import BP.Port.Emp;
import BP.Sys.*;
import BP.Sys.FrmUI.ExtImg;
import BP.Tools.AesEncodeUtil;
import BP.Tools.BaseFileUtils;
import BP.Tools.QrCodeUtil;
import BP.Tools.ZipCompress;
import BP.WF.ActionType;
import BP.WF.GenerWorkFlow;
import BP.WF.Node;
import BP.WF.NodeFormType;
import BP.WF.WFState;
import BP.WF.Template.FrmEnableRole;
import BP.WF.Template.FrmNode;
import BP.WF.Template.FrmNodes;
import BP.WF.Template.WhoIsPK;
import BP.Web.WebUser;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*; 
import javax.imageio.ImageIO;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

//import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.util.Hashtable;

/**

using ICSharpCode.SharpZipLib.Zip;

 * 
 */


/***
 * 
 * @author Administrator
 * 打印PDF翻译
 */
public class MakeForm2Html 
{
	/// <summary>
    /// 生成
    /// </summary>
    /// <param name="mapData"></param>
    /// <param name="frmID"></param>
    /// <param name="workid"></param>
    /// <param name="en"></param>
    /// <param name="path"></param>
    /// <param name="flowNo"></param>
    /// <returns></returns>
    public static StringBuilder GenerHtmlOfFree(MapData mapData, String frmID, long workid, Entity en, String path
    		, String flowNo ,String FK_Node,String basePath)throws Exception {
    
        StringBuilder sb = new StringBuilder();//  

        //字段集合.
        MapAttrs mapAttrs = new MapAttrs(frmID);
        Attrs attrs = en.getEnMap().getAttrs();

        String appPath = "";
        float wtX = MapData.GenerSpanWeiYi(mapData, 1200);
        float x = 0;

        /*region 输出Ele */
        FrmEles eles = mapData.getFrmEles();
        if (eles.size()>= 1)
        {
            for( FrmEle ele :eles.ToJavaList())
            {
            	  float y = ele.getY();
                  x = ele.getX() + wtX;
                  sb.append("<DIV id=" + ele.getMyPK() + " style='position:absolute;left:" + x + "px;top:" + y + "px;text-align:left;vertical-align:top' >");

                  sb.append("\t\n</DIV>");
            }
        }
        /*region 输出竖线与标签 & 超连接 Img.*/
        FrmLabs labs = mapData.getFrmLabs();
        for( FrmLab lab:labs.ToJavaList())
        {
              x = lab.getX() + wtX;
              sb.append("\t\n<DIV id=u2 style='position:absolute;left:" + x + "px;top:" + lab.getY() + "px;text-align:left;' >");
              sb.append("\t\n<span style='color:" + lab.getFontColorHtml()+ ";font-family: " + lab.getFontName() + ";font-size: " + lab.getFontSize() + "px;' >" + lab.getTextHtml() + "</span>");
              sb.append("\t\n</DIV>");
        }
      

        FrmLines lines = mapData.getFrmLines();
        for(FrmLine line :lines.ToJavaList() )
        {
        	 if (line.getX1() == line.getX2())
             {
            	  /* 一道竖线 */
                 float h = line.getY1() - line.getY2();
                 h = Math.abs(h);
                 if (line.getY1() < line.getY2())
                 {
                     x = line.getX1() + wtX;
                     sb.append("\t\n<img id='" + line.getMyPK() + "'  style=\"padding:0px;position:absolute; left:" + x + "px; top:" + line.getY1()+ "px; width:" + line.getBorderWidth() + "px; height:" + h + "px;background-color:" + line.getBorderColorHtml() + "\" />");
                 }
                 else
                 {
                     x = line.getX2() + wtX;
                     sb.append("\t\n<img id='" + line.getMyPK() + "'  style=\"padding:0px;position:absolute; left:" + x + "px; top:" + line.getY2() + "px; width:" + line.getBorderWidth() + "px; height:" + h + "px;background-color:" + line.getBorderColorHtml()  + "\" />");
                 }
             }
        	 else
             {
                 /* 一道横线 */
                 float w = line.getX2() - line.getX1();

                 if (line.getX1() < line.getX2())
                 {
                     x = line.getX1() + wtX;
                     sb.append("\t\n<img id='" + line.getMyPK() + "'  style=\"padding:0px;position:absolute; left:" + x + "px; top:" + line.getY1()+ "px; width:" + w + "px; height:" + line.getBorderWidth() + "px;background-color:" + line.getBorderColorHtml() + "\" />");
                 }
                 else
                 {
                     x = line.getX2() + wtX;
                     sb.append("\t\n<img id='" + line.getMyPK() + "'  style=\"padding:0px;position:absolute; left:" + x + "px; top:" + line.getY2() + "px; width:" + w + "px; height:" + line.getBorderWidth() + "px;background-color:" + line.getBorderColorHtml() + "\" />");
                 }
             }
        }
      

        FrmLinks links = mapData.getFrmLinks();
        for(FrmLink link :links.ToJavaList())
        {
        	  String url = link.getURL();
              if (url.contains("@"))
              {
                  for (MapAttr attr : mapAttrs.ToJavaList())
                  {
                      if (url.contains("@") == false)
                          break;
                      url = url.replace("@" + attr.getKeyOfEn(), en.GetValStrByKey(attr.getKeyOfEn()));
                  }
              }
              x = link.getX() + wtX;
              sb.append("\t\n<DIV id=u2 style='position:absolute;left:" + x + "px;top:" + link.getY() + "px;text-align:left;' >");
              sb.append("\t\n<span style='color:" + link.getFontColorHtml() + ";font-family: " + link.getFontName() + ";font-size: " + link.getFontSize() + "px;' > <a href=\"" + url + "\" target='" + link.getTarget() + "'> " + link.getText() + "</a></span>");
              sb.append("\t\n</DIV>");
        }
       

        FrmImgs imgs = mapData.getFrmImgs();
        for(FrmImg img :imgs.ToJavaList())
        {
            float y = img.getY();
            String imgSrc = "";

           //  ////#region 图片类型
            if (img.getHisImgAppType() == ImgAppType.Img)
            {
                //数据来源为本地.
                if (img.getImgSrcType() == 0)
                {
                    if (img.getImgPath().contains(";") == false)
                        imgSrc = img.getImgPath();
                }

                //数据来源为指定路径.
                if (img.getImgSrcType() == 1)
                {
                    //图片路径不为默认值
                    imgSrc = img.getImgURL();
                    if (imgSrc.contains("@"))
                    {
                        /*如果有变量*/
                        imgSrc = BP.WF.Glo.DealExp(imgSrc, en, "");
                    }
                }

                x = img.getX() + wtX;
                // 由于火狐 不支持onerror 所以 判断图片是否存在
                imgSrc = "icon.png";

                sb.append("\t\n<div id=" + img.getMyPK() + " style='position:absolute;left:" + x + "px;top:" + y + "px;text-align:left;vertical-align:top' >");
                if (DataType.IsNullOrEmpty(img.getLinkURL()) == false)
                    sb.append("\t\n<a href='" + img.getLinkURL() + "' target=" + img.getLinkTarget() + " ><img src='" + imgSrc + "'  onerror=\"this.src='/DataUser/ICON/CCFlow/LogBig.png'\"  style='padding: 0px;margin: 0px;border-width: 0px;width:" + img.getW() + "px;height:" + img.getH() + "px;' /></a>");
                else
                    sb.append("\t\n<img src='" + imgSrc + "'  onerror=\"this.src='/DataUser/ICON/CCFlow/LogBig.png'\"  style='padding: 0px;margin: 0px;border-width: 0px;width:" + img.getW() + "px;height:" + img.getH() + "px;' />");
                sb.append("\t\n</div>");
                
                continue;
            }
         

           //  ////#region 二维码
            
            if (img.getHisImgAppType() == ImgAppType.QRCode)
            {
                x = img.getX() + wtX;
                String pk = String.valueOf(en.getPKVal());
                String myPK = frmID + "_" + img.getMyPK() + "_" + pk;
                FrmEleDB frmEleDB = new FrmEleDB();
                frmEleDB.setMyPK(myPK);
                if (frmEleDB.RetrieveFromDBSources() == 0)
                {
                    //生成二维码
                }

                sb.append("\t\n<DIV id=" + img.getMyPK() + " style='position:absolute;left:" + x + "px;top:" + y + "px;text-align:left;vertical-align:top' >");
                sb.append("\t\n<img src='" + frmEleDB.getTag2() + "' style='padding: 0px;margin: 0px;border-width: 0px;width:" + img.getW() + "px;height:" + img.getH() + "px;' />");
                sb.append("\t\n</DIV>");

                continue;
            }
          //  ////#endregion

           //  ////#region 电子签章
            //图片类型
            if (img.getHisImgAppType() == ImgAppType.Seal)
            {
                //获取登录人岗位
                String stationNo = "";
                //签章对应部门
                String fk_dept = WebUser.getFK_Dept();
                //部门来源类别
                String sealType = "0";
                //签章对应岗位
                String fk_station = img.getTag0();
                //表单字段
                String sealField = "";
                String sql = "";

                //重新加载 可能有缓存
                img.RetrieveFromDBSources();
                //0.不可以修改，从数据表中取，1可以修改，使用组合获取并保存数据
                if ((img.getIsEdit() == 1))
                {
                   //  ////#region 加载签章
                    //如果设置了部门与岗位的集合进行拆分
                    if (!DataType.IsNullOrEmpty(img.getTag0()) && img.getTag0().contains("^") && img.getTag0().split("^").length == 4)
                    {
                        fk_dept = img.getTag0().split("^")[0];
                        fk_station = img.getTag0().split("^")[1];
                        sealType = img.getTag0().split("^")[2];
                        sealField = img.getTag0().split("^")[3];
                        //如果部门没有设定，就获取部门来源
                        if (fk_dept == "all")
                        {
                            //默认当前登陆人
                            fk_dept = WebUser.getFK_Dept();
                            //发起人
                            if (sealType == "1")
                            {
                                sql = "SELECT FK_Dept FROM WF_GenerWorkFlow WHERE WorkID=" + en.GetValStrByKey("OID");
                                fk_dept = DBAccess.RunSQLReturnString(sql);
                            }
                            //表单字段
                            if (sealType == "2" && !DataType.IsNullOrEmpty(sealField))
                            {
                                //判断字段是否存在
                                for (MapAttr attr: mapAttrs.ToJavaList())
                                {
                                    if (attr.getKeyOfEn() == sealField)
                                    {
                                        fk_dept = en.GetValStrByKey(sealField);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //判断本部门下是否有此人
                    //sql = "SELECT fk_station from port_deptEmpStation where fk_dept='" + fk_dept + "' and fk_emp='" + WebUser.No + "'";
                    sql = String.format(" select FK_Station from Port_DeptStation where FK_Dept ='{0}' and FK_Station in (select FK_Station from " + BP.WF.Glo.getEmpStation()+ " where FK_Emp='{1}')", fk_dept, WebUser.getNo());
                    DataTable dt = DBAccess.RunSQLReturnTable(sql);
                    for (DataRow dr : dt.Rows)
                    {
                        if (fk_station.contains(dr.getValue(0).toString() + ","))
                        {
                            stationNo = dr.getValue(0).toString();
                            break;
                        }
                    }
                    ////#endregion 加载签章

                    imgSrc = CCFlowAppPath + "DataUser/Seal/" + fk_dept + "_" + stationNo + ".png";
                    //设置主键
                    String myPK = DataType.IsNullOrEmpty(img.getEnPK()) ? "seal" : img.getEnPK();
                    myPK = myPK + "_" + en.GetValStrByKey("OID") + "_" + img.getMyPK();

                    FrmEleDB imgDb = new FrmEleDB();
                    QueryObject queryInfo = new QueryObject(imgDb);
                    queryInfo.AddWhere(FrmEleAttr.MyPK, myPK);
                    queryInfo.DoQuery();
                    //判断是否存在
                    if (imgDb == null || DataType.IsNullOrEmpty(imgDb.getFK_MapData()))
                    {
                        imgDb.setFK_MapData( DataType.IsNullOrEmpty(img.getEnPK()) ? "seal" : img.getEnPK());
                        imgDb.setEleID( en.GetValStrByKey("OID"));
                        imgDb.setRefPKVal(img.getMyPK());
                        imgDb.setTag1(imgSrc);
                        imgDb.Insert();
                    }

                    //添加控件
                    x = img.getX() + wtX;
                    sb.append("\t\n<DIV id=" + img.getMyPK() + " style='position:absolute;left:" + x + "px;top:" + y + "px;text-align:left;vertical-align:top' >");
                    sb.append("\t\n<img src='" + imgSrc + "' onerror=\"javascript:this.src='" + appPath + "DataUser/Seal/Def.png'\" style=\"padding: 0px;margin: 0px;border-width: 0px;width:" + img.getW() + "px;height:" + img.getH() + "px;\" />");
                    sb.append("\t\n</DIV>");
                }
                else
                {
                    FrmEleDB realDB = null;
                    FrmEleDB imgDb = new FrmEleDB();
                    QueryObject objQuery = new QueryObject(imgDb);
                    objQuery.AddWhere(FrmEleAttr.FK_MapData, img.getEnPK());
                    objQuery.addAnd();
                    objQuery.AddWhere(FrmEleAttr.EleID, en.GetValStrByKey("OID"));

                    if (objQuery.DoQuery() == 0)
                    {
                        FrmEleDBs imgdbs = new FrmEleDBs();
                        QueryObject objQuerys = new QueryObject(imgdbs);
                        objQuerys.AddWhere(FrmEleAttr.EleID, en.GetValStrByKey("OID"));
                        if (objQuerys.DoQuery() > 0)
                        {
                            for (FrmEleDB single : imgdbs.ToJavaList())
                            {
                                if (single.getFK_MapData().substring(6, single.getFK_MapData().length() - 6).equals(img.getEnPK().substring(6, img.getEnPK().length() - 6)))
                                {
                                    single.setFK_MapData(img.getEnPK());
                                    single.setMyPK(img.getEnPK() + "_" + en.GetValStrByKey("OID") + "_" + img.getEnPK());
                                    single.setRefPKVal( img.getEnPK());
                                    break;
                                }
                            }
                        }
                        else
                        {
                            realDB = imgDb;
                        }
                    }
                    else
                    {
                        realDB = imgDb;
                    }

                    if (realDB != null)
                    {
                        imgSrc = realDB.getTag1();
                        //如果没有查到记录，控件不显示。说明没有走盖章的一步
                        x =  img.getX() + wtX;
                        sb.append("\t\n<DIV id=" + img.getMyPK() + " style='position:absolute;left:" + x + "px;top:" + y + "px;text-align:left;vertical-align:top' >");
                        sb.append("\t\n<img src='" + imgSrc + "' onerror='javascript:this.src='" + appPath + "DataUser/ICON/" + SystemConfig.getCustomerNo() + "/LogBiger.png';' style='padding: 0px;margin: 0px;border-width: 0px;width:" + img.getW() + "px;height:" + img.getH() + "px;' />");
                        sb.append("\t\n</DIV>");
                    }
                }
            }
         //   ////#endregion
        }


        FrmBtns btns = mapData.getFrmBtns();
        for (FrmBtn btn : btns.ToJavaList())
        {
            x = btn.getX() + wtX;
            sb.append("\t\n<DIV id=u2 style='position:absolute;left:" + x + "px;top:" + btn.getY() + "px;text-align:left;' >");
            sb.append("\t\n<span >");

            String doDoc = BP.WF.Glo.DealExp(btn.getEventContext(), en, null);
            doDoc = doDoc.replaceAll("~", "'");
            switch (btn.getHisBtnEventType())
            {
                case Disable:
                    sb.append("<input type=button class=Btn value='" + btn.getText().replace("&nbsp;", " ") + "' disabled='disabled'/>");
                    break;
                case RunExe:
                case RunJS:
                    sb.append("<input type=button class=Btn value=\"" +btn.getText().replace("&nbsp;", " ") + "\" enable=true onclick=\"" + doDoc + "\" />");
                    break;
                default:
                    sb.append("<input type=button value='" + btn.getText() + "' />");
                    break;
            }
            sb.append("\t\n</span>");
            sb.append("\t\n</DIV>");
        }
        ////#endregion 输出竖线与标签
         ////#region 输出数据控件.
        for (MapAttr attr : mapAttrs.ToJavaList())
        {
            //处理隐藏字段，如果是不可见并且是启用的就隐藏.
            if (attr.getUIVisible() == false && attr.getUIIsEnable())
            {
                sb.append("<input type=text value='" + en.GetValStrByKey(attr.getKeyOfEn()) + "' style='display:none;' />");
                continue;
            }

            if (attr.getUIVisible() == false)
                continue;

            x = attr.getX() + wtX;
            if (attr.getLGType() == FieldTypeS.Enum || attr.getLGType() == FieldTypeS.FK)
                sb.append("<DIV id='F" + attr.getKeyOfEn() + "' style='position:absolute; left:" + x + "px; top:" + attr.getY() + "px;  height:16px;text-align: left;word-break: keep-all;' >");
            else
                sb.append("<DIV id='F" + attr.getKeyOfEn() + "' style='position:absolute; left:" + x + "px; top:" + attr.getY() + "px; width:" + attr.getUIWidth() + "px; height:16px;text-align: left;word-break: keep-all;' >");

            sb.append("<span>");

             ////#region add contrals.
            if (attr.getMaxLen() >= 3999 && attr.getTBModel() == TBModel.RichText.getValue())
            {
                sb.append(en.GetValStrByKey(attr.getKeyOfEn()));

                sb.append("</span>");
                sb.append("</DIV>");
                continue;
            }

             ////#region 通过逻辑类型，输出相关的控件.
            String text = "";
            switch (attr.getLGType())
            {
                case Normal:  // 输出普通类型字段.
                   if(attr.getIsSigan()==true){
                	   text = en.GetValStrByKey(attr.getKeyOfEn());
                	   text = SignPic(text);
                	   break;
                   }
                   if(attr.getMyDataType() == 1 && attr.getUIContralType().getValue() == DataType.AppString){
                	   if(attrs.Contains(attr.getKeyOfEn()+"Text") ==true)
                   			text = en.GetValRefTextByKey(attr.getKeyOfEn());
                   		if(DataType.IsNullOrEmpty(text))
                   			if(attrs.Contains(attr.getKeyOfEn()+"T") ==true)
                   				text = en.GetValStrByKey(attr.getKeyOfEn()+"T");	
                   }else{
                	   text = en.GetValStrByKey(attr.getKeyOfEn());
                   }
                    break;
                case Enum:
                case FK:
                   text = en.GetValRefTextByKey(attr.getKeyOfEn());
                    break;
                default:
                    break;
            }
            ////#endregion 通过逻辑类型，输出相关的控件.

          

            if (attr.getIsBigDoc())
            {
                //这几种字体生成 pdf都乱码
                text = text.replace("仿宋,", "宋体,");
                text = text.replace("仿宋;", "宋体;");
                text = text.replace("仿宋\"", "宋体\"");
                text = text.replace("黑体,", "宋体,");
                text = text.replace("黑体;", "宋体;");
                text = text.replace("黑体\"", "宋体\"");
                text = text.replace("楷体,", "宋体,");
                text = text.replace("楷体;", "宋体;");
                text = text.replace("楷体\"", "宋体\"");
                text = text.replace("隶书,", "宋体,");
                text = text.replace("隶书;", "宋体;");
                text = text.replace("隶书\"", "宋体\"");
            }

            if (attr.getMyDataType() == DataType.AppBoolean)
            {
            	if (DataType.IsNullOrEmpty(text) == true || text.equals("0"))
                    text = "[&#10005]"+attr.getName();
                else
                    text = "[&#10004]"+attr.getName();
            }

             sb.append(text);

            sb.append("</span>");
            sb.append("</DIV>");
        }

       //  ////#region  输出 rb.
        FrmRBs myrbs = mapData.getFrmRBs();
        for (FrmRB rb : myrbs.ToJavaList())
        {
            x = rb.getX() + wtX;
            sb.append("<DIV id='F" + rb.getMyPK() + "' style='position:absolute; left:" + x + "px; top:" + rb.getY() + "px; height:16px;text-align: left;word-break: keep-all;' >");
            sb.append("<span style='word-break: keep-all;font-size:12px;'>");

            if (rb.getIntKey() == en.GetValIntByKey(rb.getKeyOfEn()))
                sb.append("<b>" + rb.getLab() + "</b>");
            else
                sb.append(rb.getLab());

            sb.append("</span>");
            sb.append("</DIV>");
        }
        ////#endregion  输出 rb.

        ////#endregion 输出数据控件.

         ////#region 输出明细.
        MapDtls dtls = new MapDtls(frmID);
        for (MapDtl dtl :dtls.ToJavaList())
        {
            if (dtl.getIsView() == false)
                continue;

            x = dtl.getX() + wtX;
            float y = dtl.getY();

            sb.append("<DIV id='Fd" + dtl.getNo() + "' style='position:absolute; left:" + x + "px; top:" + y + "px; width:" + dtl.getW() + "px; height:" + dtl.getH() + "px;text-align: left;' >");
            sb.append("<span>");
            MapAttrs attrsOfDtls = new MapAttrs(dtl.getNo());
           
            sb.append("<table style='wdith:100%' >");
            sb.append("<tr>");
            for (MapAttr item :attrsOfDtls.ToJavaList())
            {
                if (item.getKeyOfEn() == "OID")
                    continue;
                if (item.getUIVisible() == false)
                    continue;

                sb.append("<th class='DtlTh'>" + item.getName() + "</th>");
            }
            sb.append("</tr>");
            //#endregion 输出标题.


            //#region 输出数据.
            GEDtls gedtls = new GEDtls(dtl.getNo());
            gedtls.Retrieve(GEDtlAttr.RefPK, workid);
            for (GEDtl gedtl :gedtls.ToJavaList())
            {
                sb.append("<tr>");

                for (MapAttr item :attrsOfDtls.ToJavaList())
                {
                    if (item.getKeyOfEn().equals("OID") || item.getUIVisible() == false)
                        continue;

                    if (item.getUIContralType() == UIContralType.DDL)
                    {
                        sb.append("<td class='DtlTd'>" + gedtl.GetValRefTextByKey(item.getKeyOfEn()) + "</td>");
                        continue;
                    }

                    if (item.getIsNum())
                    {
                        sb.append("<td class='DtlTd' style='text-align:right' >" + gedtl.GetValStrByKey(item.getKeyOfEn()) + "</td>");
                        continue;
                    }

                    sb.append("<td class='DtlTd'>" + gedtl.GetValStrByKey(item.getKeyOfEn()) + "</td>");
                }
                sb.append("</tr>");
            }
            //#endregion 输出数据.


            sb.append("</table>");
            
            sb.append("</span>");
            sb.append("</DIV>");
        }
        ////#endregion 输出明细.

         ////#region 审核组件
        if (flowNo != null)
        {
            FrmWorkCheck fwc = new FrmWorkCheck(frmID);
            if (fwc.getHisFrmWorkCheckSta() != FrmWorkCheckSta.Disable.getValue())
            {
                x = fwc.getFWC_X ()+ wtX;
                sb.append("<DIV id='DIVWC" + fwc.getNo() + "' style='position:absolute; left:" + x + "px; top:" + fwc.getFWC_Y() + "px; width:" + fwc.getFWC_W() + "px; height:" + fwc.getFWC_H() + "px;text-align: left;' >");
                sb.append("<span>");

                sb.append("<table   style='border: 1px outset #C0C0C0;padding: inherit; margin: 0;border-collapse:collapse;width:100%;' >");

                 ////#region 生成审核信息.
                if (flowNo != null)
                {
                    String sql = "SELECT EmpFrom, EmpFromT,RDT,Msg,NDFrom FROM ND" +Integer.parseInt(flowNo) + "Track WHERE WorkID=" + workid + " AND ActionType=" + ActionType.WorkCheck.getValue() + " ORDER BY RDT ";
                    DataTable dt = DBAccess.RunSQLReturnTable(sql);

                    //获得当前待办的人员,把当前审批的人员排除在外,不然就有默认同意的意见可以打印出来.
                    sql = "SELECT FK_Emp, FK_Node FROM WF_GenerWorkerList WHERE IsPass!=1 AND WorkID="+workid;
                    DataTable dtOfTodo = DBAccess.RunSQLReturnTable(sql);

                    for (DataRow dr: dt.Rows)
                    {

                         ////#region 排除正在审批的人员.
                        String nodeID = dr.getValue("NDFrom").toString();
                        String empFrom = dr.getValue("EmpFrom").toString();
                        if (dtOfTodo.Rows.size() != 0)
                        {
                            Boolean isHave = false;
                            for (DataRow mydr : dtOfTodo.Rows)
                            {
                                if (mydr.getValue("FK_Node").toString() != nodeID)
                                    continue;

                                if (mydr.getValue("FK_Emp").toString() != empFrom)
                                    continue;
                                isHave = true;
                            }

                            if (isHave == true)
                                continue;
                        }
                        ////#endregion 排除正在审批的人员.

                        sb.append("<tr>");
                        sb.append("<td valign=middle style='border-style: solid;padding: 4px;text-align: left;color: #333333;font-size: 12px;border-width: 1px;border-color: #C2D5E3;' >" + dr.getValue("NDFromT").toString() + "</td>");

                        String msg =dr.getValue("Msg") .toString();

                        msg += "<br>";
                        msg += "<br>";
                        msg += "审核人:" +dr.getValue("EmpFromT") .toString() + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT") .toString();

                        sb.append("<td colspan=3 valign=middle style='border-style: solid;padding: 4px;text-align: left;color: #333333;font-size: 12px;border-width: 1px;border-color: #C2D5E3;' >" + msg + "</td>");
                        sb.append("</tr>");
                    }
                }
                sb.append("</table>");
                ////#endregion 生成审核信息.

                sb.append("</span>");
                sb.append("</DIV>");
            }
        }
        ////#endregion 审核组件

         ////#region 父子流程组件
        if (flowNo != null)
        {
            FrmSubFlow subFlow = new FrmSubFlow(frmID);
            if (subFlow.getHisFrmSubFlowSta() != FrmSubFlowSta.Disable.getValue())
            {
                x = subFlow.getSF_X() + wtX;
                sb.append("<DIV id='DIVWC" + subFlow.getNo() + "' style='position:absolute; left:" + x + "px; top:" + subFlow.getSF_Y() + "px; width:" + subFlow.getSF_W() + "px; height:" + subFlow.getSF_H() + "px;text-align: left;' >");
                sb.append("<span>");

                String src = appPath + "WF/WorkOpt/subFlow.getaspx?s=2";
                String fwcOnload = "";
                if (subFlow.getHisFrmSubFlowSta() == FrmSubFlowSta.Readonly.getValue())
                {
                    src += "&DoType=View";
                }

                src += "&r=q";
                sb.append("<iframe ID='FSF" + subFlow.getNo() + "' " + fwcOnload + "  src='" + src + "' frameborder=0 style='padding:0px;border:0px;'  leftMargin='0'  topMargin='0' width='" + subFlow.getSF_W() + "' height='" + subFlow.getSF_H() + "'   scrolling=auto/></iframe>");

                sb.append("</span>");
                sb.append("</DIV>");
            }
        }
        ////#endregion 父子流程组件

         ////#region 输出附件
        FrmAttachments aths = new FrmAttachments(frmID);
        for(FrmAttachment ath : aths.ToJavaList())
        {

            if (ath.getUploadType() == AttachmentUploadType.Single)
            {
                /* 单个文件 */
            	FrmAttachmentDBs athDBs = BP.WF.Glo.GenerFrmAttachmentDBs(ath,String.valueOf(workid), ath.getMyPK());
                FrmAttachmentDB athDB = (FrmAttachmentDB)athDBs.GetEntityByKey(FrmAttachmentDBAttr.FK_FrmAttachment, ath.getMyPK());
                x = ath.getX() + wtX;
                float y = ath.getY();
                sb.append("<DIV id='Fa" + ath.getMyPK() + "' style='position:absolute; left:" + x + "px; top:" + y + "px; text-align: left;float:left' >");
                sb.append("<DIV>");

                sb.append("附件没有转化:" + athDB.getFileName());
                sb.append("</DIV>");
                sb.append("</DIV>");
            }

            if (ath.getUploadType() == AttachmentUploadType.Multi)
            {
                x = ath.getX() + wtX;
                sb.append("<DIV id='Fd" + ath.getMyPK() + "' style='position:absolute; left:" + x + "px; top:" + ath.getY()+ "px; width:" + ath.getW() + "px; height:auto;text-align: left;' >");
                sb.append("<span>");
                sb.append("<ul>");

                //判断是否有这个目录.
                File pathfile = new File(path + "\\pdf\\");
                if (!pathfile.exists()) {
                	pathfile.mkdirs();
                }

                //文件加密
                boolean fileEncrypt = SystemConfig.getIsEnableAthEncrypt();
                FrmAttachmentDBs athDBs = BP.WF.Glo.GenerFrmAttachmentDBs(ath,String.valueOf(workid), ath.getMyPK());

                
                for (FrmAttachmentDB item :athDBs.ToJavaList())
                {
                	 //获取文件是否加密
                    boolean isEncrypt = item.GetParaBoolen("IsEncrypt");
                    
                    if (ath.getAthSaveWay() == AthSaveWay.FTPServer)
                    {
                        try
                        {
                            String toFile = path + "\\pdf\\" + item.getFileName() ;
                            if (new File(toFile).exists() == false)
                            {
                                //把文件copy到,
                                String file = item.GenerTempFile(ath.getAthSaveWay());
                                String fileTempDecryPath = file;
                                if (fileEncrypt == true && isEncrypt == true)
                                {
                                    fileTempDecryPath = file + ".tmp";
                                    AesEncodeUtil.decryptFile(file, fileTempDecryPath);

                                }
                                Files.copy(new File(fileTempDecryPath).toPath(), new File(toFile).toPath());
                            }

                            sb.append("<li><a href='" + SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/"+FK_Node+"/"+workid+"/"+"pdf/"+item.getFileName() + "'>" + item.getFileName()  + "</a></li>");
                        }
                        catch (Exception ex)
                        {
                            sb.append("<li>" + item.getFileName()  + "(<font color=red>文件未从ftp下载成功{" + ex.getMessage() + "}</font>)</li>");
                        }
                    }
                   
                    if (ath.getAthSaveWay() == AthSaveWay.WebServer)
                    {
                        try
                        {
                            String toFile = path + "\\pdf\\" + item.getFileName() ;
                            File fileto = new File(toFile);
                            if (fileto.exists()==false)
                            {
                                //把文件copy到,
                            	String fileTempDecryPath = item.getFileFullName();
                                if (fileEncrypt == true && isEncrypt == true)
                                {
                                    fileTempDecryPath = item.getFileFullName() + ".tmp";
                                    AesEncodeUtil.decryptFile(item.getFileFullName(), fileTempDecryPath);

                                }
                            	Files.copy(new File(fileTempDecryPath).toPath()
                            		,new File(path + "\\pdf\\" + item.getFileName()).toPath());
                            }
                            sb.append("<li><a href='" + SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/"+frmID+"/"+workid+"/"+"pdf/"+item.getFileName() + "'>" + item.getFileName()  + "</a></li>");
                        }
                        catch (Exception ex)
                        {
                            sb.append("<li>" + item.getFileName()  + "(<font color=red>文件未从ftp下载成功{" + ex.getMessage() + "}</font>)</li>");
                        }
                    }

                }
                sb.append("</ul>");

                sb.append("</span>");
                sb.append("</DIV>");
            }
        }
        return sb;
}
        
private static StringBuilder GenerHtmlOfFool(MapData mapData, String frmID, long workid, Entity en
        , String path, String flowNo,String FK_Node,String basePath) throws Exception
{
    float width = mapData.getFrmW();
    StringBuilder sb =new StringBuilder();

    //字段集合.
    MapAttrs mapAttrs = new MapAttrs(frmID);
    Attrs attrs = en.getEnMap().getAttrs();

    //生成表头.
    String frmName = mapData.getName();
    if (SystemConfig.getCustomerNo() == "TianYe")
        frmName = "";

    int tableCol = mapData.getTableCol();
    if(tableCol == 0)
        tableCol = 4;
    else if(tableCol == 1)
        tableCol = 6;
    else if(tableCol == 3)
        tableCol = 3;
    else
        tableCol = 4;
    sb.append(" <table style='width:"+width+"px;height:auto;' >");

    //#region 生成头部信息.
    sb.append("<tr>");

    sb.append("<td colspan="+tableCol+" >");

    sb.append("<table border=0 style='width:"+width+"px;'>");

    sb.append("<tr  style='border:0px;' >");

    //二维码显示
    boolean IsHaveQrcode = true;
    if(SystemConfig.GetValByKeyBoolen("IsShowQrCode", false)==false)
        IsHaveQrcode = false;

    //判断当前文件是否存在图片
    boolean IsHaveImg = false;
    String IconPath = path+"/icon.png";
    if(new File(IconPath).exists() == true)
        IsHaveImg = true;
    if(IsHaveImg == true){
        sb.append("<td>");
        sb.append("<img src='icon.png' style='height:100px;border:0px;' />");
        sb.append("</td>");
    }
    if(IsHaveImg == false && IsHaveQrcode==false)
        sb.append("<td  colspan=6>");
    else if((IsHaveImg == true && IsHaveQrcode==false) ||(IsHaveImg == false && IsHaveQrcode==true) )
        sb.append("<td  colspan=5>");
    else
        sb.append("<td  colspan=4>");

    sb.append("<br><h2><b>" + frmName + "</b></h2>");
    sb.append("</td>");

    if(IsHaveQrcode ==true){
        sb.append("<td>");
        sb.append(" <img src='QR.png' style='height:100px;'  />");
        sb.append("</td>");
    }

    sb.append("</tr>");
    sb.append("</table>");

    sb.append("</td>");
    //#endregion 生成头部信息.

    GroupFields gfs = new GroupFields(frmID);
    for (GroupField gf : gfs.ToJavaList())
    {
        //输出标题.
        if(gf.getCtrlType().equals("Ath")==false){
            sb.append(" <tr>");
            sb.append("  <th colspan="+tableCol+" ><b>" + gf.getLab() + "</b></th>");
            sb.append(" </tr>");
        }

        //#region 输出字段.
        if (gf.getCtrlID().equals("") && gf.getCtrlType().equals(""))
        {
            boolean isDropTR = true;
            //右侧跨行
            boolean IsShowRight = true; // 是否显示右侧列
            int rRowSpan = 0; //跨的行数
            int ruRowSpan = 0; //已近解析的行数
            int ruColSpan = 0; //该跨行总共跨的列数

            //左侧跨行
            boolean IsShowLeft = true; // 是否显示左侧列
            int lRowSpan = 0; //跨的行数
            int luRowSpan = 0; //已近解析的行数
            int luColSpan = 0; //该跨行总共跨的列数

            //记录一行已占用的列输
            int UseColSpan = 0;
            int ewith = (int)width/100;
            //跨列的字段
            int colSpan = 1;
            int rowSpan = 1;
            int textColSpan = 2;
            String textWidth = ewith*15+"px";
            String colWidth = ewith*35+"px";
            String lab="";

            String html = "";
            for (MapAttr attr : mapAttrs.ToJavaList())
            {
                //处理隐藏字段，如果是不可见并且是启用的就隐藏.
                if (attr.getUIVisible() == false)
                    continue;
                if (attr.getGroupID() != attr.getGroupID())
                    continue;
                //处理分组数据，非当前分组的数据不输出
                if (attr.getGroupID() != gf.getOID())
                    continue;

                lab = attr.getName();
                //附件
                if(attr.getUIContralType() == UIContralType.AthShow){

                }
                //手写签名版
                else if(attr.getUIContralType() == UIContralType.HandWriting){

                }
                //超链接
                else if(attr.getUIContralType() == UIContralType.HandWriting){

                }
                //图片
                else if(attr.getUIContralType() == UIContralType.FrmImg){
                    FrmImg frmImg = new FrmImg(attr.getMyPK());
                    //解析图片
                    if (frmImg.getHisImgAppType().getValue() == 0) { //图片类型
                        //数据来源为本地.
                        String  imgSrc = "";
                        if (frmImg.getImgSrcType() == 0) {
                            //替换参数
                            String frmPath = frmImg.getImgPath();
                            frmPath = frmPath.replace("＠", "@");
                            frmPath = frmPath.replace("@basePath", basePath);
                            //替换值
                            imgSrc = DealEnExp(en, frmPath);
                        }

                        //数据来源为指定路径.
                        if (frmImg.getImgSrcType() == 1) {
                            String url = frmImg.getImgURL();
                            url = url.replace("＠", "@");
                            url = url.replace("@basePath", basePath);
                            imgSrc = DealEnExp(en, url);
                        }
                        // 由于火狐 不支持onerror 所以 判断图片是否存在放到服务器端
                        if (imgSrc == "" || imgSrc == null)
                            imgSrc = "../DataUser/ICON/CCFlow/LogBig.png";

                        //＠basePath
                        //alert(imgSrc);

                        String style = "text-align:center;";
                        if (attr.getUIWidth() == 0)
                            style += "width:100%;";
                        else
                            style += "width:" + attr.getUIWidth() + "px;";

                        if (attr.getUIHeight() == 0)
                            style += "Height:100%;";
                        else
                            style += "Height:" + attr.getUIHeight() + "px;";
                        lab = "<img src='" + imgSrc + "' style='" + style + "'  />";

                    }
                }
                //流程进度图
                else if(attr.getUIContralType() == UIContralType.JobSchedule){

                }

                String text = "";
                switch (attr.getLGType())
                {
                    case Normal:  // 输出普通类型字段.
                        if(attr.getUIContralType() == UIContralType.AthShow || attr.getUIContralType() == UIContralType.HandWriting
                                ||attr.getUIContralType() == UIContralType.HandWriting||attr.getUIContralType() == UIContralType.FrmImg
                                ||attr.getUIContralType() == UIContralType.JobSchedule){
                            text="";
                            break;
                        }

                        if(attr.getMyDataType() == 1 && attr.getUIContralType().getValue() == DataType.AppString){
                            if(attrs.Contains(attr.getKeyOfEn()+"Text") ==true)
                                text = en.GetValRefTextByKey(attr.getKeyOfEn());
                            if(DataType.IsNullOrEmpty(text))
                                if(attrs.Contains(attr.getKeyOfEn()+"T") ==true)
                                    text = en.GetValStrByKey(attr.getKeyOfEn()+"T");
                        }else{
                            text = en.GetValStrByKey(attr.getKeyOfEn());
                        }

                        break;
                    case Enum:
                    case FK:
                        text = en.GetValRefTextByKey(attr.getKeyOfEn());
                        break;
                    default:
                        break;
                }

                if (attr.getIsBigDoc())
                {
                    //这几种字体生成 pdf都乱码
                    text = text.replace("仿宋,", "宋体,");
                    text = text.replace("仿宋;", "宋体;");
                    text = text.replace("仿宋\"", "宋体\"");
                    text = text.replace("黑体,", "宋体,");
                    text = text.replace("黑体;", "宋体;");
                    text = text.replace("黑体\"", "宋体\"");
                    text = text.replace("楷体,", "宋体,");
                    text = text.replace("楷体;", "宋体;");
                    text = text.replace("楷体\"", "宋体\"");
                    text = text.replace("隶书,", "宋体,");
                    text = text.replace("隶书;", "宋体;");
                    text = text.replace("隶书\"", "宋体\"");
                }

                if (attr.getMyDataType() == DataType.AppBoolean)
                {
                    if (DataType.IsNullOrEmpty(text) == true || text.equals("0"))
                        text = "[&#10005]"+attr.getName();
                    else
                        text = "[&#10004]"+attr.getName();
                }

                //赋值
                rowSpan = attr.getRowSpan();
                colSpan = attr.getColSpan();
                textColSpan = attr.getTextColSpan();
                if (tableCol == 4) {
                    if(colSpan ==1)
                        colWidth = ewith*35 +"px";
                    if(colSpan ==2)
                        colWidth = ewith*85 + "px";
                    if(colSpan ==3)
                        colWidth =ewith*50  + "px";
                    if(textColSpan == 1)
                        textWidth = ewith*15 + "px";
                    if(textColSpan == 2)
                        textWidth = ewith*50 + "px";
                    if(textColSpan == 3)
                        textWidth = ewith*65 + "px";
                } else {
                    colWidth = 23 * colSpan*ewith + "px";
                    textWidth = 10 * textColSpan*ewith + "px";
                }


                if (colSpan == 0) {

                    //占一行
                    if (textColSpan == tableCol) {
                        isDropTR = true;

                        html += "<tr>";
                        html += "<td  colSpan=" + textColSpan + " rowSpan=" + rowSpan + " class='FDesc' style='text-align:left'>" + lab + "</br>";
                        html += "</tr>";
                        continue;

                    }
                    //线性展示都跨一个单元格
                    if (isDropTR == true) {
                        html += "<tr >";
                        UseColSpan = 0;
                        luColSpan = 0;
                        if (IsShowLeft == true) {
                            UseColSpan += colSpan + textColSpan+ruColSpan;
                            lRowSpan = rowSpan;
                            luColSpan += colSpan + textColSpan;
                            html += "<td class='FDesc'  rowSpan=" + rowSpan + " colSpan=" + textColSpan + "  style="+textWidth+">" + lab + "</td>";
                            if (rowSpan != 1) {
                                IsShowLeft = false;
                            }

                        }
                        if (UseColSpan == tableCol) {
                            ruRowSpan++;
                            isDropTR = true;
                        } else {
                            isDropTR = false;
                        }

                        //复位右侧信息
                        if (ruRowSpan == rRowSpan) {
                            ruRowSpan = 0;
                            luRowSpan = 0;
                            rRowSpan = 0;
                            IsShowRight = true;
                            if (rowSpan == 1)
                                luColSpan = 0;
                            ruColSpan = 0;
                        }


                        if (IsShowRight == false && (UseColSpan == tableCol)) {
                            html += "</tr>";
                            isDropTR = true;
                            UseColSpan = ruColSpan;

                        }

                        continue;
                    }

                    if (isDropTR == false) {
                        ruColSpan = 0;
                        if (IsShowRight == true) {
                            UseColSpan += colSpan + textColSpan;
                            rRowSpan = rowSpan;
                            ruColSpan += colSpan + textColSpan;
                            html += "<td class='FDesc'  rowSpan=" + rowSpan + " colSpan=" + textColSpan + " style="+textWidth+">" + lab + "</td>";
                            if (UseColSpan == tableCol) {
                                isDropTR = true;
                                if (rowSpan != 1) {
                                    ruRowSpan++;
                                }
                            }
                            if (rowSpan != 1) {
                                IsShowRight = false;
                                lRowSpan = rowSpan;
                            }
                        }

                        if (UseColSpan == tableCol) {
                            luRowSpan++;
                            html += "</tr>";
                        }

                        //复位左侧信息
                        if (luRowSpan == lRowSpan) {
                            luRowSpan = 0;
                            ruRowSpan = 0;
                            lRowSpan = 0;
                            IsShowLeft = true;
                            ruColSpan =0;

                        }

                        if (IsShowLeft == false && (UseColSpan == tableCol)) {
                            html += "<tr>";
                            UseColSpan = 0;
                            isDropTR = false;
                            UseColSpan = luColSpan;
                        }
                        continue;
                    }

                }

                //线性展示并且colspan=4
                if (colSpan == tableCol) {
                    isDropTR = true;
                    html += "<tr>";
                    html += " <td ColSpan="+tableCol+" style='width:100%' class='FDesc' >" + lab + "</td>";
                    html+="</tr>";
                    html +="<tr>";
                    html +="<td ColSpan="+tableCol+">";
                    html += text;
                    html += "</td>";
                    html += "</tr>";
                    continue;
                }
                int sumColSpan = colSpan + textColSpan;
                if (sumColSpan == tableCol) {

                    isDropTR = true;
                    html += "<tr >";
                    html += " <td  class='FDesc' ColSpan="+textColSpan+" style='"+textWidth+"'>" + lab + "</td>";
                    html += " <td class='FContext' ColSpan="+colSpan+"  style='"+colWidth+"'>";
                    html += text;
                    html += " </td>";
                    html += "</tr>";
                    isDropTR = true;
                    continue;
                }

                //换行的情况
                if (isDropTR == true) {
                    html += "<tr >";
                    UseColSpan = 0;
                    luColSpan = 0;
                    if (IsShowLeft == true) {
                        UseColSpan += colSpan + textColSpan+ruColSpan;
                        lRowSpan = rowSpan;
                        luColSpan += colSpan + textColSpan;
                        if (attr.getMyDataType() == 4) {
                            colSpan = colSpan + textColSpan;
                            colWidth = (colSpan * 23 + 10 *textColSpan) + "%";
                        } else {
                            html += " <td  class='FDesc' colSpan="+textColSpan+"   rowSpan=" + rowSpan + " style='"+textWidth+"'>" + lab + "</td>";
                        }
                        html += " <td class='FContext' colSpan="+colSpan+"  rowSpan=" + rowSpan + " >";
                        html += text;
                        html += " </td>";
                        if (rowSpan != 1) {
                            IsShowLeft = false;
                        }

                    }
                    if (UseColSpan == tableCol) {
                        ruRowSpan++;
                        isDropTR = true;
                    } else {
                        isDropTR = false;
                    }

                    //复位右侧信息
                    if (ruRowSpan == rRowSpan) {
                        ruRowSpan = 0;
                        luRowSpan = 0;
                        rRowSpan = 0;
                        IsShowRight = true;
                        if (rowSpan == 1)
                            luColSpan = 0;
                        ruColSpan = 0;
                    }


                    if (IsShowRight == false && (UseColSpan == tableCol)) {
                        html += "</tr>";
                        isDropTR = true;
                        UseColSpan = ruColSpan;

                    }

                    continue;
                }

                if (isDropTR == false) {
                    ruColSpan = 0;
                    if (IsShowRight == true) {
                        UseColSpan += colSpan + textColSpan;
                        rRowSpan = rowSpan;
                        ruColSpan += colSpan + textColSpan;
                        if (attr.getMyDataType() == 4) {
                            colSpan = colSpan + textColSpan;
                            colWidth = (colSpan * 23*ewith + 10 *textColSpan*ewith) + "px";
                        } else {
                            html += " <td  class='FDesc' colSpan="+textColSpan+"  rowSpan=" + rowSpan + " style='"+textWidth+"'>" + lab + "</td>";
                        }
                        html += " <td class='FContext' ColSpan="+colSpan+"  rowSpan=" + rowSpan + " style='"+colWidth+"'>";
                        html += text;
                        html += " </td>";
                        if (UseColSpan == tableCol) {
                            isDropTR = true;
                            if (rowSpan != 1) {
                                ruRowSpan++;
                            }
                        }
                        if (rowSpan != 1) {
                            IsShowRight = false;
                            lRowSpan = rowSpan;
                        }
                    }

                    if (UseColSpan == tableCol) {
                        luRowSpan++;
                        html += "</tr>";
                    }

                    //复位左侧信息
                    if (luRowSpan == lRowSpan) {
                        luRowSpan = 0;
                        ruRowSpan = 0;
                        lRowSpan = 0;
                        IsShowLeft = true;
                        ruColSpan = 0;

                    }

                    if (IsShowLeft == false && (UseColSpan == tableCol)) {
                        html += "<tr>";
                        UseColSpan = 0;
                        isDropTR = false;
                        UseColSpan = luColSpan;
                    }
                    continue;
                }

            }
            if(isDropTR == false){
                int unUseColSpan = tableCol-UseColSpan;
                html +="<td colspan="+unUseColSpan+"></td>";
                html +="</tr>";
            }
            sb.append(html); //增加到里面.
            continue;
        }
        //#endregion 输出字段.

        //#region 如果是从表.
        if (gf.getCtrlType().equals("Dtl"))
        {
            /* 如果是从表 */
            MapAttrs attrsOfDtls =null;
            try{
                attrsOfDtls = 	new MapAttrs(gf.getCtrlID());
            }catch(Exception ex){}

            //#region 输出标题.
            sb.append("<tr><td valign=top colspan="+tableCol+" >");

            sb.append("<table style='wdith:100%' >");
            sb.append("<tr>");
            for (MapAttr item :attrsOfDtls.ToJavaList())
            {
                if (item.getKeyOfEn() == "OID")
                    continue;
                if (item.getUIVisible() == false)
                    continue;

                sb.append("<th stylle='width:"+item.getUIWidthInt()+"px;'>" + item.getName() + "</th>");
            }
            sb.append("</tr>");
            //#endregion 输出标题.


            //#region 输出数据.
            GEDtls dtls = new GEDtls(gf.getCtrlID());
            dtls.Retrieve(GEDtlAttr.RefPK, workid);
            for (GEDtl dtl :dtls.ToJavaList())
            {
                sb.append("<tr>");

                for (MapAttr item :attrsOfDtls.ToJavaList())
                {
                    if (item.getKeyOfEn().equals("OID") || item.getUIVisible() == false)
                        continue;

                    if (item.getUIContralType() == UIContralType.DDL)
                    {
                        sb.append("<td>" + dtl.GetValRefTextByKey(item.getKeyOfEn()) + "</td>");
                        continue;
                    }

                    if (item.getIsNum())
                    {
                        sb.append("<td style='text-align:right' >" + dtl.GetValStrByKey(item.getKeyOfEn()) + "</td>");
                        continue;
                    }

                    sb.append("<td>" + dtl.GetValStrByKey(item.getKeyOfEn()) + "</td>");
                }
                sb.append("</tr>");
            }
            //#endregion 输出数据.


            sb.append("</table>");

            sb.append(" </td>");
            sb.append(" </tr>");
        }
        //#endregion 如果是从表.

        //#region 如果是附件.
        if (gf.getCtrlType().equals("Ath"))
        {
            FrmAttachment ath = new FrmAttachment(gf.getCtrlID());
            if(ath.getIsVisable() == false)
                continue;
            sb.append(" <tr>");
            sb.append("  <th colspan="+tableCol+" ><b>" + gf.getLab() + "</b></th>");
            sb.append(" </tr>");

            if (!ath.getMyPK().equals(gf.getCtrlID()))
                continue;

            FrmAttachmentDBs athDBs = BP.WF.Glo.GenerFrmAttachmentDBs(ath,String.valueOf(workid), ath.getMyPK());


            if (ath.getUploadType() == AttachmentUploadType.Single)
            {
                /* 单个文件 */
                sb.append("<tr><td colspan="+tableCol+" >单附件没有转化:" + ath.getMyPK() + "</td></td>");
                continue;
            }

            if (ath.getUploadType() == AttachmentUploadType.Multi)
            {
                sb.append("<tr><td valign=top colspan="+tableCol+"  >");
                sb.append("<ul>");

                //判断是否有这个目录.
                File pathfile = new  File(path + "\\pdf\\");
                if (pathfile.exists() == false)
                    pathfile.mkdirs();

                for (FrmAttachmentDB item : athDBs.ToJavaList())
                {
                    String fileTo = path + "\\pdf\\" + item.getFileName();
                    //加密信息
                    boolean fileEncrypt = SystemConfig.getIsEnableAthEncrypt();
                    boolean isEncrypt = item.GetParaBoolen("IsEncrypt");
                    //#region 从ftp服务器上下载.
                    if (ath.getAthSaveWay() == AthSaveWay.FTPServer)
                    {
                        try
                        {
                            File pathfileTo = new  File(fileTo);
                            if (pathfileTo.exists() == true)
                                pathfileTo.delete();//rn "err@删除已经存在的文件错误,请检查iis的权限:" + ex.getMessage();

                            //把文件copy到,
                            String file = item.GenerTempFile(ath.getAthSaveWay());

                            String fileTempDecryPath = file;
                            if (fileEncrypt == true && isEncrypt == true)
                            {
                                fileTempDecryPath = file + ".tmp";
                                AesEncodeUtil.decryptFile(file, fileTempDecryPath);

                            }

                            Files.copy(new File(fileTempDecryPath).toPath(),pathfileTo.toPath());

                            sb.append("<li><a href='" + SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/"+FK_Node+"/"+workid+"/"+"pdf/"+item.getFileName() + "'>" + item.getFileName() + "</a></li>");
                        }
                        catch(Exception ex )
                        {
                            sb.append("<li>" + item.getFileName() + "(<font color=red>文件未从ftp下载成功{" + ex.getMessage() + "}</font>)</li>");
                        }
                    }
                    //#endregion 从ftp服务器上下载.


                    //#region 从iis服务器上下载.
                    if (ath.getAthSaveWay() == AthSaveWay.WebServer)
                    {
                        try
                        {

                            String fileTempDecryPath = item.getFileFullName();
                            if (fileEncrypt == true && isEncrypt == true)
                            {
                                fileTempDecryPath = item.getFileFullName() + ".tmp";
                                AesEncodeUtil.decryptFile(item.getFileFullName(), fileTempDecryPath);

                            }

                            //把文件copy到,
                            File pathfileTo = new File(fileTo);
                            if (pathfileTo.exists()== false)
                                Files.copy(new File(fileTempDecryPath).toPath(),pathfileTo.toPath());

                            sb.append("<li><a href='" + SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/"+frmID+"/"+workid+"/"+"pdf/"+item.getFileName() + "'>" + item.getFileName() + "</a></li>");
                        }
                        catch (Exception ex)
                        {
                            sb.append("<li>" + item.getFileName() + "(<font color=red>文件未从web下载成功{" + ex.getMessage() + "}</font>)</li>");
                        }
                    }

                }
                sb.append("</ul>");
                sb.append("</td></tr>");
            }

        }
        //#endregion 如果是附件.

        //如果是IFrame页面
        if(gf.getCtrlType().equals("Frame") && flowNo != null ){
            sb.append("<tr>");
            sb.append("  <td colspan="+tableCol+"  >");

            //根据GroupID获取对应的
            MapFrame frame = new MapFrame(gf.getCtrlID());
            //获取URL
            String url = frame.getURL();

            //替换URL的
            url = url.replace("@basePath", basePath);
            //替换系统参数
            url = url.replaceAll("@WebUser.No", WebUser.getNo());
            url = url.replaceAll("@WebUser.Name;", WebUser.getName());
            url = url.replaceAll("@WebUser.FK_DeptName;", WebUser.getFK_DeptName());
            url = url.replaceAll("@WebUser.FK_Dept;", WebUser.getFK_Dept());

            //替换参数
            if (url.indexOf("?") > 0){
                //获取url中的参数
                url = url.substring(url.indexOf('?'));
                String[] params = url.split("&");
                for(String param : params){
                    if(DataType.IsNullOrEmpty(param) || param.indexOf("@") == -1)
                        continue;
                    String[] paramArr = param.split("=");
                    if (paramArr.length == 2 && paramArr[1].indexOf('@') == 0) {
                        if (paramArr[1].indexOf("@WebUser.") == 0)
                            continue;
                        url = url.replace(paramArr[1], en.GetValStrByKey(paramArr[1].substring(1)));
                    }
                }

            }
            sb.append("<iframe style='width:100%;height:auto;' ID='" + frame.getMyPK() + "'    src='" + url + "' frameborder=0  leftMargin='0'  topMargin='0' scrolling=auto></iframe></div>");
            sb.append("</td>");
            sb.append("</tr>");
        }


        //#region 审核组件
        if (gf.getCtrlType().equals("FWC") && flowNo != null)
        {
            FrmWorkCheck fwc =new FrmWorkCheck(frmID);

            String sql = "";
            DataTable dtTrack = null;
            Boolean bl = false;
            try
            {
                bl = DBAccess.IsExitsTableCol("Port_Emp", "SignType");
            }
            catch(Exception ex){

            }
            if (bl)
            {
                String tTable = "ND" + Integer.parseInt(flowNo) + "Track";
                sql = "SELECT a.No, a.SignType FROM Port_Emp a, " + tTable + " b WHERE a.No=b.EmpFrom AND B.WorkID=" + workid;

                dtTrack = DBAccess.RunSQLReturnTable(sql);
                dtTrack.TableName = "SignType";
                dtTrack.Columns.Add("No");
                dtTrack.Columns.Add("SignType");
            }

            String html = ""; // "<table style='width:100%;valign:middle;height:auto;' >";

            //#region 生成审核信息.
            sql = "SELECT NDFromT,Msg,RDT,EmpFromT,EmpFrom,NDFrom FROM ND" + Integer.parseInt(flowNo) + "Track WHERE WorkID=" + workid + " AND ActionType=" + ActionType.WorkCheck.getValue() + " ORDER BY RDT ";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);

            //获得当前待办的人员,把当前审批的人员排除在外,不然就有默认同意的意见可以打印出来.
            sql = "SELECT FK_Emp, FK_Node FROM WF_GenerWorkerList WHERE IsPass!=1 AND WorkID=" + workid;
            DataTable dtOfTodo = DBAccess.RunSQLReturnTable(sql);

            for (DataRow dr : dt.Rows)
            {
                //#region 排除正在审批的人员.
                String nodeID =  dr.getValue("NDFrom").toString();
                String empFrom = dr.getValue("EmpFrom").toString();
                if (dtOfTodo.Rows.size() != 0)
                {
                    Boolean isHave = false;
                    for (DataRow mydr :dtOfTodo.Rows)
                    {
                        if (mydr.getValue("FK_Node").toString() != nodeID)
                            continue;

                        if (mydr.getValue("FK_Emp").toString() != empFrom)
                            continue;
                        isHave = true;
                    }

                    if (isHave == true)
                        continue;
                }
                //#endregion 排除正在审批的人员.


                html += "<tr>";
                html += " <td valign=middle >" + dr.getValue("NDFromT").toString() + "</td>";

                String msg =dr.getValue("Msg").toString();

                msg += "<br>";
                msg += "<br>";

                String empStrs = "";
                if (dtTrack == null)
                {
                    empStrs = dr.getValue("EmpFromT").toString();
                }
                else
                {
                    String singType = "0";
                    for (DataRow drTrack :dtTrack.Rows)
                    {
                        if (drTrack.getValue("No").toString() == dr.getValue("EmpFrom").toString())
                        {
                            singType = drTrack.getValue("SignType").toString();
                            break;
                        }
                    }

                    if (singType == "0" || singType == "2")
                    {
                        empStrs = dr.getValue("EmpFromT").toString();
                    }


                    if (singType == "1")
                    {
                        empStrs = "<img src='../../../../../DataUser/Siganture/" + dr.getValue("EmpFrom").toString() + ".jpg' title='" + dr.getValue("EmpFromT").toString() + "' style='height:60px;' border=0 onerror=\"src='../../../../../DataUser/Siganture/UnName.JPG'\" /> " + dr.getValue("EmpFromT").toString();
                    }

                }

                msg += "审核人:" + empStrs + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期:" + dr.getValue("RDT").toString();

                html += " <td colspan=3 valign=middle >" + msg + "</td>";
                html += " </tr>";
            }
            //#endregion 生成审核信息.

            sb.append(" " + html);
        }
    }

    sb.append("</table>");
    return sb;
}
    /**
     * 树形表单转成PDF
     * @return
     * @throws Exception 
     */
    public static String MakeCCFormToPDF(Node node, long workid,String flowNo,String fileNameFormat,boolean urlIsHostUrl,String basePath) throws Exception{
    	//根据节点信息获取表单方案
    	MapData md = new MapData("ND"+node.getNodeID());
    	String resultMsg ="";
    	GenerWorkFlow gwf = null;
    	
    	//获取主干流程信息
    	if(flowNo!=null)
    		gwf = new GenerWorkFlow(workid);
    	
    	//存放信息地址
    	String hostURL = SystemConfig.GetValByKey("HostURL","");
		String path = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid;
		String frmID = "ND"+node.getNodeID();
		
		 //处理正确的文件名.
         if (fileNameFormat == null)
         {
             if (flowNo != null)
                 fileNameFormat = DBAccess.RunSQLReturnStringIsNull("SELECT Title FROM WF_GenerWorkFlow WHERE WorkID=" + workid, "" + String.valueOf(workid));
             else
                 fileNameFormat = String.valueOf(workid);
         }

         if (DataType.IsNullOrEmpty(fileNameFormat) == true)
             fileNameFormat = String.valueOf(workid);

         fileNameFormat = DataType.PraseStringToFileName(fileNameFormat);
        
         Hashtable ht = new Hashtable();
		
    	if(node.getHisFormType().getValue() == NodeFormType.FoolForm.getValue() || node.getHisFormType().getValue() == NodeFormType.FreeForm.getValue()){
    		resultMsg = setPDFPath(frmID,workid,flowNo,gwf );
    		if(resultMsg.indexOf("err@")!=-1)
    			return resultMsg;
    		
    		String billUrl = SystemConfig.getPathOfDataUser() + "\\InstancePacketOfData\\" + "ND"+node.getNodeID() + "\\" + workid + "\\index.htm";
    			
    		resultMsg = MakeHtmlDocument(node.getNodeFrmID(),  workid,  flowNo , fileNameFormat , urlIsHostUrl,path,billUrl,frmID,basePath);
    		
    		if(resultMsg.indexOf("err@")!=-1)
    			return resultMsg;
    		
    		ht.put("htm",SystemConfig.GetValByKey("HostURLOfBS","../../DataUser/") + "/InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid + "/index.htm");

            //#region 把所有的文件做成一个zip文件.
            //生成pdf文件
            String pdfPath = path + "\\pdf";
            
            if (new File(pdfPath).exists() == false)
            	new File(pdfPath).mkdirs();

            fileNameFormat = fileNameFormat.substring(0, fileNameFormat.length() - 1);
            String pdfFile = pdfPath + "\\" + fileNameFormat + ".pdf";       
            String pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware\\wkhtmltox\\wkhtmltopdf.exe";
            try
            {
                if(Html2Pdf(pdfFileExe, billUrl, pdfFile)== true)
	                if (urlIsHostUrl == false)
	                	ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS","../../DataUser/") + "InstancePacketOfData/" + frmID + "/" + workid + "/pdf/" + DataType.PraseStringToUrlFileName(fileNameFormat) + ".pdf");
	                else
	                	ht.put("pdf", SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/" + frmID + "/" + workid + "/pdf/" + DataType.PraseStringToUrlFileName(fileNameFormat) + ".pdf");

            }catch (Exception ex){
                /*有可能是因为文件路径的错误， 用补偿的方法在执行一次, 如果仍然失败，按照异常处理. */
                fileNameFormat = DBAccess.GenerGUID();
                pdfFile = pdfPath + "\\" + fileNameFormat + ".pdf";
                
                Html2Pdf(pdfFileExe, billUrl, pdfFile);
                ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS","") + "/InstancePacketOfData/" + frmID + "/" + workid + "/pdf/" + fileNameFormat + ".pdf");
            }
            
            //生成压缩文件
            String zipFile = path + "/" + fileNameFormat + ".zip";

            File finfo = new File(zipFile);
            ZipFilePath =finfo.getName();
            
            File zipFileFile = new File(zipFile);
    		try {
    			while (zipFileFile.exists() == true) {
    				zipFileFile.delete();
    			}
    			// 执行压缩.
    			ZipCompress fz = new ZipCompress(zipFile, pdfPath);
    			fz.zip();
    			ht.put("zip", SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/" + frmID + "/" + workid +"/"+ DataType.PraseStringToUrlFileName(fileNameFormat) + ".zip");
    		} catch (Exception ex) {
    			ht.put("zip","err@执行压缩出现错误:" + ex.getMessage() + ",路径tempPath:" + pdfPath + ",zipFile=" + finfo.getName());
    		}

    		if (zipFileFile.exists() == false)
    			ht.put("zip","err@压缩文件未生成成功,请在点击一次.");

            
            //把所有的文件做成一个zip文件.
            
            return BP.Tools.Json.ToJsonEntitiesNoNameMode(ht);
    	}
    	
    	//绑定表单库中的表单
    	if(node.getHisFormType().getValue() == NodeFormType.RefOneFrmTree.getValue()){
    		//获取绑定的表单
    		MapData mapData = new MapData(node.getNodeFrmID());
    		return MakeFormToPDF(node.getNodeFrmID(),mapData.getName(),node, workid,flowNo,fileNameFormat,urlIsHostUrl,basePath);
    	}
    	
    	if(node.getHisFormType().getValue() == NodeFormType.SheetTree.getValue()){
    		
    		 //生成pdf文件
            String pdfPath = path + "\\pdf";
            String pdfTempPath = path+"\\pdfTemp";
           
            DataRow dr =  null ;
    		resultMsg = setPDFPath("ND"+node.getNodeID(),workid,flowNo,gwf );
    		if(resultMsg.indexOf("err@")!=-1)
    			return resultMsg;
    		
    		//获取绑定的表单
    		 FrmNodes nds = new FrmNodes(node.getFK_Flow(), node.getNodeID());
    		 for(FrmNode item : nds.ToJavaList()){
    			 //判断当前绑定的表单是否启用
    			 if(item.getFrmEnableRoleInt() == FrmEnableRole.Disable.getValue())
    				 continue;

    			 //判断 who is pk
    			 if(flowNo!=null && item.getWhoIsPK() == WhoIsPK.PWorkID) //如果是父子流程
    				 workid = gwf.getPWorkID();
    			 //获取表单的信息执行打印
    			 String billUrl = SystemConfig.getPathOfDataUser() + "\\InstancePacketOfData\\" + "ND"+node.getNodeID() + "\\" + workid + "\\"+item.getFK_Frm()+"index.htm";
    			 resultMsg= MakeHtmlDocument(item.getFK_Frm(),  workid,  flowNo , fileNameFormat , urlIsHostUrl,path,billUrl,"ND"+node.getNodeID(),basePath);
    			
    			 if(resultMsg.indexOf("err@")!=-1)
    	    			return resultMsg;

    			 ht.put("htm_"+item.getFK_Frm(),SystemConfig.GetValByKey("HostURLOfBS","../../DataUser/") + "/InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid + "/"+item.getFK_Frm()+"index.htm");

    	         //#region 把所有的文件做成一个zip文件.
    	         if (new File(pdfTempPath).exists() == false)
    	        	 new File(pdfTempPath).mkdirs();

    	         fileNameFormat = fileNameFormat.substring(0, fileNameFormat.length() - 1);
    	         String pdfFormFile = pdfTempPath + "\\" + item.getFK_Frm() + ".pdf";     
    	         String pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware\\wkhtmltox\\wkhtmltopdf.exe";
    	         try
    	         {
	                Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
		           
    	         }catch (Exception ex){
	                /*有可能是因为文件路径的错误， 用补偿的方法在执行一次, 如果仍然失败，按照异常处理. */
	                Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
	            }
    	         
    			
    		 }
    		 
    		 //pdf合并
    		 String pdfFile = pdfPath + "\\" + fileNameFormat + ".pdf"; 
    		//开始合并处理
    		 if (new File(pdfPath).exists() == false)
	        	 new File(pdfPath).mkdirs();
    		 
    		 PDFMergerUtility merger=new PDFMergerUtility();
    		 String[] fileInFolder=BaseFileUtils.getFiles(pdfTempPath);
    		 for(int i=0;i<fileInFolder.length;i++){
    			merger.addSource(fileInFolder[i]);
    		  }
    		 merger.setDestinationFileName(pdfFile);
    		 merger.mergeDocuments();
    		 
    		 //合并完删除文件夹
    		 BaseFileUtils.deleteDirectory(pdfTempPath);
    		 if (urlIsHostUrl == false)
    			 ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS","../../DataUser/") + "InstancePacketOfData/" + frmID + "/" + workid + "/pdf/" + DataType.PraseStringToUrlFileName(fileNameFormat) + ".pdf");
             else
            	 ht.put("pdf", SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/" + frmID + "/" + workid + "/pdf/" + DataType.PraseStringToUrlFileName(fileNameFormat) + ".pdf");
    		
    		//生成压缩文件
             String zipFile = path + "/" + fileNameFormat + ".zip";

             File finfo = new File(zipFile);
             ZipFilePath =finfo.getName();
             
             File zipFileFile = new File(zipFile);
     		try {
     			while (zipFileFile.exists() == true) {
     				zipFileFile.delete();
     			}
     			// 执行压缩.
     			ZipCompress fz = new ZipCompress(zipFile, pdfPath);
     			fz.zip();
     			ht.put("zip", SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/" + frmID + "/" + workid +"/"+ DataType.PraseStringToUrlFileName(fileNameFormat) + ".zip");
     		} catch (Exception ex) {
     			ht.put("zip","err@执行压缩出现错误:" + ex.getMessage() + ",路径tempPath:" + pdfPath + ",zipFile=" + finfo.getName());
     		}

     		if (zipFileFile.exists() == false)
     			ht.put("zip","err@压缩文件未生成成功,请在点击一次.");
     		
     		
     		return BP.Tools.Json.ToJsonEntitiesNoNameMode(ht);
    	}
    	
    	return "warning@不存在需要打印的表单";
    	
    }
    
    /**
     * 独立表单的打印
     * @param frmId
     * @param frmName
     * @param node
     * @param workid
     * @param flowNo
     * @param fileNameFormat
     * @param urlIsHostUrl
     * @param basePath
     * @return
     * @throws Exception
     */
    public static String MakeFormToPDF(String frmId,String frmName,Node node, long workid,String flowNo,String fileNameFormat,boolean urlIsHostUrl,String basePath) throws Exception{
    	
    	GenerWorkFlow gwf = null;
    	//获取主干流程信息
    	if(flowNo!=null)
    		gwf = new GenerWorkFlow(workid);
    	//存放信息地址
    	String hostURL = SystemConfig.GetValByKey("HostURL","");
		String path = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid;
    	//生成pdf文件
        String pdfPath = path + "/pdf";

       
        DataRow dr =  null ;
		String resultMsg = setPDFPath("ND"+node.getNodeID(),workid,flowNo,gwf );
		if(resultMsg.indexOf("err@")!=-1)
			return resultMsg;
		
		 Hashtable ht = new Hashtable();
		//获取绑定的表单
		 FrmNode frmNode = new FrmNode();
		 frmNode.Retrieve("FK_Frm",frmId);
		
			 //判断当前绑定的表单是否启用
			 if(frmNode.getFrmEnableRoleInt() == FrmEnableRole.Disable.getValue())
				return "warning@"+frmName+"没有被启用";

			//处理正确的文件名.
	         if (fileNameFormat == null)
	         {
	             if (flowNo != null)
	                 fileNameFormat = DBAccess.RunSQLReturnStringIsNull("SELECT Title FROM WF_GenerWorkFlow WHERE WorkID=" + workid, "" + String.valueOf(workid));
	             else
	                 fileNameFormat = String.valueOf(workid);
	         }

	         if (DataType.IsNullOrEmpty(fileNameFormat) == true)
	             fileNameFormat = String.valueOf(workid);

	         fileNameFormat = DataType.PraseStringToFileName(fileNameFormat);
	         
			 //判断 who is pk
			 if(flowNo!=null && frmNode.getWhoIsPK() == WhoIsPK.PWorkID) //如果是父子流程
				 workid = gwf.getPWorkID();
			 //获取表单的信息执行打印
			 String billUrl = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid + "/"+frmNode.getFK_Frm()+"index.htm";
			 resultMsg= MakeHtmlDocument(frmNode.getFK_Frm(),  workid,  flowNo , fileNameFormat , urlIsHostUrl,path,billUrl,"ND"+node.getNodeID(),basePath);
			
			 if(resultMsg.indexOf("err@")!=-1)
	    			return resultMsg;

			 //ht.put("htm",SystemConfig.GetValByKey("HostURLOfBS","../../DataUser/") + "/InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid + "/"+frmNode.getFK_Frm()+"index.htm");

	         //#region 把所有的文件做成一个zip文件.
	         if (new File(pdfPath).exists() == false)
	        	 new File(pdfPath).mkdirs();

	         fileNameFormat = fileNameFormat.substring(0, fileNameFormat.length() - 1);
	         String pdfFormFile = pdfPath + "/" + frmNode.getFK_Frm() + ".pdf";     
	         String pdfFileExe = SystemConfig.getPathOfDataUser() + "ThirdpartySoftware/wkhtmltox/wkhtmltopdf.exe";
	       
	         try
	            {
	                if(Html2Pdf(pdfFileExe, resultMsg, pdfFormFile)== true)
		                if (urlIsHostUrl == false)
		                	ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS","../../DataUser/") + "InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid + "/pdf/"  + frmNode.getFK_Frm()+ ".pdf");
		                else
		                	ht.put("pdf", SystemConfig.GetValByKey("HostURL","") + "/DataUser/InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid + "/pdf/" + frmNode.getFK_Frm() + ".pdf");

	            }catch (Exception ex){
	                /*有可能是因为文件路径的错误， 用补偿的方法在执行一次, 如果仍然失败，按照异常处理. */
	                fileNameFormat = DBAccess.GenerGUID();
	                pdfFormFile = pdfPath + "\\" + fileNameFormat + ".pdf";
	                
	                Html2Pdf(pdfFileExe, resultMsg, pdfFormFile);
	                ht.put("pdf", SystemConfig.GetValByKey("HostURLOfBS","") + "/InstancePacketOfData/" + "ND"+node.getNodeID() + "/" + workid + "/pdf/"+ frmNode.getFK_Frm() + ".pdf");
	            }
	        
	        return BP.Tools.Json.ToJsonEntitiesNoNameMode(ht);    
    }
    
    //前期文件的准备
    private static String setPDFPath(String frmID,long workid,String flowNo,GenerWorkFlow gwf ) throws Exception{
    	 //准备目录文件.
        String path = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/" + frmID + "/";
        try
        {
            path = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/" + frmID + "/";
            File pathFile =new File(path);
            if (pathFile.exists()== false)
            	pathFile.mkdirs();
            
            path = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/" + frmID + "/" + workid;
            if (new File(path).exists() == false)
            	new File(path).mkdirs();

            //把模版文件copy过去.
            String templateFilePath = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/Template/";
            //判断模板文件临时目录是否存在
            File baseFile = new File(templateFilePath);
            if(baseFile.isDirectory() == false)
            	return "err@不存在模板文件夹";
            //获取模板文件列表
            File[]  finfos = baseFile.listFiles();
            if(finfos.length ==0)
            	return "err@不存在模板文件";
            for (File fl:finfos)
            {
                //if (fl.getName().contains("ShuiYin"))
                 //   continue;

                if (fl.getName().contains("htm"))
                    continue;

                //判断之前是否存在该文件 就删除掉
                if(new File(path + "/" + fl.getName()).exists())
                	new File(path + "/" + fl.getName()).delete();
                
                Files.copy( fl.getAbsoluteFile().toPath()
                		, new File(path + "/" + fl.getName()).toPath());
            }

        }
        catch (Exception ex)
        {
            return "err@读写文件出现权限问题，请联系管理员解决。" + ex.getMessage();
        }
        
        String hostURL = SystemConfig.GetValByKey("HostURL","");
        String billUrl = hostURL + "/DataUser/InstancePacketOfData/" + frmID + "/" + workid + "/index.htm";
        
        // begin生成二维码.
        if(SystemConfig.GetValByKeyBoolen("IsShowQrCode",false) == true){
            /*说明是图片文件.*/
            String qrUrl = hostURL + "/WF/WorkOpt/PrintDocQRGuide.htm?FrmID=" + frmID + "&WorkID=" + workid + "&FlowNo=" + flowNo;
            if (flowNo != null)
            {
                gwf = new GenerWorkFlow(workid);
                qrUrl = hostURL + "/WF/WorkOpt/PrintDocQRGuide.htm?AP=" + frmID + "$" + workid + "_" + flowNo + "_" + gwf.getFK_Node() + "_" + gwf.getStarter() + "_" + gwf.getFK_Dept();
            }
            
            //二维码的生成
            QrCodeUtil.createQrCode(qrUrl,path,"QR.png");
        }
        //end生成二维码.
        return "";
    }
    /***
     * 把文件生成Html
     */
    public static String ZipFilePath = "";

    public static String CCFlowAppPath = "/";
    public static String MakeHtmlDocument(String frmID, long workid, String flowNo , String fileNameFormat , 
    		boolean urlIsHostUrl,String path,String indexFile,String nodeID,String basePath){
        try
        {
            GenerWorkFlow gwf = null;
            if(flowNo!=null)
            gwf = new GenerWorkFlow(workid);

            //#region 定义变量做准备.
            //生成表单信息.
            MapData mapData = new MapData(frmID);
            
           if(mapData.getHisFrmType() == FrmType.Url){
            	String url = mapData.getUrl();
            	//替换系统参数
            	url = url.replaceAll("@WebUser.No", WebUser.getNo());
            	url = url.replaceAll("@WebUser.Name;", WebUser.getName());
            	url = url.replaceAll("@WebUser.FK_Dept;", WebUser.getFK_Dept());
            	url = url.replaceAll("@WebUser.FK_DeptName;", WebUser.getFK_DeptName());
            	//替换参数
            	if (url.indexOf("?") > 0){
            		//获取url中的参数
            		url = url.substring(url.indexOf('?'));
            		String[] params = url.split("&");
            		for(String param : params){
            			if(DataType.IsNullOrEmpty(param) || param.indexOf("@") == -1)
            				continue;
            			String[] paramArr = param.split("=");
                        if (paramArr.length == 2 && paramArr[1].indexOf('@') == 0) {
                            if (paramArr[1].indexOf("@WebUser.") == 0)
                            	continue;
                            url = url.replace(paramArr[1], gwf.GetValStrByKey(paramArr[1].substring(1)));
                        }
            		}
            		
            	}
            	String sb="<iframe style='width:100%;height:auto;' ID='" + mapData.getNo() + "'    src='" + url + "' frameborder=0  leftMargin='0'  topMargin='0' scrolling=auto></iframe></div>";
            	String  docs = DataType.ReadTextFile(SystemConfig.getPathOfDataUser() + "InstancePacketOfData/Template/indexUrl.htm");
            	docs = docs.replace("@Docs", sb.toString());
            	docs = docs.replace("@Width", String.valueOf(mapData.getFrmW())+"px");
            	docs = docs.replace("@Height", String.valueOf(mapData.getFrmH())+"px");
            	if(gwf!=null)
            		docs = docs.replace("@Title", gwf.getTitle());
            	 DataType.WriteFile(indexFile, docs);
            	return indexFile;
            }
            GEEntity en = new GEEntity(frmID, workid);
            Node nd = null;
            if(gwf!=null){
            	nd = new Node(gwf.getFK_Node());
            }
            

            //begin 生成水文.
            if(SystemConfig.GetValByKeyBoolen("IsShowShuiYin",false) == true){
	            String rdt = "";
	            if (en.getEnMap().getAttrs().contains("RDT"))
	            {
	                rdt = en.GetValStringByKey("RDT");
	                if (rdt.length() > 10)
	                    rdt = rdt.substring(0, 10);
	            }
	            String words="";
	            if(nd.getNodeID()!=0 && nd.getNodeID()!=9999)
	            	words = nd.getShuiYinModle();
	            if(DataType.IsNullOrEmpty(words) == true)
	            	words = SystemConfig.GetValByKey("PrintBackgroundWord","驰骋工作流引擎@开源驰骋 - ccflow@openc");
	            
	            words = words.replaceAll("@RDT", rdt);
	            if (words.contains("@") == true)
	                words = BP.WF.Glo.DealExp(words, en);
	
	            String templateFilePathMy = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/Template/";
	            paintWaterMarkPhoto(templateFilePathMy + "ShuiYin.png",words,path + "/ShuiYin.png");
            }
            //end 水文结束

            //生成 表单的 html.
            StringBuilder sb = new StringBuilder();
          

            //首先判断是否有约定的文件.
            String docs = "";
            String tempFile = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/Template/" + mapData.getNo() + ".htm";
            if (new File(tempFile).exists() == false){
                if (gwf != null)
                {
                    if (nd.getHisFormType() == NodeFormType.FreeForm)
                        mapData.setHisFrmType(FrmType.FreeFrm);
                    else if(nd.getHisFormType() == NodeFormType.FoolForm)
                        mapData.setHisFrmType( FrmType.FoolForm);
                    else if(nd.getHisFormType() == NodeFormType.SelfForm)
                        mapData.setHisFrmType( FrmType.Url);
                }

                if (mapData.getHisFrmType() == FrmType.FoolForm)
                {
                    docs = DataType.ReadTextFile(SystemConfig.getPathOfDataUser() + "InstancePacketOfData/Template/indexFool.htm");
                    sb =GenerHtmlOfFool(mapData, frmID, workid, en, path, flowNo,nodeID,basePath);
                    docs = docs.replace("@Width", String.valueOf(mapData.getFrmW())+"px");
                }
                else if(mapData.getHisFrmType() == FrmType.FreeFrm)
                {
                    docs = DataType.ReadTextFile(SystemConfig.getPathOfDataUser() + "InstancePacketOfData/Template/indexFree.htm");
                    sb = GenerHtmlOfFree(mapData, frmID, workid, en, path, flowNo,nodeID,basePath);
                    docs = docs.replace("@Width", String.valueOf(mapData.getFrmW()*1.5)+"px");
                    
                }
            }


            docs = docs.replace("@Docs", sb.toString());
           
            docs = docs.replace("@Height", String.valueOf(mapData.getFrmH())+"px");
            
            Date date = new Date(); 
            SimpleDateFormat sy1=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
            String dateFormat=sy1.format(date); 
            docs = docs.replace("@PrintDT", dateFormat );

            if (flowNo != null )
            {
            	gwf = new GenerWorkFlow(workid);
                gwf.setWorkID(workid);
                gwf.RetrieveFromDBSources();

                docs = docs.replace("@Title", gwf.getTitle());

                if (gwf.getWFState() == WFState.Runing)
                {
                    if ( SystemConfig.getCustomerNo()=="TianYe" && gwf.getNodeName().contains("反馈") == true)
                    {
                        nd = new Node(gwf.getFK_Node());
                        if (nd.getIsEndNode() == true)
                        {
                            //让流程自动结束.
                            BP.WF.Dev2Interface.Flow_DoFlowOver(gwf.getFK_Flow(), workid, "打印并自动结束",0);
                        }
                    }
                }

                //替换模版尾部的打印说明信息.
                String pathInfo = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/Template/EndInfo/" + flowNo + ".txt";
                if (new File(pathInfo).exists() == false)
                    pathInfo = SystemConfig.getPathOfDataUser() + "InstancePacketOfData/Template/EndInfo/Default.txt";

                docs = docs.replace("@EndInfo", DataType.ReadTextFile(pathInfo));
            }

            //indexFile = SystemConfig.getPathOfDataUser() + "\\InstancePacketOfData\\" + frmID + "\\" + workid + "\\index.htm";
            DataType.WriteFile(indexFile, docs);
            
            return indexFile;
        }
        catch (Exception ex)
        {
            return "err@报表生成错误:" + ex.getMessage();
        }
    }
    
    
    private static boolean Html2Pdf(String pdfFileExe, String htmFile, String pdf) throws Exception
    {
        BP.DA.Log.DebugWriteInfo("@开始生成PDF:" + pdfFileExe + "@pdf=" + pdf + "@htmFile=" + htmFile);
        StringBuilder cmd = new StringBuilder();
        if(System.getProperty("os.name").indexOf("Windows") == -1){
        	//非windows 系统
        	pdfFileExe = "/home/ubuntu/wkhtmltox/bin/wkhtmltopdf";
        }
        cmd.append(pdfFileExe);
        cmd.append(" ");
        cmd.append(" --header-line");//页眉下面的线
        //cmd.append(" --header-center 这里是页眉这里是页眉这里是页眉这里是页眉 ");//页眉中间内容
        cmd.append(" --margin-top 3cm ");//设置页面上边距 (default 10mm) 
        //cmd.append(" --header-html file:///"+WebUtil.getServletContext().getRealPath("")+FileUtil.convertSystemFilePath("\\style\\pdf\\head.html"));// (添加一个HTML页眉,后面是网址)
        cmd.append(" --header-spacing 5 ");// (设置页眉和内容的距离,默认0)
        //cmd.append(" --footer-center (设置在中心位置的页脚内容)");//设置在中心位置的页脚内容
       // cmd.append(" --footer-html file:///"+WebUtil.getServletContext().getRealPath("")+FileUtil.convertSystemFilePath("\\style\\pdf\\foter.html"));// (添加一个HTML页脚,后面是网址)
        cmd.append(" --footer-line");//* 显示一条线在页脚内容上)
        cmd.append(" --footer-spacing 5 ");// (设置页脚和内容的距离)

        cmd.append(htmFile);
        cmd.append(" ");
        cmd.append(pdf);     
        boolean result = true;
        try{
            Process proc = Runtime.getRuntime().exec(cmd.toString());
            HtmlToPdfInterceptor error = new HtmlToPdfInterceptor(proc.getErrorStream());
            HtmlToPdfInterceptor output = new HtmlToPdfInterceptor(proc.getInputStream());
            error.start();
            output.start();
            proc.waitFor();
        }catch(Exception e){
            result = false;
            e.printStackTrace();
        }
        return result;
    }
    
   
    
    private static void paintWaterMarkPhoto(String targerImagePath,String words,String srcImagePath) {
        Integer degree = -15;
        OutputStream os = null;
        try {
            Image srcImage = ImageIO.read(new File(srcImagePath));
            BufferedImage bufImage = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            // 得到画布对象
            Graphics2D graphics2D = bufImage.createGraphics();
            // 设置对线段的锯齿状边缘处理
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(srcImage.getScaledInstance(srcImage.getWidth(null), srcImage.getHeight(null), Image.SCALE_SMOOTH),
                    0, 0, null);
            if (null != degree) {
                // 设置水印旋转角度及坐标
                graphics2D.rotate(Math.toRadians(degree), (double) bufImage.getWidth() / 2, (double) bufImage.getHeight() / 2);
            }
            // 透明度
            float alpha = 0.25f;
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 设置颜色和画笔粗细
            graphics2D.setColor(Color.gray);
            graphics2D.setStroke(new BasicStroke(10));
            graphics2D.setFont(new Font("SimSun", Font.ITALIC, 18));
            // 绘制图案或文字
            String cont = words;
            String dateStr = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
            int charWidth1 = 8;
            int charWidth2 = 8;
            int halfGap = 12;
            graphics2D.drawString(cont, (srcImage.getWidth(null) - cont.length() * charWidth1) / 2,
                    (srcImage.getHeight(null) - (charWidth1 + halfGap)) / 2);
            graphics2D.drawString(dateStr, (srcImage.getWidth(null) - dateStr.length() * charWidth2) / 2,
                    (srcImage.getHeight(null) + (charWidth2 + halfGap)) / 2);
 
            graphics2D.dispose();
 
            os = new FileOutputStream(targerImagePath);
            // 生成图片 (可设置 jpg或者png格式)
            ImageIO.write(bufImage, "png", os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 根据En.getRow()替换exp中的参数
     * @param en
     * @param exp
     * @return
     * @throws Exception 
     */
    private static String DealEnExp(Entity en,String exp) throws Exception{
    	exp = exp.replace("@WebUser.No", WebUser.getNo());
    	exp = exp.replace("@WebUser.Name", WebUser.getName());
    	exp = exp.replace("@WebUser.FK_DeptNameOfFull", WebUser.getFK_DeptNameOfFull());
    	exp = exp.replace("@WebUser.DeptName", WebUser.getFK_DeptName());
    	exp = exp.replace("@WebUser.FK_Dept", WebUser.getFK_Dept());

    	if(exp.contains("@") == false)
    		return exp;
    	Row row = en.getRow();
    	for (String item : row.keySet()) {
    		exp = exp.replace("@"+item, row.get(item).toString());
    		if(exp.contains("@")==false)
    			return exp;
    	}
    	return exp;
    }
    
    /**
     * 图片签名
     * @param userNo
     * @return
     * @throws Exception 
     */
    private static String SignPic(String userNo) throws Exception{
        if (DataType.IsNullOrEmpty(userNo))
            return "";

        String path = SystemConfig.getPathOfDataUser() + "Siganture/"+ userNo + ".jpg";
        File file = new File(path);
        //如果文件不存在
        if (file.exists() == false)
        {
            path = SystemConfig.getPathOfDataUser() + "Siganture/"+ userNo  + ".JPG";
            if (new File(path).exists() == true)
                return "<img src='"+path+"' style='border:0px;width:100px;height:30px;'/>";
            else {
            	Emp emp = new Emp(userNo);
            	return emp.getName(); 
            }
            	
        }else{
        	return "<img src='"+path+"' style='border:0px;width:100px;height:30px;'/>";
        }
   
    }
}
