package BP.GPM;

import BP.DA.*;
import BP.En.*;

/** 
 人员信息块
 
*/
public class BarEmp extends EntityMyPK
{
		///#region 属性
	public final int getIdx()
	{
		return this.GetValIntByKey(BarEmpAttr.Idx);
	}
	public final void setIdx(int value)
	{
		this.SetValByKey(BarEmpAttr.Idx, value);
	}
	public final String getFK_Bar()
	{
		return this.GetValStringByKey(BarEmpAttr.FK_Bar);
	}
	public final void setFK_Bar(String value)
	{
		this.SetValByKey(BarEmpAttr.FK_Bar, value);
	}
	public final String getFK_Emp()
	{
		return this.GetValStringByKey(BarEmpAttr.FK_Emp);
	}
	public final void setFK_Emp(String value)
	{
		this.SetValByKey(BarEmpAttr.FK_Emp, value);
	}
	public final boolean getIsShow()
	{
		return this.GetValBooleanByKey(BarEmpAttr.IsShow);
	}
	public final void setIsShow(boolean value)
	{
		this.SetValByKey(BarEmpAttr.IsShow, value);
	}

	public final String getTitle()
	{
		return this.GetValStringByKey(BarEmpAttr.Title);
	}
	public final void setTitle(String value)
	{
		this.SetValByKey(BarEmpAttr.Title, value);
	}
	///#endregion
	///#region 构造方法
	/** 
	 人员信息块
	 
	*/
	public BarEmp()
	{
	}
	/** 
	 人员信息块
	 
	 @param mypk
	 * @throws Exception 
	*/
	public BarEmp(String no) throws Exception
	{
	  //  this.No = no;
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
		Map map = new Map("GPM_BarEmp");
		map.setDepositaryOfEntity(Depositary.None);
		map.setDepositaryOfMap(Depositary.Application);
		map.setEnDesc("人员信息块");
		map.setEnType(EnType.Sys);

		map.AddMyPK(); // 主键是由: FK_Bar+"_"+FK_Emp 组成的，它是一个复合主键.
		map.AddTBString(BarEmpAttr.FK_Bar, null, "信息块编号", true, false, 0, 90, 20);
		map.AddTBString(BarEmpAttr.FK_Emp, null, "人员编号", true, false, 0, 90, 20);
		map.AddTBString(BarEmpAttr.Title, null, "标题", true, false, 0, 3900, 20);
		map.AddTBInt(BarEmpAttr.IsShow, 1, "是否显示", false, true);
		map.AddTBInt(BarEmpAttr.Idx, 0, "显示顺序", false, true);

		this.set_enMap(map);
		return this.get_enMap();
	}

	///#region 显示与隐藏.
	public final void DoUp()
	{
		this.DoOrderUp(BarEmpAttr.FK_Bar, this.getFK_Bar(), BarEmpAttr.Idx);
	}
	public final void DoDown()
	{
		this.DoOrderDown(BarEmpAttr.FK_Bar, this.getFK_Bar(), BarEmpAttr.Idx);
	}
	public final void DoHidShow() throws Exception
	{
		this.setIsShow(this.getIsShow());
		this.Update();
	}


	///#endregion 显示与隐藏.
}