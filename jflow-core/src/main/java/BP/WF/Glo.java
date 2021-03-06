package BP.WF;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import BP.DA.AtPara;
import BP.DA.DBAccess;
import BP.DA.DBType;
import BP.DA.DataColumn;
import BP.DA.DataRow;
import BP.DA.DataSet;
import BP.DA.DataTable;
import BP.DA.DataType;
import BP.DA.Paras;
import BP.DA.TWay;
import BP.Difference.ContextHolderUtils;
import BP.En.Attr;
import BP.En.Attrs;
import BP.En.Entity;
import BP.En.FieldType;
import BP.En.QueryObject;
import BP.En.Row;
import BP.En.UIContralType;
import BP.Port.Emp;
import BP.Sys.AthCtrlWay;
import BP.Sys.AthUploadWay;
import BP.Sys.DefVal;
import BP.Sys.DtlOpenType;
import BP.Sys.FrmAttachment;
import BP.Sys.FrmAttachmentAttr;
import BP.Sys.FrmAttachmentDBAttr;
import BP.Sys.FrmAttachments;
import BP.Sys.FrmEvent;
import BP.Sys.FrmImg;
import BP.Sys.FrmRB;
import BP.Sys.FrmTree;
import BP.Sys.FrmTrees;
import BP.Sys.GEDtl;
import BP.Sys.GEDtlAttr;
import BP.Sys.GEDtls;
import BP.Sys.MapAttr;
import BP.Sys.MapAttrs;
import BP.Sys.MapData;
import BP.Sys.MapDatas;
import BP.Sys.MapDtl;
import BP.Sys.MapDtls;
import BP.Sys.MapExt;
import BP.Sys.MapExtAttr;
import BP.Sys.MapExtXmlList;
import BP.Sys.MapExts;
import BP.Sys.OSModel;
import BP.Sys.SFDBSrc;
import BP.Sys.SFTable;
import BP.Sys.SystemConfig;
import BP.Sys.ToolbarExcel;
import BP.Tools.DateUtils;
import BP.Tools.FtpUtil;
import BP.Tools.SftpUtil;
import BP.Tools.StringHelper;
import BP.WF.Data.CH;
import BP.WF.Data.CHSta;
import BP.WF.Data.GERpt;
import BP.WF.Data.GERptAttr;
import BP.WF.Port.WFEmp;
import BP.WF.Rpt.MapRpts;
import BP.WF.Template.CCList;
import BP.WF.Template.Cond;
import BP.WF.Template.Direction;
import BP.WF.Template.FlowAttr;
import BP.WF.Template.FlowExt;
import BP.WF.Template.FlowSort;
import BP.WF.Template.FlowSorts;
import BP.WF.Template.FrmField;
import BP.WF.Template.FrmFieldAttr;
import BP.WF.Template.FrmFields;
import BP.WF.Template.FrmNode;
import BP.WF.Template.FrmTransferCustom;
import BP.WF.Template.FrmWorkCheck;
import BP.WF.Template.MapDataExt;
import BP.WF.Template.NodeExt;
import BP.WF.Template.NodeToolbar;
import BP.WF.Template.SelectAccper;
import BP.WF.Template.SubFlowYanXu;
import BP.WF.Template.SysForm;
import BP.WF.Template.SysFormTree;
import BP.WF.Template.SysFormTrees;
import BP.Web.WebUser;

/**
 * 全局(方法处理)
 */
public class Glo {

	/**
	 * 获得ftp连接对象
	 * 
	 * @throws Exception
	 */
	public static SftpUtil getSftpUtil() throws Exception {
		// 获取
		String ip = SystemConfig.getFTPServerIP();

		String userNo = SystemConfig.getFTPUserNo();
		String pass = BP.Sys.Glo.String_JieMi_FTP(SystemConfig.getFTPUserPassword());

		SftpUtil ftp = new SftpUtil(ip, 22, userNo, pass);
		return ftp;

		// return Platform.JFlow;
	}

	/**
	 * 获得ftp连接对象
	 * 
	 * @throws Exception
	 */
	public static FtpUtil getFtpUtil() throws Exception {
		// 获取
		String ip = BP.Sys.Glo.String_JieMi_FTP(SystemConfig.getFTPServerIP());

		String userNo = BP.Sys.Glo.String_JieMi_FTP(SystemConfig.getFTPUserNo());
		String pass = BP.Sys.Glo.String_JieMi_FTP(SystemConfig.getFTPUserPassword());

		FtpUtil ftp = new FtpUtil(ip, 21, userNo, pass);
		return ftp;

		// return Platform.JFlow;
	}

	/**
	 * 运行平台.
	 */
	public static Platform getPlatform() {
		return Platform.JFlow;
	}

	/**
	 * 得到WebService对象
	 * 
	 * @return
	 **/

	public static void GetPortalInterfaceSoapClient(String method, String xmlStr) {
		/*
		 * JaxWsDynamicClientFactory dcf =
		 * JaxWsDynamicClientFactory.newInstance(); String wsUrl =
		 * "http://127.0.0.1:8080/jflow-web/service/PortalInterfaceWS?wsdl";
		 * Client client = dcf.createClient(wsUrl); Object[] result = null; try
		 * { result = client.invoke(method, xmlStr);//调用webservice
		 * System.out.println("ws:"+result[0].toString()); } catch (Exception e)
		 * { e.printStackTrace(); }
		 */
		HttpPost httppost = new HttpPost("http://127.0.0.1:8080/jflow-web/service/PortalInterfaceWS?wsdl");
		StringEntity reqEntity = null;
		try {
			reqEntity = new StringEntity(xmlStr, "UTF-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		httppost.setEntity(reqEntity);
		HttpClient client = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httppost);
			InputStream is = response.getEntity().getContent();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = is.read()) != -1) {
				baos.write(i);
			}
			System.out.println("消息的返回值:" + baos.toString("utf-8"));
			// return baos.toString("utf-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return null;
	}

	/**
	 * 短消息写入类型
	 */
	public static ShortMessageWriteTo getShortMessageWriteTo() {
		return ShortMessageWriteTo.forValue(SystemConfig.GetValByKeyInt("ShortMessageWriteTo", 0));
	}

	public static void setCurrFlow(String value) {
		ContextHolderUtils.getRequest().setAttribute("CurrFlow", value);
	}

	/**
	 * 用户编号.
	 */
	public static String UserNo = null;
	/**
	 * 运行平台(用于处理不同的平台，调用不同的URL)
	 */
	public static Plant Plant = BP.WF.Plant.JFlow;
	/**
	 * 当前版本号-为了升级使用.
	 */
	public static int Ver = 20190621;

	/**
	 * 执行升级
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String UpdataCCFlowVer() throws Exception {

		String sql = "SELECT IntVal FROM Sys_Serial WHERE CfgKey='Ver'";
		int currVer = DBAccess.RunSQLReturnValInt(sql, 0);
		
		 //检查子流程表.
        if (DBAccess.IsExitsObject("WF_NodeSubFlow") == true)
        {
            if (DBAccess.IsExitsTableCol("WF_NodeSubFlow", "OID") == true)
            {
                DBAccess.RunSQL("DROP TABLE WF_NodeSubFlow");
                SubFlowYanXu sub = new SubFlowYanXu();
                sub.CheckPhysicsTable();
            }
        }else{
        	 SubFlowYanXu sub = new SubFlowYanXu();
             sub.CheckPhysicsTable();
        }

		// 执行sql文件升级.
		UpdataCCFlowVerSQLScript();

		if (Ver <= currVer)
			return null; // 不需要升级.

		// #region 升级填充事件.
		/*// pop自动填充
		MapExts exts = new MapExts();
		QueryObject qo = new QueryObject(exts);
		qo.AddWhere(MapExtAttr.ExtType, " LIKE ", "Pop%");
		qo.DoQuery();
		for (MapExt ext : exts.ToJavaList()) {
			String mypk = ext.getFK_MapData() + "_" + ext.getAttrOfOper();
			MapAttr ma = new MapAttr();
			ma.setMyPK(mypk);
			if (ma.RetrieveFromDBSources() == 0) {
				ext.Delete();
				continue;
			}

			if (ma.GetParaString("PopModel").equals(ext.getExtType()) == true)
				continue; // 已经修复了，或者配置了.

			ma.SetPara("PopModel", ext.getExtType());
			ma.Update();

			if (DataType.IsNullOrEmpty(ext.getTag4()) == true)
				continue;

			MapExt extP = new MapExt();
			// extP.MyPK = ext.MyPK + "_FullData";
			extP.setMyPK(ext.getMyPK() + "_FullData");
			int i = extP.RetrieveFromDBSources();
			if (i == 1)
				continue;
			extP.setExtType("FullData");
			extP.setFK_MapData(ext.getFK_MapData());
			extP.setAttrOfOper(ext.getAttrOfOper());
			extP.setDBType(ext.getDBType());
			extP.setDoc(ext.getTag4());
			extP.Insert(); // 执行插入.
		}

		// 文本自动填充
		exts = new MapExts();
		exts.Retrieve(MapExtAttr.ExtType, MapExtXmlList.TBFullCtrl);
		for (MapExt ext : exts.ToJavaList()) {
			String mypk = ext.getFK_MapData() + "_" + ext.getAttrOfOper();
			MapAttr ma = new MapAttr();
			ma.setMyPK(mypk);
			if (ma.RetrieveFromDBSources() == 0) {
				ext.Delete();
				continue;
			}
			String modal = ma.GetParaString("TBFullCtrl");
			if (DataType.IsNullOrEmpty(modal) == false)
				continue; // 已经修复了，或者配置了.

			if (DataType.IsNullOrEmpty(ext.getTag3()) == false)
				ma.SetPara("TBFullCtrl", "Simple");
			else
				ma.SetPara("TBFullCtrl", "Table");

			ma.Update();

			if (DataType.IsNullOrEmpty(ext.getTag4()) == true)
				continue;

			MapExt extP = new MapExt();
			extP.setMyPK(ext.getMyPK() + "_FullData");
			int i = extP.RetrieveFromDBSources();
			if (i == 1)
				continue;
			extP.setExtType("FullData");
			extP.setFK_MapData(ext.getFK_MapData());
			extP.setAttrOfOper(ext.getAttrOfOper());
			extP.setDBType(ext.getDBType());
			extP.setDoc(ext.getTag4());

			// 填充从表
			extP.setTag1(ext.getTag1());
			// 填充下拉框
			extP.setTag(ext.getTag());

			extP.Insert(); // 执行插入.
		}

		// 下拉框填充其他控件
		// 文本自动填充
		exts = new MapExts();
		exts.Retrieve(MapExtAttr.ExtType, MapExtXmlList.DDLFullCtrl);
		for (MapExt ext : exts.ToJavaList()) {
			String mypk = ext.getFK_MapData() + "_" + ext.getAttrOfOper();
			MapAttr ma = new MapAttr();
			ma.setMyPK(mypk);
			if (ma.RetrieveFromDBSources() == 0) {
				ext.Delete();
				continue;
			}
			String modal = ma.GetParaString("IsFullData");
			if (DataType.IsNullOrEmpty(modal) == false)
				continue; // 已经修复了，或者配置了.

			// 启用填充其他控件
			ma.SetPara("IsFullData", 1);
			ma.Update();

			MapExt extP = new MapExt();
			extP.setMyPK(ext.getMyPK() + "_FullData");
			int i = extP.RetrieveFromDBSources();
			if (i == 1)
				continue;

			extP.setExtType("FullData");
			extP.setFK_MapData(ext.getFK_MapData());
			extP.setAttrOfOper(ext.getAttrOfOper());
			extP.setDBType(ext.getDBType());
			extP.setDoc(ext.getDoc());

			// 填充从表
			extP.setTag1(ext.getTag1());
			// 填充下拉框
			extP.setTag(ext.getTag());

			extP.Insert(); // 执行插入.

		}*/
		// #region 升级填充事件.

		String msg = "";
		try {

			// #region 升级事件.
			if (DBAccess.IsExitsTableCol("Sys_FrmEvent", "DoType") == true) {
				FrmEvent fe = new FrmEvent();
				fe.CheckPhysicsTable();

				DBAccess.RunSQL("UPDATE Sys_FrmEvent SET EventDoType=DoType  ");
				DBAccess.RunSQL("ALTER TABLE Sys_FrmEvent   DROP COLUMN	DoType  ");
			}
			// #endregion

			/*
			 * 升级版本记录: 20150330: 优化发起列表的效率, by:zhoupeng. 2, 升级表单树,支持动态表单树. 1,
			 * 执行一次Sender发送人的升级，原来由GenerWorkerList 转入WF_GenerWorkFlow. 0,
			 * 静默升级启用日期.2014-12
			 */
			FrmRB rb = new FrmRB();
			rb.CheckPhysicsTable();
			// 序列号.
			BP.Sys.Serial se = new BP.Sys.Serial();
			se.CheckPhysicsTable();

			NodeExt ext = new NodeExt();
			ext.CheckPhysicsTable();

			BP.Sys.EnCfg cfg = new BP.Sys.EnCfg();
			cfg.CheckPhysicsTable();

			// 执行升级 2016.04.08
			Cond cnd = new Cond();
			cnd.CheckPhysicsTable();
			
			 //增加列FlowStars
            WFEmp wfemp = new WFEmp();
            wfemp.CheckPhysicsTable();
            //#region 更新wf_emp. 的字段类型. 2019.06.19
            DBType dbType = SystemConfig.getAppCenterDBType();
            if (dbType == DBType.Oracle)
            {
                DBAccess.RunSQL("ALTER TABLE  WF_EMP add startFlows_temp BLOB");
                //将需要改成大字段的项内容copy到大字段中
                DBAccess.RunSQL("UPDate WF_EMP set startFlows_temp=STARTFLOWS");
                //删除原有字段
                 DBAccess.RunSQL("ALTER TABLE  WF_EMP drop column STARTFLOWS");
                //将大字段名改成原字段名
                 DBAccess.RunSQL("ALTER TABLE  WF_EMP rename column startFlows_temp to STARTFLOWS");
                
            }
            if (dbType == DBType.MySQL)
                DBAccess.RunSQL("ALTER TABLE WF_Emp modify StartFlows longtext ");
            if (dbType == DBType.MSSQL)
            {
                DataTable dtYueSu = DBAccess.RunSQLReturnTable("SELECT b.name, a.name FName from sysobjects b join syscolumns a on b.id = a.cdefault where a.id = object_id('WF_Emp') and a.Name='StartFlows' ");
                if (dtYueSu.Rows.size() != 0)
                    DBAccess.RunSQL(" ALTER TABLE WF_Emp drop  constraint " + dtYueSu.Rows.get(0).getValue(0));

                DBAccess.RunSQL(" ALTER TABLE WF_Emp ALTER column  StartFlows text");
            }
           
            //#endregion 更新wf_emp 的字段类型.

			// /#region 标签Ext
			sql = "DELETE FROM Sys_EnCfg WHERE No='BP.WF.Template.NodeExt'";
			DBAccess.RunSQL(sql);
			sql = "INSERT INTO Sys_EnCfg(No,GroupTitle) VALUES ('BP.WF.Template.NodeExt','";
			sql += "@NodeID=基本配置";
			sql += "@FWCSta=审核组件,适用于sdk表单审核组件与ccform上的审核组件属性设置.";
			sql += "@SendLab=按钮权限,控制工作节点可操作按钮.";
			sql += "@RunModel=运行模式,分合流,父子流程";
			sql += "@AutoJumpRole0=跳转,自动跳转规则当遇到该节点时如何让其自动的执行下一步.";
			sql += "@MPhone_WorkModel=移动,与手机平板电脑相关的应用设置.";
			sql += "@TSpanDay=考核,时效考核,质量考核.";
			// sql += "@OfficeOpen=公文按钮,只有当该节点是公文流程时候有效";
			sql += "')";
			DBAccess.RunSQL(sql);

			sql = "DELETE FROM Sys_EnCfg WHERE No='BP.WF.Template.FlowExt'";
			DBAccess.RunSQL(sql);
			sql = "INSERT INTO Sys_EnCfg(No,GroupTitle) VALUES ('BP.WF.Template.FlowExt','";
			sql += "@No=基本配置";
			sql += "')";
			DBAccess.RunSQL(sql);

			sql = "DELETE FROM Sys_EnCfg WHERE No='BP.WF.Template.MapDataExt'";
			DBAccess.RunSQL(sql);
			sql = "INSERT INTO Sys_EnCfg(No,GroupTitle) VALUES ('BP.WF.Template.MapDataExt','";
			sql += "@No=基本属性";
			sql += "@Designer=设计者信息";
			sql += "')";
			DBAccess.RunSQL(sql);

			// 更新表单应用类型，注意会涉及到其他问题.
			sql = "UPDATE Sys_MapData SET AppType=0 WHERE No NOT LIKE 'ND%'";
			DBAccess.RunSQL(sql);
			// /#endregion

			// /#region 标签
			sql = "DELETE FROM Sys_EnCfg WHERE No='BP.WF.Template.NodeSheet'";
			DBAccess.RunSQL(sql);
			sql = "INSERT INTO Sys_EnCfg(No,GroupTitle) VALUES ('BP.WF.Template.NodeSheet','";
			sql += "@NodeID=基本配置";
			sql += "@FormType=表单";
			sql += "@FWCSta=审核组件,适用于sdk表单审核组件与ccform上的审核组件属性设置.";
			sql += "@SFSta=父子流程,对启动，查看父子流程的控件设置.";
			sql += "@SendLab=按钮权限,控制工作节点可操作按钮.";
			sql += "@RunModel=运行模式,分合流,父子流程";
			sql += "@AutoJumpRole0=跳转,自动跳转规则当遇到该节点时如何让其自动的执行下一步.";
			sql += "@MPhone_WorkModel=移动,与手机平板电脑相关的应用设置.";
			sql += "@TSpanDay=考核,时效考核,质量考核.";
			// sql += "@MsgCtrl=消息,流程消息信息.";
			sql += "@OfficeOpen=公文按钮,只有当该节点是公文流程时候有效";
			sql += "')";
			DBAccess.RunSQL(sql);

			sql = "DELETE FROM Sys_EnCfg WHERE No='BP.WF.Template.FlowSheet'";
			DBAccess.RunSQL(sql);
			sql = "INSERT INTO Sys_EnCfg(No,GroupTitle) VALUES ('BP.WF.Template.FlowSheet','";
			sql += "@No=基本配置";
			sql += "@FlowRunWay=启动方式,配置工作流程如何自动发起，该选项要与流程服务一起工作才有效.";
			sql += "@StartLimitRole=启动限制规则";
			sql += "@StartGuideWay=发起前置导航";
			sql += "@DTSWay=流程数据与业务数据同步";
			sql += "@PStarter=轨迹查看权限";
			sql += "')";
			DBAccess.RunSQL(sql);
			// /#endregion

			// /#region 增加week字段,方便按周统计.
			GenerWorkFlow gwf = new GenerWorkFlow();
			gwf.CheckPhysicsTable();
			sql = "SELECT WorkID,RDT FROM WF_GenerWorkFlow WHERE WeekNum=0 or WeekNum is null ";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow dr : dt.Rows) {
				sql = "UPDATE WF_GenerWorkFlow SET WeekNum="
						+ DataType.WeekOfYear(DataType.ParseSysDateTime2DateTime(dr.getValue(1).toString()))
						+ " WHERE WorkID=" + dr.getValue(0).toString();
				DBAccess.RunSQL(sql);
			}
			// 查询.
			CH ch = new CH();
			ch.CheckPhysicsTable();

			// /#region 检查数据源.
			SFDBSrc src = new SFDBSrc();
			src.setNo("local");
			src.setName("本机数据源(默认)");
			if (src.RetrieveFromDBSources() == 0) {
				src.Insert();
			}
			// /#endregion 检查数据源.

			// /#region 更新枚举类型.
			// 删除枚举值,让其自动生成.
			sql = "DELETE FROM Sys_Enum WHERE EnumKey IN ('CodeStruct'";
			sql += ",'DBSrcType'";
			sql += ",'WebOfficeEnable'";
			sql += ",'BlockModel'";
			sql += ",'CCRole','FWCType','SelectAccepterEnable','NodeFormType','StartGuideWay','"
					+ FlowAttr.StartLimitRole
					+ "','BillFileType','EventDoType','FormType','BatchRole','StartGuideWay','NodeFormType','FormRunType')";
			DBAccess.RunSQL(sql);
			// /#endregion 更新枚举类型.

			// /#region 其他.
			// 更新 PassRate.
			sql = "UPDATE WF_Node SET PassRate=100 WHERE PassRate IS NULL";
			DBAccess.RunSQL(sql);
			// /#endregion 其他.

			// /#region 升级统一规则.
			try {
				String sqls = "";
				// sunxd 20170714
				// oracle数据库无法识别“+”
				// 增加oracle数据库判断
				if (SystemConfig.getAppCenterDBType() == DBType.Oracle) {
					sqls += "UPDATE Sys_MapExt SET MyPK= ExtType || '_' ||FK_Mapdata || '_' ||AttrOfOper WHERE ExtType='"
							+ MapExtXmlList.TBFullCtrl + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= ExtType || '_' || FK_Mapdata || '_' || AttrOfOper WHERE ExtType='"
							+ MapExtXmlList.PopVal + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= ExtType || '_' || FK_Mapdata || '_' || AttrOfOper WHERE ExtType='"
							+ MapExtXmlList.DDLFullCtrl + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= ExtType || '_' || FK_Mapdata || '_' || AttrOfOper WHERE ExtType='"
							+ MapExtXmlList.ActiveDDL + "'";
				} else if (SystemConfig.getAppCenterDBType() == DBType.MySQL) {
					sqls += "UPDATE Sys_MapExt SET MyPK= CONCAT(ExtType,'_',FK_Mapdata,'_',AttrOfOper) WHERE ExtType='"
							+ MapExtXmlList.TBFullCtrl + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= CONCAT(ExtType,'_',FK_Mapdata,'_',AttrOfOper) WHERE ExtType='"
							+ MapExtXmlList.PopVal + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= CONCAT(ExtType,'_',FK_Mapdata,'_',AttrOfOper) WHERE ExtType='"
							+ MapExtXmlList.DDLFullCtrl + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= CONCAT(ExtType,'_',FK_Mapdata,'_',AttrOfOper) WHERE ExtType='"
							+ MapExtXmlList.ActiveDDL + "'";
				} else {
					sqls += "UPDATE Sys_MapExt SET MyPK= ExtType+'_'+FK_Mapdata+'_'+AttrOfOper WHERE ExtType='"
							+ MapExtXmlList.TBFullCtrl + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= ExtType+'_'+FK_Mapdata+'_'+AttrOfOper WHERE ExtType='"
							+ MapExtXmlList.PopVal + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= ExtType+'_'+FK_Mapdata+'_'+AttrOfOper WHERE ExtType='"
							+ MapExtXmlList.DDLFullCtrl + "'";
					sqls += "@UPDATE Sys_MapExt SET MyPK= ExtType+'_'+FK_Mapdata+'_'+AttrOfOper WHERE ExtType='"
							+ MapExtXmlList.ActiveDDL + "'";
				}

				DBAccess.RunSQLs(sqls);
			} catch (Exception e) {
			}
			// /#region 更新CA签名(2015-03-03)。
			// 升级表单树. 2015.10.05
			SysFormTree sft = new SysFormTree();
			sft.CheckPhysicsTable();

			// sql = "UPDATE Sys_FormTree SET DBSrc='local' WHERE DBSrc IS NULL
			// OR DBSrc=''";
			// BP.DA.DBAccess.RunSQL(sql);

			// 表单信息表.
			MapDataExt mapext = new MapDataExt();
			mapext.CheckPhysicsTable();

			TransferCustom tc = new TransferCustom();
			tc.CheckPhysicsTable();
			// 增加部门字段。
			CCList cc = new CCList();
			cc.CheckPhysicsTable();

			// /#region 升级sys_sftable
			// 升级
			SFTable tab = new SFTable();
			tab.CheckPhysicsTable();
			Node wf_Node = new Node();
			wf_Node.CheckPhysicsTable();
			// 设置节点ICON.
			sql = "UPDATE WF_Node SET ICON='审核.png' WHERE ICON IS NULL";
			DBAccess.RunSQL(sql);

			BP.WF.Template.NodeSheet nodeSheet = new BP.WF.Template.NodeSheet();
			nodeSheet.CheckPhysicsTable();

			// /#region 把节点的toolbarExcel, word 信息放入mapdata
			BP.WF.Template.NodeSheets nss = new BP.WF.Template.NodeSheets();
			nss.RetrieveAll();
			for (BP.WF.Template.NodeSheet ns : nss.ToJavaList()) {
				ToolbarExcel te = new ToolbarExcel();
				te.setNo("ND" + ns.getNodeID());
				te.RetrieveFromDBSources();
			}
			Direction dir = new Direction();
			dir.CheckPhysicsTable();

			SelectAccper selectAccper = new SelectAccper();
			selectAccper.CheckPhysicsTable();
			// /#region 升级wf-generworkerlist 2014-05-09
			GenerWorkerList gwl = new GenerWorkerList();
			gwl.CheckPhysicsTable();
			// /#region 升级 NodeToolbar
			FrmField ff = new FrmField();
			ff.CheckPhysicsTable();

			MapAttr attr = new MapAttr();
			attr.CheckPhysicsTable();

			NodeToolbar bar = new NodeToolbar();
			bar.CheckPhysicsTable();

			BP.WF.Template.FlowFormTree tree = new BP.WF.Template.FlowFormTree();
			tree.CheckPhysicsTable();

			FrmNode nff = new FrmNode();
			nff.CheckPhysicsTable();

			SysForm ssf = new SysForm();
			ssf.CheckPhysicsTable();

			SysFormTree ssft = new SysFormTree();
			ssft.CheckPhysicsTable();

			FrmAttachment ath = new FrmAttachment();
			ath.CheckPhysicsTable();

			FrmField ffs = new FrmField();
			ffs.CheckPhysicsTable();

			// /#region 升级sys_sftable
			FrmTransferCustom ftc = new FrmTransferCustom();
			ftc.CheckPhysicsTable();
			// /#region 执行sql．
			DBAccess.RunSQL(
					"delete  from Sys_Enum WHERE EnumKey in ('BillFileType','EventDoType','FormType','BatchRole','StartGuideWay','NodeFormType')");
			DBAccess.RunSQL(
					"UPDATE Sys_FrmSln SET FK_Flow =(SELECT FK_FLOW FROM WF_Node WHERE NODEID=Sys_FrmSln.FK_Node) WHERE FK_Flow IS NULL");

			if (SystemConfig.getAppCenterDBType() == DBType.Oracle)
				DBAccess.RunSQL("UPDATE WF_FrmNode SET MyPK=FK_Frm||'_'||FK_Node||'_'||FK_Flow");
			else if (SystemConfig.getAppCenterDBType() == DBType.MySQL)
				DBAccess.RunSQL("UPDATE WF_FrmNode SET MyPK=CONCAT(FK_Frm,'_',FK_Node,'_',FK_Flow)");
			else
				DBAccess.RunSQL("UPDATE WF_FrmNode SET MyPK=FK_Frm+'_'+convert(varchar,FK_Node )+'_'+FK_Flow");

			// /#region 检查必要的升级。

			FrmWorkCheck fwc = new FrmWorkCheck();
			fwc.CheckPhysicsTable();

			Flow myfl = new Flow();
			myfl.CheckPhysicsTable();

			Node nd = new Node();
			nd.CheckPhysicsTable();
			// Sys_SFDBSrc
			SFDBSrc sfDBSrc = new SFDBSrc();
			sfDBSrc.CheckPhysicsTable();
			// /#region 执行更新.wf_node
			sql = "UPDATE WF_Node SET FWCType=0 WHERE FWCType IS NULL";
			sql += "@UPDATE WF_Node SET FWC_X=0 WHERE FWC_X IS NULL";
			sql += "@UPDATE WF_Node SET FWC_Y=0 WHERE FWC_Y IS NULL";
			sql += "@UPDATE WF_Node SET FWC_W=0 WHERE FWC_W IS NULL";
			sql += "@UPDATE WF_Node SET FWC_H=0 WHERE FWC_H IS NULL";
			DBAccess.RunSQLs(sql);

			sql = "UPDATE WF_Node SET SFSta=0 WHERE SFSta IS NULL";
			sql += "@UPDATE WF_Node SET SF_X=0 WHERE SF_X IS NULL";
			sql += "@UPDATE WF_Node SET SF_Y=0 WHERE SF_Y IS NULL";
			sql += "@UPDATE WF_Node SET SF_W=0 WHERE SF_W IS NULL";
			sql += "@UPDATE WF_Node SET SF_H=0 WHERE SF_H IS NULL";
			DBAccess.RunSQLs(sql);
			// /#region 执行报表设计。
			Flows fls = new Flows();
			fls.RetrieveAll();
			for (Flow fl : fls.ToJavaList()) {
				try {
					MapRpts rpts = new MapRpts();
				} catch (Exception e3) {
					fl.DoCheck();
				}
			}
			// /#region 升级站内消息表 2013-10-20
			SMS sms = new SMS();
			sms.CheckPhysicsTable();
			// /#region 重新生成view WF_EmpWorks, 2013-08-06.
			try {

				if (DBAccess.IsExitsObject("WF_EmpWorks") == true)
					DBAccess.RunSQL("DROP VIEW WF_EmpWorks");

				if (DBAccess.IsExitsObject("V_FlowStarter") == true)
					DBAccess.RunSQL("DROP VIEW V_FlowStarter");

				if (DBAccess.IsExitsObject("V_FlowStarterBPM") == true)
					DBAccess.RunSQL("DROP VIEW V_FlowStarterBPM");

				if (DBAccess.IsExitsObject("V_TOTALCH") == true)
					DBAccess.RunSQL("DROP VIEW V_TOTALCH");

				if (DBAccess.IsExitsObject("V_TOTALCHYF") == true)
					DBAccess.RunSQL("DROP VIEW V_TOTALCHYF");

				if (DBAccess.IsExitsObject("V_TOTALCHWeek") == true)
					DBAccess.RunSQL("DROP VIEW V_TOTALCHWeek");
				
				if (DBAccess.IsExitsObject("V_WF_Delay") == true)
					DBAccess.RunSQL("DROP VIEW V_WF_Delay");
			} catch (Exception e4) {
			}

			String sqlscript = "";
			// 执行必须的sql.
			switch (SystemConfig.getAppCenterDBType()) {
			case Oracle:
				sqlscript = SystemConfig.getCCFlowAppPath() + "/WF/Data/Install/SQLScript/InitView_Ora.sql";
				break;
			case MSSQL:
			case Informix:
				sqlscript = SystemConfig.getCCFlowAppPath() + "/WF/Data/Install/SQLScript/InitView_SQL.sql";
				break;
			case MySQL:
				sqlscript = SystemConfig.getCCFlowAppPath() + "/WF/Data/Install/SQLScript/InitView_MySQL.sql";
				break;
			default:
				break;
			}
			
			 
		

			DBAccess.RunSQLScript(sqlscript);
			// /#region 更新表单的边界.2014-10-18
			MapDatas mds = new MapDatas();
			mds.RetrieveAll();

			for (MapData md : mds.ToJavaList()) {
				md.ResetMaxMinXY(); // 更新边界.
			}
			FrmImg img = new FrmImg();
			img.CheckPhysicsTable();
			switch (SystemConfig.getAppCenterDBType()) {
			case Oracle:
				msg = "@Sys_MapAttr 修改字段";
				break;
			case MSSQL:
				msg = "@修改sql server控件高度和宽度字段。";
				DBAccess.RunSQL("ALTER TABLE Sys_MapAttr ALTER COLUMN UIWidth float");
				DBAccess.RunSQL("ALTER TABLE Sys_MapAttr ALTER COLUMN UIHeight float");
				break;
			default:
				break;
			}
			// /#region 升级常用词汇
			switch (SystemConfig.getAppCenterDBType()) {
			case Oracle:
				int i = DBAccess.RunSQLReturnCOUNT(
						"SELECT * FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'SYS_DEFVAL' AND COLUMN_NAME = 'PARENTNO'");
				if (i == 0) {
					if (DBAccess.IsExitsObject("Sys_DefVal")) {
						DBAccess.RunSQL("drop table Sys_DefVal");
					}
					DefVal dv = new DefVal();
					dv.CheckPhysicsTable();
				}
				msg = "@Sys_DefVal 修改字段";
				break;
			case MSSQL:
				msg = "@修改 Sys_DefVal。";
				i = DBAccess.RunSQLReturnCOUNT(
						"SELECT * FROM SYSCOLUMNS WHERE ID=OBJECT_ID('Sys_DefVal') AND NAME='ParentNo'");
				if (i == 0) {
					if (DBAccess.IsExitsObject("Sys_DefVal")) {
						DBAccess.RunSQL("drop table Sys_DefVal");
					}
					DefVal dv = new DefVal();
					dv.CheckPhysicsTable();
				}
				break;
			default:
				break;
			}
			// /#region 登录更新错误
			msg = "@登录时错误。";
			DBAccess.RunSQL(
					"DELETE FROM Sys_Enum WHERE EnumKey IN ('DeliveryWay','RunModel','OutTimeDeal','FlowAppType')");
			// /#region 升级表单树
			// 首先检查是否升级过.
			sql = "SELECT * FROM Sys_FormTree WHERE No = '1'";
			DataTable formTree_dt = DBAccess.RunSQLReturnTable(sql);
			if (formTree_dt.Rows.size() == 0) {
				/* 没有升级过.增加根节点 */
				SysFormTree formTree = new SysFormTree();
				formTree.setNo("1");
				formTree.setName("表单库");
				formTree.setParentNo("0");
				// formTree.TreeNo = "0";
				formTree.setIdx(0);
				formTree.setIsDir(true);

				try {
					formTree.DirectInsert();
				} catch (Exception e5) {
				}
				// 将表单库中的数据转入表单树
				SysFormTrees formSorts = new SysFormTrees();
				formSorts.RetrieveAll();

				for (SysFormTree item : formSorts.ToJavaList()) {
					if (item.getNo().equals("0")) {
						continue;
					}
					SysFormTree subFormTree = new SysFormTree();
					subFormTree.setNo(item.getNo());
					subFormTree.setName(item.getName());
					subFormTree.setParentNo("0");
					subFormTree.Save();
				}
				// 表单于表单树进行关联
				sql = "UPDATE Sys_MapData SET FK_FormTree=FK_FrmSort WHERE FK_FrmSort <> '' AND FK_FrmSort is not null";
				DBAccess.RunSQL(sql);
			}
			// /#region 执行admin登录. 2012-12-25 新版本.
			Emp emp = new Emp();
			emp.setNo("admin");
			if (emp.RetrieveFromDBSources() == 1) {
				WebUser.SignInOfGener(emp, true);
			} else {
				emp.setNo("admin");
				emp.setName("admin");
				emp.setFK_Dept("01");
				emp.setPass("123");
				emp.Insert();
				WebUser.SignInOfGener(emp, true);
				// throw new Exception("admin 用户丢失，请注意大小写。");
			}
			// /#region 修复 Sys_FrmImg 表字段 ImgAppType Tag0
			switch (SystemConfig.getAppCenterDBType()) {
			case Oracle:
				int i = DBAccess.RunSQLReturnCOUNT(
						"SELECT * FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'SYS_FRMIMG' AND COLUMN_NAME = 'TAG0'");
				if (i == 0) {
					DBAccess.RunSQL("ALTER TABLE SYS_FRMIMG ADD (ImgAppType number,TAG0 nvarchar(500))");
				}
				msg = "@Sys_FrmImg 修改字段";
				break;
			case MSSQL:
				msg = "@修改 Sys_FrmImg。";
				i = DBAccess
						.RunSQLReturnCOUNT("SELECT * FROM SYSCOLUMNS WHERE ID=OBJECT_ID('Sys_FrmImg') AND NAME='Tag0'");
				if (i == 0) {
					DBAccess.RunSQL("ALTER TABLE Sys_FrmImg ADD ImgAppType int");
					DBAccess.RunSQL("ALTER TABLE Sys_FrmImg ADD Tag0 nvarchar(500)");
				}
				break;
			default:
				break;
			}

			// 最后更新版本号，然后返回.
			sql = "UPDATE Sys_Serial SET IntVal=" + Ver + " WHERE CfgKey='Ver'";
			if (DBAccess.RunSQL(sql) == 0) {
				sql = "INSERT INTO Sys_Serial (CfgKey,IntVal) VALUES('Ver'," + Ver + ") ";
				DBAccess.RunSQL(sql);
			}
			// 返回版本号.
			return Ver + ""; // +"\t\n解决问题:" + updataNote;
		} catch (RuntimeException ex) {
			String err = "问题出处:" + ex.getMessage() + "<hr>" + msg + "<br>详细信息:@" + ex.getStackTrace()
					+ "<br>@<a href='../DBInstall.jsp' >点这里到系统升级界面。</a>";
			BP.DA.Log.DefaultLogWriteLineError("系统升级错误:" + err, ex);
			return "0";
			// return "升级失败,详细请查看日志.\\DataUser\\Log\\";
		}
	}

	/// <summary>
	/// 如果发现升级sql文件日期变化了，就自动升级.
	/// 就是说该文件如果被修改了就会自动升级.
	/// </summary>
	public static void UpdataCCFlowVerSQLScript() {

		String sql = "SELECT IntVal FROM Sys_Serial WHERE CfgKey='UpdataCCFlowVer'";
		String currDBVer = DBAccess.RunSQLReturnStringIsNull(sql, "");

		String sqlScript = SystemConfig.getPathOfData() + "/UpdataCCFlowVer.sql";
		File fi = new File(sqlScript);

		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(fi.lastModified());
		String myVer = sdf.format(cal.getTime());
		// String myVer = fi.getLastWriteTime.ToString("MMddHHmmss");
		if ("".equals(currDBVer) || Integer.parseInt(currDBVer) != Integer.parseInt(myVer)) {
			DBAccess.RunSQLScript(SystemConfig.getPathOfData() + "/UpdataCCFlowVer.sql");
			sql = "UPDATE Sys_Serial SET IntVal=" + myVer + " WHERE CfgKey='UpdataCCFlowVer'";

			if (DBAccess.RunSQL(sql) == 0) {
				sql = "INSERT INTO Sys_Serial (CfgKey,IntVal) VALUES('UpdataCCFlowVer'," + myVer + ") ";
				DBAccess.RunSQL(sql);
			}
		}
	}

	/**
	 * CCFlowAppPath
	 */
	public static String getCCFlowAppPath() {
		HttpServletRequest request = ContextHolderUtils.getRequest();
		String basePath = "";
		if (request == null || request.getServerName() == null) {
			basePath = Glo.getHostURL();
		} else if (request.getServerPort() == 80) {
			basePath = request.getScheme() + "://" + request.getServerName() + request.getContextPath() + "/";
		} else {
			basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath() + "/";
		}
		return basePath;
	}

	/**
	 * 检查是否可以安装系统
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static boolean IsCanInstall() throws Exception {
		 String sql = "";
		 String errInfo = "";
		 try
         {
             errInfo = " 当前用户没有[查询系统表]的权限. ";

             if (DBAccess.IsExitsObject("AA"))
             {
                 errInfo = " 当前用户没有[删除表]的权限. ";
                 sql = "DROP TABLE AA";
                 DBAccess.RunSQL(sql);
             }

             errInfo = " 当前用户没有[创建表]的权限. ";
             sql = "CREATE TABLE AA (OID int NOT NULL)"; //检查是否可以创建表.
             DBAccess.RunSQL(sql);

             errInfo = " 当前用户没有[插入数据]的权限. ";
             sql = "INSERT INTO AA (OID) VALUES(100 )"; 
             DBAccess.RunSQL(sql);

             errInfo = " 当前用户没有[update 表数据]的权限. ";
             sql = "UPDATE AA SET OID=101";
             DBAccess.RunSQL(sql);

             errInfo = " 当前用户没有[delete 表数据]的权限. ";
             sql = "DELETE FROM AA";
             DBAccess.RunSQL(sql);

             errInfo = " 当前用户没有[创建表主键]的权限. ";
             DBAccess.CreatePK("AA", "OID", SystemConfig.getAppCenterDBType());

             errInfo = " 当前用户没有[创建索引]的权限. ";
             DBAccess.CreatIndex("AA", "OID"); //可否创建索引.


             errInfo = " 当前用户没有[查询数据表]的权限. ";
             sql = "select * from AA"; //检查是否有查询的权限.
             DBAccess.RunSQLReturnTable(sql);

             errInfo = " 当前数据库设置区分了大小写，不能对大小写敏感，如果是mysql数据库请参考 https://blog.csdn.net/ccflow/article/details/100079825 ";
             sql = "select * from aa"; //检查是否区分大小写.
             DBAccess.RunSQLReturnTable(sql);

             if (DBAccess.IsExitsObject("AAVIEW"))
             {
            	 errInfo = " 当前用户没有[删除视图]的权限. ";
                 sql = "DROP VIEW AAVIEW";
                 DBAccess.RunSQL(sql);
             }

             errInfo = " 当前用户没有[创建视图]的权限. ";
             sql = "CREATE VIEW AAVIEW AS SELECT * FROM AA "; //检查是否可以创建视图.
             DBAccess.RunSQL(sql);

             errInfo = " 当前用户没有[删除视图]的权限. ";
             sql = "DROP VIEW AAVIEW"; //检查是否可以删除视图.
             DBAccess.RunSQL(sql);

             errInfo = " 当前用户没有[删除表]的权限. ";
             sql = "DROP TABLE AA"; //检查是否可以删除表.
             DBAccess.RunSQL(sql);
             return true;
         }
         catch (Exception ex)
         {
             if (DBAccess.IsExitsObject("AA") == true)
             {
                 sql = "DROP TABLE AA";
                 DBAccess.RunSQL(sql);
             }

             if (DBAccess.IsExitsObject("AAVIEW") == true)
             {
                 sql = "DROP VIEW AAVIEW";
                 DBAccess.RunSQL(sql);
             }

             String info = "检查数据库安装权限出现错误:";
             info += "\t\n1. 当前登录的数据库帐号，必须有创建、删除视图或者table的权限。";
             info += "\t\n2. 必须对表有增、删、改、查的权限。 ";
             info += "\t\n3. 必须有删除创建索引主键的权限。 ";
             info += "\t\n4. 我们建议您设置当前数据库连接用户为管理员权限。 ";
             info += "\t\n ccbpm检查出来的信息如下：" + errInfo;

             info += "\t\n etc: 数据库测试异常信息:" + ex.getMessage();

             throw new Exception("err@" + info);
             //  return false;
         }
     }

	/**
	 * 安装包
	 * 
	 * @param lang
	 *            语言
	 * @param demoType
	 *            0开发人员流程, 1,业务流程 , 2不安装流程.
	 * @throws Exception
	 */
	public static void DoInstallDataBase(String lang, int demoType) throws Exception {
		boolean isInstallFlowDemo = true;
		if (demoType == 2) {
			isInstallFlowDemo = false;
		}

		ArrayList al = null;
		String info = "BP.En.Entity";
		al = BP.En.ClassFactory.GetObjects(info);

		FrmRB frmRb = new FrmRB();
		frmRb.CheckPhysicsTable();

		BP.Sys.SysEnum se = new BP.Sys.SysEnum();
		se.CheckPhysicsTable();

		BP.Sys.SysEnumMain sem = new BP.Sys.SysEnumMain();
		sem.CheckPhysicsTable();
	
		
		// 先创建表，否则列的顺序就会变化.
		FlowExt fe = new FlowExt();
		fe.CheckPhysicsTable();

		NodeExt ne = new NodeExt();
		ne.CheckPhysicsTable();

		WFEmp wfemp = new WFEmp();
		wfemp.CheckPhysicsTable();

		if (DBAccess.IsExitsTableCol("WF_Emp", "StartFlows") == false)
		{
			String sql = "";
			//增加StartFlows这个字段
			switch (SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					sql = "ALTER TABLE WF_Emp ADD StartFlows Text DEFAULT  NULL";
					break;
				case Oracle:
					sql = "ALTER TABLE  WF_EMP add StartFlows BLOB";
					break;
				case MySQL:
					sql = "ALTER TABLE WF_Emp ADD StartFlows TEXT COMMENT '可以发起的流程'";
					break;
				case Informix:
					sql = "ALTER TABLE WF_Emp ADD StartFlows VARCHAR(4000) DEFAULT  NULL";
					break;
				case PostgreSQL:
					sql = "ALTER TABLE WF_Emp ADD StartFlows Text DEFAULT  NULL";
					break;
				default:
					throw new Exception("@没有涉及到的数据库类型");
			}
			DBAccess.RunSQL(sql);

		}
		// 先创建表，否则列的顺序就会变化.

		// 1, 创建or修复表
		for (Object obj : al) {

			Entity en = null;
			en = (Entity) ((obj instanceof Entity) ? obj : null);
			if (en == null) {
				continue;
			}

			// 获得类名.
			String clsName = en.toString();

			if (clsName == null)
				continue;

			// 不安装CCIM的表.
			if (clsName != null && clsName.contains("BP.CCIM")) {
				continue;
			}

			// 不安装CCIM的表.
			if (clsName != null && clsName.contains("StartWork")) {
				continue;
			}

			// 不安装Work的表.
			if (clsName != null && clsName.equals("BP.WF.Work")) {
				continue;
			}

			if (clsName != null && clsName.equals("BP.WF.GEWork")) {
				continue;
			}

			// 抽象的类不允许创建表.
			switch (clsName) {
			case "BP.WF.StartWork":
			case "BP.WF.Work":
			case "BP.WF.GEStartWork":
			case "BP.En.GENoName":
			case "BP.En.GETree":
			case "BP.WF.Data.GERpt":
			case "BP.WF.GEWork":
				continue;
			default:
				break; 
			}

			if (isInstallFlowDemo == false) {
				// 如果不安装demo.
				if (clsName != null) {
					if (clsName.contains("BP.CN") || clsName.contains("BP.Demo")) {
						continue;
					}
				}
			}

			String table = null;
			try {
				
				if (null == en || null == en.getEnMap()) {
					continue;
				}
				table = en.getEnMap().getPhysicsTable();
				if (table == null || table.length() == 0) {
					continue;
				}
			} catch (Exception e) {
				continue;
			}

			// switch (table)
			// ORIGINAL LINE: case "WF_EmpWorks":
			if (table.equals("WF_EmpWorks") || table.equals("WF_GenerEmpWorkDtls") || table.equals("WF_GenerEmpWorks")
					|| table.equals("V_FlowData")) {
				continue;
			}

			en.CheckPhysicsTable();

		}

		// 创建视图.
		// 修复
		// 2, 注册枚举类型 SQL
		// 2, 注册枚举类型。
		BP.Sys.XML.EnumInfoXmls xmls = new BP.Sys.XML.EnumInfoXmls();
		xmls.RetrieveAll();
		for (BP.Sys.XML.EnumInfoXml xml : xmls.ToJavaList()) {
			BP.Sys.SysEnums ses = new BP.Sys.SysEnums();
			ses.RegIt(xml.getKey(), xml.getVals());
		}
		// 注册枚举类型
		// 3, 执行基本的 sql
		if (isInstallFlowDemo == false) {
			SysFormTree frmSort = new SysFormTree();
			frmSort.setNo("01");
			frmSort.setName("表单类别1");
			frmSort.setParentNo("0");
			frmSort.Insert();
		}

		// 删除这个数据, 没有找到，初始化这些数据失败的原因.
		DBAccess.RunSQL("DELETE FROM Port_DeptStation");

		String sqlscript = "";

		sqlscript = SystemConfig.getCCFlowAppPath() + "\\WF\\Data\\Install\\SQLScript\\Port_Inc_CH_BPM.sql";
		DBAccess.RunSQLScript(sqlscript);

		//删除视图.
		if (DBAccess.IsExitsObject("WF_EmpWorks")==true)
			DBAccess.RunSQL("DROP VIEW WF_EmpWorks");
		
		if (DBAccess.IsExitsObject("V_WF_Delay")==true)
			DBAccess.RunSQL("DROP VIEW V_WF_Delay");

		if (DBAccess.IsExitsObject("V_FlowStarter")==true)
			DBAccess.RunSQL("DROP VIEW V_FlowStarter");
		
		if (DBAccess.IsExitsObject("V_FlowStarterBPM")==true)
			DBAccess.RunSQL("DROP VIEW V_FlowStarterBPM");
		
		if (DBAccess.IsExitsObject("V_TOTALCH")==true)
			DBAccess.RunSQL("DROP VIEW V_TOTALCH");
		

		if (DBAccess.IsExitsObject("V_TOTALCHYF")==true)
			DBAccess.RunSQL("DROP VIEW V_TOTALCHYF");
		

		if (DBAccess.IsExitsObject("V_TotalCHWeek")==true)
			DBAccess.RunSQL("DROP VIEW V_TotalCHWeek");
		
		
		           
		
		// 修复
		// 4, 创建视图与数据.
		// 执行必须的sql.
		sqlscript = "";
		// 执行必须的sql.
		switch (SystemConfig.getAppCenterDBType()) {
		case Oracle:
			sqlscript = SystemConfig.getCCFlowAppPath() + "\\WF\\Data\\Install\\SQLScript\\InitView_Ora.sql";
			break;
		case MSSQL:
		case Informix:
			sqlscript = SystemConfig.getCCFlowAppPath() + "\\WF\\Data\\Install\\SQLScript\\InitView_SQL.sql";
			break;
		case MySQL:
			sqlscript = SystemConfig.getCCFlowAppPath() + "\\WF\\Data\\Install\\SQLScript\\InitView_MySQL.sql";
			break;
		default:
			break;
		}

		DBAccess.RunSQLScript(sqlscript);
		// 创建视图与数据
		// 5, 初始化数据.
		if (isInstallFlowDemo) {
			sqlscript = SystemConfig.getPathOfData() + "/Install/SQLScript/InitPublicData.sql";
			DBAccess.RunSQLScript(sqlscript);
		} else {
			FlowSort fs = new FlowSort();
			fs.setNo("02");
			fs.setParentNo("1");
			fs.setName("其他类");
			fs.DirectInsert();
		}
		// 初始化数据
		// 6, 生成临时的 wf_emp 数据。
		BP.Port.Emps emps = new BP.Port.Emps();
		emps.RetrieveAllFromDBSource();
		int i = 0;
		for (Emp emp : emps.ToJavaList()) {
			i++;
			WFEmp wfEmp = new WFEmp();
			wfEmp.Copy(emp);
			wfEmp.setNo(emp.getNo());

			if (wfEmp.getEmail().length() == 0) {
				wfEmp.setEmail(emp.getNo() + "@ccflow.org");
			}

			if (wfEmp.getTel().length() == 0) {
				wfEmp.setTel("82374939-6" + StringHelper.padLeft(String.valueOf(i), 2, '0'));
			}
			if (wfEmp.getIsExits()) {
				wfEmp.Update();
			} else {
				wfEmp.Insert();
			}
		}

		// 生成年度月份数据.
		String sqls = "";
		for (int num = 0; num < 12; num++) {
			sqls = "@ INSERT INTO Pub_NY (No,Name) VALUES ('" + DateUtils.format(new Date(), "yyyy-MM") + "','"
					+ DateUtils.format(new Date(), "yyyy-MM") + "')  ";
			// dtNow = DateUtils.addMonth(dtNow, 1);
		}
		DBAccess.RunSQLs(sqls);

		// 初始化数据
		// 装载 demo.flow
		if (isInstallFlowDemo == true) {

			Emp emp = new Emp("admin");
			WebUser.SignInOfGener(emp);
			BP.Sys.Glo.WriteLineInfo("开始装载模板...");

			// 装载数据模版.
			BP.WF.DTS.LoadTemplete l = new BP.WF.DTS.LoadTemplete();
			Object tempVar = l.Do();
			String msg = (String) ((tempVar instanceof String) ? tempVar : null);

			BP.Sys.Glo.WriteLineInfo("装载模板完成......");

		}

		if (isInstallFlowDemo == false) {
			// 创建一个空白的流程，不然，整个结构就出问题。
			FlowSorts fss = new FlowSorts();
			fss.RetrieveAll();
			fss.Delete();

			FlowSort fs = new FlowSort();
			fs.setName("流程树");
			fs.setNo("1");

			fs.setParentNo("0");
			fs.Insert();

			FlowSort s1 = (FlowSort) fs.DoCreateSubNode();
			s1.setName("日常办公类");
			s1.Update();

			s1 = (FlowSort) fs.DoCreateSubNode();
			s1.setName("财务类");
			s1.Update();

			s1 = (FlowSort) fs.DoCreateSubNode();
			s1.setName("人力资源类");
			s1.Update();
		}

		// 装载demo.flow

		// 增加图片签名
		if (isInstallFlowDemo == true) {
			// 增加图片签名
			BP.WF.DTS.GenerSiganture gs = new BP.WF.DTS.GenerSiganture();
			gs.Do();

		}
		// 增加图片签名.
		// 执行补充的sql, 让外键的字段长度都设置成100.
		DBAccess.RunSQL("UPDATE Sys_MapAttr SET maxlen=100 WHERE LGType=2 AND MaxLen<100");
		DBAccess.RunSQL("UPDATE Sys_MapAttr SET maxlen=100 WHERE KeyOfEn='FK_Dept'");
		// 执行补充的sql, 让外键的字段长度都设置成100.
		// 如果是第一次运行，就执行检查。
		if (isInstallFlowDemo == true) {
			Flows fls = new Flows();
			fls.RetrieveAllFromDBSource();
			for (Flow fl : fls.ToJavaList()) {
				try {
					fl.DoCheck();
				} catch (RuntimeException ex) {
					BP.DA.Log.DebugWriteError(ex.getMessage());
				}
			}
		}

		BP.Sys.Glo.WriteLineInfo("*********************************************");

		BP.Sys.Glo.WriteLineInfo("1. jflow 已经成功安装如果有demo流程在运行之前执行流程检查,就会自动创建数据表。");

		BP.Sys.Glo.WriteLineInfo("2. 再运行的时候有时候会出现数据库字段的异常信息，请刷新一次，系统就可以自动修复字段。");

		BP.Sys.Glo.WriteLineInfo("3. 登录界面 http://loaclhost:8080/  默认密码为pub 或者123 。");

		BP.Sys.Glo.WriteLineInfo("*********************************************");

		// 如果是第一次运行，就执行检查。
	}

	/**
	 * 安装包
	 * 
	 * @throws Exception
	 */
	public static void DoInstallDataBase(String lang, boolean isInstallFlowDemo, boolean isInstallCCIM)
			throws Exception {
		ArrayList al = null;
		String info = "BP.En.Entity";
		al = BP.En.ClassFactory.GetObjects(info);

		// /#region 1, 创建or修复表
		for (Object obj : al) {
			Entity en = null;
			en = (Entity) ((obj instanceof Entity) ? obj : null);
			if (en == null) {
				continue;
			}

			// 获得类名.
			String clsName = en.toString();

			if (isInstallFlowDemo == false) {
				/* 如果不安装demo. */
				if (clsName != null) {
					if (clsName.contains("BP.CN") || clsName.contains("BP.Demo")) {
						continue;
					}
				}
			}

			String table = null;
			try {
				table = en.getEnMap().getPhysicsTable();
				if (table == null) {
					continue;
				}
			} catch (Exception e) {
				continue;
			}

			if ("".equals(table) || table == null)
				continue;

			if (table.equals("WF_EmpWorks") || table.equals("V_WF_Delay") || table.equals("WF_GenerEmpWorkDtls") || table.equals("WF_GenerEmpWorks")
					|| table.equals("WF_FlowFormTree") || table.equals("V_FlowData")) {
				continue;
			} else if (table.equals("Sys_Enum")) {
				en.CheckPhysicsTable();
			} else {
				en.CheckPhysicsTable();
			}

		}

		BP.GPM.Emp empP = new BP.GPM.Emp();
		empP.CheckPhysicsTable();

		// /#endregion 修复

		// /#region 2, 注册枚举类型 SQL
		// 2, 注册枚举类型。
		BP.Sys.XML.EnumInfoXmls xmls = new BP.Sys.XML.EnumInfoXmls();
		xmls.RetrieveAll();
		for (BP.Sys.XML.EnumInfoXml xml : xmls.ToJavaList()) {
			BP.Sys.SysEnums ses = new BP.Sys.SysEnums();
			ses.RegIt(xml.getKey(), xml.getVals());
		}
		// /#endregion 注册枚举类型

		// /#region 3, 执行基本的 sql
		if (isInstallFlowDemo == false) {
			SysFormTree frmSort = new SysFormTree();
			frmSort.setNo("01");
			frmSort.setName("表单类别1");
			frmSort.setParentNo("0");
			frmSort.Insert();
		}

		String sqlscript = "";		  
			/* 如果是BPM模式 */
			sqlscript = SystemConfig.getCCFlowAppPath() + "WF/Data/Install/SQLScript/Port_Inc_CH_BPM.sql";
			DBAccess.RunSQLScript(sqlscript);
		 

		// /#endregion 修复

		// /#region 4, 创建视图与数据.
		// 执行必须的sql.

		sqlscript = "";
		// 执行必须的sql.
		switch (SystemConfig.getAppCenterDBType()) {
		case Oracle:
			sqlscript = SystemConfig.getCCFlowAppPath() + "WF/Data/Install/SQLScript/InitView_Ora.sql";
			break;
		case MSSQL:
		case Informix:
			sqlscript = SystemConfig.getCCFlowAppPath() + "WF/Data/Install/SQLScript/InitView_SQL.sql";
			break;
		case MySQL:
			sqlscript = SystemConfig.getCCFlowAppPath() + "WF/Data/Install/SQLScript/InitView_MySQL.sql";
			break;
		default:
			break;
		}
		 

		DBAccess.RunSQLScript(sqlscript);
		// /#endregion 创建视图与数据.

		// /#region 5, 初始化数据.
		if (isInstallFlowDemo) {
			sqlscript = SystemConfig.getPathOfData() + "Install/SQLScript/InitPublicData.sql";
			DBAccess.RunSQLScript(sqlscript);
		} else {
			FlowSort fs = new FlowSort();
			fs.setNo("02");
			fs.setParentNo("99");
			fs.setName("其他类");
			fs.DirectInsert();
		}
		// /#endregion 初始化数据

		// /#region 6, 生成临时的 wf_emp 数据。
		if (isInstallFlowDemo) {
			BP.Port.Emps emps = new BP.Port.Emps();
			emps.RetrieveAllFromDBSource();
			int i = 0;
			for (Emp emp : emps.ToJavaList()) {
				i++;
				WFEmp wfEmp = new WFEmp();
				wfEmp.Copy(emp);
				wfEmp.setNo(emp.getNo());

				if (wfEmp.getEmail().length() == 0) {
					wfEmp.setEmail(emp.getNo() + "@ccflow.org");
				}

				if (wfEmp.getTel().length() == 0) {
					wfEmp.setTel("82374939-6" + StringHelper.padLeft(String.valueOf(i), 2, '0'));
				}

				if (wfEmp.getIsExits()) {
					wfEmp.Update();
				} else {
					wfEmp.Insert();
				}
			}

			// 生成简历数据.
			for (Emp emp : emps.ToJavaList()) {
				for (int myIdx = 0; myIdx < 6; myIdx++) {
					String sql = "";
					sql = "INSERT INTO Demo_Resume (OID,RefPK,NianYue,GongZuoDanWei,ZhengMingRen,BeiZhu,QT) ";
					sql += "VALUES(" + DBAccess.GenerOID("Demo_Resume") + ",'" + emp.getNo() + "','200" + myIdx
							+ "-01','山东.济南.驰骋" + myIdx + "公司','张三','表现良好','其他-" + myIdx + "无')";
					DBAccess.RunSQL(sql);
				}
			}

			DataTable dtStudent = DBAccess.RunSQLReturnTable("SELECT No FROM Demo_Student");
			for (DataRow dr : dtStudent.Rows) {
				String no = dr.getValue(0).toString();
				for (int myIdx = 0; myIdx < 6; myIdx++) {
					String sql = "";
					sql = "INSERT INTO Demo_Resume (OID,RefPK,NianYue,GongZuoDanWei,ZhengMingRen,BeiZhu,QT) ";
					sql += "VALUES(" + DBAccess.GenerOID("Demo_Resume") + ",'" + no + "','200" + myIdx
							+ "-01','山东.济南.驰骋" + myIdx + "公司','张三','表现良好','其他-" + myIdx + "无')";
					DBAccess.RunSQL(sql);
				}
			}

			// 生成年度月份数据.
			String sqls = "";
			Date dtNow = new Date();
			for (int num = 0; num < 12; num++) {
				sqls = "@ INSERT INTO Pub_NY (No,Name) VALUES ('" + DateUtils.format(new Date(), "yyyy-MM") + "','"
						+ DateUtils.format(new Date(), "yyyy-MM") + "')  ";
				// dtNow = dtNow.plusMonths(1);
				dtNow = DateUtils.addMonth(dtNow, 1);
			}
			DBAccess.RunSQLs(sqls);
		}
		// /#endregion 初始化数据

		// /#region 装载 demo.flow
		if (isInstallFlowDemo == true) {
			Emp emp = new Emp("admin");
			WebUser.SignInOfGener(emp);

			// 装载数据模版.
			BP.WF.DTS.LoadTemplete l = new BP.WF.DTS.LoadTemplete();
			Object tempVar = l.Do();
			String msg = (String) ((tempVar instanceof String) ? tempVar : null);

			// 修复视图.
			Flow.RepareV_FlowData_View();

		} else {

			// 创建一个空白的流程，不然，整个结构就出问题。
			FlowSorts fss = new FlowSorts();
			fss.RetrieveAll();
			fss.Delete();

			FlowSort fs = new FlowSort();
			fs.setName("流程树");
			fs.setNo("01");

			fs.setParentNo("0");
			fs.Insert();

			FlowSort s1 = (FlowSort) fs.DoCreateSubNode();
			s1.setName("日常办公类");
			s1.Update();

			s1 = (FlowSort) fs.DoCreateSubNode();
			s1.setName("财务类");
			s1.Update();

			s1 = (FlowSort) fs.DoCreateSubNode();
			s1.setName("人力资源类");
			s1.Update();
			 

            //创建一个空白的流程，不然，整个结构就出问题。
            FrmTrees frmTrees = new FrmTrees();
            frmTrees.RetrieveAll();
            frmTrees.Delete();

            FrmTree ftree = new FrmTree();
            ftree.setName("表单树");
            ftree.setNo("1");
            ftree.setParentNo("0");
            ftree.Insert();

            FrmTree subFrmTree = (FrmTree)ftree.DoCreateSubNode();
            subFrmTree.setName("流程独立表单");
            subFrmTree.Update();

            subFrmTree = (FrmTree)ftree.DoCreateSubNode();
            subFrmTree.setName("常用信息管理");
            subFrmTree.Update();

            subFrmTree = (FrmTree)ftree.DoCreateSubNode();
            subFrmTree.setName("常用单据");
            subFrmTree.Update();
            

		}
		// /#endregion 装载demo.flow

		// /#region 增加图片签名
		if (isInstallFlowDemo == true) {
			try {
				// 增加图片签名
				BP.WF.DTS.GenerSiganture gs = new BP.WF.DTS.GenerSiganture();
				gs.Do();
			} catch (Exception e4) {
			}
		}
		// /#endregion 增加图片签名.

		// /#region 执行补充的sql, 让外键的字段长度都设置成100.
		DBAccess.RunSQL("UPDATE Sys_MapAttr SET maxlen=100 WHERE LGType=2 AND MaxLen<100");
		DBAccess.RunSQL("UPDATE Sys_MapAttr SET maxlen=100 WHERE KeyOfEn='FK_Dept'");


	}

	/**
	 * 检查树结构是否符合需求
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean CheckTreeRoot() throws Exception {
		// 流程树根节点校验
		String tmp = "SELECT Name FROM WF_FlowSort WHERE ParentNo='0'";
		tmp = DBAccess.RunSQLReturnString(tmp);
		if (StringHelper.isNullOrEmpty(tmp)) {
			tmp = "INSERT INTO WF_FlowSort(No,Name,ParentNo,idx) values('01','流程树',0,0)";
			DBAccess.RunSQLReturnString(tmp);
		}

		// 表单树根节点校验
		tmp = "SELECT Name FROM Sys_FormTree WHERE ParentNo = '0' ";
		tmp = DBAccess.RunSQLReturnString(tmp);
		if (StringHelper.isNullOrEmpty(tmp)) {
			tmp = "INSERT INTO Sys_FormTree(No,Name,ParentNo,Idx) values('001','表单树',0,0)";
			DBAccess.RunSQLReturnString(tmp);
		}

		// 检查组织解构是否正确.
		String sql = "SELECT * FROM Port_Dept WHERE ParentNo='0' ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0) {
			BP.Port.Dept rootDept = new BP.Port.Dept();
			rootDept.setName("总部");
			rootDept.setParentNo("0");
			try {
				rootDept.Insert();
			} catch (RuntimeException ex) {
				BP.DA.Log.DefaultLogWriteLineWarning("@尝试向port_dept插入数据失败，应该是视图问题. 技术信息:" + ex.getMessage());
			}
			throw new RuntimeException("@没有找到部门树为0个根节点, 有可能是因为您在集成cc的时候，没有遵守cc的规则，部门树的根节点必须是ParentNo=0。");
		}

		if (Glo.getOSModel() == getOSModel().OneOne) {
			try {
				BP.Port.Dept dept = new BP.Port.Dept();
				dept.Retrieve(BP.Port.DeptAttr.ParentNo, "0");
			} catch (RuntimeException ex) {
				throw new RuntimeException(
						"@cc的运行模式为OneOne @检查部门的时候错误:有可能是因为您在集成cc的时候，没有遵守cc的规则,Port_Dept列不符合要求，请仔细对比集成手册. 技术信息:"
								+ ex.getMessage());
			}
		}

		if (Glo.getOSModel() == getOSModel().OneMore) {
			try {
				// BP.GPM.Depts rootDepts = new BP.GPM.Depts("0");
			} catch (RuntimeException ex) {
				throw new RuntimeException(
						"@cc的运行模式为OneMore @检查部门的时候错误:有可能是因为您在集成cc的时候，没有遵守cc的规则,Port_Dept列不符合要求，请仔细对比集成手册. 技术信息:"
								+ ex.getMessage());
			}
		}
		return true;
	}

	public static void KillProcess(String processName) // 杀掉进程的方法
	{

		try {
			Process process = Runtime.getRuntime().exec("taskList");
			Scanner in = new Scanner(process.getInputStream());
			while (in.hasNextLine()) {
				String temp = in.nextLine();
				if (temp.toLowerCase().contains(processName.toLowerCase())) {
					String pid = temp.substring(9, temp.indexOf("Console"));
					Runtime.getRuntime().exec("tskill " + pid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 产生新的编号
	 * 
	 * @param rptKey
	 * @return
	 */
	public static String GenerFlowNo(String rptKey) {
		rptKey = rptKey.replace("ND", "");
		rptKey = rptKey.replace("Rpt", "");
		switch (rptKey.length()) {
		case 0:
			return "001";
		case 1:
			return "00" + rptKey;
		case 2:
			return "0" + rptKey;
		case 3:
			return rptKey;
		default:
			return "001";
		}
	}

	/** 
	 
	*/
	public static boolean getIsShowFlowNum() {
		if (SystemConfig.getAppSettings().get("IsShowFlowNum").toString().equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 产生word文档.
	 * 
	 * @param wk
	 */
	public static void GenerWord(Object filename, Work wk) {

	}

	// 执行安装
	// 全局的方法处理
	/**
	 * 流程数据表系统字段,中间用,分开.
	 */
	public static ArrayList<String> getFlowFields() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(GERptAttr.AtPara);
		list.add(GERptAttr.BillNo);
		list.add(GERptAttr.CFlowNo);
		list.add(GERptAttr.CWorkID);
		list.add(GERptAttr.FID);
		list.add(GERptAttr.FK_Dept);
		list.add(GERptAttr.FK_NY);
		list.add(GERptAttr.FlowDaySpan);
		list.add(GERptAttr.FlowEmps);
		list.add(GERptAttr.FlowEnder);
		list.add(GERptAttr.FlowEnderRDT);
		list.add(GERptAttr.FlowEndNode);
		list.add(GERptAttr.FlowNote);
		list.add(GERptAttr.FlowStarter);
		list.add(GERptAttr.FlowStartRDT);
		list.add(GERptAttr.GuestName);
		list.add(GERptAttr.GuestNo);
		list.add(GERptAttr.GUID);
		list.add(GERptAttr.MyNum);
		list.add(GERptAttr.OID);
		list.add(GERptAttr.PEmp);
		list.add(GERptAttr.PFlowNo);
		list.add(GERptAttr.PNodeID);
		// list.add(GERptAttr.ProjNo);
		list.add(GERptAttr.PWorkID);
		list.add(GERptAttr.Title);
		list.add(GERptAttr.WFSta);
		list.add(GERptAttr.WFState);
		list.add("Rec");
		list.add("RDT");
		 
		return list;
		// String str = "";
		// str += GERptAttr.OID + ",";
		// str += GERptAttr.AtPara + ",";
		// str += GERptAttr.BillNo + ",";
		// // str += GERptAttr.CFlowNo + ",";
		// // str += GERptAttr.CWorkID + ",";
		// str += GERptAttr.FID + ",";
		// str += GERptAttr.FK_Dept + ",";
		// str += GERptAttr.FK_NY + ",";
		// str += GERptAttr.FlowDaySpan + ",";
		// str += GERptAttr.FlowEmps + ",";
		// str += GERptAttr.FlowEnder + ",";
		// str += GERptAttr.FlowEnderRDT + ",";
		// str += GERptAttr.FlowEndNode + ",";
		// str += GERptAttr.FlowNote + ",";
		// str += GERptAttr.FlowStarter + ",";
		// str += GERptAttr.FlowStartRDT + ",";
		// str += GERptAttr.GuestName + ",";
		// str += GERptAttr.GuestNo + ",";
		// str += GERptAttr.GUID + ",";
		// str += GERptAttr.MyNum + ",";
		// str += GERptAttr.PEmp + ",";
		// str += GERptAttr.PFID + ",";
		// str += GERptAttr.PFlowNo + ",";
		// str += GERptAttr.PNodeID + ",";
		// str += GERptAttr.PrjName + ",";
		// str += GERptAttr.PrjNo + ",";
		// str += GERptAttr.PWorkID + ",";
		// str += GERptAttr.Title + ",";
		// str += GERptAttr.WFSta + ",";
		// str += GERptAttr.WFState + ",";
		// return str;
		// return typeof(GERptAttr).GetFields().Select(o =>
		// o.getName()).ToJavaList();
	}

	/**
	 * 根据文字处理抄送，与发送人
	 * 
	 * @param note
	 * @param emps
	 */
	public static void DealNote(String note, BP.Port.Emps emps) {
		note = "请综合处阅知。李江龙核示。请王薇、田晓红批示。";
		note = note.replace("阅知", "阅知@");

		note = note.replace("请", "@");
		note = note.replace("呈", "@");
		note = note.replace("报", "@");
		String[] strs = note.split("[@]", -1);

		String ccTo = "";
		String sendTo = "";
		for (String str : strs) {
			if (StringHelper.isNullOrEmpty(str)) {
				continue;
			}

			if (str.contains("阅知") == true || str.contains("阅度") == true) {
				// 抄送的.
				for (Emp emp : emps.ToJavaList()) {
					if (str.contains(emp.getNo()) == false) {
						continue;
					}
					ccTo += emp.getNo() + ",";
				}
				continue;
			}

			if (str.contains("阅处") == true || str.contains("阅办") == true) {
				// 发送送的.
				for (Emp emp : emps.ToJavaList()) {
					if (str.contains(emp.getNo()) == false) {
						continue;
					}
					sendTo += emp.getNo() + ",";
				}
				continue;
			}
		}
	}

	// 与流程事件实体相关.
	private static Hashtable Htable_FlowFEE = null;

	/**
	 * 获得节点事件实体
	 * 
	 * @param enName
	 *            实例名称
	 * @return 获得节点事件实体,如果没有就返回为空.
	 */
	public static FlowEventBase GetFlowEventEntityByEnName(String enName) {
		if (Htable_FlowFEE == null || Htable_FlowFEE.isEmpty()) {
			Htable_FlowFEE = new Hashtable();
			ArrayList<FlowEventBase> al = BP.En.ClassFactory.GetObjects("BP.WF.FlowEventBase");
			for (FlowEventBase en : al) {
				Htable_FlowFEE.put(en.toString().split("@")[0], en);
			}
		}
		FlowEventBase myen = (FlowEventBase) ((Htable_FlowFEE.get(enName) instanceof FlowEventBase)
				? Htable_FlowFEE.get(enName) : null);
		if (myen == null) {
			BP.DA.Log.DefaultLogWriteLineError("@根据类名称获取流程事件实体实例出现错误:" + enName + ",没有找到该类的实体.");
			return null;
		}
		return myen;
	}

	/**
	 * 获得事件实体String，根据编号或者流程标记
	 * 
	 * @param flowMark
	 *            流程标记s
	 * @param flowNo
	 *            流程编号
	 * @return null, 或者流程实体.
	 */
	public static String GetFlowEventEntityStringByFlowMark(String flowMark, String flowNo) {
		FlowEventBase en = GetFlowEventEntityByFlowMark(flowMark, flowNo);
		if (en == null) {
			return "";
		} else {
			return en.toString().split("@")[0];
		}
	}

	/**
	 * 获得事件实体，根据编号或者流程标记.
	 * 
	 * @param flowMark
	 *            流程标记
	 * @param flowNo
	 *            流程编号
	 * @return null, 或者流程实体.
	 */
	public static FlowEventBase GetFlowEventEntityByFlowMark(String flowMark, String flowNo) {
		if (Htable_FlowFEE == null || Htable_FlowFEE.isEmpty()) {
			Htable_FlowFEE = new Hashtable();
			ArrayList<FlowEventBase> al = BP.En.ClassFactory.GetObjects("BP.WF.FlowEventBase");
			for (FlowEventBase en : al) {
				Htable_FlowFEE.put(en.toString().split("@")[0], en);
			}
		}

		for (Object key : Htable_FlowFEE.keySet()) {
			FlowEventBase fee = (FlowEventBase) ((Htable_FlowFEE.get(key) instanceof FlowEventBase)
					? Htable_FlowFEE.get(key) : null);
			if(fee == null || fee.getFlowMark() == null)
				continue;
			if (fee.getFlowMark().equals(flowMark) || fee.getFlowMark().equals(flowNo)
					|| fee.getFlowMark().indexOf(flowNo + ",") == 0
					|| fee.getFlowMark().contains("," + flowNo + ",") == true) {
				return fee;
			}
		}
		return null;
	}

	// 与流程事件实体相关.
	/**
	 * 执行发送工作后处理的业务逻辑 用于流程发送后事件调用. 如果处理失败，就会抛出异常.
	 * 
	 * @throws Exception
	 */
	public static void DealBuinessAfterSendWork(String fk_flow, long workid, String doFunc, String WorkIDs,
			String cFlowNo, int cNodeID, String cEmp) throws Exception {

		if (doFunc.equals("SetParentFlow")) {
			// 如果需要设置子父流程信息.
			// * 应用于合并审批,当多个子流程合并审批,审批后发起一个父流程.
			//
			GenerWorkFlow gwfParent = new GenerWorkFlow(workid);

			String[] workids = WorkIDs.split("[,]", -1);
			String okworkids = ""; // 成功发送后的workids.
			GenerWorkFlow gwfSubFlow = new GenerWorkFlow();
			for (String id : workids) {
				if (StringHelper.isNullOrEmpty(id)) {
					continue;
				}

				// 把数据copy到里面,让子流程也可以得到父流程的数据。
				long workidC = Long.parseLong(id);
				gwfSubFlow.setWorkID(workidC);
				gwfSubFlow.RetrieveFromDBSources();

				// 设置当前流程的ID
				Dev2Interface.SetParentInfo(gwfSubFlow.getFK_Flow(), workidC, gwfParent.getFK_Flow(),
						gwfParent.getWorkID(), gwfParent.getFK_Node(), WebUser.getNo());

				// 是否可以执行？
				if (Dev2Interface.Flow_IsCanDoCurrentWork(workidC, WebUser.getNo()) == true) {
					// 执行向下发送.
					try {
						Dev2Interface.Node_SendWork(gwfSubFlow.getFK_Flow(), workidC);
						okworkids += workidC;
					} catch (RuntimeException ex) {

						// 如果有一个发送失败，就撤销子流程与父流程.
						// 首先把主流程撤销发送.
						Dev2Interface.Flow_DoUnSend(fk_flow, workid, 0);

						// 把已经发送成功的子流程撤销发送.
						String[] myokwokid = okworkids.split("[,]", -1);
						for (String okwokid : myokwokid) {
							if (StringHelper.isNullOrEmpty(id)) {
								continue;
							}

							// 把数据copy到里面,让子流程也可以得到父流程的数据。
							workidC = Long.parseLong(id);
							Dev2Interface.Flow_DoUnSend(cFlowNo, workidC, 0);
						}

						// 如果有一个发送失败，就撤销子流程与父流程.
						throw new RuntimeException(
								"@在执行子流程(" + gwfSubFlow.getTitle() + ")发送时出现如下错误:" + ex.getMessage());
					}
				}
			}
		}

	}

	// 全局的方法处理
	// web.config 属性.
	/**
	 * 根据配置的信息不同，从不同的表里获取人员岗位信息。
	 */
	public static String getEmpStation() {

		return "Port_DeptEmpStation";

	}

	public static String getEmpDept() {
		if (Glo.getOSModel() == OSModel.OneMore) {
			return "Port_DeptEmp";
		} else {
			return "Port_EmpDept";
		}
	}

	/**
	 * 是否admin
	 * 
	 * @throws Exception
	 */
	public static boolean getIsAdmin() throws Exception {
		String s = SystemConfig.getAppSettings().get("adminers").toString();
		if (StringHelper.isNullOrEmpty(s)) {
			s = "admin,";
		}
		return s.contains(WebUser.getNo());
	}

	/**
	 * 是否admin
	 */
	public static boolean getIsAdmin(String userNo) {
		String adminers = SystemConfig.getAppSettings().get("adminers").toString();
		if (StringHelper.isNullOrEmpty(adminers)) {
			adminers = "admin";
		}
		String[] ss = adminers.split(",");
		for (String s : ss) {
			if (s.equals(userNo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取mapdata字段查询Like。
	 * 
	 * @param flowNo
	 *            流程编号
	 * @param colName
	 *            列编号
	 */
	public static String MapDataLikeKeyV1(String flowNo, String colName) {
		flowNo = String.valueOf(Integer.parseInt(flowNo));
		String len = SystemConfig.getAppCenterDBLengthStr();
		if (flowNo.length() == 1) {
			return " " + colName + " LIKE 'ND" + flowNo + "%' AND " + len + "(" + colName + ")=5";
		}
		if (flowNo.length() == 2) {
			return " " + colName + " LIKE 'ND" + flowNo + "%' AND " + len + "(" + colName + ")=6";
		}
		if (flowNo.length() == 3) {
			return " " + colName + " LIKE 'ND" + flowNo + "%' AND " + len + "(" + colName + ")=7";
		}

		return " " + colName + " LIKE 'ND" + flowNo + "%' AND " + len + "(" + colName + ")=8";
	}

	public static String MapDataLikeKey(String flowNo, String colName) {
		flowNo = String.valueOf(Integer.parseInt(flowNo));
		String len = SystemConfig.getAppCenterDBLengthStr();

		// edited by liuxc,2016-02-22,合并逻辑，原来分流程编号的位数，现在统一处理
		return " (" + colName + " LIKE 'ND" + flowNo + "%' AND " + len + "(" + colName + ")="
				+ ((new String("ND")).length() + flowNo.length() + 2) + ") OR (" + colName + " = 'ND" + flowNo
				+ "Rpt' ) OR (" + colName + " LIKE 'ND" + flowNo + "__Dtl%' AND " + len + "(" + colName + ")>"
				+ ((new String("ND")).length() + flowNo.length() + 2 + (new String("Dtl")).length()) + ")";
	}

	/**
	 * 短信时间发送从 默认从 8 点开始.
	 */
	public static int getSMSSendTimeFromHour() {
		try {
			return Integer.parseInt(SystemConfig.getAppSettings().get("SMSSendTimeFromHour").toString());
		} catch (Exception e) {
			return 8;
		}
	}

	/**
	 * 短信时间发送到 默认到 20 点结束.
	 * 
	 */
	public static int getSMSSendTimeToHour() {
		try {
			return Integer.parseInt(SystemConfig.getAppSettings().get("SMSSendTimeToHour").toString());
		} catch (Exception e) {
			return 8;
		}
	}

	// webconfig属性.

	// 常用方法
	private static String html = "";
	private static ArrayList htmlArr = new ArrayList();
	private static String backHtml = "";
	private static long workid = 0;

	/**
	 * 模拟运行
	 * 
	 * @param flowNo
	 *            流程编号
	 * @param empNo
	 *            要执行的人员.
	 * @return 执行信息.
	 * @throws Exception
	 */
	public static String Simulation_RunOne(String flowNo, String empNo, String paras) throws Exception {
		backHtml = ""; // 需要重新赋空值
		Hashtable ht = null;
		if (StringHelper.isNullOrEmpty(paras) == false) {
			AtPara ap = new AtPara(paras);
			ht = ap.getHisHT();
		}

		Emp emp = new Emp(empNo);
		backHtml += " **** 开始使用:" + Glo.GenerUserImgSmallerHtml(emp.getNo(), emp.getName()) + "登录模拟执行工作流程";
		Dev2Interface.Port_Login(empNo);

		workid = Dev2Interface.Node_CreateBlankWork(flowNo, ht, null, emp.getNo(), null, 0, 0, null, 0, null, 0,
				null);
		SendReturnObjs objs = Dev2Interface.Node_SendWork(flowNo, workid, ht, null);
		backHtml += objs.ToMsgOfHtml().replace("@", "<br>@"); // 记录消息.

		String[] accepters = objs.getVarAcceptersID().split("[,]", -1);

		for (String acce : accepters) {
			if (StringHelper.isNullOrEmpty(acce) == true) {
				continue;
			}

			// 执行发送.
			Simulation_Run_S1(flowNo, workid, acce, ht, empNo);
			break;
		}
		// return html;
		// return htmlArr;
		return backHtml;
	}

	private static boolean isAdd = true;

	private static void Simulation_Run_S1(String flowNo, long workid, String empNo, Hashtable ht,
			String beginEmp) throws Exception {
		// htmlArr.Add(html);
		Emp emp = new Emp(empNo);
		// html = "";
		backHtml += "empNo" + beginEmp;
		backHtml += "<br> **** 让:" + Glo.GenerUserImgSmallerHtml(emp.getNo(), emp.getName()) + "执行模拟登录. ";
		// 让其登录.
		Dev2Interface.Port_Login(empNo);

		// 执行发送.
		SendReturnObjs objs = Dev2Interface.Node_SendWork(flowNo, workid, ht, null);
		backHtml += "<br>" + objs.ToMsgOfHtml().replace("@", "<br>@");

		if (objs.getVarAcceptersID() == null) {
			isAdd = false;
			backHtml += " <br> **** 流程结束,查看<a href='/WF/WFRpt.jsp?WorkID=" + workid + "&FK_Flow=" + flowNo
					+ "' target=_blank >流程轨迹</a> ====";
			// htmlArr.Add(html);
			// backHtml += "nextEmpNo";
			return;
		}

		if (StringHelper.isNullOrEmpty(objs.getVarAcceptersID())) // 此处添加为空判断，跳过下面方法的执行，否则出错。
		{
			return;
		}
		String[] accepters = objs.getVarAcceptersID().split("[,]", -1);

		for (String acce : accepters) {
			if (StringHelper.isNullOrEmpty(acce) == true) {
				continue;
			}

			// 执行发送.
			Simulation_Run_S1(flowNo, workid, acce, ht, beginEmp);
			break; // 就不让其执行了.
		}
	}

	/**
	 * 产生单据编号
	 * 
	 * @param billNo
	 * @param workid
	 * @param en
	 * @return
	 * @throws Exception
	 */
	public static String GenerBillNo(String billNo, long workid, Entity en, String flowPTable) throws Exception {

		return WorkFlowBuessRole.GenerBillNo(billNo, workid, en, flowPTable);
	}

	/**
	 * 是否手机访问?
	 * 
	 * @return
	 */
	public static boolean IsMobile() {
		if (SystemConfig.getIsBSsystem() == false) {
			return false;
		}
		String agent = BP.Sys.Glo.getRequest().getHeader("UserAgent").toLowerCase().trim();
		if (agent.equals("") || agent.indexOf("mozilla") != -1 || agent.indexOf("opera") != -1) {
			return false;
		}
		return true;
	}

	/**
	 * 是否启用草稿
	 */
	public static boolean getIsEnableDraft() {
		return SystemConfig.GetValByKeyBoolen("IsEnableDraft", false);
	}

	/**
	 * 加入track
	 * 
	 * @param at
	 *            事件类型
	 * @param flowNo
	 *            流程编号
	 * @param workID
	 *            工作ID
	 * @param fid
	 *            流程ID
	 * @param fromNodeID
	 *            从节点编号
	 * @param fromNodeName
	 *            从节点名称
	 * @param fromEmpID
	 *            从人员ID
	 * @param fromEmpName
	 *            从人员名称
	 * @param toNodeID
	 *            到节点编号
	 * @param toNodeName
	 *            到节点名称
	 * @param toEmpID
	 *            到人员ID
	 * @param toEmpName
	 *            到人员名称
	 * @param note
	 *            消息
	 * @param tag
	 *            参数用@分开
	 * @throws Exception
	 */
	public static String AddToTrack(ActionType at, String flowNo, long workID, long fid, int fromNodeID,
			String fromNodeName, String fromEmpID, String fromEmpName, int toNodeID, String toNodeName, String toEmpID,
			String toEmpName, String note, String tag) throws Exception {
		if (toNodeID == 0) {
			toNodeID = fromNodeID;
			toNodeName = fromNodeName;
		}

		Track t = new Track();
		t.setWorkID(workID);
		t.setFID(fid);
		t.setRDT(DataType.getCurrentDataTimess());
		t.setHisActionType(at);

		t.setNDFrom(fromNodeID);
		t.setNDFromT(fromNodeName);

		t.setEmpFrom(fromEmpID);
		t.setEmpFromT(fromEmpName);
		t.FK_Flow = flowNo;

		t.setNDTo(toNodeID);
		t.setNDToT(toNodeName);

		t.setEmpTo(toEmpID);
		t.setEmpToT(toEmpName);
		t.setMsg(note);

		// 参数.
		if (tag != null) {
			t.setTag(tag);
		}

		try {
			t.Insert();
		} catch (Exception e) {
			t.CheckPhysicsTable();
			t.Insert();
		}
		return t.getMyPK();
	}

	/**
	 * 计算表达式是否通过(或者是否正确.)
	 * 
	 * @param exp
	 *            表达式
	 * @param en
	 *            实体
	 * @return true/false
	 * @throws Exception
	 */
	public static boolean ExeExp(String exp, Entity en) throws Exception {
		exp = exp.replace("WebUser.No", WebUser.getNo());
		exp = exp.replace("@WebUser.Name", WebUser.getName());
		exp = exp.replace("@WebUser.FK_DeptNameOfFull", WebUser.getFK_DeptNameOfFull());
		exp = exp.replace("@WebUser.FK_DeptName", WebUser.getFK_DeptName());
		exp = exp.replace("@WebUser.FK_Dept", WebUser.getFK_Dept());

		String[] strs = exp.split("[ ]", -1);
		boolean isPass = false;

		String key = strs[0].trim();
		String oper = strs[1].trim();
		String val = strs[2].trim();
		val = val.replace("'", "");
		val = val.replace("%", "");
		val = val.replace("~", "");
		Row row = en.getRow();
		for (String item : row.keySet()) {
			if (!item.trim().equals(key)) {
				continue;
			}

			String valPara = row.get(key).toString();
			if (oper.equals("=")) {
				if (val.equals(valPara)) {
					return true;
				}
			}

			if (oper.toUpperCase().equals("LIKE")) {
				if (valPara.contains(val)) {
					return true;
				}
			}

			if (oper.equals(">")) {
				if (Float.parseFloat(valPara) > Float.parseFloat(val)) {
					return true;
				}
			}
			if (oper.equals(">=")) {
				if (Float.parseFloat(valPara) >= Float.parseFloat(val)) {
					return true;
				}
			}
			if (oper.equals("<")) {
				if (Float.parseFloat(valPara) < Float.parseFloat(val)) {
					return true;
				}
			}
			if (oper.equals("<=")) {
				if (Float.parseFloat(valPara) <= Float.parseFloat(val)) {
					return true;
				}
			}

			if (oper.equals("!=")) {
				if (Float.parseFloat(valPara) != Float.parseFloat(val)) {
					return true;
				}
			}

			throw new RuntimeException("@参数格式错误:" + exp + " Key=" + key + " oper=" + oper + " Val=" + val);
		}

		return false;
	}

	/**
	 * 装载填充
	 * 
	 * @param en
	 * @param item
	 * @param mattrs
	 * @param dtls
	 * @return
	 * @throws Exception
	 */
	public static Entity DealPageLoadFull(Entity en, MapExt item, MapAttrs mattrs, MapDtls dtls) throws Exception {
		return DealPageLoadFull(en, item, mattrs, dtls, false, 0, 0);
	}

	/**
	 * 执行PageLoad装载数据
	 * 
	 * @param item
	 * @param en
	 * @param mattrs
	 * @param dtls
	 * @return
	 * @throws Exception
	 */
	public static Entity DealPageLoadFull(Entity en, MapExt item, MapAttrs mattrs, MapDtls dtls, boolean isSelf,
			int nodeID, long workID) throws Exception {
		if (item == null) {
			return en;
		}

		DataTable dt = null;
		String sql = item.getTag();
		if (StringHelper.isNullOrEmpty(sql) == false) {
			// 如果有填充主表的sql
			sql = Glo.DealExp(sql, en, null);

			if (StringHelper.isNullOrEmpty(sql) == false) {
				if (sql.contains("@")) {
					throw new RuntimeException("设置的sql有错误可能有没有替换的变量:" + sql);
				}
				dt = DBAccess.RunSQLReturnTable(sql);
				Attrs attrs = en.getEnMap().getAttrs();
				if (dt.Rows.size() == 1) {
					DataRow dr = dt.Rows.get(0);
					for (DataColumn dc : dt.Columns) {
						// 去掉一些不需要copy的字段.
						String columnName = dc.ColumnName;
						if (attrs.Contains(columnName) == false)
							continue;
						if (columnName.equals(WorkAttr.OID) || columnName.equals(WorkAttr.FID)
								|| columnName.equals(WorkAttr.Rec) || columnName.equals(WorkAttr.MD5)
								|| columnName.equals(WorkAttr.MyNum) || columnName.equals(WorkAttr.RDT)
								|| columnName.equals("RefPK") || columnName.equals(WorkAttr.RecText)) {
							continue;
						}

						if (DataType.IsNullOrEmpty(en.GetValStringByKey(dc.ColumnName))) {
							en.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
						} else if (en.GetValStringByKey(dc.ColumnName).equals("0")
								|| en.GetValStringByKey(dc.ColumnName).equals("0.0")) {
							en.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
						}
					}
				}
			}
		}

		if (StringHelper.isNullOrEmpty(item.getTag1()) || item.getTag1().length() < 15) {
			return en;
		}

		// 填充从表.
		for (MapDtl dtl : dtls.ToJavaList()) {
			String[] sqls = item.getTag1().split("[*]", -1);
			for (String mysql : sqls) {
				if (StringHelper.isNullOrEmpty(mysql)) {
					continue;
				}
				if (mysql.contains(dtl.getNo() + "=") == false) {
					continue;
				}
				if (mysql.equals(dtl.getNo() + "=") == true) {
					continue;
				}

				// 处理sql.
				sql = Glo.DealExp(mysql, en, null);

				if (StringHelper.isNullOrEmpty(sql)) {
					continue;
				}

				if (sql.contains("@")) {
					throw new RuntimeException("设置的sql有错误可能有没有替换的变量:" + sql);
				}
				if (isSelf == true) {
					MapDtl mdtlSln = new MapDtl();
					mdtlSln.setNo(dtl.getNo() + "_" + nodeID);
					int result = mdtlSln.RetrieveFromDBSources();
					if (result != 0) {
						// dtl.No = mdtlSln.No;
						dtl.setDtlOpenType(mdtlSln.getDtlOpenType());
					}
				}

				GEDtls gedtls = null;

				try {
					gedtls = new GEDtls(dtl.getNo());
					if (dtl.getDtlOpenType() == DtlOpenType.ForFID) {
						if (gedtls.RetrieveByAttr(GEDtlAttr.RefPK, workID) > 0)
							continue;
					} else {
						if (gedtls.RetrieveByAttr(GEDtlAttr.RefPK, en.getPKVal()) > 0)
							continue;
					}
				} catch (RuntimeException ex) {
					((GEDtl) ((gedtls.getGetNewEntity() instanceof GEDtl) ? gedtls.getGetNewEntity() : null))
							.CheckPhysicsTable();
				}

				dt = DBAccess.RunSQLReturnTable(
						sql.startsWith(dtl.getNo() + "=") ? sql.substring((dtl.getNo() + "=").length()) : sql);
				for (DataRow dr : dt.Rows) {
					GEDtl gedtl = (GEDtl) ((gedtls.getGetNewEntity() instanceof GEDtl) ? gedtls.getGetNewEntity()
							: null);
					for (DataColumn dc : dt.Columns) {
						gedtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
					}

					switch (dtl.getDtlOpenType()) {
					case ForEmp: // 按人员来控制.
						gedtl.setRefPK(en.getPKVal().toString());
						gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
						break;
					case ForWorkID: // 按工作ID来控制
						gedtl.setRefPK(en.getPKVal().toString());
						gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
						break;
					case ForFID: // 按流程ID来控制.
						gedtl.setRefPK(String.valueOf(workID));
						gedtl.setFID(Long.parseLong(en.getPKVal().toString()));
						break;
					}
					gedtl.setRDT(DataType.getCurrentDataTime());
					gedtl.setRec(WebUser.getNo());
					gedtl.Insert();
				}
			}
		}
		return en;
	}
	public static boolean CondExpSQL(String sqlExp, Hashtable ht) throws Exception{
		return CondExpSQL(sqlExp, ht,0);
	}
	/// <summary>
	/// SQL表达式是否正确
	/// </summary>
	/// <param name="sqlExp"></param>
	/// <param name="ht"></param>
	/// <returns></returns>
	public static boolean CondExpSQL(String sqlExp, Hashtable ht,long myWorkID) throws Exception {
		String sql = sqlExp;
		sql = sql.replace("~", "'");
		sql = sql.replace("@WebUser.No", WebUser.getNo());
		sql = sql.replace("@WebUser.Name", WebUser.getName());
		sql = sql.replace("@WebUser.FK_Dept", WebUser.getFK_Dept());
		// 使用 Enumeration 遍历 HashTable
		@SuppressWarnings("rawtypes")
		Enumeration e = ht.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			if (key.toString() == "OID") {
				sql = sql.replace("@WorkID", ht.get("OID").toString());
				sql = sql.replace("@OID", ht.get("OID").toString());
				continue;
			}
			sql = sql.replace("@" + key, ht.get(key).toString());
		}
		
		 //从工作流参数里面替换 
        if (sql.contains("@") == true && myWorkID != 0)
        {
            GenerWorkFlow gwf = new GenerWorkFlow(myWorkID);
            AtPara ap = gwf.getatPara();
            for(String str : ap.getHisHT().keySet())
            {
                sql = sql.replace("@" + str, ap.GetValStrByKey(str));
            }
        }


		int result = DBAccess.RunSQLReturnValInt(sql, -1);
		if (result <= 0)
			return false;

		return true;
	}

	/**
	 * 判断表达式是否成立
	 * 
	 * @param exp
	 * @param ht
	 * @return
	 * @throws Exception
	 */
	public static boolean CondExpPara(String exp, Hashtable ht) throws Exception {
		return CondExpPara(exp, ht, 0);
	}

	/// <summary>
	/// 判断表达式是否成立
	/// </summary>
	/// <param name="exp">表达式</param>
	/// <param name="en">变量</param>
	/// <returns>是否成立</returns>
	public static boolean CondExpPara(String exp, Hashtable ht, long myWorkID) throws Exception {
		String[] strs = exp.trim().split(" ");

		String key = strs[0].trim();
		String oper = strs[1].trim();
		String val = strs[2].trim();
		val = val.replace("'", "");
		val = val.replace("%", "");
		val = val.replace("~", "");

		String valPara = null;
		if (ht.containsKey(key) == false) {
			/* 如果不包含指定的关键的key, 就到公共变量里去找. */
			boolean isHave = false;
			if (myWorkID != 0) {
				// 把外部传来的参数传入到 rptGE 让其做方向条件的判断.
				GenerWorkFlow gwf = new GenerWorkFlow(myWorkID);
				AtPara at = gwf.getatPara();
				for (String str : at.getHisHT().keySet()) {
					if (key.equals(str) == false)
						continue;

					valPara = at.GetValStrByKey(key);
					isHave = true;
					break;
				}
			}
			if (isHave == false) {
				if (Glo.getSendHTOfTemp().containsKey(key) == false) {
					try {
						throw new Exception("@判断条件时错误,请确认参数是否拼写错误,没有找到对应的表达式:" + exp + " Key=(" + key + ") oper=("
								+ oper + ")Val=(" + val + ")");
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 有可能是常量.
					valPara = key;
				}else
				valPara = Glo.getSendHTOfTemp().get(key).toString().trim();
			}

		} else {
			valPara = ht.get(key).toString().trim();
		}

		// 开始执行判断
		if (oper.equals("=")) {
			if (valPara == val)
				return true;
			else
				return false;
		}

		if (oper.toUpperCase().equals("LIKE")) {
			if (valPara.contains(val))
				return true;
			else
				return false;
		}
		
		if (DataType.IsNumStr(valPara) == false)
            throw new Exception("err@表达式错误:["+exp+"]没有找到参数["+valPara+"]的值，导致无法计算。");

		if (oper.equals(">")) {
			if (Float.parseFloat(valPara) > Float.parseFloat(val))
				return true;
			else
				return false;
		}
		if (oper.equals(">=")) {
			if (Float.parseFloat(valPara) >= Float.parseFloat(val))
				return true;
			else
				return false;
		}
		if (oper.equals("<")) {
			if (Float.parseFloat(valPara) < Float.parseFloat(val))
				return true;
			else
				return false;
		}
		if (oper.equals("<=")) {
			if (Float.parseFloat(valPara) <= Float.parseFloat(val))
				return true;
			else
				return false;
		}

		if (oper.equals("!=")) {
			if (Float.parseFloat(valPara) != Float.parseFloat(val))
				return true;
			else
				return false;
		}
		try {
			throw new Exception("@参数格式错误:" + exp + " Key=" + key + " oper=" + oper + " Val=" + val);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 执行判断结束.
		return false;
	}
	
	 /// <summary>
    /// 表达式替换/枚举下拉框替换成文本值
    /// </summary>
    /// <param name="exp"></param>
    /// <param name="en"></param>
    /// <returns></returns>
    public static String DealExp(String exp, Entity en) throws Exception
    {
        //替换字符
        exp = exp.replace("~", "'");

        if (exp.contains("@") == false)
            return exp;

        //首先替换加; 的。
        // 首先替换加; 的。
 		exp = exp.replace("@WebUser.No;", WebUser.getNo());
 		exp = exp.replace("@WebUser.Name;", WebUser.getName());
 		exp = exp.replace("@WebUser.FK_Dept;", WebUser.getFK_Dept());
 		exp = exp.replace("@WebUser.FK_DeptName;", WebUser.getFK_DeptName());

 		// 替换没有 ; 的 .
 		exp = exp.replace("@WebUser.No", WebUser.getNo());
 		exp = exp.replace("@WebUser.Name", WebUser.getName());
 		exp = exp.replace("@WebUser.FK_DeptName", WebUser.getFK_DeptName());
 		exp = exp.replace("@WebUser.FK_Dept", WebUser.getFK_Dept());

        if (exp.contains("@") == false)
            return exp;

        //增加对新规则的支持. @MyField; 格式.
        if (en != null)
        {
            Attrs attrs = en.getEnMap().getAttrs();
            Row row = en.getRow();
            //特殊判断.
            if (row.containsKey("OID") == true)
            	exp = exp.replaceAll("@WorkID", row.GetValByKey("OID").toString());

            if (exp.contains("@") == false)
                return exp;

            for (String key : row.keySet()) {
    			if (row.get(key) == null || row.get(key).toString().equals("") == true)
    				continue;

                if (exp.contains("@" + key))
                {
                    Attr attr = attrs.GetAttrByKey(key);
                    //是枚举或者外键替换成文本
                    if (attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum
                        || attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK)
                    {
                        exp = exp.replace("@" + key, row.get(key+"Text").toString());
                    }
                    else
                    {
                        if(attr.getMyDataType() == DataType.AppString  && attr.getUIContralType() ==  UIContralType.DDL && attr.getMyFieldType() ==FieldType.Normal)
                             exp = exp.replace("@" + key, row.get(key+"T").toString());
                        else
                            exp = exp.replace("@" + key, row.get(key).toString());
;
                    }

                    
                }

                //不包含@则返回SQL语句
                if (exp.contains("@") == false)
                    return exp;
            }

        }
      
        Enumeration enu = ContextHolderUtils.getRequest().getParameterNames();
		while (enu.hasMoreElements()) {

			String key = (String) enu.nextElement();
			if (exp.contains(key) == false)
				continue;

			exp = exp.replaceAll("@" + key, ContextHolderUtils.getRequest().getParameter(key));
		}

		return exp;
    }

	/**
	 * 处理表达式
	 * 
	 * @param exp
	 *            表达式
	 * @param en
	 *            数据源
	 * @param errInfo
	 *            错误
	 * @return
	 * @throws Exception
	 */
	public static String DealExp(String exp, Entity en, String errInfo) throws Exception {

		exp = exp.replace("~", "'");

		if (exp.contains("@") == false) {
			return exp;
		}

		// 首先替换加; 的。
		exp = exp.replace("@WebUser.No;", WebUser.getNo());
		exp = exp.replace("@WebUser.Name;", WebUser.getName());
		exp = exp.replace("@WebUser.FK_Dept;", WebUser.getFK_Dept());
		exp = exp.replace("@WebUser.FK_DeptName;", WebUser.getFK_DeptName());

		// 替换没有 ; 的 .
		exp = exp.replace("@WebUser.No", WebUser.getNo());
		exp = exp.replace("@WebUser.Name", WebUser.getName());
		exp = exp.replace("@WebUser.FK_DeptName", WebUser.getFK_DeptName());
		exp = exp.replace("@WebUser.FK_Dept", WebUser.getFK_Dept());

		if (exp.contains("@") == false)
			return exp;
		
		if(en!=null){
			// 增加对新规则的支持. @MyField; 格式.
			Row row = en.getRow();
	
			// 特殊判断.
			if (row.containsKey("OID") == true){
				if(row.GetValByKey("OID").toString().equals("0") == true){
					if (row.containsKey("WorkID") == true)
						exp = exp.replaceAll("@WorkID", row.GetValByKey("WorkID").toString());
				}else
					exp = exp.replaceAll("@WorkID", row.GetValByKey("OID").toString());
			}

	
			if (exp.contains("@") == false)
				return exp;
	
			for (String key : row.keySet()) {
				if (row.get(key) == null || row.get(key).toString().equals("") == true)
					continue;
				if (exp.contains("@" + key + ";"))
					exp = exp.replace("@" + key + ";", row.get(key).toString());
			}
	
			if (exp.contains("@") == false)
				return exp;
	
			// 解决排序问题.
			Attrs attrs = en.getEnMap().getAttrs();
			String mystrs = "";
			for (Attr attr : attrs) {
				if (attr.getMyDataType() == DataType.AppString) {
					mystrs += "@" + attr.getKey() + ",";
				} else {
					mystrs += "@" + attr.getKey();
				}
			}
			String[] strs = mystrs.split("[@]", -1);
			DataTable dt = new DataTable();
			dt.Columns.Add(new DataColumn("No", String.class));
			for (String str : strs) {
				if (StringHelper.isNullOrEmpty(str)) {
					continue;
				}
	
				DataRow dr = dt.NewRow();
				dr.setValue(0, str);
				dt.Rows.add(dr);
			}
	
			// 解决排序问题.
			// 替换变量.
			for (DataRow dr : dt.Rows) {
				String key = dr.getValue(0).toString();
				boolean isStr = key.contains(",");
				if (isStr == true)
					key = key.replace(",", "");
				if (DataType.IsNullOrEmpty(en.GetValStrByKey(key)) == true)
					continue;
				exp = exp.replace("@" + key, en.GetValStrByKey(key));
	
			}
		}
		// 处理Para的替换.
		if (exp.contains("@") && Glo.getSendHTOfTemp() != null) {
			for (Object key : Glo.getSendHTOfTemp().keySet()) {
				exp = exp.replace("@" + key, Glo.getSendHTOfTemp().get(key).toString());
			}
		}

		Enumeration enu = ContextHolderUtils.getRequest().getParameterNames();
		while (enu.hasMoreElements()) {

			String key = (String) enu.nextElement();
			if (exp.contains(key) == false)
				continue;

			exp = exp.replaceAll("@" + key, ContextHolderUtils.getRequest().getParameter(key));
		}

		return exp;
	}

	/**
	 * 加密MD5
	 * 
	 * @param s
	 * @return
	 */
	public static String GenerMD5(Work wk) {
		String s = null;
		for (Attr attr : wk.getEnMap().getAttrs()) {
			String key = attr.getKey();
			if (key.equals(WorkAttr.MD5) || key.equals(WorkAttr.RDT) || key.equals(WorkAttr.CDT)
					|| key.equals(WorkAttr.Rec) || key.equals(StartWorkAttr.Title) || key.equals(StartWorkAttr.Emps)
					|| key.equals(StartWorkAttr.FK_Dept) || key.equals(StartWorkAttr.PRI)
					|| key.equals(StartWorkAttr.FID)) {
				continue;
			}

			String obj = (String) ((attr.getDefaultVal() instanceof String) ? attr.getDefaultVal() : null);
			// if (obj == null)
			// continue;
			if (obj != null && obj.contains("@")) {
				continue;
			}

			s += wk.GetValStrByKey(attr.getKey());
		}
		s += "ccflow";
		// return
		// System.Web.Security.FormsAuthentication.HashPasswordForStoringInConfigFile(s,
		// "MD5").toLowerCase();
		return DigestUtils.md5Hex(s).toLowerCase();
	}

	/**
	 * 装载流程数据
	 * 
	 * @param xlsFile
	 * @throws Exception
	 */
	public static String LoadFlowDataWithToSpecNode(String xlsFile) throws Exception {
		DataTable dt = BP.DA.DBLoad.GetTableByExt(xlsFile);
		String err = "";
		String info = "";

		for (DataRow dr : dt.Rows) {
			String flowPK = dr.getValue("FlowPK").toString();
			String starter = dr.getValue("Starter").toString();
			String executer = dr.getValue("Executer").toString();
			int toNode = Integer.parseInt(dr.getValue("ToNodeID").toString().replace("ND", ""));
			Node nd = new Node();
			nd.setNodeID(toNode);
			if (nd.RetrieveFromDBSources() == 0) {
				err += "节点ID错误:" + toNode;
				continue;
			}
			String sql = "SELECT count(*) as Num FROM ND" + Integer.parseInt(nd.getFK_Flow()) + "01 WHERE FlowPK='"
					+ flowPK + "'";
			int i = DBAccess.RunSQLReturnValInt(sql);
			if (i == 1) {
				continue; // 此数据已经调度了。
			}

			// /#region 检查数据是否完整。
			Emp emp = new Emp();
			emp.setNo(executer);
			if (emp.RetrieveFromDBSources() == 0) {
				err += "@账号:" + starter + ",不存在。";
				continue;
			}
			if (StringHelper.isNullOrEmpty(emp.getFK_Dept())) {
				err += "@账号:" + starter + ",没有部门。";
				continue;
			}

			emp.setNo(starter);
			if (emp.RetrieveFromDBSources() == 0) {
				err += "@账号:" + executer + ",不存在。";
				continue;
			}
			if (StringHelper.isNullOrEmpty(emp.getFK_Dept())) {
				err += "@账号:" + executer + ",没有部门。";
				continue;
			}
			// /#endregion 检查数据是否完整。

			WebUser.SignInOfGener(emp);
			Flow fl = nd.getHisFlow();
			Work wk = fl.NewWork();

			Attrs attrs = wk.getEnMap().getAttrs();
			// foreach (Attr attr in wk.getEnMap().getAttrs())
			// {
			// }

			for (DataColumn dc : dt.Columns) {
				Attr attr = attrs.GetAttrByKey(dc.ColumnName.trim());
				if (attr == null) {
					continue;
				}

				String val = dr.getValue(dc.ColumnName).toString().trim();
				switch (attr.getMyDataType()) {
				case DataType.AppString:
				case DataType.AppDate:
				case DataType.AppDateTime:
					wk.SetValByKey(attr.getKey(), val);
					break;
				case DataType.AppInt:
				case DataType.AppBoolean:
					wk.SetValByKey(attr.getKey(), Integer.parseInt(val));
					break;
				case DataType.AppMoney:
				case DataType.AppDouble:
				case DataType.AppRate:
				case DataType.AppFloat:
					wk.SetValByKey(attr.getKey(), Float.parseFloat(val));
					break;
				default:
					wk.SetValByKey(attr.getKey(), val);
					break;
				}
			}

			wk.SetValByKey(WorkAttr.Rec, WebUser.getNo());
			wk.SetValByKey(StartWorkAttr.FK_Dept, WebUser.getFK_Dept());
			wk.SetValByKey("FK_NY", DataType.getCurrentYearMonth());
			wk.SetValByKey(WorkAttr.MyNum, 1);
			wk.Update();

			Node ndStart = nd.getHisFlow().getHisStartNode();
			WorkNode wn = new WorkNode(wk, ndStart);
			try {
				info += "<hr>" + wn.NodeSend(nd, executer).ToMsgOfHtml();
			} catch (RuntimeException ex) {
				err += "<hr>" + ex.getMessage();
				WorkFlow wf = new WorkFlow(fl, wk.getOID());
				wf.DoDeleteWorkFlowByReal(true);
				continue;
			}

			// /#region 更新 下一个节点数据。
			Work wkNext = nd.getHisWork();
			wkNext.setOID(wk.getOID());
			wkNext.RetrieveFromDBSources();
			attrs = wkNext.getEnMap().getAttrs();
			for (DataColumn dc : dt.Columns) {
				Attr attr = attrs.GetAttrByKey(dc.ColumnName.trim());
				if (attr == null) {
					continue;
				}

				String val = dr.getValue(dc.ColumnName).toString().trim();
				switch (attr.getMyDataType()) {
				case DataType.AppString:
				case DataType.AppDate:
				case DataType.AppDateTime:
					wkNext.SetValByKey(attr.getKey(), val);
					break;
				case DataType.AppInt:
				case DataType.AppBoolean:
					wkNext.SetValByKey(attr.getKey(), Integer.parseInt(val));
					break;
				case DataType.AppMoney:
				case DataType.AppDouble:
				case DataType.AppRate:
				case DataType.AppFloat:
					wkNext.SetValByKey(attr.getKey(), Float.parseFloat(val));
					break;
				default:
					wkNext.SetValByKey(attr.getKey(), val);
					break;
				}
			}

			wkNext.DirectUpdate();

			GERpt rtp = fl.getHisGERpt();
			rtp.SetValByKey("OID", wkNext.getOID());
			rtp.RetrieveFromDBSources();
			rtp.Copy(wkNext);
			rtp.DirectUpdate();

			// /#endregion 更新 下一个节点数据。
		}
		return info + err;
	}

	public static String LoadFlowDataWithToSpecEndNode(String xlsFile) throws Exception {
		DataTable dt = BP.DA.DBLoad.GetTableByExt(xlsFile);
		DataSet ds = new DataSet();
		ds.Tables.add(dt);
		ds.WriteXml("C:\\已完成.xml");

		String err = "";
		String info = "";
		int idx = 0;
		for (DataRow dr : dt.Rows) {
			String flowPK = dr.getValue("FlowPK").toString().trim();
			if (StringHelper.isNullOrEmpty(flowPK)) {
				continue;
			}

			String starter = dr.getValue("Starter").toString();
			String executer = dr.getValue("Executer").toString();
			int toNode = Integer.parseInt(dr.getValue("ToNodeID").toString().replace("ND", ""));
			Node ndOfEnd = new Node();
			ndOfEnd.setNodeID(toNode);
			if (ndOfEnd.RetrieveFromDBSources() == 0) {
				err += "节点ID错误:" + toNode;
				continue;
			}

			if (ndOfEnd.getIsEndNode() == false) {
				err += "节点ID错误:" + toNode + ", 非结束节点。";
				continue;
			}

			String sql = "SELECT count(*) as Num FROM ND" + Integer.parseInt(ndOfEnd.getFK_Flow()) + "01 WHERE FlowPK='"
					+ flowPK + "'";
			int i = DBAccess.RunSQLReturnValInt(sql);
			if (i == 1) {
				continue; // 此数据已经调度了。
			}

			// /#region 检查数据是否完整。
			// 发起人发起。
			Emp emp = new Emp();
			emp.setNo(executer);
			if (emp.RetrieveFromDBSources() == 0) {
				err += "@账号:" + starter + ",不存在。";
				continue;
			}

			if (StringHelper.isNullOrEmpty(emp.getFK_Dept())) {
				err += "@账号:" + starter + ",没有设置部门。";
				continue;
			}

			emp = new Emp();
			emp.setNo(starter);
			if (emp.RetrieveFromDBSources() == 0) {
				err += "@账号:" + starter + ",不存在。";
				continue;
			} else {
				emp.RetrieveFromDBSources();
				if (StringHelper.isNullOrEmpty(emp.getFK_Dept())) {
					err += "@账号:" + starter + ",没有设置部门。";
					continue;
				}
			}
			// /#endregion 检查数据是否完整。

			WebUser.SignInOfGener(emp);
			Flow fl = ndOfEnd.getHisFlow();
			Work wk = fl.NewWork();
			for (DataColumn dc : dt.Columns) {
				wk.SetValByKey(dc.ColumnName.trim(), dr.getValue(dc.ColumnName).toString().trim());
			}

			wk.SetValByKey(WorkAttr.Rec, WebUser.getNo());
			wk.SetValByKey(StartWorkAttr.FK_Dept, WebUser.getFK_Dept());
			wk.SetValByKey("FK_NY", DataType.getCurrentYearMonth());
			wk.SetValByKey(WorkAttr.MyNum, 1);
			wk.Update();

			Node ndStart = fl.getHisStartNode();
			WorkNode wn = new WorkNode(wk, ndStart);
			try {
				info += "<hr>" + wn.NodeSend(ndOfEnd, executer).ToMsgOfHtml();
			} catch (RuntimeException ex) {
				err += "<hr>启动错误:" + ex.getMessage();
				DBAccess.RunSQL(
						"DELETE FROM ND" + Integer.parseInt(ndOfEnd.getFK_Flow()) + "01 WHERE FlowPK='" + flowPK + "'");
				WorkFlow wf = new WorkFlow(fl, wk.getOID());
				wf.DoDeleteWorkFlowByReal(true);
				continue;
			}

			// 结束点结束。
			emp = new Emp(executer);
			WebUser.SignInOfGener(emp);

			Work wkEnd = ndOfEnd.GetWork(wk.getOID());
			for (DataColumn dc : dt.Columns) {
				wkEnd.SetValByKey(dc.ColumnName.trim(), dr.getValue(dc.ColumnName).toString().trim());
			}

			wkEnd.SetValByKey(WorkAttr.Rec, WebUser.getNo());
			wkEnd.SetValByKey(StartWorkAttr.FK_Dept, WebUser.getFK_Dept());
			wkEnd.SetValByKey("FK_NY", DataType.getCurrentYearMonth());
			wkEnd.SetValByKey(WorkAttr.MyNum, 1);
			wkEnd.Update();

			try {
				WorkNode wnEnd = new WorkNode(wkEnd, ndOfEnd);
				// wnEnd.AfterNodeSave();
				info += "<hr>" + wnEnd.NodeSend().ToMsgOfHtml();
			} catch (RuntimeException ex) {
				err += "<hr>结束错误(系统直接删除它):" + ex.getMessage();
				WorkFlow wf = new WorkFlow(fl, wk.getOID());
				wf.DoDeleteWorkFlowByReal(true);
				continue;
			}
		}
		return info + err;
	}

	/**
	 * 判断是否登陆当前UserNo
	 * 
	 * @param userNo
	 * @throws Exception
	 */
	public static void IsSingleUser(String userNo) throws Exception {
		if (StringHelper.isNullOrEmpty(WebUser.getNo()) || !userNo.equals(WebUser.getNo())) {
			if (!StringHelper.isNullOrEmpty(userNo)) {
				Dev2Interface.Port_Login(userNo);
			}
		}
	}
	// public static void ResetFlowView()
	// {
	// string sql = "DROP VIEW V_WF_Data ";
	// try
	// {
	// BP.DA.DBAccess.RunSQL(sql);
	// }
	// catch
	// {
	// }

	// Flows fls = new Flows();
	// fls.RetrieveAll();
	// sql = "CREATE VIEW V_WF_Data AS ";
	// foreach (Flow fl in fls)
	// {
	// fl.CheckRpt();
	// sql += "\t\n SELECT '" + fl.No + "' as FK_Flow, '" + fl.Name + "' AS
	// FlowName, '" + fl.FK_FlowSort + "' as
	// FK_FlowSort,CDT,Emps,FID,FK_Dept,FK_NY,";
	// sql += "MyNum,OID,RDT,Rec,Title,WFState,FlowEmps,";
	// sql += "FlowStarter,FlowStartRDT,FlowEnder,FlowEnderRDT,FlowDaySpan FROM
	// ND" + int.Parse(fl.No) + "Rpt";
	// sql += "\t\n UNION";
	// }
	// sql = sql.substing(0, sql.Length - 6);
	// sql += "\t\n GO";
	// BP.DA.DBAccess.RunSQL(sql);
	// }
	public static void Rtf2PDF(Object pathOfRtf, Object pathOfPDF) {
		// Object Nothing = System.Reflection.Missing.Value;
		// //创建一个名为WordApp的组件对象
		// Microsoft.Office.Interop.Word.Application wordApp =
		// new Microsoft.Office.Interop.Word.ApplicationClass();
		// //创建一个名为WordDoc的文档对象并打开
		// Microsoft.Office.Interop.Word.Document doc =
		// wordApp.Documents.Open(ref pathOfRtf, ref Nothing, ref Nothing, ref
		// Nothing, ref Nothing,
		// ref Nothing, ref Nothing, ref Nothing, ref Nothing, ref Nothing,
		// ref Nothing, ref Nothing, ref Nothing, ref Nothing, ref Nothing, ref
		// Nothing);

		// //设置保存的格式
		// object filefarmat =
		// Microsoft.Office.Interop.Word.WdSaveFormat.wdFormatPDF;

		// //保存为PDF
		// doc.SaveAs(ref pathOfPDF, ref filefarmat, ref Nothing, ref Nothing,
		// ref Nothing, ref Nothing,
		// ref Nothing, ref Nothing, ref Nothing, ref Nothing, ref Nothing, ref
		// Nothing, ref Nothing,
		// ref Nothing, ref Nothing, ref Nothing);
		// //关闭文档对象
		// doc.Close(ref Nothing, ref Nothing, ref Nothing);
		// //推出组建
		// wordApp.Quit(ref Nothing, ref Nothing, ref Nothing);
		// GC.Collect();
	}

	// 常用方法

	// 属性
	/**
	 * 消息
	 * 
	 * @throws Exception
	 */
	public static String getSessionMsg() throws Exception {
		Paras p = new Paras();
		p.SQL = "SELECT Msg FROM WF_Emp where No=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
		p.AddFK_Emp();
		return DBAccess.RunSQLReturnString(p);
	}

	public static void setSessionMsg(String value) throws Exception {
		if (StringHelper.isNullOrEmpty(value) == true) {
			return;
		}
		Paras p = new Paras();
		p.SQL = "UPDATE WF_Emp SET Msg=" + SystemConfig.getAppCenterDBVarStr() + "v WHERE No="
				+ SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
		p.Add("v", value);
		p.AddFK_Emp();

		int i = DBAccess.RunSQL(p);
		if (i == 0) {
			// 如果没有更新到.
			WFEmp emp = new WFEmp();
			emp.setNo(WebUser.getNo());
			emp.setName(WebUser.getName());
			emp.setFK_Dept(WebUser.getFK_Dept());
			emp.Insert();
			DBAccess.RunSQL(p);
		}
	}

	private static String _FromPageType = null;

	public static String getFromPageType() {
		_FromPageType = null;
		if (_FromPageType == null) {
			try {
				String url = BP.Sys.Glo.getRequest().getRemoteAddr();
				int i = url.lastIndexOf("/") + 1;
				int i2 = url.indexOf(".jsp") - 6;

				url = url.substring(i);
				url = url.substring(0, url.indexOf(".jsp"));
				_FromPageType = url;
				if (_FromPageType.contains("SmallSingle")) {
					_FromPageType = "SmallSingle";
				} else if (_FromPageType.contains("Small")) {
					_FromPageType = "Small";
				} else {
					_FromPageType = "";
				}
			} catch (RuntimeException ex) {
				_FromPageType = "";
				// throw new Exception(ex.Message + url + " i=" + i + " i2=" +
				// i2);
			}
		}
		return _FromPageType;
	}

	private static Hashtable _SendHTOfTemp = null;

	/**
	 * 临时的发送传输变量.
	 * 
	 * @throws Exception
	 */
	public static Hashtable getSendHTOfTemp() throws Exception {
		if (_SendHTOfTemp == null) {
			_SendHTOfTemp = new Hashtable();
		}
		return (Hashtable) ((_SendHTOfTemp.get(WebUser.getNo()) instanceof Hashtable)
				? _SendHTOfTemp.get(WebUser.getNo()) : null);
	}

	public static void setSendHTOfTemp(Hashtable value) throws Exception {
		if (_SendHTOfTemp == null) {
			_SendHTOfTemp = new Hashtable();
		}
		if (null != value)
			_SendHTOfTemp.put(WebUser.getNo(), value);
		else
			_SendHTOfTemp.put(WebUser.getNo(), new Hashtable());
	}

	/**
	 * 报表属性集合
	 */
	private static Attrs _AttrsOfRpt = null;

	/**
	 * 报表属性集合
	 * 
	 */
	public static Attrs getAttrsOfRpt() {
		if (_AttrsOfRpt == null) {
			_AttrsOfRpt = new Attrs();
			_AttrsOfRpt.AddTBInt(GERptAttr.OID, 0, "WorkID", true, true);
			_AttrsOfRpt.AddTBInt(GERptAttr.FID, 0, "FlowID", false, false);

			_AttrsOfRpt.AddTBString(GERptAttr.Title, null, "标题", true, false, 0, 10, 10);
			_AttrsOfRpt.AddTBString(GERptAttr.FlowStarter, null, "发起人", true, false, 0, 10, 10);
			_AttrsOfRpt.AddTBString(GERptAttr.FlowStartRDT, null, "发起时间", true, false, 0, 10, 10);
			_AttrsOfRpt.AddTBString(GERptAttr.WFState, null, "状态", true, false, 0, 10, 10);

			// Attr attr = new Attr();
			// attr.Desc = "流程状态";
			// attr.Key = "WFState";
			// attr.MyFieldType = FieldType.Enum;
			// attr.UIBindKey = "WFState";
			// attr.UITag = "@0=进行中@1=已经完成";

			_AttrsOfRpt.AddDDLSysEnum(GERptAttr.WFState, 0, "流程状态", true, true, GERptAttr.WFState);
			_AttrsOfRpt.AddTBString(GERptAttr.FlowEmps, null, "参与人", true, false, 0, 10, 10);
			_AttrsOfRpt.AddTBString(GERptAttr.FlowEnder, null, "结束人", true, false, 0, 10, 10);
			_AttrsOfRpt.AddTBString(GERptAttr.FlowEnderRDT, null, "最后处理时间", true, false, 0, 10, 10);
			_AttrsOfRpt.AddTBDecimal(GERptAttr.FlowEndNode, new BigDecimal(0), "结束节点", true, false);
			_AttrsOfRpt.AddTBDecimal(GERptAttr.FlowDaySpan, new BigDecimal(0), "跨度(天)", true, false);
			// _AttrsOfRpt.AddTBString(GERptAttr.FK_NY, null, "隶属月份", true,
			// false, 0, 10, 10);
		}
		return _AttrsOfRpt;
	}

	// 属性
	// 其他配置.
	/**
	 * 帮助
	 * 
	 * @param id1
	 * @param id2
	 * @return
	 */
	public static String GenerHelpCCForm(String text, String id1, String id2) {
		if (id1 == null) {
			return "<div style='float:right' ><a href='http://ccform.mydoc.io' target=_blank><img src='/WF/Img/Help.gif'>"
					+ text + "</a></div>";
		} else {
			return "<div style='float:right' ><a href='" + id1 + "' target=_blank><img src='../../..//Img/Help.gif'>"
					+ text + "</a></div>";
		}
	}

	public static String GenerHelpCCFlow(String text, String id1, String id2) {
		return "<div style='float:right' ><a href='" + id1 + "' target=_blank><img src='/WF/Img/Help.gif'>" + text
				+ "</a></div>";
	}

	public static String getNodeImagePath() {
		return Glo.getIntallPath() + "/Data/Node/";
	}

	public static void ClearDBData() {
		String sql = "DELETE FROM WF_GenerWorkFlow WHERE fk_flow not in (select no from wf_flow )";
		DBAccess.RunSQL(sql);

		sql = "DELETE FROM WF_GenerWorkerlist WHERE fk_flow not in (select no from wf_flow )";
		DBAccess.RunSQL(sql);
	}

	public static String OEM_Flag = "CCS";

	public static String getFlowFileBill() {
		return SystemConfig.getPathOfDataUser()  + "/Bill/";
	}

	private static String _IntallPath = null;

	public static String getIntallPath() {
		if (_IntallPath == null) {
			if (SystemConfig.getIsBSsystem() == true) {
				_IntallPath = Glo.getCCFlowAppPath();
			}
		}

		if (_IntallPath == null) {
			throw new RuntimeException("@没有实现如何获得 cs 下的根目录.");
		}

		return _IntallPath;
	}

	public static void setIntallPath(String value) {
		_IntallPath = value;
	}

	private static String _ServerIP = null;

	public static String getServerIP() {
		if (_ServerIP == null) {
			/*
			 * String ip = "127.0.0.1"; System.Net.IPAddress[] addressList =
			 * System.Net.Dns.GetHostByName(System.Net.Dns.GetHostName()).
			 * AddressList; if (addressList.length > 1) { _ServerIP =
			 * addressList[1].toString(); } else { _ServerIP =
			 * addressList[0].toString(); }
			 */
			String ip = getHostURL();
		}
		return _ServerIP;
	}

	public static void setServerIP(String value) {
		_ServerIP = value;
	}

	/**
	 * 流程控制器按钮
	 */
	public static String getFlowCtrlBtnPos() {
		String s = (String) ((SystemConfig.getAppSettings().get("FlowCtrlBtnPos") instanceof String)
				? SystemConfig.getAppSettings().get("FlowCtrlBtnPos") : null);
		if (s == null || s.equals("Top")) {
			return "Top";
		}
		return "Bottom";
	}

	/**
	 * 全局的安全验证码
	 */
	public static String getGloSID() {
		String s = (String) ((SystemConfig.getAppSettings().get("GloSID") instanceof String)
				? SystemConfig.getAppSettings().get("GloSID") : null);
		if (s == null || s.equals("")) {
			s = "sdfq2erre-2342-234sdf23423-323";
		}
		return s;
	}

	/**
	 * 是否启用检查用户的状态? 如果启用了:在MyFlow.htm中每次都会检查当前的用户状态是否被禁
	 * 用，如果禁用了就不能执行任何操作了。启用后，就意味着每次都要 访问数据库。
	 * 
	 */
	public static boolean getIsEnableCheckUseSta() {
		String s = (String) ((SystemConfig.getAppSettings().get("IsEnableCheckUseSta") instanceof String)
				? SystemConfig.getAppSettings().get("IsEnableCheckUseSta") : null);
		if (s == null || s.equals("0")) {
			return false;
		}
		return true;
	}

	/**
	 * 是否启用显示节点名称
	 * 
	 */
	public static boolean getIsEnableMyNodeName() {
		String s = (String) ((SystemConfig.getAppSettings().get("IsEnableMyNodeName") instanceof String)
				? SystemConfig.getAppSettings().get("IsEnableMyNodeName") : null);
		if (s == null || s.equals("0")) {
			return false;
		}
		return true;
	}

	/**
	 * 检查一下当前的用户是否仍旧有效使用？
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean CheckIsEnableWFEmp() throws Exception {
		Paras ps = new Paras();
		ps.SQL = "SELECT UseSta FROM WF_Emp WHERE No=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
		ps.AddFK_Emp();
		String s = DBAccess.RunSQLReturnStringIsNull(ps, "1");
		if (s.equals("1") || s == null) {
			return true;
		}
		return false;
	}

	/**
	 * 语言
	 */
	public static String Language = "CH";

	public static boolean getIsQL() {
		String s = SystemConfig.getAppSettings().get("IsQL").toString();
		if (s == null || s.equals("0")) {
			return false;
		}
		return true;
	}

	/**
	 * 是否启用共享任务池？
	 */
	public static boolean getIsEnableTaskPool() {
		return SystemConfig.GetValByKeyBoolen("IsEnableTaskPool", false);
	}

	/**
	 * 是否显示标题
	 */
	public static boolean getIsShowTitle() {
		return SystemConfig.GetValByKeyBoolen("IsShowTitle", false);
	}

	/**
	 * 是否为工作增加一个优先级
	 */
	public static boolean getIsEnablePRI() {
		return SystemConfig.GetValByKeyBoolen("IsEnablePRI", false);
	}

	/**
	 * 用户信息显示格式
	 */
	public static UserInfoShowModel getUserInfoShowModel() {
		return UserInfoShowModel.forValue(SystemConfig.GetValByKeyInt("UserInfoShowModel", 0));
	}

	/**
	 * 产生用户数字签名
	 * 
	 * @return
	 */
	public static String GenerUserSigantureHtml(String userNo, String userName) {
		return "<img src='" + getCCFlowAppPath() + "DataUser/Siganture/" + userNo + ".jpg' title='" + userName
				+ "' border=0 onerror=\"src='" + getCCFlowAppPath() + "DataUser/UserIcon/DefaultSmaller.png'\" />";
	}

	/**
	 * 产生用户小图片
	 * 
	 * @return
	 */
	public static String GenerUserImgSmallerHtml(String userNo, String userName) {
		return "<img src='" + getCCFlowAppPath() + "DataUser/UserIcon/" + userNo
				+ "Smaller.png' border=0 width='15px' height='15px' style='padding-right:5px;vertical-align:middle;'  onerror=\"src='"
				+ getCCFlowAppPath() + "DataUser/UserIcon/DefaultSmaller.png'\" />" + userName;
	}

	/**
	 * 产生用户大图片
	 * 
	 * @return
	 */
	public static String GenerUserImgHtml(String userNo, String userName) {
		return "<img src='" + getCCFlowAppPath() + "DataUser/UserIcon/" + userNo
				+ ".png'  style='padding-right:5px;width:60px;height:80px;border:0px;text-align:middle' onerror=\"src='"
				+ getCCFlowAppPath() + "DataUser/UserIcon/Default.png'\" /><br>" + userName;
	}

	/**
	 * 更新主表的SQL
	 */
	public static String getUpdataMainDeptSQL() {
		return SystemConfig.GetValByKey("UpdataMainDeptSQL",
				"UPDATE Port_Emp SET FK_Dept=" + SystemConfig.getAppCenterDBVarStr() + "FK_Dept WHERE No="
						+ SystemConfig.getAppCenterDBVarStr() + "No");
	}

	/**
	 * 更新SID的SQL
	 */
	public static String getUpdataSID() {
		return SystemConfig.GetValByKey("UpdataSID",
				"UPDATE Port_Emp SET SID=" + SystemConfig.getAppCenterDBVarStr() + "SID WHERE No="
						+ SystemConfig.getAppCenterDBVarStr() + "No");
	}

	/**
	 * 下载sl的地址
	 */
	public static String getSilverlightDownloadUrl() {
		return SystemConfig.GetValByKey("SilverlightDownloadUrl",
				"http://go.microsoft.com/fwlink/?LinkID=124807");
	}

	/**
	 * 处理显示格式
	 * 
	 * @param no
	 * @param name
	 * @return 现实格式
	 */
	public static String DealUserInfoShowModel(String no, String name) {
		switch (Glo.getUserInfoShowModel()) {
		case UserIDOnly:
			return "(" + no + ")";
		case UserIDUserName:
			return "(" + no + "," + name + ")";
		case UserNameOnly:
			return "(" + name + ")";
		default:
			throw new RuntimeException("@没有判断的格式类型.");

		}
	}

	/**
	 * 钉钉是否启用
	 */
	public static boolean getIsEnable_DingDing() {
		// 如果两个参数都不为空说明启用
		String corpid = SystemConfig.GetValByKey("Ding_CorpID", "");
		String corpsecret = SystemConfig.GetValByKey("Ding_CorpSecret", "");
		if (StringHelper.isNullOrEmpty(corpid) || StringHelper.isNullOrEmpty(corpsecret)) {
			return false;
		}

		return true;
	}

	/**
	 * 微信是否启用
	 */
	public static boolean getIsEnable_WeiXin() {
		// 如果两个参数都不为空说明启用
		String corpid = SystemConfig.GetValByKey("WX_CorpID", "");
		String corpsecret = SystemConfig.GetValByKey("WX_AppSecret", "");
		if (StringHelper.isNullOrEmpty(corpid) || StringHelper.isNullOrEmpty(corpsecret)) {
			return false;
		}
		return true;
	}

	/**
	 * 运行模式
	 */
	public static OSModel getOSModel() {
		/*
		 * 取消一对一模式，默认一对多模式 OSModel os =
		 * OSModel.forValue(BP.Sys.SystemConfig.GetValByKeyInt("OSModel", 0));
		 * return os;
		 */
		return OSModel.OneMore;
	}

	/**
	 * 是否是集团使用
	 */
	public static boolean getIsUnit() {
		return SystemConfig.GetValByKeyBoolen("IsUnit", false);
	}

	/**
	 * 是否启用制度
	 */
	public static boolean getIsEnableZhiDu() {
		return SystemConfig.GetValByKeyBoolen("IsEnableZhiDu", false);
	}

	/**
	 * 是否删除流程注册表数据？
	 */
	public static boolean getIsDeleteGenerWorkFlow() {
		return SystemConfig.GetValByKeyBoolen("IsDeleteGenerWorkFlow", false);
	}

	/**
	 * 是否检查表单树字段填写是否为空
	 */
	public static boolean getIsEnableCheckFrmTreeIsNull() {
		return SystemConfig.GetValByKeyBoolen("IsEnableCheckFrmTreeIsNull", true);
	}

	/**
	 * 是否启动工作时打开新窗口
	 */
	public static int getIsWinOpenStartWork() {
		return SystemConfig.GetValByKeyInt("IsWinOpenStartWork", 1);
	}

	/**
	 * 是否打开待办工作时打开窗口
	 */
	public static boolean getIsWinOpenEmpWorks() {
		return SystemConfig.GetValByKeyBoolen("IsWinOpenEmpWorks", true);
	}

	/**
	 * 是否启用消息系统消息。
	 */
	public static boolean getIsEnableSysMessage() {
		return SystemConfig.GetValByKeyBoolen("IsEnableSysMessage", true);
	}

	/**
	 * 与ccflow流程服务相关的配置: 执行自动任务节点，间隔的时间，以分钟计算，默认为2分钟。
	 */
	public static int getAutoNodeDTSTimeSpanMinutes() {
		return SystemConfig.GetValByKeyInt("AutoNodeDTSTimeSpanMinutes", 2);
	}

	/**
	 * ccim集成的数据库. 是为了向ccim写入消息.
	 */
	public static String getCCIMDBName() {
		String baseUrl = SystemConfig.getAppSettings().get("CCIMDBName").toString();
		if (StringHelper.isNullOrEmpty(baseUrl) == true) {
			baseUrl = "ccPort.dbo";
		}
		return baseUrl;
	}

	/**
	 * 主机
	 */
	public static String getHostURL() {
		if (SystemConfig.getIsBSsystem()) {
			// 如果是BS 就要求 路径.
		}

		String baseUrl = SystemConfig.getAppSettings().get("HostURL").toString();
		if (StringHelper.isNullOrEmpty(baseUrl) == true) {
			baseUrl = "http://127.0.0.1/";
		}

		if (!baseUrl.substring(baseUrl.length() - 1).equals("/")) {
			baseUrl = baseUrl + "/";
		}
		return baseUrl;
	}

	public static String getCurrPageID() {
		try {
			String url = BP.Sys.Glo.getRequest().getRequestURI();

			int i = url.lastIndexOf("/") + 1;
			int i2 = url.indexOf(".jsp") - 6;
			try {
				url = url.substring(i);
				return url.substring(0, url.indexOf(".jsp"));

			} catch (RuntimeException ex) {
				throw new RuntimeException(ex.getMessage() + url + " i=" + i + " i2=" + i2);
			}
		} catch (RuntimeException ex) {
			throw new RuntimeException("获取当前PageID错误:" + ex.getMessage());
		}
	}

	public static String getFlowFileTemp() {
		return Glo.getIntallPath() + "/Temp/";
	}

	// 用户表单风格控制
	public static String getGetUserStyle() {
		// BP.WF.Port.WFEmp emp = new WFEmp(WebUser.getNo());
		// if(string.IsNullOrEmpty(emp.Style) || emp.Style=="0")
		// {
		String userStyle = SystemConfig.getAppSettings().get("UserStyle").toString();
		if (StringHelper.isNullOrEmpty(userStyle)) {
			return "ccflow默认";
		} else {
			return userStyle;
		}
	}

	// 时间计算.
	/**
	 * 设置成工作时间
	 * 
	 * @param DateTime
	 * @return
	 * @throws Exception
	 */
	public static Date SetToWorkTime(Date dt) throws Exception {
		if (BP.Sys.GloVar.getHolidays().contains(DateUtils.format(dt, "MM-dd"))) {
			dt = DateUtils.addDay(dt, 1);
			// 如果当前是节假日，就要从下一个有效期计算。
			while (true) {
				if (BP.Sys.GloVar.getHolidays().contains(DateUtils.format(dt, "MM-dd")) == false) {
					{
						break;
					}
				}
				// 从下一个上班时间计算.
				dt = DataType.ParseSysDate2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getAMFrom());
				return dt;
			}
		}
		int timeInt = Integer.parseInt(DateUtils.format(dt, "HHmm"));

		// 判断是否在A区间, 如果是，就返回A区间的时间点.
		if (Glo.getAMFromInt() >= timeInt) {
			return DataType.ParseSysDate2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getPMFrom());
		}

		// 判断是否在E区间, 如果是就返回第2天的上班时间点.
		if (Glo.getPMToInt() <= timeInt) {
			return DataType.ParseSysDate2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getPMTo());
		}

		// 如果在午休时间点中间.
		if (Glo.getAMToInt() <= timeInt && Glo.getPMFromInt() > timeInt) {
			return DataType.ParseSysDate2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getPMFrom());
		}
		return dt;
	}

	/**
	 * 在指定的日期上增加小时数。 1，扣除午休。 2，扣除节假日。
	 * 
	 * @param dt
	 * @param hours
	 * @return
	 * @throws Exception
	 */
	private static Date AddMinutes(Date dt, int hh, int minutes) throws Exception {

		if (1 == 1) {
			Calendar c = Calendar.getInstance();
			c.setTime(dt);
			c.add(Calendar.HOUR, hh);
			c.add(Calendar.MINUTE, minutes);
			return c.getTime();

			
		}

		// 如果没有设置,就返回.
		if (minutes == 0) {
			return dt;
		}

		// 设置成工作时间.
		dt = SetToWorkTime(dt);

		// 首先判断是否是在一天整的时间完成.
		if (minutes == Glo.getAMPMHours() * 60) {
			// 如果需要在一天完成
			dt = DataType.AddDays(dt, 1, TWay.Holiday);
			return dt;
		}

		// 判断是否是AM.
		boolean isAM = false;
		int timeInt = Integer.parseInt(DateUtils.format(dt, "HHmm"));
		if (Glo.getAMToInt() > timeInt) {
			isAM = true;
		}
		// 如果是当天的情况.
		// 如果规定的时间在 1天之内.
		if (minutes / 60 / Glo.getAMPMHours() < 1) {
			if (isAM == true) {
				/* 如果是中午, 中午到中午休息之间的时间. */

				long ts = DataType.ParseSysDateTime2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getAMTo())
						.getTime() - dt.getTime();
				if (ts / (60 * 1000) >= minutes) {
					/* 如果剩余的分钟大于 要增加的分钟数，就是说+上分钟后，仍然在中午，就直接增加上这个分钟，让其返回。 */
					return DateUtils.addMinutes(dt, minutes);
				} else {
					// 求出到下班时间的分钟数。
					long myts = DataType
							.ParseSysDateTime2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getAMTo())
							.getTime() - dt.getTime();

					// 扣除午休的时间.
					int leftMuit = (int) (myts / (60 * 1000) - Glo.getAMPMTimeSpan() * 60);
					if (leftMuit - minutes >= 0) {
						/* 说明还是在当天的时间内. */
						Date mydt = DataType
								.ParseSysDateTime2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getPMTo());
						return DateUtils.addMinutes(mydt, (minutes - leftMuit));
					}

					// 说明要跨到第2天上去了.
					dt = DataType.AddDays(dt, 1, TWay.Holiday);
					// return Glo.AddMinutes(DateUtils.format(dt,"yyyy-MM-dd") +
					// " " + Glo.getAMFrom(), minutes - leftMuit);
				}

				// 把当前的时间加上去.
				dt = DateUtils.addMinutes(dt, minutes);

				// 判断是否是中午.
				boolean isInAM = false;
				timeInt = Integer.parseInt(DateUtils.format(dt, "HHmm"));
				if (Glo.getAMToInt() >= timeInt) {
					isInAM = true;
				}

				if (isInAM == true) {
					// 加上时间后仍然是中午就返回.
					return dt;
				}

				// 延迟一个午休时间.
				dt = DateUtils.addHours(dt, (int) Glo.getAMPMTimeSpan());

				// 判断时间点是否落入了E区间.
				timeInt = Integer.parseInt(DateUtils.format(dt, "HHmm"));
				if (Glo.getPMToInt() <= timeInt) {
					/* 如果落入了E区间. */

					// 求出来时间点到，下班之间的分钟数.
					long tsE = dt.getTime() - DataType
							.ParseSysDate2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getPMTo()).getTime();

					// 从次日的上班时间计算+ 这个时间差.
					dt = DataType.ParseSysDate2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getPMTo());
					return DateUtils.addMinutes(dt, (int) tsE / (60 * 1000));
				} else {
					/* 过了第2天的情况很少，就不考虑了. */
					return dt;
				}
			} else {
				// 如果是下午, 计算出来到下午下班还需多少分钟，与增加的分钟数据相比较.
				long ts = DataType.ParseSysDateTime2DateTime(DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getPMTo())
						.getTime() - dt.getTime();
				if (ts / (60 * 1000) >= minutes) {
					// 如果剩余的分钟大于 要增加的分钟数，就直接增加上这个分钟，让其返回。
					return DateUtils.addMinutes(dt, minutes);
				} else {

					// 剩余的分钟数 = 总分钟数 - 今天下午剩余的分钟数.
					int leftMin = minutes - (int) ts / (60 * 1000);

					// 否则要计算到第2天上去了， 计算时间要从下一个有效的工作日上班时间开始.
					dt = DataType
							.AddDays(
									DataType.ParseSysDateTime2DateTime(
											DateUtils.format(dt, "yyyy-MM-dd") + " " + Glo.getAMFrom()),
									1, TWay.Holiday);
					// 递归调用,让其在次日的上班时间在增加，分钟数。
					return Glo.AddMinutes(dt, 0, leftMin);
				}

			}
		}

		// 如果是当天的情况.

		return dt;
	}

	/**
	 * 增加分钟数.
	 * 
	 * @param sysdt
	 * @param minutes
	 * @return
	 * @throws Exception
	 */
	public static Date AddMinutes(String sysdt, int minutes) throws Exception {
		Date dt = DataType.ParseSysDate2DateTime(sysdt);
		return AddMinutes(dt, 0, minutes);
	}

	/**
	 * 在指定的日期上增加n天n小时，并考虑节假日
	 * 
	 * @param sysdt
	 *            指定的日期
	 * @param day
	 *            天数
	 * @param minutes
	 *            分钟数
	 * @return 返回计算后的日期
	 */
	public static Date AddDayHoursSpan(Date specDT, int day, int hh, int minutes) {
		if (specDT == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(specDT);

		cal.add(Calendar.DATE, day);
		cal.add(Calendar.HOUR, hh);
		cal.add(Calendar.MINUTE, minutes);

		return cal.getTime();
	}

	public static Date AddDayHoursSpan(String specDT, int day, int hh, int minutes, TWay tWay) throws Exception {
		if (specDT == null) {
			return null;
		}
		Date mydt = DataType.AddDays(specDT, day, tWay);
		return Glo.AddMinutes(mydt, hh, minutes);
	}


	// 与考核相关.
	/**
	 * 当流程发送下去以后，就开始执行考核。
	 * 
	 * @param fl
	 * @param nd
	 * @param workid
	 * @param fid
	 * @param title
	 * @throws Exception
	 */
	public static void InitCH(Flow fl, Node nd, long workid, long fid, String title, GenerWorkerList gwl)
			throws Exception {
		InitCH2017(fl, nd, workid, fid, title, null, null, new Date(), gwl);
	}

	/**
	 * 执行考核
	 * 
	 * @param fl
	 *            流程
	 * @param nd
	 *            节点
	 * @param workid
	 *            工作ID
	 * @param fid
	 *            FID
	 * @param title
	 *            标题
	 * @param prvRDT
	 *            上一个时间点
	 * @param sdt
	 *            应完成日期
	 * @param dtNow
	 *            当前日期
	 * @throws Exception
	 */
	private static void InitCH2017(Flow fl, Node nd, long workid, long fid, String title, String prvRDT, String sdt,
			Date dtNow, GenerWorkerList gwl) throws Exception {
		// 开始节点不考核.
		if (nd.getIsStartNode() || nd.getHisCHWay() == CHWay.None) {
			return;
		}

		// 如果设置为0 则不考核.
		if (nd.getTimeLimit() == 0 && nd.getTimeLimitHH() == 0 && nd.getTimeLimitMM() == 0) {
			return;
		}

		if (dtNow == null) {
			dtNow = new Date();
		}

		// 求参与人员 todoEmps ，应完成日期 sdt ，与工作派发日期 prvRDT.
		// 参与人员.
		String todoEmps = "";
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		if (nd.getIsEndNode() == true) {
			// 如果是最后一个节点，可以使用这样的方式来求人员信息 。

			Paras ps = new Paras();
			switch (SystemConfig.getAppCenterDBType()) {
			case MSSQL:
			case Oracle:
			case MySQL:
				ps.SQL = "SELECT RDT, SDTOfNode, TodoEmps FROM WF_GenerWorkFlow  WHERE WorkID=" + dbstr + "WorkID ";
				break;
			default:
				break;
			}

			ps.Add("WorkID", workid);
			DataTable dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0) {
				return;
			}

			sdt = dt.Rows.get(0).getValue("SDTOfNode").toString(); // 应完成日期.
			todoEmps = dt.Rows.get(0).getValue("TodoEmps").toString(); // 参与人员.

			/// #region 求上一个节点的日期.
			dt = Dev2Interface.Flow_GetPreviousNodeTrack(workid, nd.getNodeID());
			if (dt.Rows.size() == 0) {
				return;
			}
			// 上一个节点的活动日期.
			prvRDT = dt.Rows.get(0).getValue("RDT").toString();
		}

		if (nd.getIsEndNode() == false) {
			if (gwl == null) {
				gwl = new GenerWorkerList();
				gwl.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, nd.getNodeID(),
						GenerWorkerListAttr.FK_Emp, WebUser.getNo());
			}

			prvRDT = gwl.getRDT();//上一个时间点的记录日期. 
									
			sdt = gwl.getSDT(); //应完成日期.
			todoEmps = WebUser.getNo() + "," + WebUser.getName() + ";";
		}
		// 求参与人员，应完成日期，与工作派发日期.

		/// #region 求 preSender上一个发送人，preSenderText 发送人姓名
		String preSender = "";
		String preSenderText = "";
		DataTable dt_Sender = Dev2Interface.Flow_GetPreviousNodeTrack(workid, nd.getNodeID());
		if (dt_Sender.Rows.size() > 0) {
			preSender = dt_Sender.Rows.get(0).getValue("EmpFrom").toString();
			preSenderText = dt_Sender.Rows.get(0).getValue("EmpFromT").toString();
		}

		// 初始化基础数据
		CH ch = new CH();
		ch.setWorkID(workid);
		ch.setFID(fid);
		ch.setTitle(title);

		// 记录当时设定的值.
		ch.setTimeLimit(nd.getTimeLimit());

		ch.setFK_NY(DateUtils.format(new Date(), "yyyy-MM"));
		ch.setDTFrom(prvRDT); // 任务下达时间.
		ch.setDTTo(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm"));

		ch.setSDT(sdt); // 应该完成时间.

		ch.setFK_Flow(nd.getFK_Flow()); // 流程信息.
		ch.setFK_FlowT(nd.getFlowName());

		ch.setFK_Node(nd.getNodeID()); // 节点.
		ch.setFK_NodeT(nd.getName());

		ch.setFK_Dept(WebUser.getFK_Dept()); // 部门.
		ch.setFK_DeptT(WebUser.getFK_DeptName());

		ch.setFK_Emp(WebUser.getNo()); // 当事人.
		ch.setFK_EmpT(WebUser.getName());

		// 处理相关联的当事人.
		ch.setGroupEmpsNames(todoEmps);
		// 上一步发送人
		ch.setSender(preSender);
		ch.setSenderT(preSenderText);
		// 考核状态
		ch.setDTSWay(nd.getHisCHWay().getValue());

		// 求参与人员数量.
		String[] strs = todoEmps.split("[;]", -1);
		ch.setGroupEmpsNum(strs.length - 1); // 个数.

		// 求参与人的ids.
		String empids = ",";
		for (String str : strs) {
			if (StringHelper.isNullOrEmpty(str)) {
				continue;
			}

			String[] mystr = str.split("[,]", -1);
			empids += mystr[0] + ",";
		}
		ch.setGroupEmps(empids);

		// mypk.
		ch.setMyPK(nd.getNodeID() + "_" + workid + "_" + fid + "_" + WebUser.getNo());

		Calendar cal = Calendar.getInstance();
		cal.setTime(dtNow);
		ch.setWeekNum(cal.get(Calendar.WEEK_OF_YEAR));

		// UseDays . 求出实际使用天数.
		Date dtFrom = DataType.ParseSysDate2DateTime(ch.getDTFrom());
		Date dtTo = DataType.ParseSysDate2DateTime(ch.getDTTo());

		long ts = dtTo.getTime() - dtFrom.getTime();
		ch.setUseDays(ts / 1000 / 60 / 60 / 24); // 用时，天数
		ch.setUseMinutes(ts / 1000 / 60); // 用时，分钟

		// OverDays . 求出 逾期天 数.
		Date sdtOfDT = DataType.ParseSysDate2DateTime(ch.getSDT());

		long myts = dtTo.getTime() - sdtOfDT.getTime();
		ch.setOverDays(myts / 1000 / 60 / 60 / 24); // 逾期的天数.
		ch.setOverMinutes(myts / 1000 / 60); // 逾期的分钟数
		if (sdtOfDT.compareTo(dtTo) >= 0) {
			// 正常完成
			ch.setCHSta(CHSta.AnQi); // 按期完成.
			ch.setPoints(0);
		} else {
			// 逾期完成.
			ch.setCHSta(CHSta.YuQi); // 按期完成.
			float sum = ch.getOverDays() * nd.getTCent();
			ch.setPoints((float) (Math.round(sum * 100)) / 100);
		}
		// 求计算属性.
		// 执行保存.
		try {
			ch.DirectInsert();
		} catch (Exception e) {
			if (ch.getIsExits()) {
				ch.Update();
			} else {
				// 如果遇到退回的情况就可能涉及到主键重复的问题.
				ch.setMyPK(DBAccess.GenerGUID());
				ch.Insert();
			}
		}

	}

	/**
	 * 中午时间从
	 * 
	 */
	public static String getAMFrom() {
		return SystemConfig.GetValByKey("AMFrom", "08:30");
	}

	/**
	 * 中午时间从
	 * 
	 */
	public static int getAMFromInt() {
		return Integer.parseInt(Glo.getAMFrom().replace(":", ""));
	}

	/**
	 * 一天有效的工作小时数 是中午工作小时+下午工作小时.
	 * 
	 */
	public static float getAMPMHours() {
		return SystemConfig.GetValByKeyFloat("AMPMHours", 8);
	}

	/**
	 * 中午间隔的小时数
	 * 
	 */
	public static float getAMPMTimeSpan() {
		return SystemConfig.GetValByKeyFloat("AMPMTimeSpan", 1);
	}

	/**
	 * 中午时间到
	 * 
	 */
	public static String getAMTo() {
		return SystemConfig.GetValByKey("AMTo", "11:30");
	}

	/**
	 * 中午时间到
	 * 
	 */
	public static int getAMToInt() {
		return Integer.parseInt(Glo.getAMTo().replace(":", ""));
	}

	/**
	 * 下午时间从
	 * 
	 */
	public static String getPMFrom() {
		return SystemConfig.GetValByKey("PMFrom", "13:30");
	}

	/**
	 * 到
	 * 
	 */
	public static int getPMFromInt() {
		return Integer.parseInt(Glo.getPMFrom().replace(":", ""));
	}

	/**
	 * 到
	 * 
	 */
	public static String getPMTo() {
		return SystemConfig.GetValByKey("PMTo", "17:30");
	}

	/**
	 * 到
	 * 
	 */
	public static int getPMToInt() {
		return Integer.parseInt(Glo.getPMTo().replace(":", ""));
	}
	// 与考核相关.

	public static BP.Sys.FrmAttachmentDBs GenerFrmAttachmentDBs(FrmAttachment athDesc, String pkval,
			String FK_FrmAttachment) throws Exception {

		BP.Sys.FrmAttachmentDBs dbs = new BP.Sys.FrmAttachmentDBs();
		if (athDesc.getHisCtrlWay() == AthCtrlWay.PWorkID) {
			String pWorkID = Integer.toString(
					DBAccess.RunSQLReturnValInt("SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + pkval, 0));
			if (pWorkID == null || pWorkID == "0")
				pWorkID = pkval;

			if (athDesc.getAthUploadWay() == AthUploadWay.Inherit) {
				/* 继承模式 */
				QueryObject qo = new QueryObject(dbs);
				
				   if (pWorkID.equals(pkval) == true)
                       qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, pkval);
                   else
                       qo.AddWhereIn(FrmAttachmentDBAttr.RefPKVal, "('" + pWorkID + "','" + pkval + "')");
				   
				
				qo.addOrderBy("RDT");
				qo.DoQuery();
			}

			if (athDesc.getAthUploadWay() == AthUploadWay.Interwork) {
				/* 共享模式 */
				dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, pWorkID);
			}
			return dbs;
		}

		if (athDesc.getHisCtrlWay() == AthCtrlWay.WorkID) {
			/* 继承模式 */
			QueryObject qo = new QueryObject(dbs);
			qo.AddWhere(FrmAttachmentDBAttr.NoOfObj, athDesc.getNoOfObj());
			qo.addAnd();
			qo.AddWhere(FrmAttachmentDBAttr.RefPKVal,  pkval);

			// qo.addAnd();
			// qo.AddWhere(FrmAttachmentDBAttr.FK_FrmAttachment,
			// FK_FrmAttachment);

			qo.addOrderBy("RDT");
			qo.DoQuery();
			return dbs;
		}

		if (athDesc.getHisCtrlWay() == AthCtrlWay.FID) {
			/* 继承模式 */
			QueryObject qo = new QueryObject(dbs);
			qo.AddWhere(FrmAttachmentDBAttr.FK_FrmAttachment, athDesc.getMyPK());
			qo.addAnd();
			qo.addLeftBracket();
			qo.AddWhere(FrmAttachmentDBAttr.FID, Integer.parseInt(pkval));
			qo.addOr();
			qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, pkval);
			qo.addRightBracket();

			qo.addOrderBy("RDT");
			qo.DoQuery();
			return dbs;
		}

		if (athDesc.getHisCtrlWay() == AthCtrlWay.MySelfOnly || athDesc.getHisCtrlWay() == AthCtrlWay.PK) {
			if (FK_FrmAttachment.contains("AthMDtl")) {
				/* 如果是一个明细表的多附件，就直接按照传递过来的PK来查询. */
				QueryObject qo = new QueryObject(dbs);
				qo.AddWhere(FrmAttachmentDBAttr.RefPKVal, pkval);
				qo.addAnd();
				qo.AddWhere(FrmAttachmentDBAttr.FK_FrmAttachment, FK_FrmAttachment);
				qo.DoQuery();
			} else {
				dbs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, FK_FrmAttachment, FrmAttachmentDBAttr.RefPKVal,
						pkval, "RDT");
			}
			return dbs;
		}

		try {
			throw new Exception("@没有判断的权限控制模式:" + athDesc.getHisCtrlWay());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dbs;
	}

	/**
	 * 获得一个表单的动态附件字段
	 * 
	 * @param exts
	 *            扩展
	 * @param nd
	 *            节点
	 * @param en
	 *            实体
	 * @param md
	 *            map
	 * @param attrs
	 *            属性集合
	 * @return 附件的主键
	 * @throws Exception
	 */
	public static String GenerActiveAths(MapExts exts, Node nd, Entity en, MapData md, MapAttrs attrs)
			throws Exception {
		String strs = "";
		for (MapExt me : exts.ToJavaList()) {
			if (me.getExtType() != MapExtXmlList.SepcAthSepcUsers) {
				continue;
			}

			boolean isCando = false;
			if (!me.getTag1().equals("")) {
				String tag1 = me.getTag1() + ",";
				if (tag1.contains(WebUser.getNo() + ",")) {
					// 根据设置的人员计算.
					isCando = true;
				}
			}

			if (!me.getTag2().equals("")) {
				// 根据sql判断.
				Object tempVar = me.getTag2();
				String sql = (String) ((tempVar instanceof String) ? tempVar : null);
				sql = Glo.DealExp(sql, en, null);
				if (DBAccess.RunSQLReturnValFloat(sql) > 0) {
					isCando = true;
				}
			}

			if (!me.getTag3().equals("") && WebUser.getFK_Dept() == me.getTag3()) {
				// 根据部门编号判断.
				isCando = true;
			}

			if (isCando == false) {
				continue;
			}
			strs += me.getDoc();
		}
		return strs;
	}

	/**
	 * 获得一个表单的动态权限字段
	 * 
	 * @param exts
	 * @param nd
	 * @param en
	 * @param md
	 * @param attrs
	 * @return
	 * @throws Exception
	 */
	public static String GenerActiveFiels(MapExts exts, Node nd, Entity en, MapData md, MapAttrs attrs)
			throws Exception {
		String strs = "";
		for (MapExt me : exts.ToJavaList()) {
			if (me.getExtType() != MapExtXmlList.SepcFiledsSepcUsers) {
				continue;
			}

			boolean isCando = false;
			if (!me.getTag1().equals("")) {
				String tag1 = me.getTag1() + ",";
				if (tag1.contains(WebUser.getNo() + ",")) {
					// 根据设置的人员计算.
					isCando = true;
				}
			}

			if (!me.getTag2().equals("")) {
				// 根据sql判断.
				Object tempVar = me.getTag2();
				String sql = (String) ((tempVar instanceof String) ? tempVar : null);
				sql = Glo.DealExp(sql, en, null);
				if (DBAccess.RunSQLReturnValFloat(sql) > 0) {
					isCando = true;
				}
			}

			if (!me.getTag3().equals("") && WebUser.getFK_Dept() == me.getTag3()) {
				// 根据部门编号判断.
				isCando = true;
			}

			if (isCando == false) {
				continue;
			}
			strs += me.getDoc();
		}
		return strs;
	}

	/**
	 * 转到消息显示界面.
	 * 
	 * @param info
	 */
	public static void ToMsg(String info, HttpServletResponse response) {
		// string rowUrl = BP.Sys.Glo.getRequest().getRequestURI();
		// if (rowUrl.Contains("&IsClient=1"))
		// {
		// /*说明这是vsto调用的.*/
		// return;
		// }

		ContextHolderUtils.getSession().setAttribute("info", info);
		try {
			response.sendRedirect(Glo.getCCFlowAppPath() + "WF/MyFlowInfo.jsp?Msg=" + info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void ToMsgErr(String info) {
		// info = "<font color=red>" + info + "</font>";
		// System.Web.HttpContext.Current.Session.setValue("info", info);
		// System.Web.HttpContext.Current.Response.Redirect(Glo.getCCFlowAppPath()
		// + "WF/MyFlowInfo.jsp?Msg=" + DataType.getCurrentDataTime(), false);
		info = "<font color=red>" + info + "</font>";
		ContextHolderUtils.getSession().setAttribute("info", info);
		try {
			ContextHolderUtils.getResponse().sendRedirect(Glo.getCCFlowAppPath() + "WF/MyFlowInfo.jsp?Msg=" + info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查流程发起限制
	 * 
	 * @param flow
	 *            流程
	 * @param wk
	 *            开始节点工作
	 * @return
	 * @throws Exception
	 */
	public static boolean CheckIsCanStartFlow_InitStartFlow(Flow flow) throws Exception {
		StartLimitRole role = flow.getStartLimitRole();
		if (role == StartLimitRole.None) {
			return true;
		}

		String sql = "";
		String ptable = flow.getPTable();

		// 按照时间的必须是，在表单加载后判断, 不管用户设置是否正确.
		Date dtNow = new Date();
		if (role == StartLimitRole.Day) {
			// 仅允许一天发起一次
			sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE RDT LIKE '" + DataType.getCurrentDate()
					+ "%' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
			if (DBAccess.RunSQLReturnValInt(sql, 0) == 0) {
				if (flow.getStartLimitPara().equals("")) {
					return true;
				}

				// 判断时间是否在设置的发起范围内. 配置的格式为 @11:00-12:00@15:00-13:45
				String[] strs = flow.getStartLimitPara().split("[@]", -1);
				for (String str : strs) {
					if (StringHelper.isNullOrEmpty(str)) {
						continue;
					}
					String[] timeStrs = str.split("[-]", -1);
					String tFrom = DateUtils.getCurrentDate("yyyy-MM-dd") + " " + timeStrs[0].trim();
					String tTo = DateUtils.getCurrentDate("yyyy-MM-dd") + " " + timeStrs[1].trim();
					if (DataType.ParseSysDateTime2DateTime(tFrom).getTime() <= dtNow.getTime()
							&& dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}

		if (role == StartLimitRole.Week) {
			//
			// * 1, 找出周1 与周日分别是第几日.
			// * 2, 按照这个范围去查询,如果查询到结果，就说明已经启动了。
			//
			sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE RDT >= '" + DataType.WeekOfMonday(dtNow)
					+ "' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
			if (DBAccess.RunSQLReturnValInt(sql, 0) == 0) {
				if (flow.getStartLimitPara().equals("")) {
					return true; // 如果没有时间的限制.
				}

				// 判断时间是否在设置的发起范围内.
				// 配置的格式为 @Sunday,11:00-12:00@Monday,15:00-13:45,
				// 意思是.周日，周一的指定的时间点范围内可以启动流程.

				String[] strs = flow.getStartLimitPara().split("[@]", -1);
				for (String str : strs) {
					if (StringHelper.isNullOrEmpty(str)) {
						continue;
					}
					String[] timeStrs = str.split("[-]", -1);
					String tFrom = DateUtils.format(new Date(), "yyyy-MM-dd") + " " + timeStrs[0].trim();
					String tTo = DateUtils.format(new Date(), "yyyy-MM-dd") + " " + timeStrs[1].trim();
					if (DataType.ParseSysDateTime2DateTime(tFrom).getTime() <= dtNow.getTime()
							&& dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}
		if (role == StartLimitRole.Month) {
			sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE FK_NY = '" + DataType.getCurrentYearMonth()
					+ "' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
			if (DBAccess.RunSQLReturnValInt(sql, 0) == 0) {
				if (flow.getStartLimitPara().equals("")) {
					return true;
				}
				// 判断时间是否在设置的发起范围内. 配置格式: @-01 12:00-13:11@-15 12:00-13:11 ,
				// 意思是：在每月的1号,15号 12:00-13:11可以启动流程.
				String[] strs = flow.getStartLimitPara().split("[@]", -1);
				for (String str : strs) {
					if (StringHelper.isNullOrEmpty(str)) {
						continue;
					}
					String[] timeStrs = str.split("[-]", -1);
					String tFrom = DateUtils.format(new Date(), "yyyy-MM-") + " " + timeStrs[0].trim();
					String tTo = DateUtils.format(new Date(), "yyyy-MM-") + " " + timeStrs[1].trim();
					if (DataType.ParseSysDateTime2DateTime(tFrom).getTime() <= dtNow.getTime()
							&& dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}

		if (role == StartLimitRole.JD) {
			sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE FK_NY = '" + DataType.getCurrentAPOfJD()
					+ "' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
			if (DBAccess.RunSQLReturnValInt(sql, 0) == 0) {
				if (flow.getStartLimitPara().equals("")) {
					return true;
				}

				// 判断时间是否在设置的发起范围内.
				String[] strs = flow.getStartLimitPara().split("[@]", -1);
				for (String str : strs) {
					if (StringHelper.isNullOrEmpty(str)) {
						continue;
					}
					String[] timeStrs = str.split("[-]", -1);
					String tFrom = DateUtils.format(new Date(), "yyyy-MM-dd") + " " + timeStrs[0].trim();
					String tTo = DateUtils.format(new Date(), "yyyy-MM-dd") + " " + timeStrs[1].trim();
					if (DataType.ParseSysDateTime2DateTime(tFrom).getTime() <= dtNow.getTime()
							&& dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}

		if (role == StartLimitRole.Year) {
			sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE FK_NY LIKE '" + DataType.getCurrentYear()
					+ "%' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
			if (DBAccess.RunSQLReturnValInt(sql, 0) == 0) {
				if (flow.getStartLimitPara().equals("")) {
					return true;
				}

				// 判断时间是否在设置的发起范围内.
				String[] strs = flow.getStartLimitPara().split("[@]", -1);
				for (String str : strs) {
					if (StringHelper.isNullOrEmpty(str)) {
						continue;
					}
					String[] timeStrs = str.split("[-]", -1);
					String tFrom = DateUtils.format(new Date(), "yyyy-MM-dd") + " " + timeStrs[0].trim();
					String tTo = DateUtils.format(new Date(), "yyyy-MM-dd") + " " + timeStrs[1].trim();
					if (DataType.ParseSysDateTime2DateTime(tFrom).getTime() <= dtNow.getTime()
							&& dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 当要发送是检查流程是否可以允许发起.
	 * 
	 * @param flow
	 *            流程
	 * @param wk
	 *            开始节点工作
	 * @return
	 * @throws Exception
	 */
	public static boolean CheckIsCanStartFlow_SendStartFlow(Flow flow, Work wk) throws Exception {
		StartLimitRole role = flow.getStartLimitRole();
		if (role == StartLimitRole.None) {
			return true;
		}

		String sql = "";
		String ptable = flow.getPTable();

		if (role == StartLimitRole.ColNotExit) {
			// 指定的列名集合不存在，才可以发起流程。

			// 求出原来的值.
			String[] strs = flow.getStartLimitPara().split("[,]", -1);
			String val = "";
			for (String str : strs) {
				if (StringHelper.isNullOrEmpty(str) == true) {
					continue;
				}
				try {
					val += wk.GetValStringByKey(str);
				} catch (RuntimeException ex) {
					throw new RuntimeException(
							"@流程设计错误,您配置的检查参数(" + flow.getStartLimitPara() + "),中的列(" + str + ")已经不存在表单里.");
				}
			}

			// 找出已经发起的全部流程.
			sql = "SELECT " + flow.getStartLimitPara() + " FROM " + ptable
					+ " WHERE  WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow dr : dt.Rows) {
				String v = dr.getValue(0) + "" + dr.getValue(1) + "" + dr.getValue(2);
				if (val.equals(v)) {
					return false;
				}
			}
			return true;
		}

		// 配置的sql,执行后,返回结果是 0 .
		if (role == StartLimitRole.ResultIsZero) {
			sql = Glo.DealExp(flow.getStartLimitPara(), wk, null);
			if (DBAccess.RunSQLReturnValInt(sql, 0) == 0) {
				return true;
			} else {
				return false;
			}
		}

		// 配置的sql,执行后,返回结果是 <> 0 .
		if (role == StartLimitRole.ResultIsNotZero) {
			sql = Glo.DealExp(flow.getStartLimitPara(), wk, null);
			if (DBAccess.RunSQLReturnValInt(sql, 0) != 0) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 复制表单权限-从一个节点到另一个节点.
	 * 
	 * @param fk_flow
	 *            流程编号
	 * @param frmID
	 *            表单ID
	 * @param currNodeID
	 *            当前节点
	 * @param fromNodeID
	 *            从节点
	 * @throws Exception
	 */
	public static void CopyFrmSlnFromNodeToNode(String fk_flow, String frmID, int currNodeID, int fromNodeID)
			throws Exception {

		// 处理字段.
		// 删除现有的.
		FrmFields frms = new FrmFields();
		frms.Delete(FrmFieldAttr.FK_Node, currNodeID, FrmFieldAttr.FK_MapData, frmID);

		// 查询出来,指定的权限方案.
		frms.Retrieve(FrmFieldAttr.FK_Node, fromNodeID, FrmFieldAttr.FK_MapData, frmID);

		// 开始复制.
		for (FrmField item : frms.ToJavaList()) {
			item.setMyPK(frmID + "_" + fk_flow + "_" + currNodeID + "_" + item.getKeyOfEn());
			item.setFK_Node(currNodeID);
			item.Insert(); // 插入数据库.
		}
		// 处理字段.
		// 没有考虑到附件的权限 20161020 hzm
		FrmAttachments fas = new FrmAttachments();
		// 删除现有节点的附件权限
		fas.Delete(FrmAttachmentAttr.FK_Node, currNodeID, FrmAttachmentAttr.FK_MapData, frmID);
		// 查询出 现在表单上是否有附件的情况
		fas.Retrieve(FrmAttachmentAttr.FK_Node, fromNodeID, FrmAttachmentAttr.FK_MapData, frmID);

		// 复制权限
		for (FrmAttachment fa : fas.ToJavaList()) {
			fa.setMyPK(fa.getFK_MapData() + "_" + fa.getNoOfObj() + "_" + currNodeID);
			fa.setFK_Node(currNodeID);
			fa.Insert();
		}
	}

	/**
	 * 产生消息,senderEmpNo是为了保证写入消息的唯一性，receiveid才是真正的接收者. 如果插入失败.
	 * 
	 * @param fromEmpNo
	 *            发送人
	 * @param now
	 *            发送时间
	 * @param msg
	 *            消息内容
	 * @param sendToEmpNo
	 *            接受人
	 */
	public static void SendMessageToCCIM(String fromEmpNo, String sendToEmpNo, String msg, String now) {
		// 周朋@于庆海.
		return; // 暂停支持.

	}

	public static boolean getIsEnableTrackRec() {
		String s = (String) SystemConfig.getAppSettings().get("IsEnableTrackRec");// get("IsEnableTrackRec").toString();
		if (DotNetToJavaStringHelper.isNullOrEmpty(s)) {
			return false;
		}
		if (s.equals("0")) {
			return false;
		}

		return true;
	}

	/**
	 * 是否启用beta?
	 * 
	 */
	public static boolean getIsBeta() {
		String s = SystemConfig.GetValByKey("IsBeta", "");
		if (DotNetToJavaStringHelper.isNullOrEmpty(s)) {
			return false;
		}

		if (s.equals("0")) {
			return false;
		}

		return true;
	}

	/**
	 * 前置导航导入表单数据
	 * 
	 * @param WorkID
	 * @param FK_Flow
	 * @param FK_Node
	 * @param sKey
	 *            选中的No
	 * @throws Exception
	 */
	public static DataTable StartGuidEnties(long WorkID, String FK_Flow, int FK_Node, String sKey) throws Exception {
		Flow fl = new Flow(FK_Flow);
		switch (fl.getStartGuideWay()) {
		case SubFlowGuide:
		case BySQLOne:
			String sql = "";
			Object tempVar = fl.getStartGuidePara3();
			sql = (String) ((tempVar instanceof String) ? tempVar : null);
			if (!StringUtils.isEmpty(sql)) {
				sql = sql.replaceAll("@Key", sKey).replaceAll("@key", sKey);
				sql = sql.replaceAll("~", "'");
			} else {
				Object tempVar2 = fl.getStartGuidePara2();
				sql = (String) ((tempVar2 instanceof String) ? tempVar2 : null);

			}

			// 替换变量
			sql = sql.replaceAll("@WebUser.No", WebUser.getNo());
			sql = sql.replaceAll("@WebUser.Name", WebUser.getName());
			sql = sql.replaceAll("@WebUser.FK_DeptName", WebUser.getFK_DeptName());
			sql = sql.replaceAll("@WebUser.FK_Dept", WebUser.getFK_Dept());
			

			Enumeration enu = ContextHolderUtils.getRequest().getParameterNames();
			while (enu.hasMoreElements()) {

				String key = (String) enu.nextElement();
				if (sql.contains(key) == false)
					continue;

				sql = sql.replaceAll("@" + key, ContextHolderUtils.getRequest().getParameter(key));
			}

			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
				throw new Exception("err@没有找到那一行数据." + sql);

			Hashtable ht = new Hashtable();
			// 转换成ht表
			DataRow row = dt.Rows.get(0);
			for (int i = 0; i < row.getTable().Columns.size(); i++) {
				if (row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("no")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("name")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("workid")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("fk_flow")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("fk_node")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("fid")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("oid")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("mypk")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("title")
						|| row.getTable().Columns.get(i).ColumnName.toLowerCase().equals("pworkid")) {
				} else {
					ht.put(row.getTable().Columns.get(i).ColumnName,
							row.getValue(row.getTable().Columns.get(i).ColumnName));
				}

			}
			// 保存
			Dev2Interface.Node_SaveWork(FK_Flow, FK_Node, WorkID, ht);
			return dt;
		case SubFlowGuideEntity:
		case BySystemUrlOneEntity:
			break;
		default:
			break;
		}
		return null;

	}

}