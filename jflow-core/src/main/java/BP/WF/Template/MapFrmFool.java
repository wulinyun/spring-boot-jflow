package BP.WF.Template;

import java.io.IOException;

import BP.DA.*;
import BP.Difference.ContextHolderUtils;
import BP.En.*;
import BP.WF.*;
import BP.WF.Glo;
import BP.Sys.*;

/** 
 傻瓜表单属性
 
*/
public class MapFrmFool extends EntityNoName
{
 
	/** 
	 是否是节点表单?
	 
	*/
	public final boolean getIsNodeFrm()
	{
		if (this.getNo().contains("ND") == false)
		{
			return false;
		}

		if (this.getNo().contains("Rpt") == true)
		{
			return false;
		}

		if (this.getNo().substring(0, 2).equals("ND") && this.getNo().contains("Dtl")==false)
		{
			return true;
		}

		return false;
	}
	/** 
	物理存储表.
	 
	*/
    public final String getPTable()
    {
    	return this.GetValStrByKey(MapDataAttr.PTable);
    }
	/** 
	物理存储表.
	 
	*/
    public final void setPTable(String value)
    {
    	this.SetValByKey(MapDataAttr.PTable, value);
    }
	/** 
	 节点ID.
	 
	*/
	public final int getNodeID()
	{
		return Integer.parseInt(this.getNo().replace("ND", ""));
	}
	/** 
	 傻瓜表单-宽度
	 
	*/
	public final String getTableWidth()
	{
		int i = this.GetValIntByKey(MapDataAttr.TableWidth);
		if (i <= 50)
		{
			return "900";
		}
		return (new Integer(i)).toString();
	}
	/** 
	 傻瓜表单-高度
	 
	*/
	public final String getTableHeight()
	{
		int i = this.GetValIntByKey(MapDataAttr.TableHeight);
		if (i <= 500)
		{
			return "900";
		}
		return (new Integer(i)).toString();
	}
	/** 
	 表格显示的列
	 
	*/
	public final int getTableCol()
	{
		return 4;

	}
	public final void setTableCol(int value)
	{
		this.SetValByKey(MapDataAttr.TableCol, value);
	}


		///#endregion


		///#region 权限控制.
	@Override
	public UAC getHisUAC() throws Exception
	{
		UAC uac = new UAC();
		if (BP.Web.WebUser.getNo().equals("admin"))
		{
			uac.IsDelete = false;
			uac.IsUpdate = true;
			return uac;
		}
		uac.Readonly();
		return uac;
	}

		///#endregion 权限控制.


		
	/** 
	 傻瓜表单属性
	 
	*/
	public MapFrmFool()
	{
	}
	/** 
	 傻瓜表单属性
	 
	 @param no 表单ID
	 * @throws Exception 
	*/
	public MapFrmFool(String no) throws Exception
	{
		super(no);
	}
	/** 
	 EnMap
	 
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_MapData", "傻瓜傻瓜表单属性");
		map.Java_SetEnType(EnType.Sys);
		map.Java_SetCodeStruct("4");


		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, false, 1, 190, 20);
		map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 100, 20);
		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 500, 20, true);

		 map.AddDDLSysEnum(MapDataAttr.TableCol, 0, "表单显示列数", true, true, MapDataAttr.TableCol,
                 "@0=4列@1=6列@2=3列");
		map.AddTBInt(MapDataAttr.TableWidth, 900, "傻瓜表单宽度", true, false);
		map.AddTBInt(MapDataAttr.TableHeight, 900, "傻瓜表单高度", true, false);

			//数据源.
		map.AddDDLEntities(MapDataAttr.DBSrc, "local", "数据源", new SFDBSrcs(), true);
		map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), true);
			//表单的运行类型.
		map.AddDDLSysEnum(MapDataAttr.FrmType, 0, "表单类型", true, true, MapDataAttr.FrmType);

			///#endregion 基本属性.


			///#region 设计者信息.
		map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, true);
		map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
		map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);

		map.AddTBStringDoc(MapDataAttr.Note, null, "备注", true, false, true);
			//增加参数字段.
		map.AddTBAtParas(4000);
		map.AddTBInt(MapDataAttr.Idx, 100, "顺序号", false, false);

			///#endregion 设计者信息.

			//查询条件.
		map.AddSearchAttr(MapDataAttr.DBSrc);


			///#region 方法 - 基本功能.

		RefMethod rm = new RefMethod();
		rm = new RefMethod();
		rm.Title = "启动傻瓜表单设计器";
		rm.ClassMethodName = this.toString() + ".DoDesignerFool";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/FileType/xlsx.gif";
		rm.Visable = true;
		rm.Target = "_blank";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "装载填充"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoPageLoadFull";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/FullData.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单事件"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoEvent";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "批量设置验证规则";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/RegularExpression.png";
		rm.ClassMethodName = this.toString() + ".DoRegularExpressionBatch";
		rm.refMethodType = RefMethodType.RightFrameOpen;

		rm = new RefMethod();
		rm.Title = "批量修改字段"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBatchEditAttr";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Admin/CCBPMDesigner/Img/field.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";


		rm = new RefMethod();
		rm.Title = "手机端表单";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Admin/CCFormDesigner/Img/telephone.png";
		rm.ClassMethodName = this.toString() + ".DoSortingMapAttrs";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "内置JavaScript脚本"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoInitScript";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/Script.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单body属性"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBodyAttr";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/Script.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "导出XML表单模版"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoExp";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/Export.png";
		rm.Visable = true;
		rm.RefAttrLinkLabel = "导出到xml";
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		//带有参数的方法.
		rm = new RefMethod();
		rm.Title = "重命名字段";
		rm.getHisAttrs().AddTBString("FieldOld", null, "旧字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNew", null, "新字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNewName", null, "新字段中文名", true, false, 0, 100, 100);
		rm.ClassMethodName = this.toString() + ".DoChangeFieldName";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/ReName.png";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单检查"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoCheckFixFrmForUpdateVer";
		rm.Visable = true;
		rm.RefAttrLinkLabel = "表单检查";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/check.png";
		rm.Target = "_blank";
		map.AddRefMethod(rm);
		
		rm = new RefMethod();
        rm.Title = "模板打印";
        rm.ClassMethodName = this.toString() + ".DoBill";
        rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/FileType/doc.gif";
        rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);


			///#endregion 方法 - 基本功能.


			///#region 方法 - 开发接口.
		rm = new RefMethod();
		rm.Title = "调用查询API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSearch";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/Table.gif";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "调用分析API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoGroup";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/Table.gif";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);

			///#endregion 方法 - 开发接口.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected void afterInsertUpdateAction() throws Exception {
		//修改关联明细表
        MapDtl dtl = new MapDtl();
        dtl.setNo(this.getNo());
        if (dtl.RetrieveFromDBSources() == 1)
        {
        	dtl.setName(this.getName());
        	dtl.setPTable(this.getPTable());
        	dtl.DirectUpdate();

            MapData map = new MapData(this.getNo());
            //避免显示在表单库中
            map.setFK_FrmSort("");
            map.setFK_FormTree("");
            map.DirectUpdate();
        }	
	}

		///#region 节点表单方法.
	/** 
	 单据打印
	 @return 
	*/
    public String DoBill()
    {
        return "../../Admin/AttrNode/Bill.htm?FK_MapData=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();
    }
	/** 
	 傻瓜表单设计器
	 
	 @return 
	*/
	public final String DoDesignerFool()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&MyPK=" + this.getNo() + "&IsEditMapData=True&IsFirst=1";
	}

	/** 
	 节点表单组件
	 
	 @return 
	*/
	public final String DoNodeFrmCompent()
	{
		if (this.getNo().contains("ND") == true)
		{
			return Glo.getCCFlowAppPath() + "WF/Comm/EnOnly.htm?EnName=BP.WF.Template.FrmNodeComponent&PK=" + this.getNo().replace("ND", "") + "&t=" + DataType.getCurrentDataTime();
		}
		else
		{
			return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/Do.htm&DoType=FWCShowError";
		}
	}
	/** 
	 执行旧版本的兼容性检查.
	 * @throws Exception 
	 
	*/
	public final String DoCheckFixFrmForUpdateVer() throws Exception
	{
		// 更新状态.
		DBAccess.RunSQL("UPDATE Sys_GroupField SET CtrlType='' WHERE CtrlType IS NULL");
		DBAccess.RunSQL("UPDATE Sys_GroupField SET CtrlID='' WHERE CtrlID IS NULL");
		 

		String str = "";
  
        //一直遇到遇到自动变长的问题, 强制其修复过来.
        DBAccess.RunSQL("UPDATE Sys_Mapattr SET colspan=3 WHERE UIHeight<=38 AND colspan=4");

         // 处理失去分组的字段. 
        String sql = "SELECT MyPK FROM Sys_MapAttr WHERE FK_MapData='" + this.getNo() + "' AND GroupID NOT IN (SELECT OID FROM Sys_GroupField WHERE FrmID='" + this.getNo() + "' AND ( CtrlType='' OR CtrlType IS NULL)  )  OR GroupID IS NULL ";
        MapAttrs attrs = new MapAttrs();
        attrs.RetrieveInSQL(sql);
        if (attrs.size() != 0)
        {
            GroupField gf = null;
            GroupFields gfs = new GroupFields(this.getNo());
            for (GroupField mygf : gfs.ToJavaList())
            {
                if ( DataType.IsNullOrEmpty( mygf.getCtrlID()) )
                    gf = mygf;
            }

            if (gf == null)
            {
                gf = new GroupField();
                gf.setLab(  "基本信息");
                gf.setFrmID( this.getNo());
                gf.Insert();
            }

            //设置GID.
            for (MapAttr attr : attrs.ToJavaList())
            {
                attr.Update(MapAttrAttr.GroupID, gf.getOID());
            }
        }
		//从表.
		MapDtls dtls = new MapDtls(this.getNo());
		for (MapDtl dtl : dtls.ToJavaList())
		{
			GroupField gf = new GroupField();
			int i = gf.Retrieve(GroupFieldAttr.CtrlID, dtl.getNo(), GroupFieldAttr.FrmID, this.getNo());
            if (i == 1)
                continue;
		
			gf.setLab(dtl.getName());
			gf.setCtrlID(dtl.getNo());
			gf.setCtrlType("Dtl");
			gf.setFrmID(dtl.getFK_MapData());
			gf.DirectSave();

			str += "@为从表" + dtl.getName() + " 增加了分组.";
		}

		// 框架.
		MapFrames frams = new MapFrames(this.getNo());
		for (MapFrame fram : frams.ToJavaList())
		{
			GroupField gf = new GroupField();
			 int i = gf.Retrieve(GroupFieldAttr.CtrlID, fram.getMyPK(), GroupFieldAttr.FrmID, this.getNo());
             if (i == 1)
                 continue;
			

			gf.setLab(fram.getName());
			gf.setCtrlID(fram.getMyPK());
			gf.setCtrlType("Frame");
			gf.setFrmID(fram.getFK_MapData());
			gf.Insert();

			str += "@为框架 " + fram.getName() + " 增加了分组.";

		}
 
		// 附件.
		FrmAttachments aths = new FrmAttachments(this.getNo());
		for (FrmAttachment ath : aths.ToJavaList())
		{
			   GroupField gf = new GroupField();
			   int i = gf.Retrieve(GroupFieldAttr.CtrlID, ath.getMyPK(), GroupFieldAttr.FrmID, this.getNo());
               if (i == 1)
                   continue;
               
              

			gf.setLab(ath.getName());
			gf.setCtrlID(ath.getMyPK());
			gf.setCtrlType("Ath");
			gf.setFrmID(ath.getFK_MapData());
			gf.Insert();

			str += "@为附件 " + ath.getName() + " 增加了分组.";
		}

		if (this.getIsNodeFrm() == true)
		{
			FrmNodeComponent conn = new FrmNodeComponent(this.getNodeID());
			conn.Update();
		}

		//删除重复的数据, 比如一个从表显示了多个分组里. @于庆海增加此部分.
		String sqls = "";
		if (SystemConfig.getAppCenterDBType() == DBType.Oracle)
		{
			sqls = "SELECT * FROM (SELECT FrmID,CtrlID,CtrlType, count(*) as Num FROM sys_groupfield WHERE CtrlID!='' GROUP BY FrmID,CtrlID,CtrlType ) WHERE Num > 1";
		}
		else
		{
			sqls = "SELECT * FROM (SELECT FrmID,CtrlID,CtrlType, count(*) as Num FROM sys_groupfield WHERE CtrlID!='' GROUP BY FrmID,CtrlID,CtrlType ) AS A WHERE A.Num > 1";
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sqls);
		for (DataRow dr : dt.Rows)
		{
			String enName = dr.getValue(0).toString();
			String ctrlID = dr.getValue(1).toString();
			String ctrlType = dr.getValue(2).toString();

			GroupFields gfs = new GroupFields();
			gfs.Retrieve(GroupFieldAttr.EnName, enName, GroupFieldAttr.CtrlID, ctrlID, GroupFieldAttr.CtrlType, ctrlType);

			if (gfs.size() <= 1)
			{
				continue;
			}
			for (GroupField gf : gfs.ToJavaList())
			{
				gf.Delete(); //删除其中的一个.
				break;
			}
		}
		
		if (str.equals(""))
		{
			return "检查成功.";
		}

		return str + ", @@@ 检查成功。";
	}

		///#endregion


		///#region 通用方法.
	/** 
	 替换名称
	 
	 @param fieldOldName 旧名称
	 @param newField 新字段
	 @param newFieldName 新字段名称(可以为空)
	 @return 
	 * @throws Exception 
	*/
	public final String DoChangeFieldName(String fieldOld, String newField, String newFieldName) throws Exception
	{
		MapAttr attrOld = new MapAttr();
		attrOld.setKeyOfEn(fieldOld);
		attrOld.setFK_MapData(this.getNo());
		attrOld.setMyPK(attrOld.getFK_MapData() + "_" + attrOld.getKeyOfEn());
		if (attrOld.RetrieveFromDBSources() == 0)
		{
			return "@旧字段输入错误[" + attrOld.getKeyOfEn() + "].";
		}

		//检查是否存在该字段？
		MapAttr attrNew = new MapAttr();
		attrNew.setKeyOfEn(newField);
		attrNew.setFK_MapData(this.getNo());
		attrNew.setMyPK(attrNew.getFK_MapData() + "_" + attrNew.getKeyOfEn());
		if (attrNew.RetrieveFromDBSources() == 1)
		{
			return "@该字段[" + attrNew.getKeyOfEn() + "]已经存在.";
		}

		//删除旧数据.
		attrOld.Delete();

		//copy这个数据,增加上它.
		attrNew.Copy(attrOld);
		attrNew.setKeyOfEn(newField);
		attrNew.setFK_MapData(this.getNo());

		if (!newFieldName.equals(""))
		{
			attrNew.setName(newFieldName);
		}

		attrNew.Insert();

		//更新处理他的相关业务逻辑.
		MapExts exts = new MapExts(this.getNo());
		for (MapExt item : exts.ToJavaList())
		{
			item.setMyPK(item.getMyPK().replace("_" + fieldOld, "_" + newField));

			if (fieldOld.equals(item.getAttrOfOper()))
			{
				item.setAttrOfOper(newField);
			}

			if (fieldOld.equals(item.getAttrsOfActive()))
			{
				item.setAttrsOfActive(newField);
			}

			item.setTag(item.getTag().replace(fieldOld, newField));
			item.setTag1(item.getTag1().replace(fieldOld, newField));
			item.setTag2(item.getTag2().replace(fieldOld, newField));
			item.setTag3(item.getTag3().replace(fieldOld, newField));

			item.setAtPara(item.getAtPara().replace(fieldOld, newField));
			item.setDoc(item.getDoc().replace(fieldOld, newField));
			item.Save();
		}
		return "执行成功";
	}
	/** 
	 批量设置正则表达式规则.
	 
	 @return 
	*/
	public final String DoRegularExpressionBatch()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/MapExt/RegularExpressionBatch.jsp?FK_Flow=&FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDataTime();
	}
	/** 
	 批量修改字段
	 
	 @return 
	*/
	public final String DoBatchEditAttr()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/BatchEdit.htm?FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDataTime();
	}
	/** 
	 排序字段顺序
	 
	 @return 
	*/
	public final String DoSortingMapAttrs()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/AttrNode/SortingMapAttrs.htm?FK_Flow=&FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDataTime();
	}
	/** 
	 设计表单
	 
	 @return 
	 * @throws Exception 
	*/
	public final String DoDFrom() throws Exception
	{
		String url = Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/CCForm/Frm.jsp?FK_MapData=" + this.getNo() + "&UserNo=" + BP.Web.WebUser.getNo() + "&SID=" + BP.Web.WebUser.getSID() + "&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&CustomerNo=" + SystemConfig.getCustomerNo();
		try {
			PubClass.WinOpen(ContextHolderUtils.getResponse(), url, 800, 650);
		} catch (IOException e) {
			Log.DebugWriteError("MapFrmFool DoDFrom()" + e);
		}
		return null;
	}
	/** 
	 设计傻瓜表单
	 
	 @return 
	 * @throws Exception 
	*/
	public final String DoDFromCol4() throws Exception
	{
		String url = Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&UserNo=" + BP.Web.WebUser.getNo() + "&SID=" + BP.Web.WebUser.getSID() + "&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&IsFirst=1&CustomerNo=" + SystemConfig.getCustomerNo();
		try {
			PubClass.WinOpen(ContextHolderUtils.getResponse(), url, 800, 650);
		} catch (IOException e) {
			Log.DebugWriteError("MapFrmFool DoDFromCol4()" + e);
		}
		return null;
	}
	/** 
	 查询
	 @return 
	*/
	public final String DoSearch()
	{
		return Glo.getCCFlowAppPath() + "WF/Comm/Search.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=" + this.getNo();
	}
	/** 
	 调用分析API
	 @return 
	*/
	public final String DoGroup()
	{
		return Glo.getCCFlowAppPath() + "WF/Comm/Group.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=" + this.getNo();
	}
	/** 
	 数据源管理
	 @return 
	*/
	public final String DoDBSrc()
	{
		return Glo.getCCFlowAppPath() + "WF/Comm/Search.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=BP.Sys.SFDBSrcs";
	}
	public final String DoWordFrm()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/MapExt/WordFrm.jsp?s=34&FK_MapData=" + this.getNo() + "&ExtType=WordFrm&RefNo=";
	}
	public final String DoExcelFrm()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/MapExt/ExcelFrm.jsp?s=34&FK_MapData=" + this.getNo() + "&ExtType=ExcelFrm&RefNo=";
	}
	public final String DoPageLoadFull()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/MapExt/PageLoadFull.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=";
	}
	public final String DoInitScript()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/MapExt/InitScript.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=";
	}
	/** 
	 傻瓜表单属性.
	 @return 
	*/
	public final String DoBodyAttr()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/MapExt/BodyAttr.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=BodyAttr&RefNo=";
	}
	/** 
	 表单事件
	 @return 
	*/
	public final String DoEvent()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/CCFormDesigner/Action.htm?FK_MapData=" + this.getNo() + "&T=sd&FK_Node=0";
	}
	/** 
	 导出
	 @return 
	*/
	public final String DoMapExt()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/MapExt/List.jsp?FK_MapData=" + this.getNo() + "&T=sd";
	}
	/** 
	 导出表单
	 @return 
	*/
	public final String DoExp()
	{
		return Glo.getCCFlowAppPath() + "WF/Admin/FoolFormDesigner/ImpExp/Exp.htm?FK_MapData=" + this.getNo();
		/*String urlExt = Glo.getCCFlowAppPath() + "WF/Admin/XAP/DoPort.jsp?DoType=DownFormTemplete&FK_MapData=" + this.getNo();
		try {
			PubClass.WinOpen(ContextHolderUtils.getResponse(), urlExt, 900, 1000);
		} catch (IOException e) {
			Log.DebugWriteError("MapFrmFool DoExp()" + e);
		}
		return null;*/
	}

		///#endregion 方法.
}