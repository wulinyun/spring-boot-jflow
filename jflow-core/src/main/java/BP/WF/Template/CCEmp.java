package BP.WF.Template;

import BP.En.EntityMM;
import BP.En.Map;
import BP.WF.Port.Emps;

/** 
 节点到人员
 节点的到人员有两部分组成.	 
 记录了从一个节点到其他的多个节点.
 也记录了到这个节点的其他的节点.
*/
public class CCEmp extends EntityMM
{

		
	/** 
	节点
	*/
	public final int getFK_Node()
	{
		return this.GetValIntByKey(CCEmpAttr.FK_Node);
	}
	public final void setFK_Node(int value)
	{
		this.SetValByKey(CCEmpAttr.FK_Node,value);
	}
	/** 
	 到人员
	*/
	public final String getFK_Emp()
	{
		return this.GetValStringByKey(CCEmpAttr.FK_Emp);
	}
	public final void setFK_Emp(String value)
	{
		this.SetValByKey(CCEmpAttr.FK_Emp,value);
	}
	public final String getFK_EmpT()
	{
		return this.GetValRefTextByKey(CCEmpAttr.FK_Emp);
	}
		
	/** 
	 节点到人员
	*/
	public CCEmp()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_CCEmp", "抄送人员");

		map.AddTBIntPK(CCEmpAttr.FK_Node, 0, "节点", true, true);
		map.AddDDLEntitiesPK(CCEmpAttr.FK_Emp, null, "人员", new Emps(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}
}