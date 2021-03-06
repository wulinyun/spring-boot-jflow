package BP.GPM;

import BP.DA.*;
import BP.En.*;

/** 
 岗位
 
*/
public class StationExt extends EntityNoName
{

		///#region 属性
	public final String getFK_StationExtType()
	{
		return this.GetValStrByKey(StationAttr.FK_StationType);
	}
	public final void setFK_StationExtType(String value)
	{
		this.SetValByKey(StationAttr.FK_StationType, value);
	}

		///#region 实现基本的方方法
	@Override
	public UAC getHisUAC() throws Exception
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}

		///#region 构造方法
	/** 
	 岗位
	  
	*/
	public StationExt()
	{
	}
	/** 
	 岗位
	 
	 @param _No
	 * @throws Exception 
	*/
	public StationExt(String _No) throws Exception
	{
		super(_No);
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

		Map map = new Map("Port_Station","岗位");

		map.Java_SetEnType(EnType.Admin);
		map.Java_SetDepositaryOfMap(Depositary.Application);
		map.Java_SetDepositaryOfEntity(Depositary.Application);
		map.Java_SetCodeStruct("4");

		map.AddTBStringPK(StationAttr.No, null, "编号", true, true, 4, 4, 36);
		map.AddTBString(StationAttr.Name, null, "名称", true, false, 0, 100, 200);
		map.AddDDLEntities(StationAttr.FK_StationType, null, "类型", new StationTypes(), true);
		map.AddTBString(StationAttr.OrgNo, null, "隶属组织", true, false, 0, 50, 250);
		map.AddSearchAttr(StationAttr.FK_StationType);

			//岗位绑定菜单
		map.getAttrsOfOneVSM().AddBranches(new StationMenus(), new Menus(), StationMenuAttr.FK_Station, StationMenuAttr.FK_Menu, "绑定菜单", EmpAttr.Name, EmpAttr.No, "0");


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}