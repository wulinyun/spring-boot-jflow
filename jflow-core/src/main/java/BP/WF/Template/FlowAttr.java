package BP.WF.Template;

/** 
 流程属性
 
*/
public class FlowAttr
{

		
	/** 
	 编号
	 
	*/
	public static final String No = "No";
	/** 
	 名称
	 
	*/
	public static final String Name = "Name";
	/** 
	 CCType
	 
	*/
	public static final String CCType = "CCType";
	/** 
	 抄送方式
	 
	*/
	public static final String CCWay = "CCWay";
	/** 
	 流程类别
	 
	*/
	public static final String FK_FlowSort = "FK_FlowSort";
	/** 
	 建立的日期。
	 
	*/
	public static final String CreateDate = "CreateDate";
	/** 
	 BillTable
	 
	*/
	public static final String BillTable = "BillTable";
	/** 
	 开始工作节点类型
	 
	*/
	public static final String StartNodeType = "StartNodeType";
	/** 
	 StartNodeID
	 
	*/
	public static final String StartNodeID = "StartNodeID";
	/** 
	 能不能流程Num考核。
	 
	*/
	public static final String IsCanNumCheck = "IsCanNumCheck";
	/** 
	 是否显示附件
	 
	*/
	public static final String IsFJ = "IsFJ";
	/** 
	 标题生成规则
	 
	*/
	public static final String TitleRole = "TitleRole";
	/** 
	 流程类型
	 
	*/
	public static final String FlowType = "FlowType";
	/** 
	 平均用天
	 
	*/
	public static final String AvgDay = "AvgDay";
	/** 
	 流程运行类型
	 
	*/
	public static final String FlowRunWay = "FlowRunWay";
	/** 
	 运行的设置
	 
	*/
	public static final String RunObj = "RunObj";
	/** 
	 是否有Bill
	 
	*/
	public static final String NumOfBill = "NumOfBill";
	/** 
	 明细表数量
	 
	*/
	public static final String NumOfDtl = "NumOfDtl";
	/** 
	 是否可以启动？
	 
	*/
	public static final String IsCanStart = "IsCanStart";
	/**
	 *  是否可以在手机里启用?
	 */
   
    public static final String IsStartInMobile = "IsStartInMobile";
	
	/** 
	 是否自动计算未来的处理人？
	 
	*/
	public static final String IsFullSA = "IsFullSA";
	/** 
	 类型
	 
	*/
	public static final String FlowAppType = "FlowAppType";
	/** 
	 HelpUrl帮助.
	 
	*/
	public static final String HelpUrl = "HelpUrl";
	/** 
	 图像类型
	 
	*/
	public static final String ChartType = "ChartType";
	/** 
	 时效性规则
	 
	*/
	public static final String TimelineRole = "TimelineRole";
	/** 
	 草稿
	 
	*/
	public static final String Draft = "Draft";
	/** 
	 顺序号
	 
	*/
	public static final String Idx = "Idx";
	/** 
	 参数
	 
	*/
	public static final String Paras = "Paras";
	/** 
	 业务主表
	 
	*/
	public static final String PTable = "PTable";
	/** 
	 流程数据存储模式
	 
	*/
	public static final String DataStoreModel = "DataStoreModel";
	/** 
	 流程标记
	 
	*/
	public static final String FlowMark = "FlowMark";
	/** 
	 流程事件实体
	 
	*/
	public static final String FlowEventEntity = "FlowEventEntity";
	/** 
	 流程设计者编号
	 
	*/
	public static final String DesignerNo = "DesignerNo";
	/** 
	 流程设计者名称
	 
	*/
	public static final String DesignerName = "DesignerName";
	/** 
	 历史发起查看字段
	 
	*/
	public static final String HistoryFields = "HistoryFields";
	/** 
	 是否是客户参与流程
	 
	*/
	public static final String IsGuestFlow = "IsGuestFlow";
	/** 
	 单据编号格式
	 
	*/
	public static final String BillNoFormat = "BillNoFormat";
	/** 
	 流程备注的表达式
	 
	*/
	public static final String FlowNoteExp = "FlowNoteExp";
	/** 
	 数据权限控制方式
	 
	*/
	public static final String DRCtrlType = "DRCtrlType";
	/** 
	 是否可以批量发起?
	 
	*/
	public static final String IsBatchStart = "IsBatchStart";
	/** 
	 批量发起填写的字段.
	 
	*/
	public static final String BatchStartFields = "BatchStartFields";
	 
 
	/** 
	 是否是MD5
	 
	*/
	public static final String IsMD5 = "IsMD5";
	public static final String CCStas = "CCStas";
	public static final String Note = "Note";
	/** 
	 运行的SQL
	 
	*/
	public static final String RunSQL = "RunSQL";

		///#endregion 基本属性


		///#region 发起限制规则.
	/** 
	 发起限制规则
	 
	*/
	public static final String StartLimitRole = "StartLimitRole";
	/** 
	 规则内容
	 
	*/
	public static final String StartLimitPara = "StartLimitPara";
	/** 
	 规则提示
	 
	*/
	public static final String StartLimitAlert = "StartLimitAlert";
	/** 
	 提示时间
	 
	*/
	public static final String StartLimitWhen = "StartLimitWhen";

		///#endregion 发起限制规则.


		///#region 开始节点数据导入规则.
	/** 
	 发起前置规则
	 
	*/
	public static final String StartGuideWay = "StartGuideWay";
	/** 
	 超链接
	 
	*/
	public static final String StartGuideLink = "StartGuideLink";
	/** 
	 标签
	 
	*/
	public static final String StartGuideLab = "StartGuideLab";
	/** 
	 发起前置参数1
	 
	*/
	public static final String StartGuidePara1 = "StartGuidePara1";
	/** 
	 发起前置参数2
	 
	*/
	public static final String StartGuidePara2 = "StartGuidePara2";
	/** 
	 StartGuidePara3
	 
	*/
	public static final String StartGuidePara3 = "StartGuidePara3";
	/** 
	 是否启用开始节点的数据重置？
	 
	*/
	public static final String IsResetData = "IsResetData";
	/** 
	 是否启用导入历史数据按钮?
	 
	*/
	public static final String IsImpHistory = "IsImpHistory";
	/** 
	 是否自动装载上一笔数据？
	 
	*/
	public static final String IsLoadPriData = "IsLoadPriData";
	public static final String IsDBTemplate = "IsDBTemplate";
	
	/** 
	 系统类别（第2级流程树节点编号）
	 
	*/
	public static final String SysType = "SysType";
	
	public static final String Tester = "Tester";
	
	

		///#endregion 开始节点数据导入规则.


		///#region 父子流程
	/** 
	 (当前节点为子流程时)是否检查所有子流程完成后父流程自动发送
	 
	*/
	public static final String IsAutoSendSubFlowOver = "IsAutoSendSubFlowOver";
	/** 
	 版本号
	 
	*/
	public static final String Ver = "Ver";
	/** 
	 设计类型
	 
	*/
	public static final String DType = "DType";
	/** 
	 删除规则
	 
	*/
	public static final String FlowDeleteRole = "FlowDeleteRole";
	
		///#endregion 父子流程


		///#region 数据同步方式.
	/** 
	 数据同步方式.
	 
	*/
	public static final String DTSWay = "DTSWay";
	/** 
	 业务表主键
	 
	*/
	public static final String DTSBTablePK = "DTSBTablePK";
	/** 
	 执行同步时间点
	 
	*/
	public static final String DTSTime = "DTSTime";
	/** 
	 同步格式配置.
	 
	*/
	public static final String DTSSpecNodes = "DTSSpecNodes";
	public static final String DTSField = "DTSField";
	public static final String DTSFields = "DTSFields";
	/** 
	 业务表
	 
	*/
	public static final String DTSBTable = "DTSBTable";
	/** 
	 数据源
	 
	*/
	public static final String DTSDBSrc = "DTSDBSrc";

		///#endregion


		///#region 权限组.
	/** 
	 发起人可看
	 
	*/
	public static final String PStarter = "PStarter";
	/** 
	 参与人可看
	 
	*/
	public static final String PWorker = "PWorker";
	/** 
	 被抄送人可看
	 
	*/
	public static final String PCCer = "PCCer";
	/** 
	 本部门人可看
	 
	*/
	public static final String PMyDept = "PMyDept";
	/** 
	 直属上级部门可看
	 
	*/
	public static final String PPMyDept = "PPMyDept";
	/** 
	 上级部门可看
	 
	*/
	public static final String PPDept = "PPDept";
	/** 
	 平级部门可看
	 
	*/
	public static final String PSameDept = "PSameDept";
	/** 
	 指定部门可看
	 
	*/
	public static final String PSpecDept = "PSpecDept";
	/** 
	 指定的岗位可看
	 
	*/
	public static final String PSpecSta = "PSpecSta";
	/** 
	 指定的权限组可看
	 
	*/
	public static final String PSpecGroup = "PSpecGroup";
	/** 
	 指定的人员可看
	 
	*/
	public static final String PSpecEmp = "PSpecEmp";
 
	
	public static final String Sta0 = "Sta0";
	public static final String Sta1 = "Sta1";
	public static final String Sta2 = "Sta2";
}