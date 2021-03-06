package BP.WF.HttpHandler;

import java.math.BigDecimal;
import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;

import BP.DA.AtPara;
import BP.DA.DBAccess;
import BP.DA.DBType;
import BP.DA.DataColumn;
import BP.DA.DataRow;
import BP.DA.DataSet;
import BP.DA.DataTable;
import BP.DA.DataType;
import BP.DA.Paras;
import BP.Difference.Handler.WebContralBase;
import BP.En.Attr;
import BP.En.Attrs;
import BP.En.Entities;
import BP.En.EntitiesSimpleTree;
import BP.En.EntitiesTree;
import BP.En.Entity;
import BP.En.FieldType;
import BP.En.QueryObject;
import BP.En.UIContralType;
import BP.Sys.DTSearchWay;
import BP.Sys.GEEntitys;
import BP.Sys.MapAttr;
import BP.Sys.MapAttrAttr;
import BP.Sys.MapAttrs;
import BP.Sys.MapData;
import BP.Sys.SysEnum;
import BP.Sys.SysEnums;
import BP.Sys.SystemConfig;
import BP.Sys.UserRegedit;
import BP.Sys.UserRegeditAttr;
import BP.WF.DotNetToJavaStringHelper;
import BP.WF.Flow;
import BP.WF.Rpt.RptDfine;
import BP.Web.WebUser;

public class WF_RptDfine extends WebContralBase{
	
	/**
	 * 构造函数
	 */
	public WF_RptDfine()
	{
	
	}
	
	
	public final String getSearchType()
	{
		String val = this.GetRequestVal("SearchType");
		if(DataType.IsNullOrEmpty(val))
			val = getGroupType();
		return val;
	}
	
	public final String getGroupType(){
		return this.GetRequestVal("GroupType");
	}
	/** 流程列表
	 
	 @return 
	 */
	public final String Flowlist_Init()
	{
		DataSet ds = new DataSet();
		String sql = "SELECT No,Name,ParentNo FROM WF_FlowSort ORDER BY ParentNo, Idx";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sort";
		if (SystemConfig.getAppCenterDBType() == DBType.Oracle)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
			dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
		}
		ds.Tables.add(dt);

		sql = "SELECT No,Name,FK_FlowSort FROM WF_Flow ORDER BY FK_FlowSort, Idx";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Flows";
		if (SystemConfig.getAppCenterDBType() == DBType.Oracle)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
		}
		ds.Tables.add(dt);

		return BP.Tools.Json.ToJson(ds);
	}
	
	/** 功能列表
	 
	 @return 
	 * @throws Exception 
*/
	public final String Default_Init() throws Exception
	{
		java.util.Hashtable ht = new java.util.Hashtable();
		ht.put("My", "我发起的流程");
		ht.put("MyJoin", "我参与的流程");

		RptDfine rd = new RptDfine(this.getFK_Flow());

		///#region 增加本部门发起流程的查询.
		if (rd.getMyDeptRole() == 0)
		{
			//如果仅仅部门领导可以查看: 检查当前人是否是部门领导人.
			if (DBAccess.IsExitsTableCol("Port_Dept", "Leader") == true)
			{
				String sql = "SELECT Leader FROM Port_Dept WHERE No='" + WebUser.getFK_Dept() + "'";
				String strs = DBAccess.RunSQLReturnStringIsNull(sql, null);
				if (strs != null && strs.contains(WebUser.getNo()) == true)
				{
					ht.put("MyDept", "我本部门发起的流程");
				}
			}
		}

		if (rd.getMyDeptRole() == 1)
		{
			//如果部门下所有的人都可以查看: 
			ht.put("MyDept", "我本部门发起的流程");
		}

		if (rd.getMyDeptRole() == 2)
		{
			//如果部门下所有的人都可以查看: 
			ht.put("MyDept", "我本部门发起的流程");
		}
		///#endregion 增加本部门发起流程的查询.

		if (WebUser.getIsAdmin())
		{
			ht.put("Adminer", "高级查询");
		}
		
		Flow fl = new Flow(this.getFK_Flow());
		ht.put("FlowName",fl.getName());

		return BP.Tools.Json.ToJsonEntitiesNoNameModel(ht);
	}
	
	public final String FlowSearch_Init() throws Exception
	{
		if (StringUtils.isEmpty(this.getFK_Flow()))
		{
			return "err@参数FK_Flow不能为空";
		}

		String pageSize = GetRequestVal("pageSize");
		String fcid = "";
		DataSet ds = new DataSet();
		java.util.HashMap<String, String> vals = null;
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();

		//报表信息，包含是否显示关键字查询RptIsSearchKey，过滤条件枚举/下拉字段RptSearchKeys，时间段查询方式RptDTSearchWay，时间字段RptDTSearchKey
		MapData md = new MapData();
		md.setNo(rptNo);
		if (md.RetrieveFromDBSources() == 0)
		{
			//如果没有找到，就让其重置一下.
			RptDfine rd = new RptDfine(this.getFK_Flow());

			if (this.getSearchType().equals("My"))
			{
				rd.DoReset(this.getSearchType(), "我发起的流程");
			}

			if (this.getSearchType().equals("MyJoin"))
			{
				rd.DoReset(this.getSearchType(), "我参与的流程");
			}

			if (this.getSearchType().equals("Adminer"))
			{
				rd.DoReset(this.getSearchType(), "高级查询");
			}

			md.RetrieveFromDBSources();
		}

		MapAttr ar = null;

		String cfgfix = "_SearchAttrs";
		UserRegedit ur = new UserRegedit();
		ur.setAutoMyPK(false);
		ur.setMyPK(WebUser.getNo() + rptNo + cfgfix);

		if (ur.RetrieveFromDBSources() == 0)
		{
			ur.setMyPK(WebUser.getNo() + rptNo + cfgfix);
			ur.setFK_Emp(WebUser.getNo());
			ur.setCfgKey(rptNo + cfgfix);

			ur.Insert();
		}

		vals = ur.GetVals();

		md.SetPara("RptDTSearchWay",md.getRptDTSearchWay().getValue());
		md.SetPara("RptDTSearchKey",md.getRptDTSearchKey());
		md.SetPara("RptIsSearchKey",md.getRptIsSearchKey());

		md.SetPara("T_SearchKey", ur.getSearchKey());

		if (md.getRptDTSearchWay() != DTSearchWay.None)
		{
			ar = new MapAttr(rptNo, md.getRptDTSearchKey());
			md.SetPara("T_DateLabel", ar.getName());

			if (md.getRptDTSearchWay() == DTSearchWay.ByDate)
			{
				md.SetPara("T_DTFrom", ur.GetValStringByKey(UserRegeditAttr.DTFrom));
				md.SetPara("T_DTTo", ur.GetValStringByKey(UserRegeditAttr.DTTo));
			}
			else
			{
				md.SetPara("T_DTFrom", ur.GetValStringByKey(UserRegeditAttr.DTFrom));
				md.SetPara("T_DTTo", ur.GetValStringByKey(UserRegeditAttr.DTTo));
			}
		}


		///#region //增加显示列信息
		DataRow row = null;
		DataTable dt = new DataTable("Sys_MapAttr");
		dt.Columns.Add("No", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Width", Integer.class);

		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);

		for (MapAttr attr : attrs.ToJavaList())
		{
			row = dt.NewRow();
			row.setValue("No", attr.getKeyOfEn());
			row.setValue("Name",attr.getName());
			row.setValue("Width",attr.getUIWidthInt());

			if (attr.getHisAttr().getIsFKorEnum())
			{
				row.setValue("No",attr.getKeyOfEn() + "Text");
			}

			dt.Rows.add(row);
		}

		ds.Tables.add(dt);

		///#endregion


		///#region //增加枚举/外键字段信息
		attrs = new MapAttrs(rptNo);
		dt = new DataTable("FilterCtrls");
		dt.Columns.Add("Id", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Type", String.class);
		dt.Columns.Add("DataType", Integer.class);
		dt.Columns.Add("DefaultValue", String.class);
		dt.Columns.Add("ValueField", String.class);
		dt.Columns.Add("TextField", String.class);
		dt.Columns.Add("ParentField", String.class);
		String[] ctrls = md.getRptSearchKeys().split("[*]", -1);
		DataTable dtNoName = null;

		for (String ctrl : ctrls)
		{
			//增加判断，如果URL中有传参，则不进行此SearchAttr的过滤条件显示
			if (StringUtils.isEmpty(ctrl) || !DotNetToJavaStringHelper.isNullOrEmpty(this.getRequest().getParameter(ctrl)))
			{
				continue;
			}

			Object tempVar = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, ctrl);
			ar = (MapAttr)((tempVar instanceof MapAttr) ? tempVar : null);
			if (ar == null)
			{
				continue;
			}

			row = dt.NewRow();
			row.setValue("Id", ctrl);
			row.setValue("Name", ar.getName());
			row.setValue("DataType", ar.getMyDataType());
			row.setValue("W", ar.getUIWidth());
           

			//判断，如果URL中有传参，则不进行此SearchAttr的过滤条件显示

			switch (ar.getUIContralType())
			{
				case DDL:
					row.setValue("Type", "combo");
					fcid = "DDL_" + ar.getKeyOfEn();

					if (vals.containsKey(fcid))
					{
						if (vals.get(fcid).equals("mvals"))
						{
							AtPara ap = new AtPara(ur.getMVals());
							row.setValue("DefaultValue", ap.GetValStrByKey(ar.getKeyOfEn()));
						}
						else
						{
							row.setValue("DefaultValue", vals.get(fcid));
						}
					}

					switch (ar.getLGType())
					{
						
						case FK:
							Entities ens = ar.getHisAttr().getHisFKEns();
							ens.RetrieveAll();
							EntitiesTree treeEns = (EntitiesTree)((ens instanceof EntitiesTree) ? ens : null);

							if (treeEns != null)
							{
								row.setValue("Type", "combotree");
								dtNoName = ens.ToDataTableField(ar.getKeyOfEn());
								//dtNoName.TableName = ar.getKeyOfEn();
								ds.Tables.add(dtNoName);

								row.setValue("ValueField", "No");
								row.setValue("TextField", "Name");
								row.setValue("ParentField", "ParentNo");
							}
							else
							{
								EntitiesSimpleTree treeSimpEns = (EntitiesSimpleTree)((ens instanceof EntitiesSimpleTree) ? ens : null);

								if (treeSimpEns != null)
								{
									row.setValue("Type", "combotree");
									dtNoName = ens.ToDataTableField();
									dtNoName.TableName = ar.getKeyOfEn();
									ds.Tables.add(dtNoName);

									row.setValue("ValueField", "No");
									row.setValue("TextField", "Name");
									row.setValue("ParentField", "ParentNo");
								}
								else
								{
									dtNoName = GetNoNameDataTable(ar.getKeyOfEn());
									dtNoName.Rows.AddDatas("all", "全部");

									for (Entity en : ens.ToJavaListEn())
									{
										dtNoName.Rows.AddDatas(en.GetValStringByKey(ar.getHisAttr().getUIRefKeyValue()), en.GetValStringByKey(ar.getHisAttr().getUIRefKeyText()));
									}

									ds.Tables.add(dtNoName);

									row.setValue("ValueField", "No");
									row.setValue("TextField", "Name");
								}
							}
							break;
						
						case Enum:
							dtNoName = GetNoNameDataTable(ar.getKeyOfEn());
							dtNoName.Rows.AddDatas("all", "全部");

							SysEnums enums = new SysEnums(ar.getUIBindKey());

							for (SysEnum en : enums.ToJavaList())
							{
								dtNoName.Rows.AddDatas(en.getIntKey(), en.getLab());
							}

							ds.Tables.add(dtNoName);

							row.setValue("ValueField", "No");
							row.setValue("TextField", "Name");
							break;
						default:
							break;
					}
					break;
			
				default:
					break;
			}

			dt.Rows.add(row);
		}

		ds.Tables.add(dt);

		///#endregion


		///#region //增加第一页数据
		GEEntitys ges = new GEEntitys(rptNo);
		QueryObject qo = new QueryObject(ges);

		if (this.getSearchType().equals("My")) //我发起的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FlowStarter, WebUser.getNo());
		}

		else if (this.getSearchType().equals("MyDept")) //我部门发起的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FK_Dept, WebUser.getFK_Dept());
		}

		else if (this.getSearchType().equals("MyJoin")) //我参与的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
		}
		else if (this.getSearchType().equals("Adminer"))
		{
		}
		else
		{
				return "err@" + this.getSearchType() + "标记错误.";
		}

		qo = InitQueryObject(qo, md, ges.getGetNewEntity().getEnMap().getAttrs(), attrs, ur);

		qo.AddWhere(" AND  WFState > 1 ");
		qo.AddWhere(" AND  FID = 0  ");
		
		md.SetPara("T_total", qo.GetCount());
		qo.DoQuery("OID", StringUtils.isEmpty(pageSize) ? SystemConfig.getPageSize() : Integer.parseInt(pageSize), 1);
		ds.Tables.add(ges.ToDataTableField("MainData"));
		ds.Tables.add(md.ToDataTableField("Sys_MapData"));

		///#endregion

		return BP.Tools.Json.ToJson(ds);
	}
	
	private DataTable GetNoNameDataTable(String tableName)
	{
		DataTable dt = new DataTable(tableName);
		dt.Columns.Add("No", String.class);
		dt.Columns.Add("Name", String.class);

		return dt;
	}
	
	public final QueryObject InitQueryObject(QueryObject qo, MapData md, Attrs attrs, MapAttrs rptAttrs, UserRegedit ur) throws Exception
	{
		java.util.HashMap<String, String> kvs = null;
		java.util.ArrayList<String> keys = new java.util.ArrayList<String>();
		String cfg = "_SearchAttrs";
		String searchKey = "";
		String val = null;

		kvs = ur.GetVals();

		if ( ! this.getSearchType().equals("Adminer"))
		{
			qo.addAnd();
		}


		///#region 关键字查询
		if (md.getRptIsSearchKey())
		{
			searchKey = ur.getSearchKey();
		}

		if (StringUtils.isEmpty(searchKey))
		{
			qo.addLeftBracket();
			qo.AddWhere("abc", "all");
			qo.addRightBracket();
		}
		else
		{
			int i = 0;

			for (Attr attr : attrs)
			{
				switch (attr.getMyFieldType())
				{
					case Enum:
					case FK:
					case PKFK:
						continue;
					default:
						break;
				}

				if (attr.getMyDataType() != DataType.AppString)
				{
					continue;
				}

				if (attr.getMyFieldType() == FieldType.RefText)
				{
					continue;
				}

				if (attr.getKey().equals("FK_Dept"))
				{
					continue;
				}

				i++;

				if (i == 1)
				{
					qo.addLeftBracket();
					if (SystemConfig.getAppCenterDBVarStr().equals("@")|| SystemConfig.getAppCenterDBVarStr().equals(":"))
					{
						qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
					}
					else
					{
						qo.AddWhere(attr.getKey(), " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
					}
					continue;
				}

				qo.addOr();

				if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBVarStr().equals(":"))
				{
					qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
				}
				else
				{
					qo.AddWhere(attr.getKey(), " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
				}
			}

			qo.getMyParas().Add("SKey", searchKey);
			qo.addRightBracket();
		}

		///#endregion


		///#region Url传参条件
		for (Attr attr : attrs)
		{
			if (DotNetToJavaStringHelper.isNullOrEmpty(this.getRequest().getParameter(attr.getKey())))
			{
				continue;
			}

			qo.addAnd();
			qo.addLeftBracket();

			val = this.getRequest().getParameter(attr.getKey());

			switch (attr.getMyDataType())
			{
				case DataType.AppBoolean:
					qo.AddWhere(attr.getKey(), toStrInt(val));
					break;
				case DataType.AppDate:
				case DataType.AppDateTime:
				case DataType.AppString:
					qo.AddWhere(attr.getKey(), val);
					break;
				case DataType.AppDouble:
				case DataType.AppFloat:
				case DataType.AppMoney:
					qo.AddWhere(attr.getKey(), Double.parseDouble(val));
					break;
				case DataType.AppInt:
					qo.AddWhere(attr.getKey(), Integer.parseInt(val));
					break;
				default:
					break;
			}

			qo.addRightBracket();

			if (keys.contains(attr.getKey()) == false)
			{
				keys.add(attr.getKey());
			}
		}

		///#endregion


		///#region 过滤条件
		for (MapAttr attr1 : rptAttrs.ToJavaList())
		{
			Attr attr = attr1.getHisAttr();
			//此处做判断，如果在URL中已经传了参数，则不算SearchAttrs中的设置
			if (keys.contains(attr.getKey()))
			{
				continue;
			}

			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			String selectVal = "";
			String cid = "";

			switch (attr.getUIContralType())
			{
				//case UIContralType.TB:
				//    switch (attr.MyDataType)
				//    {
				//        case DataType.AppDate:
				//        case DataType.AppDateTime:
				//            if (attr.MyDataType == DataType.AppDate)
				//                cid = "D_" + attr.Key;
				//            else
				//                cid = "DT_" + attr.Key;

				//            if (kvs.ContainsKey(cid) == false || string.IsNullOrWhiteSpace(kvs[cid]))
				//                continue;

				//            selectVal = kvs[cid];

				//            qo.addAnd();
				//            qo.addLeftBracket();
				//            qo.AddWhere(attr.Key, selectVal);
				//            qo.addRightBracket();
				//            break;
				//        default:
				//            cid = "TB_" + attr.Key;

				//            if (kvs.ContainsKey(cid) == false || string.IsNullOrWhiteSpace(kvs[cid]))
				//                continue;

				//            selectVal = kvs[cid];

				//            qo.addAnd();
				//            qo.addLeftBracket();
				//            qo.AddWhere(attr.Key, " LIKE ", "%" + selectVal + "%");
				//            qo.addRightBracket();
				//            break;
				//    }
				//    break;
				case DDL:
					cid = "DDL_" + attr.getKey();

					if (kvs.containsKey(cid) == false || StringUtils.isEmpty(kvs.get(cid)))
					{
						continue;
					}

					selectVal = kvs.get(cid);

					if (selectVal.equals("all") || selectVal.equals("-1"))
					{
						continue;
					}

					if (selectVal.equals("mvals"))
					{
						// 如果是多选值 
						AtPara ap = new AtPara(ur.getMVals());
						String instr = ap.GetValStrByKey(attr.getKey());

						if (DotNetToJavaStringHelper.isNullOrEmpty(instr))
						{
							if (attr.getKey().equals("FK_Dept") || attr.getKey().equals("FK_Unit"))
							{
								if (attr.getKey().equals("FK_Dept"))
								{
									selectVal = WebUser.getFK_Dept();
								}
							}
							else
							{
								continue;
							}
						}
						else
						{
							instr = instr.replace("..", ".");
							instr = instr.replace(".", "','");
							instr = instr.substring(2);
							instr = instr.substring(0, instr.length() - 2);

							qo.addAnd();
							qo.addLeftBracket();
							qo.AddWhereIn(attr.getKey(), "(" + instr + ")");
							qo.addRightBracket();
							continue;
						}
					}

					qo.addAnd();
					qo.addLeftBracket();

					if (attr.getUIBindKey().equals("BP.Port.Depts") || attr.getUIBindKey().equals("BP.Port.Units")) //判断特殊情况。
					{
						qo.AddWhere(attr.getKey(), " LIKE ", selectVal + "%");
					}
					else
					{
						qo.AddWhere(attr.getKey(), selectVal);
					}

					qo.addRightBracket();
					break;
				//case UIContralType.CheckBok:
				//    cid = "CB_" + attr.Key;

				//    if (kvs.ContainsKey(cid) == false || string.IsNullOrWhiteSpace(kvs[cid]))
				//        continue;

				//    selectVal = kvs[cid];

				//    qo.addAnd();
				//    qo.addLeftBracket();
				//    qo.AddWhere(attr.Key, int.Parse(selectVal));
				//    qo.addRightBracket();
				//    break;
				default:
					break;
			}
		}

		///#endregion


		///#region 日期处理
		if (md.getRptDTSearchWay() != DTSearchWay.None)
		{
			String dtKey = md.getRptDTSearchKey();
			String dtFrom = ur.GetValStringByKey(UserRegeditAttr.DTFrom).trim();
			String dtTo = ur.GetValStringByKey(UserRegeditAttr.DTTo).trim();

			if (DotNetToJavaStringHelper.isNullOrEmpty(dtFrom) == true)
			{
				if (md.getRptDTSearchWay() == DTSearchWay.ByDate)
				{
					dtFrom = "1900-01-01";
				}
				else
				{
					dtFrom = "1900-01-01 00:00";
				}
			}

			if (DotNetToJavaStringHelper.isNullOrEmpty(dtTo) == true)
			{
				if (md.getRptDTSearchWay() == DTSearchWay.ByDate)
				{
					dtTo = "2999-01-01";
				}
				else
				{
					dtTo = "2999-12-31 23:59";
				}
			}

			if (md.getRptDTSearchWay() == DTSearchWay.ByDate)
			{
				dtFrom += " 00:00:00";
                dtTo += " 23:59:59";
				qo.addAnd();
				qo.addLeftBracket();
				qo.setSQL(dtKey + " >= '" + dtFrom + "'");
				qo.addAnd();
				qo.setSQL(dtKey + " <= '" + dtTo + "'");
				qo.addRightBracket();
			}

			if (md.getRptDTSearchWay() == DTSearchWay.ByDateTime)
			{
				qo.addAnd();
				qo.addLeftBracket();
				qo.setSQL(dtKey + " >= '" + dtFrom + " 00:00'");
				qo.addAnd();
				qo.setSQL(dtKey + " <= '" + dtTo + " 23:59'");
				qo.addRightBracket();
			}
		}

		///#endregion

		return qo;
	}
	
	public boolean toStrInt(String str){
		try {
			int i = Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public final String FlowSearch_Done() throws Exception
	{
		String vals = this.GetRequestVal("vals");
		String searchKey = GetRequestVal("key");
		String dtFrom = GetRequestVal("dtFrom");
		String dtTo = GetRequestVal("dtTo");
		String mvals = GetRequestVal("mvals");
		String pageSize = GetRequestVal("pageSize");
		int pageIdx = Integer.parseInt(GetRequestVal("pageIdx"));

		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();
		UserRegedit ur = new UserRegedit();
		ur.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs");
		ur.RetrieveFromDBSources();

		ur.setSearchKey(searchKey);
		ur.setDTFrom_Data(dtFrom);
		ur.setDTTo_Data(dtTo);
		ur.setVals(vals);
		ur.setMVals(mvals);
		ur.Update();

		DataSet ds = new DataSet();
		MapData md = new MapData(rptNo);
		MapAttrs attrs = new MapAttrs(rptNo);
		GEEntitys ges = new GEEntitys(rptNo);
		QueryObject qo = new QueryObject(ges);

		if (this.getSearchType().equals("My")) //我发起的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FlowStarter, WebUser.getNo());
		}
		else if (this.getSearchType().equals("MyDept")) //我部门发起的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FK_Dept, WebUser.getFK_Dept());
		}
		else if (this.getSearchType().equals("MyJoin")) //我参与的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
		}
		else if (this.getSearchType().equals("Adminer"))
		{
		}
		else
		{
				return "err@" + this.getSearchType() + "标记错误.";
		}


		qo = InitQueryObject(qo, md, ges.getGetNewEntity().getEnMap().getAttrs(), attrs, ur);
		qo.AddWhere(" AND  WFState > 1 "); //排除空白，草稿数据.


		md.SetPara("T_total", qo.GetCount());
		qo.DoQuery("OID", StringUtils.isEmpty(pageSize) ? SystemConfig.getPageSize() : Integer.parseInt(pageSize), pageIdx);
		ds.Tables.add(ges.ToDataTableField("MainData"));
		ds.Tables.add(md.ToDataTableField("Sys_MapData"));

		return BP.Tools.Json.ToJson(ds);
	}
	
	/**
	 * 流程查询导出
	 * @return
	 * @throws Exception
	 */
	public final String FlowSearch_Exp() throws Exception
	{
		String vals = this.GetRequestVal("vals");
		String searchKey = GetRequestVal("key");
		String dtFrom = GetRequestVal("dtFrom");
		String dtTo = GetRequestVal("dtTo");
		String mvals = GetRequestVal("mvals");

		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();
		UserRegedit ur = new UserRegedit();
		ur.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs");
		ur.RetrieveFromDBSources();

		ur.setSearchKey(searchKey);
		ur.setDTFrom_Data(dtFrom);
		ur.setDTTo_Data(dtTo);
		ur.setVals(vals);
		ur.setMVals(mvals);
		ur.Update();

		MapData md = new MapData(rptNo);
		MapAttrs attrs = new MapAttrs(rptNo);
		GEEntitys ges = new GEEntitys(rptNo);
		QueryObject qo = new QueryObject(ges);
		
		String title="数据导出";
		if (this.getSearchType().equals("My")) //我发起的.
		{
				title = "我发起的流程";
				qo.AddWhere(BP.WF.Data.GERptAttr.FlowStarter, WebUser.getNo());
		}
		else if (this.getSearchType().equals("MyDept")) //我部门发起的.
		{
				title = "我部门发起的流程";
				qo.AddWhere(BP.WF.Data.GERptAttr.FK_Dept, WebUser.getFK_Dept());
		}
		else if (this.getSearchType().equals("MyJoin")) //我参与的.
		{
				title = "我参与的流程";
				qo.AddWhere(BP.WF.Data.GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
		}
		else if (this.getSearchType().equals("Adminer"))
		{
		}
		else
		{
				return "err@" + this.getSearchType() + "标记错误.";
		}
		
		Entity entity = ges.getGetNewEntity();
		Attrs enAttrs = entity.getEnMap().getAttrs();
		qo = InitQueryObject(qo, md,enAttrs , attrs, ur);
		qo.AddWhere(" AND  WFState > 1 "); //排除空白，草稿数据.

		String filePath = ExportDGToExcel(qo.DoQueryToTable(), entity, title, enAttrs);
		return filePath;
	}
	
	 /** 
	 流程分組分析 1.获取查询条件 2.获取分组的枚举或者外键值 3.获取分析的信息列表进行求和、求平均
	 
	 @return 
	 * @throws Exception 
*/
	public final String FlowGroup_Init() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getFK_Flow()))
		{
			return "err@参数FK_Flow不能为空";
		}

		String fcid = "";
		DataSet ds = new DataSet();
		java.util.HashMap<String, String> vals = null;
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getGroupType();

		//报表信息，包含是否显示关键字查询RptIsSearchKey，过滤条件枚举/下拉字段RptSearchKeys，时间段查询方式RptDTSearchWay，时间字段RptDTSearchKey
		MapData md = new MapData();
		md.setNo(rptNo);
		if (md.RetrieveFromDBSources() == 0)
		{
			//如果没有找到，就让其重置一下.
			RptDfine rd = new RptDfine(this.getFK_Flow());

			if (this.getGroupType().equals("My"))
			{
				rd.DoReset(this.getGroupType(), "我发起的流程");
			}

			if (this.getGroupType().equals("MyJoin"))
			{
				rd.DoReset(this.getGroupType(), "我审批的流程");
			}

			if (this.getGroupType().equals("MyDept"))
			{
				rd.DoReset(this.getGroupType(), "本部门发起的流程");
			}

			if (this.getGroupType().equals("Adminer"))
			{
				rd.DoReset(this.getGroupType(), "高级查询");
			}

			md.RetrieveFromDBSources();
		}

		MapAttr ar = null;

		//查询条件的信息表
		String cfgfix = "_SearchAttrs";
		UserRegedit ur = new UserRegedit();
		ur.setAutoMyPK(false);
		ur.setMyPK(WebUser.getNo() + rptNo + cfgfix);

		if (ur.RetrieveFromDBSources() == 0)
		{
			ur.setMyPK(WebUser.getNo() + rptNo + cfgfix);
			ur.setFK_Emp(WebUser.getNo());
			ur.setCfgKey(rptNo + cfgfix);

			ur.Insert();
		}

		//分组条件存储的信息表
		cfgfix = "_GroupAttrs";
		UserRegedit groupUr = new UserRegedit();
		groupUr.setAutoMyPK(false);
		groupUr.setMyPK(WebUser.getNo() + rptNo + cfgfix);

		if (groupUr.RetrieveFromDBSources() == 0)
		{
			groupUr.setMyPK(WebUser.getNo() + rptNo + cfgfix);
			groupUr.setFK_Emp(WebUser.getNo());
			groupUr.setCfgKey(rptNo + cfgfix);

			groupUr.Insert();
		}



		vals = ur.GetVals();
		md.SetPara("RptDTSearchWay",md.getRptDTSearchWay().getValue());
		md.SetPara("RptDTSearchKey",md.getRptDTSearchKey());
		md.SetPara("RptIsSearchKey",md.getRptIsSearchKey());
		md.SetPara("T_SearchKey", ur.getSearchKey());

		if (md.getRptDTSearchWay() != DTSearchWay.None)
		{
			ar = new MapAttr(rptNo, md.RptDTSearchKey);
			md.SetPara("T_DateLabel", ar.getName());

			if (md.getRptDTSearchWay() == DTSearchWay.ByDate)
			{
				md.SetPara("T_DTFrom", ur.GetValStringByKey(UserRegeditAttr.DTFrom));
				md.SetPara("T_DTTo", ur.GetValStringByKey(UserRegeditAttr.DTTo));
			}
			else
			{
				md.SetPara("T_DTFrom", ur.GetValStringByKey(UserRegeditAttr.DTFrom));
				md.SetPara("T_DTTo", ur.GetValStringByKey(UserRegeditAttr.DTTo));
			}
		}
		///#region //显示的内容
		DataRow row = null;
		DataTable dt = new DataTable("Group_MapAttr");
		dt.Columns.Add("Field", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Checked", String.class);


		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);

		for (MapAttr attr : attrs.ToJavaList())
		{
			if (attr.getUIContralType() == UIContralType.DDL)
			{
				DataRow dr = dt.NewRow();
				dr.setValue("Field", attr.getKeyOfEn());
				dr.setValue("Name",attr.getHisAttr().getDesc());

				// 根据状态 设置信息.
				if (groupUr.getVals().indexOf(attr.getKeyOfEn()) != -1)
				{
					dr.setValue("Checked","true");
				}

				if (groupUr.getVals().indexOf(attr.getKeyOfEn()) != -1)
				{
					dr.setValue("Checked","true");
				}

				dt.Rows.add(dr);
			}
		}
		ds.Tables.add(dt);
		///#endregion

		///#region //分析的内容
		dt = new DataTable("Analysis_MapAttr");
		dt.Columns.Add("Field", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Checked", String.class);

		//如果不存在分析项手动添加一个分析项
		DataRow dtr = dt.NewRow();
		dtr.setValue("Field", "Group_Number");
		dtr.setValue("Name","数量");
		dtr.setValue("Checked","true");
		dt.Rows.add(dtr);

		DataTable ddlDt = new DataTable();
		ddlDt.TableName = "Group_Number";
		ddlDt.Columns.Add("No");
		ddlDt.Columns.Add("Name");
		ddlDt.Columns.Add("Selected");
		DataRow ddlDr = ddlDt.NewRow();
		ddlDr.setValue("No", "SUM");
		ddlDr.setValue("Name","求和");
		ddlDr.setValue("Selected","true");
		ddlDt.Rows.add(ddlDr);
		ds.Tables.add(ddlDt);


		for (MapAttr attr : attrs.ToJavaList())
		{
			if (attr.getIsPK() || attr.getIsNum() == false)
			{
				continue;
			}
			if (attr.getUIContralType() == UIContralType.TB == false)
			{
				continue;
			}
			if (attr.getUIVisible() == false)
			{
				continue;
			}
			if (attr.getHisAttr().getMyFieldType() == FieldType.FK)
			{
				continue;
			}
			if (attr.getHisAttr().getMyFieldType() == FieldType.Enum)
			{
				continue;
			}
			if (attr.getKeyOfEn().equals("OID") || attr.getKeyOfEn().equals("WorkID") || attr.getKeyOfEn().equals("MID"))
			{
				continue;
			}

			dtr = dt.NewRow();
			dtr.setValue("Field", attr.getKeyOfEn());
			dtr.setValue("Name", attr.getHisAttr().getDesc());


			// 根据状态 设置信息.
			if (groupUr.getVals().indexOf(attr.getKeyOfEn()) != -1)
			{
				dtr.setValue("Checked", "true");
			}
			dt.Rows.add(dtr);

			ddlDt = new DataTable();
			ddlDt.Columns.Add("No");
			ddlDt.Columns.Add("Name");
			ddlDt.Columns.Add("Selected");
			ddlDt.TableName = attr.getKeyOfEn();

			ddlDr = ddlDt.NewRow();
			ddlDr.setValue("No", "SUM");
			ddlDr.setValue("Name", "求和");
			if (groupUr.getVals().indexOf("@" + attr.getKeyOfEn() + "=SUM") != -1)
			{
				ddlDr.setValue("Selected","true");
			}
			ddlDt.Rows.add(ddlDr);

			ddlDr = ddlDt.NewRow();
			ddlDr.setValue("No","AVG");
			ddlDr.setValue("Name","求平均");
			if (groupUr.getVals().indexOf("@" + attr.getKeyOfEn() + "=AVG") != -1)
			{
				ddlDr.setValue("Selected","true");
			}
			ddlDt.Rows.add(ddlDr);
			boolean IsContainsNDYF = false;
			if (IsContainsNDYF)
			{
				ddlDr = ddlDt.NewRow();
				ddlDr.setValue("No","AMOUNT");
				ddlDr.setValue("Name","求累计");
				if (groupUr.getVals().indexOf("@" + attr.getKeyOfEn() + "=AMOUNT") != -1)
				{
					ddlDr.setValue("Selected","true");
				}
				ddlDt.Rows.add(ddlDr);
			}

			ds.Tables.add(ddlDt);

		}
		ds.Tables.add(dt);
		///#endregion

		///#region //增加枚举/外键字段信息
		attrs = new MapAttrs(rptNo);
		dt = new DataTable("FilterCtrls");
		dt.Columns.Add("Field", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Type", String.class);
		dt.Columns.Add("DataType", Integer.class);
		dt.Columns.Add("DefaultValue", String.class);
		dt.Columns.Add("ValueField", String.class);
		dt.Columns.Add("TextField", String.class);
		dt.Columns.Add("ParentField", String.class);
		dt.Columns.Add("W", String.class);
		String[] ctrls = md.RptSearchKeys.split("[*]", -1);
		DataTable dtNoName = null;

		for (String ctrl : ctrls)
		{
			//增加判断，如果URL中有传参，则不进行此SearchAttr的过滤条件显示
			if (DataType.IsNullOrEmpty(ctrl) || !DataType.IsNullOrEmpty(this.GetRequestVal(ctrl)))
			{
				continue;
			}

			Object tempVar = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, ctrl);
			ar = (MapAttr)((tempVar instanceof MapAttr) ? tempVar : null);
			if (ar == null)
			{
				continue;
			}

			row = dt.NewRow();
			row.setValue("Field", ctrl);
			row.setValue("Name",ar.getName());
			row.setValue("DataType",ar.getMyDataType());
			row.setValue("W",ar.getUIWidth()); //宽度.

			switch (ar.getUIContralType())
			{
				case DDL:
					row.setValue("Type","combo");
					fcid = "DDL_" + ar.getKeyOfEn();
					if (vals.containsKey(fcid))
					{
						if (vals.get(fcid).equals("mvals"))
						{
							AtPara ap = new AtPara(ur.getMVals());
							row.setValue("DefaultValue",ap.GetValStrByKey(ar.getKeyOfEn()));
						}
						else
						{
							row.setValue("DefaultValue",vals.get(fcid));
						}
					}

					switch (ar.getLGType())
					{

						case FK:
							Entities ens = ar.getHisAttr().getHisFKEns();
							ens.RetrieveAll();
							EntitiesTree treeEns = (EntitiesTree)((ens instanceof EntitiesTree) ? ens : null);

							if (treeEns != null)
							{
								row.setValue("Type","combotree");
								dtNoName = ens.ToDataTableField();
								dtNoName.TableName = ar.getKeyOfEn();
								ds.Tables.add(dtNoName);

								row.setValue("ValueField","No");
								row.setValue("TextField","Name");
								row.setValue("ParentField","ParentNo");
							}
							else
							{
								EntitiesTree treeSimpEns = (EntitiesTree)((ens instanceof EntitiesTree) ? ens : null);

								if (treeSimpEns != null)
								{
									row.setValue("Type","combotree");
									dtNoName = ens.ToDataTableField();
									dtNoName.TableName = ar.getKeyOfEn();
									ds.Tables.add(dtNoName);

									row.setValue("ValueField","No");
									row.setValue("TextField","Name");
									row.setValue("ParentField","ParentNo");
								}
								else
								{
									dtNoName = GetNoNameDataTable(ar.getKeyOfEn());
									DataRow dr = dtNoName.NewRow();
									dr.setValue("No", "all");
									dr.setValue("Name", "全部");
									dtNoName.Rows.add(dr);

									for (Entity en : ens)
									{
										dr = dtNoName.NewRow();
										dr.setValue("No", en.GetValStringByKey(ar.getHisAttr().getUIRefKeyValue()));
										dr.setValue("Name", en.GetValStringByKey(ar.getHisAttr().getUIRefKeyText()));
										
										dtNoName.Rows.add(dr);
									}

									ds.Tables.add(dtNoName);

									row.setValue("ValueField","No");
									row.setValue("TextField","Name");
								}
							}
							break;

						case Enum:
							dtNoName = GetNoNameDataTable(ar.getKeyOfEn());
							DataRow dr = dtNoName.NewRow();
							dr.setValue("No", "all");
							dr.setValue("Name", "全部");
							dtNoName.Rows.add(dr);

							SysEnums enums = new SysEnums(ar.getUIBindKey());

							for (SysEnum en : enums.ToJavaList())
							{
								dr = dtNoName.NewRow();
								dr.setValue("No", en.getIntKey());
								dr.setValue("Name", en.getLab());
								dtNoName.Rows.add(dr);
								dtNoName.Rows.add(dr);
							}

							ds.Tables.add(dtNoName);

							row.setValue("ValueField","No");
							row.setValue("TextField","Name");
							break;
						default:
							break;
					}
					break;

				default:
					break;
			}

			dt.Rows.add(row);
		}

		ds.Tables.add(dt);
		ds.Tables.add(md.ToDataTableField("Sys_MapData"));
		///#endregion

		return BP.Tools.Json.ToJson(ds);
	}

	public final String FlowGropu_Done() throws Exception
	{

		if (!this.getGroupType().equals("My") && this.getGroupType().equals("MyJoin") && this.getGroupType().equals("MyDept") && this.getGroupType().equals("Adminer"))
		{
			 return "info@<img src='../Img/Pub/warning.gif' /><b><font color=red>"+ this.getGroupType() + "标记错误.</font></b>";
		}
		DataSet ds = new DataSet();
		ds = FlowGroupDoneSet();
		if (ds == null)
		{
			return "info@<img src='../Img/Pub/warning.gif' /><b><font color=red> 您没有选择显示的内容</font></b>";
		}

		return BP.Tools.Json.ToJson(ds);
	}
	/** 
	 
	 
	 
	 @return 
	 * @throws Exception 
	*/
	public final DataSet FlowGroupDoneSet() throws Exception
	{
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getGroupType();
		DataSet ds = new DataSet();
		MapData md = new MapData(rptNo);
		MapAttrs attrs = new MapAttrs(rptNo);
		GEEntitys ges = new GEEntitys(rptNo);


		UserRegedit groupUr = new UserRegedit(WebUser.getNo(),rptNo+"_GroupAttrs");
		//分组的参数
		String groupVals = groupUr.getVals();
		//查询条件
		//分组
		String groupKey = "";
		Attrs AttrsOfNum = new Attrs(); //列
		String Condition = ""; //处理特殊字段的条件问题。

		//根据注册表信息获取里面的分组信息
		String StateNumKey = groupVals.substring(groupVals.indexOf("@StateNumKey") + 1);
		String[] statNumKeys = StateNumKey.split("[@]", -1);
		for (String ct : statNumKeys)
		{
			if (ct.split("[=]", -1).length != 2)
			{
				continue;
			}
			String[] paras = ct.split("[=]", -1);

			//判断paras[0]的类型
			int dataType = 2;
			if (paras[0].equals("Group_Number"))
			{
				AttrsOfNum.Add(new Attr("Group_Number", "Group_Number", 1, DataType.AppInt, false, "数量"));
			}
			else
			{
				Attr attr = GetAttrByKey(attrs, paras[0]);
				AttrsOfNum.Add(attr);
				dataType = attr.getMyDataType();
			}

			if (paras[0].equals("Group_Number"))
			{
				groupKey += " count(*) \"" + paras[0] + "\",";
			}
			else
			{
				if (paras[1].equals("SUM"))
				{
						if (dataType == 2)
						{
							groupKey += " SUM(" + paras[0] + ") \"" + paras[0] + "\",";
						}
						else
						{
							groupKey += " round ( SUM(" + paras[0] + "), 4) \"" + paras[0] + "\",";
						}
				}
				else if (paras[1].equals("AVG"))
				{
						groupKey += " round (AVG(" + paras[0] + "), 4)  \"" + paras[0] + "\",";
				}
				else if (paras[1].equals("AMOUNT"))
				{
						if (dataType == 2)
						{
							groupKey += " SUM(" + paras[0] + ") \"" + paras[0] + "\",";
						}
						else
						{
							groupKey += " round ( SUM(" + paras[0] + "), 4) \"" + paras[0] + "\",";
						}
				}
				else
				{
						throw new RuntimeException("没有判断的情况.");
				}

			}

		}
		boolean isHaveLJ = false; // 是否有累计字段。
		if (StateNumKey.indexOf("AMOUNT@") != -1)
		{
			isHaveLJ = true;
		}

		if (groupKey.equals(""))
		{
			return null;
		}

		// 如果包含累计数据，那它一定需要一个月份字段。业务逻辑错误。
		groupKey = groupKey.substring(0, groupKey.length() - 1);
		Paras ps = new Paras();
		// 生成 sql.
		String selectSQL = "SELECT ";
		String groupBy = " GROUP BY ";
		Attrs AttrsOfGroup = new Attrs();

		String SelectedGroupKey = groupVals.substring(0, groupVals.indexOf("@StateNumKey")); // 为保存操作状态的需要。
		if (!DataType.IsNullOrEmpty(SelectedGroupKey))
		{
			boolean isSelected = false;
			String[] SelectedGroupKeys = SelectedGroupKey.split("[@]", -1);
			for (String key : SelectedGroupKeys)
			{
				if (key.contains("=") == true)
				{
					continue;
				}
				selectSQL += key + " \"" + key + "\",";
				groupBy += key + ",";
				// 加入组里面。
				AttrsOfGroup.Add(GetAttrByKey(attrs, key), false, false);

			}
		}

		String groupList = this.GetRequestVal("GroupList");
		if (!DataType.IsNullOrEmpty(SelectedGroupKey))
		{
			// 如果是年月 分组， 并且如果内部有 累计属性，就强制选择。
			if (groupList.indexOf("FK_NY") != -1 && isHaveLJ)
			{
				selectSQL += "FK_NY,";
				groupBy += "FK_NY,";
				SelectedGroupKey += "@FK_NY";
				// 加入组里面。
				AttrsOfGroup.Add(GetAttrByKey(attrs, "FK_NY"), false, false);
			}
		}

		groupBy = groupBy.substring(0, groupBy.length() - 1);

		if (groupBy.equals(" GROUP BY"))
		{
			return null;
		}



		String orderByReq = this.GetRequestVal("OrderBy");

		String orderby = "";

		if (orderByReq != null && (selectSQL.contains(orderByReq) || groupKey.contains(orderByReq)))
		{
			orderby = " ORDER BY " + orderByReq;
			String orderWay = this.GetRequestVal("OrderWay");
			if (!DataType.IsNullOrEmpty(orderWay) && !orderWay.equals("Up"))
			{
				orderby += " DESC ";
			}
		}

		//查询语句
		QueryObject qo = new QueryObject(ges);

		if (this.getGroupType().equals("My")) //我发起的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FlowStarter, WebUser.getNo());
		}
		else if (this.getGroupType().equals("MyDept")) //我部门发起的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FK_Dept, WebUser.getFK_Dept());
		}
		else if (this.getGroupType().equals("MyJoin")) //我参与的.
		{
				qo.AddWhere(BP.WF.Data.GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
		}
		else if (this.getGroupType().equals("Adminer"))
		{
		}
		else
		{
				return null;
		}

		//查询注册信息表
		UserRegedit ur = new UserRegedit();
		ur.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs");
		ur.RetrieveFromDBSources();
		qo = InitQueryObject(qo, md, ges.getGetNewEntity().getEnMap().getAttrs(), attrs, ur);
		qo.AddWhere(" AND  WFState > 1 "); //排除空白，草稿数据.

		DataTable dt2 = qo.DoGroupQueryToTable(selectSQL + groupKey, groupBy, orderby);

		 DataTable dt1 = dt2.clone();

		 dt1.Columns.Add("IDX", Integer.class);

		 ///#region 对他进行分页面

		 int myIdx = 0;
		 for (DataRow dr : dt2.Rows)
		 {
			 myIdx++;
			 DataRow mydr = dt1.NewRow();
			 mydr.setValue("IDX", myIdx);
			 for (DataColumn dc : dt2.Columns)
			 {
				 mydr.setValue(dc.ColumnName,dr.getValue(dc.ColumnName));
			 }
			 dt1.Rows.add(mydr);
		 }
		 ///#region 处理 Int 类型的分组列。
		 DataTable dt = dt1.clone();
		 dt.TableName = "GroupSearch";
		 dt.Rows.clear();
		 for (Attr attr : AttrsOfGroup)
		 {
			 dt.Columns.get(attr.getKey()).DataType = String.class;
		 }
		 for (DataRow dr : dt1.Rows)
        {
            dt.Rows.add(dr);
        }
		 // 处理这个物理表 , 如果有累计字段, 就扩展它的列。
		 if (isHaveLJ)
		 {
			 // 首先扩充列.
			 for (Attr attr : AttrsOfNum)
			 {
				 if (StateNumKey.indexOf(attr.getKey() + "=AMOUNT") == -1)
				 {
					 continue;
				 }

				 switch (attr.getMyDataType())
				 {
					 case DataType.AppInt:
						 dt.Columns.Add(attr.getKey() + "Amount", Integer.class);
						 break;
					 default:
						 dt.Columns.Add(attr.getKey() + "Amount", BigDecimal.class);
						 break;
				 }
			 }

			 String sql = "";
			 String whereOFLJ = "";
			 AtPara ap = new AtPara(ur.getVals());
			 /** #region 获得查询数据.
			 */
			 for (String str : ap.getHisHT().keySet())
			 {
				 Object val = ap.GetValStrByKey(str);
				 if (val.equals("all"))
				 {
					 continue;
				 }
				 if (!str.equals("FK_NY"))
				 {
					 whereOFLJ += " " + str + " =" + SystemConfig.getAppCenterDBVarStr() + str + "   AND ";
				 }

			 }

			 // 添加累计汇总数据.
			 for (DataRow dr : dt.Rows)
			 {
				 for (Attr attr : AttrsOfNum)
				 {
					 if (StateNumKey.indexOf(attr.getKey() + "=AMOUNT") == -1)
					 {
						 continue;
					 }

					 //形成查询sql.
					 if (whereOFLJ.length() > 0)
					 {
						 sql = "SELECT SUM(" + attr.getKey() + ") FROM " + ges.getGetNewEntity().getEnMap().getPhysicsTable() + whereOFLJ + " AND ";
					 }
					 else
					 {
						 sql = "SELECT SUM(" + attr.getKey() + ") FROM " + ges.getGetNewEntity().getEnMap().getPhysicsTable() + " WHERE ";
					 }

					 for (Attr attr1 : AttrsOfGroup)
					 {
						 if (attr1.getKey().equals("FK_NY"))
						 {
								 sql += " FK_NY <= '" + dr.get("FK_NY") + "' AND FK_ND='" + dr.get("FK_NY").toString().substring(0, 4) + "' AND ";
						 }
						 else if (attr1.getKey().equals("FK_Dept"))
						 {
								 sql += attr1.getKey() + "='" + dr.get(attr1.getKey()) + "' AND ";
						 }
						 else if (attr1.getKey().equals("FK_SJ") || attr1.getKey().equals("FK_XJ"))
						 {
								 sql += attr1.getKey() + " LIKE '" + dr.get(attr1.getKey()) + "%' AND ";
						 }
						 else
						 {
								 sql += attr1.getKey() + "='" + dr.get(attr1.getKey()) + "' AND ";
						 }
					 }

					 sql = sql.substring(0, sql.length() - (new String("AND ")).length());
					 if (attr.getMyDataType() == DataType.AppInt)
					 {
						 dr.setValue(attr.getKey() + "Amount", DBAccess.RunSQLReturnValInt(sql, 0)) ;
					 }
					 else
					 {
						 dr.setValue(attr.getKey() + "Amount",DBAccess.RunSQLReturnValDecimal(sql, BigDecimal.valueOf(0), 2));
					 }
				 }
			 }
		 }

		 // 为表扩充外键
		 for (Attr attr : AttrsOfGroup)
		 {
			 dt.Columns.Add(attr.getKey() + "T", String.class);
		 }
		 for (Attr attr : AttrsOfGroup)
		 {
			 if (attr.getUIBindKey().indexOf(".") == -1)
			 {
				 // 说明它是枚举类型 
				 SysEnums ses = new SysEnums(attr.getUIBindKey());
				 for (DataRow dr : dt.Rows)
				 {
					 int val = 0;
					 try
					 {
						 val = Integer.parseInt(dr.get(attr.getKey()).toString());
					 }
					 catch (Exception e)
					 {
						 dr.setValue(attr.getKey() + "T"," ");
						 continue;
					 }

					 for (SysEnum se : ses.ToJavaList())
					 {
						 if (se.getIntKey() == val)
						 {
							 dr.setValue(attr.getKey() + "T",se.getLab());
						 }
					 }
				 }
				 continue;
			 }
			 for (DataRow dr : dt.Rows)
			 {
				 Entity myen = attr.getHisFKEn();
				 String val = dr.get(attr.getKey()).toString();
				 myen.SetValByKey(attr.getUIRefKeyValue(), val);
				 try
				 {
					 myen.Retrieve();
					 dr.setValue(attr.getKey() + "T", myen.GetValStrByKey(attr.getUIRefKeyText())) ;
				 }
				 catch (Exception e2)
				 {
					 if (val == null || val.length() <= 1)
					 {
						 dr.setValue(attr.getKey() + "T", val);
					 }
					 else if (val.substring(0, 2).equals("63"))
					 {
						 try
						 {
							 BP.Port.Dept Dept = new BP.Port.Dept(val);
							 dr.setValue(attr.getKey() + "T", Dept.getName());
						 }
						 catch (Exception e3)
						 {
							 dr.setValue(attr.getKey() + "T",val);
						 }
					 }
					 else
					 {
						 dr.setValue(attr.getKey() + "T",val);
					 }
				 }
			 }
		 }
		 ds.Tables.add(dt);
		 ds.Tables.add(AttrsOfNum.ToMapAttrs().ToDataTableField("AttrsOfNum"));
		 ds.Tables.add(AttrsOfGroup.ToMapAttrs().ToDataTableField("AttrsOfGroup"));


		return ds;
	}

	/** 
	 执行导出
	 
	 @return 
	 * @throws Exception 
	*/
	public final String FlowGroup_Exp() throws Exception
	{
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getGroupType();
		String desc = "";

		if (this.getGroupType().equals("My"))
		{
			desc="我发起的流程";
		}
		else if (this.getGroupType().equals("MyJoin"))
		{
			 desc="我审批的流程";
		}
		else if(this.getGroupType().equals("MyDept"))
		{
			 desc="本部门发起的流程";
		}
		else if(this.getGroupType().equals("Adminer"))
		{
			 desc="高级查询";
		}
		else
		{
			return "info@<img src='../Img/Pub/warning.gif' /><b><font color=red>" + this.getGroupType() + "标记错误.</font></b>";
		}

		DataSet ds = new DataSet();
		ds = FlowGroupDoneSet();
		if (ds == null)
		{
			return "info@<img src='../Img/Pub/warning.gif' /><b><font color=red> 您没有选择显示的内容</font></b>";
		}

		//获取注册信息表
		UserRegedit ur = new UserRegedit(WebUser.getNo(), rptNo + "_GroupAttrs");



		String filePath = ExportDGToExcel(ds,desc, ur.getVals());


		return filePath;
	}

	/** 
	 初始化数据
	 
	 @return 
	 * @throws Exception 
	*/
	public final String FlowContrastDtl_Init() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getFK_Flow()))
		{
			return "err@参数FK_Flow不能为空";
		}

		String pageSize = GetRequestVal("pageSize");
		String fcid = "";
		DataSet ds = new DataSet();
		java.util.HashMap<String, String> vals = null;
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();

		GEEntitys ges = new GEEntitys(rptNo);

		//属性集合.
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);
		DataRow row = null;
		DataTable dt = new DataTable("Sys_MapAttr");
		dt.Columns.Add("No", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Width", Integer.class);
		dt.Columns.Add("UIContralType", Integer.class);
		for (MapAttr attr : attrs.ToJavaList())
		{
			row = dt.NewRow();
			row.setValue("No", attr.getKeyOfEn());
			row.setValue("Name",attr.getName());
			row.setValue("Width",attr.getUIWidthInt());
			row.setValue("UIContralType",attr.getUIContralType().getValue());

			if (attr.getHisAttr().getIsFKorEnum())
			{
				row.setValue("No",attr.getKeyOfEn() + "Text");
			}

			dt.Rows.add(row);
		}

		ds.Tables.add(dt);

		//查询结果
		QueryObject qo = new QueryObject(ges);
		Enumeration enu=getRequest().getParameterNames();  
        boolean isExist = false;
        String newFK_Dept=this.getFK_Dept();
        while(enu.hasMoreElements()){ 
        	isExist = false;
        	String key=(String)enu.nextElement();  
			if (key.equals("FK_Flow")|| key.equals("SearchType"))
			{
				continue;
			}

			if (key.equals("OID") || key.equals("MyPK"))
			{
				continue;
			}

			if (key.equals("FK_Dept"))
			{
				newFK_Dept = getRequest().getParameter(key);
				continue;
			}
			for (MapAttr attr : attrs.ToJavaList())
			{
				if (attr.getKeyOfEn().toUpperCase().equals(key.toUpperCase()))
				{
					isExist = true;
					break;
				}
			}

			if (isExist == false)
			{
				continue;
			}

			if (getRequest().getParameter(key).equals("mvals"))
			{
				//如果用户多项选择了，就要找到它的选择项目.

				UserRegedit sUr = new UserRegedit();
				sUr.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs");
				sUr.RetrieveFromDBSources();

				// 如果是多选值 
				String cfgVal = sUr.getMVals();
				AtPara ap = new AtPara(cfgVal);
				String instr = ap.GetValStrByKey(key);
				String val = "";
				if (instr == null || instr.equals(""))
				{
					if (key.equals("FK_Dept") || key.equals("FK_Unit"))
					{
						if (key.equals("FK_Dept"))
						{
							val = WebUser.getFK_Dept();
						}
					}
					else
					{
						continue;
					}
				}
				else
				{
					instr = instr.replace("..", ".");
					instr = instr.replace(".", "','");
					instr = instr.substring(2);
					instr = instr.substring(0, instr.length() - 2);
					qo.AddWhereIn(key, instr);
				}
			}
			else
			{
				qo.AddWhere(key, getRequest().getParameter(key));
			}
			qo.addAnd();
		}

		if (newFK_Dept != null && (this.GetRequestVal("FK_Emp") == null || this.GetRequestVal("FK_Emp").equals("all")))
		{
			if (this.getFK_Dept().length() == 2)
			{
				qo.AddWhere("FK_Dept", " = ", "all");
				qo.addAnd();
			}
			else
			{
				if (this.getFK_Dept().length() == 8)
				{
					qo.AddWhere("FK_Dept", " = ", newFK_Dept);
				}
				else
				{
					qo.AddWhere("FK_Dept", " like ", newFK_Dept + "%");
				}

				qo.addAnd();
			}
		}

		qo.AddHD();

		dt = qo.DoQueryToTable();
		dt.TableName = "Group_Dtls";
		ds.Tables.add(dt);

		return BP.Tools.Json.ToJson(ds);
	}


	/** 
	 执行导出
	 
	 @return 
	 * @throws Exception 
	*/
	public final String FlowGroupDtl_Exp() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getFK_Flow()))
		{
			return "err@参数FK_Flow不能为空";
		}

		String pageSize = GetRequestVal("pageSize");
		String fcid = "";
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();

		GEEntitys ges = new GEEntitys(rptNo);

		//属性集合.
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);


		//查询结果
		QueryObject qo = new QueryObject(ges);
		Enumeration enu=getRequest().getParameterNames();  
        boolean isExist = false;
        String newFK_Dept=this.getFK_Dept();
        while(enu.hasMoreElements()){ 
        	isExist = false;
        	String key=(String)enu.nextElement();  
			if (key.equals("FK_Flow")|| key.equals("SearchType"))
			{
				continue;
			}

			if (key.equals("OID") || key.equals("MyPK"))
			{
				continue;
			}

			if (key.equals("FK_Dept"))
			{
				newFK_Dept = getRequest().getParameter(key);
				continue;
			}
			for (MapAttr attr : attrs.ToJavaList())
			{
				if (attr.getKeyOfEn().toUpperCase().equals(key.toUpperCase()))
				{
					isExist = true;
					break;
				}
			}

			if (isExist == false)
			{
				continue;
			}

			if (getRequest().getParameter(key).equals("mvals"))
			{
				//如果用户多项选择了，就要找到它的选择项目.

				UserRegedit sUr = new UserRegedit();
				sUr.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs");
				sUr.RetrieveFromDBSources();

				// 如果是多选值 
				String cfgVal = sUr.getMVals();
				AtPara ap = new AtPara(cfgVal);
				String instr = ap.GetValStrByKey(key);
				String val = "";
				if (instr == null || instr.equals(""))
				{
					if (key.equals("FK_Dept") || key.equals("FK_Unit"))
					{
						if (key.equals("FK_Dept"))
						{
							val = WebUser.getFK_Dept();
						}
					}
					else
					{
						continue;
					}
				}
				else
				{
					instr = instr.replace("..", ".");
					instr = instr.replace(".", "','");
					instr = instr.substring(2);
					instr = instr.substring(0, instr.length() - 2);
					qo.AddWhereIn(key, instr);
				}
			}
			else
			{
				qo.AddWhere(key, getRequest().getParameter(key));
			}
			qo.addAnd();
		}

		if (newFK_Dept != null && (this.GetRequestVal("FK_Emp") == null || this.GetRequestVal("FK_Emp").equals("all")))
		{
			if (this.getFK_Dept().length() == 2)
			{
				qo.AddWhere("FK_Dept", " = ", "all");
				qo.addAnd();
			}
			else
			{
				if (this.getFK_Dept().length() == 8)
				{
					qo.AddWhere("FK_Dept", " = ", newFK_Dept);
				}
				else
				{
					qo.AddWhere("FK_Dept", " like ", newFK_Dept + "%");
				}

				qo.addAnd();
			}
		}

		qo.AddHD();

		DataTable dt = qo.DoQueryToTable();
		Attrs newAttrs = new Attrs();
		for (MapAttr attr : attrs.ToJavaList())
		{
			if(attr.getKeyOfEn().toUpperCase().equals("OID"))
			{
				continue;
			}

				newAttrs.Add(attr.getHisAttr());
		}

		String filePath = ExportDGToExcel(dt, ges.getGetNewEntity(), rptNo, newAttrs);


		return filePath;
	}

	/** 
	 通过一个key 得到它的属性值。
	 
	 @param key key
	 @return attr
	*/
	public final Attr GetAttrByKey(MapAttrs mapAttrs, String key)
	{
		for (MapAttr attr : mapAttrs.ToJavaList())
		{
			if (attr.getKeyOfEn().toUpperCase().equals(key.toUpperCase()))
			{
				return attr.getHisAttr();
			}
		}
		return null;

	}
}

