package BP.GPM;

import BP.Web.WebUser;
import BP.DA.*;
import BP.En.*;

/** 
 系统
 
*/
public class App extends EntityNoName
{

	///#region 属性
	/** 
	 打开方式
	 
	*/
	public final String getOpenWay()
	{
		int openWay = 0;

		switch (openWay)
		{
			case 0:
				return "_blank";
			case 1:
				return this.getNo();
			default:
				return "";
		}
	}
	/** 
	 路径
	 
	*/
	public final String getWebPath()
	{
		return this.GetValStringByKey("WebPath");
	}
	/** 
	 ICON
	 
	*/
	public final String getICON()
	{
		return this.getWebPath();
	}
	public final void setICON(String value)
	{
		this.SetValByKey("ICON", value);
	}
	/** 
	 连接
	 * @throws Exception 
	 
	*/
	public final String getUrl() throws Exception
	{
		String url = this.GetValStrByKey(AppAttr.Url);
		if (DataType.IsNullOrEmpty(url))
		{
			return "";
		}

		if (this.getSSOType().equals("0")) //SID验证
		{
			String SID = DBAccess.RunSQLReturnStringIsNull("SELECT SID FROM Port_Emp WHERE No='" + WebUser.getNo() + "'", null);
			if (url.contains("?")){
				url += "&UserNo=" + WebUser.getNo() + "&SID=" + SID;
			}else{
				url += "?UserNo=" + WebUser.getNo() + "&SID=" + SID;
			}
		}
		return url;
	}
	public final void setUrl(String value)
	{
		this.SetValByKey(AppAttr.Url, value);
	}
	/** 
	 跳转连接
	 
	*/
	public final String getSubUrl()
	{
		return this.GetValStrByKey(AppAttr.SubUrl);
	}
	public final void setSubUrl(String value)
	{
		this.SetValByKey(AppAttr.Url, value);
	}
	/** 
	 是否启用
	 
	*/
	public final boolean getIsEnable()
	{
		return this.GetValBooleanByKey(AppAttr.IsEnable);
	}
	public final void setIsEnable(boolean value)
	{
		this.SetValByKey(AppAttr.IsEnable, value);
	}
	/** 
	 顺序
	 
	*/
	public final int getIdx()
	{
		return this.GetValIntByKey(AppAttr.Idx);
	}
	public final void setIdx(int value)
	{
		this.SetValByKey(AppAttr.Idx, value);
	}
	/** 
	 用户控件ID
	 
	*/
	public final String getUidControl()
	{
		return this.GetValStrByKey(AppAttr.UidControl);
	}
	public final void setUidControl(String value)
	{
		this.SetValByKey(AppAttr.UidControl, value);
	}
	/** 
	 密码控件ID
	 
	*/
	public final String getPwdControl()
	{
		return this.GetValStrByKey(AppAttr.PwdControl);
	}
	public final void setPwdControl(String value)
	{
		this.SetValByKey(AppAttr.PwdControl, value);
	}
	/** 
	 提交方式
	 
	*/
	public final String getActionType()
	{
		return this.GetValStrByKey(AppAttr.ActionType);
	}
	public final void setActionType(String value)
	{
		this.SetValByKey(AppAttr.ActionType, value);
	}
	/** 
	 登录方式@0=SID验证@1=连接@2=表单提交
	 
	*/
	public final String getSSOType()
	{
		return this.GetValStrByKey(AppAttr.SSOType);
	}
	public final void setSSOType(String value)
	{
		this.SetValByKey(AppAttr.SSOType, value);
	}
	public final String getFK_AppSort()
	{
		return this.GetValStringByKey(AppAttr.FK_AppSort);
	}
	public final void setFK_AppSort(String value)
	{
		this.SetValByKey(AppAttr.FK_AppSort, value);
	}
	public final String getRefMenuNo()
	{
		return this.GetValStringByKey(AppAttr.RefMenuNo);
	}
	public final void setRefMenuNo(String value)
	{
		this.SetValByKey(AppAttr.RefMenuNo, value);
	}
		///#region 构造方法
	/** 
	 系统
	 
	*/
	public App()
	{
	}
	/** 
	 系统
	 
	 @param mypk
	 * @throws Exception 
	*/
	public App(String no) throws Exception
	{
		this.setNo(no);
		this.Retrieve();
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
		Map map = new Map("GPM_App");
		map.setDepositaryOfEntity(Depositary.None);
		map.setDepositaryOfMap(Depositary.Application);
		map.setEnDesc("系统");
		map.setEnType(EnType.Sys);


		map.AddTBStringPK(AppAttr.No, null, "编号", true, false, 2, 30, 20);
		map.AddTBString(AppAttr.Name, null, "名称", true, false, 0, 3900, 20);

		map.AddDDLSysEnum(AppAttr.AppModel, 0, "应用类型", true, true, AppAttr.AppModel, "@0=BS系统@1=CS系统");
		map.AddDDLEntities(AppAttr.FK_AppSort, null, "类别", new AppSorts(), true);
		map.AddTBString(AppAttr.Url, null, "默认连接", true, false, 0, 3900, 100, true);
		map.AddTBString(AppAttr.SubUrl, null, "第二连接", true, false, 0, 3900, 100, true);
		map.AddTBString(AppAttr.UidControl, null, "用户名控件", true, false, 0, 100, 100);
		map.AddTBString(AppAttr.PwdControl, null, "密码控件", true, false, 0, 100, 100);
		map.AddDDLSysEnum(AppAttr.ActionType, 0, "提交类型", true, true, AppAttr.ActionType, "@0=GET@1=POST");
		map.AddDDLSysEnum(AppAttr.SSOType, 0, "登录方式", true, true, AppAttr.SSOType, "@0=SID验证@1=连接@2=表单提交@3=不传值");
		map.AddDDLSysEnum(AppAttr.OpenWay, 0, "打开方式", true, true, AppAttr.OpenWay, "@0=新窗口@1=本窗口@2=覆盖新窗口");

		map.AddTBInt(AppAttr.Idx, 0, "显示顺序", true, false);
		map.AddBoolean(AppAttr.IsEnable, true, "是否启用", true, true);

		map.AddTBString(AppAttr.RefMenuNo, null, "关联菜单编号", true, false, 0, 3900, 20);
		map.AddTBString(AppAttr.AppRemark, null, "备注", true, false, 0, 500, 500,true);
		map.AddMyFile("ICON");

		RefMethod rm = new RefMethod();
		rm.Title = "编辑菜单";
		rm.ClassMethodName = this.toString() + ".DoMenu";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "查看可访问该系统的人员";
		rm.ClassMethodName = this.toString() + ".DoWhoCanUseApp";

			//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "刷新设置";
		rm.ClassMethodName = this.toString() + ".DoRef";
			//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "第二连接";
			//rm.Title = "第二连接：登录方式为不传值、连接不设置用户名密码转为第二连接。";
		rm.ClassMethodName = this.toString() + ".About";
		   // map.AddRefMethod(rm);
		this.set_enMap(map);
		return this.get_enMap();
	}
		///#endregion

	@Override
	protected boolean beforeDelete() throws Exception
	{
		Menu appMenu = new Menu(this.getRefMenuNo());
		if (appMenu != null && appMenu.getFlag().contains("Flow"))
		{
			throw new RuntimeException("@删除失败,此项为工作流菜单，不能删除。");
		}
		// 删除该系统.
		Menu menu = new Menu();
		menu.Delete(MenuAttr.FK_App, this.getNo());

		// 删除用户数据.
		EmpMenu em = new EmpMenu();
		em.Delete(MenuAttr.FK_App, this.getNo());

		EmpApp ea = new EmpApp();
		ea.Delete(MenuAttr.FK_App, this.getNo());

		return super.beforeDelete();
	}

	@Override
	protected boolean beforeUpdate() throws Exception
	{

		if (DataType.IsNullOrEmpty(this.getRefMenuNo()) == false)
		{
			//系统类别
			AppSort appSort = new AppSort(this.getFK_AppSort());

			Menu menu = new Menu(this.getRefMenuNo());
			menu.setName(this.getName());
			menu.setParentNo(appSort.getRefMenuNo());
			menu.Update();
		}

		return super.beforeUpdate();
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		AppSort sort = new AppSort(this.getFK_AppSort());

		// 求系统类别的菜单 .
		Menu menu = new Menu(sort.getRefMenuNo());

		// 创建子菜单.
		Object tempVar = menu.DoCreateSubNode();
		Menu appMenu = (Menu)((tempVar instanceof Menu) ? tempVar : null);
		appMenu.setFK_App(this.getNo());
		appMenu.setName(this.getName());
		appMenu.setHisMenuType(MenuType.App);
		appMenu.Update();

		//设置相关的菜单编号.
		this.setRefMenuNo(appMenu.getNo());


		Object tempVar2 = appMenu.DoCreateSubNode();
		Menu dir = (Menu)((tempVar2 instanceof Menu) ? tempVar2 : null);
		dir.setFK_App(this.getNo());
		dir.setName("功能目录1");
		dir.setMenuType(3);
		dir.Update();

		Object tempVar3 = dir.DoCreateSubNode();
		Menu func = (Menu)((tempVar3 instanceof Menu) ? tempVar3 : null);
		func.setName("xxx管理1");
		func.setFK_App(this.getNo());
		func.setMenuType(4);
		func.setUrl("http://ccflow.org");
		func.Update();

		Object tempVar4 = func.DoCreateSubNode();
		Menu funcDot = (Menu)((tempVar4 instanceof Menu) ? tempVar4 : null);
		funcDot.setName("查看");
		funcDot.setMenuType(5);
		funcDot.setFK_App(this.getNo());
		funcDot.Update();

		Object tempVar5 = func.DoCreateSubNode();
		funcDot = (Menu)((tempVar5 instanceof Menu) ? tempVar5 : null);
		funcDot.setName("增加");
		funcDot.setMenuType(5);
		funcDot.setFK_App(this.getNo());
		funcDot.Update();

		Object tempVar6 = func.DoCreateSubNode();
		funcDot = (Menu)((tempVar6 instanceof Menu) ? tempVar6 : null);
		funcDot.setName("删除");
		funcDot.setMenuType(5);
		funcDot.setFK_App(this.getNo());
		funcDot.Update();
			///#endregion

		return super.beforeInsert();
	}

	/** 
	 为BPM初始化菜单.
	 * @throws Exception 
	 
	*/
	public static void InitBPMMenu() throws Exception
	{
		AppSort sort = new AppSort();
		sort.setNo("01");
		if (sort.RetrieveFromDBSources() == 0)
		{
			sort.setName("应用系统");
			sort.setRefMenuNo("2000");
			sort.Insert();
		}

		App app = new App();
		app.setNo("CCFlowBPM");
		app.setName("BPM系统");
		app.setFK_AppSort("01");
		app.Insert();
	}
	/** 
	 信息介绍
	 
	 @return 
	*/
	public final String About()
	{
		return null;
	}
	/** 
	 刷新设置
	 
	 @return 
	*/
	public final String DoRef()
	{
		return "../../../GPM/WhoCanUseApp.aspx?FK_App=" + this.getNo();

	   // PubClass.WinOpen("/GPM/WhoCanUseApp.aspx?FK_App=" + this.No + "&IsRef=1", 500, 700);
		//return null;
	}
	/** 
	 查看可以访问的人员
	 
	 @return 
	*/
	public final String DoWhoCanUseApp()
	{
		return "../../../GPM/WhoCanUseApp.aspx?FK_App=" + this.getNo();
	}
	/** 
	 打开菜单
	 
	 @return 
	*/
	public final String DoMenu()
	{
		return "../../../GPM/AppMenu.htm?FK_App=" + this.getNo();
	}
	/** 
	 刷新数据.
	 * @throws Exception 
	 
	*/
	public final void RefData() throws Exception
	{
		//删除数据.
		EmpMenus mymes = new EmpMenus();
		mymes.Delete(EmpMenuAttr.FK_App, this.getNo());

		//删除系统.
		EmpApps empApps = new EmpApps();
		empApps.Delete(EmpMenuAttr.FK_App, this.getNo());

		//查询出来菜单.
		Menus menus = new Menus();
		menus.Retrieve(EmpMenuAttr.FK_App, this.getNo());

		//查询出来人员.
		Emps emps = new Emps();
		emps.RetrieveAllFromDBSource();

		for (Emp emp : emps.ToJavaList())
		{
			///#region 初始化系统访问权限.

			EmpApp me = new EmpApp();
			me.Copy(this);
			me.setFK_Emp(emp.getNo());
			me.setFK_App(this.getNo());
			me.setMyPK(this.getNo() + "_" + me.getFK_Emp());
			me.Insert();
				///#endregion 初始化系统访问权限.
		}
	}
}