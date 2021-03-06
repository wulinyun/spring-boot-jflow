package BP.WF.Template;

import BP.En.EntitiesMM;
import BP.En.Entity;
import BP.En.QueryObject;
import BP.WF.Node;
import BP.WF.Nodes;
import BP.WF.Port.Station;
import BP.WF.Port.Stations;

/** 
 节点部门
 
*/
public class NodeDepts extends EntitiesMM
{
	/** 
	 他的工作部门
	 * @throws Exception 
	 
	*/
	public final Stations getHisStations() throws Exception
	{
		Stations ens = new Stations();
		for (NodeDept ns : this.ToJavaList())
		{
			ens.AddEntity(new Station(ns.getFK_Dept()));
		}
		return ens;
	}
	/** 
	 他的工作节点
	 * @throws Exception 
	 
	*/
	public final Nodes getHisNodes() throws Exception
	{
		Nodes ens = new Nodes();
		for (NodeDept ns : this.ToJavaList())
		{
			ens.AddEntity(new Node(ns.getFK_Node()));
		}
		return ens;

	}
	/** 
	 节点部门
	 
	*/
	public NodeDepts()
	{
	}
	/** 
	 节点部门
	 
	 @param NodeID 节点ID
	 * @throws Exception 
	*/
	public NodeDepts(int NodeID) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeDeptAttr.FK_Node, NodeID);
		qo.DoQuery();
	}
	/** 
	 节点部门
	 
	 @param StationNo StationNo 
	 * @throws Exception 
	*/
	public NodeDepts(String StationNo) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeDeptAttr.FK_Dept, StationNo);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	 
	*/
	@Override
	public Entity getGetNewEntity()
	{
		return new NodeDept();
	}
	/** 
	 取到一个工作部门集合能够访问到的节点s
	 
	 @param sts 工作部门集合
	 @return 
	 * @throws Exception 
	*/
	public final Nodes GetHisNodes(Stations sts) throws Exception
	{
		Nodes nds = new Nodes();
		Nodes tmp = new Nodes();
		for (Station st : sts.ToJavaList())
		{
			tmp = this.GetHisNodes(st.getNo());
			for (Node nd : tmp.ToJavaList())
			{
				if (nds.Contains(nd))
				{
					continue;
				}
				nds.AddEntity(nd);
			}
		}
		return nds;
	}
	/** 
	 工作部门对应的节点
	 
	 @param stationNo 工作部门编号
	 @return 节点s
	 * @throws Exception 
	*/
	public final Nodes GetHisNodes(String stationNo) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeDeptAttr.FK_Dept, stationNo);
		qo.DoQuery();

		Nodes ens = new Nodes();
		for (NodeDept en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getFK_Node()));
		}
		return ens;
	}
	/** 
	 转向此节点的集合的Nodes
	 
	 @param nodeID 此节点的ID
	 @return 转向此节点的集合的Nodes (FromNodes) 
	 * @throws Exception 
	*/
	public final Stations GetHisStations(int nodeID) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeDeptAttr.FK_Node, nodeID);
		qo.DoQuery();

		Stations ens = new Stations();
		for (NodeDept en : this.ToJavaList())
		{
			ens.AddEntity(new Station(en.getFK_Dept()));
		}
		return ens;
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeDept> ToJavaList()
	{
		return (java.util.List<NodeDept>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final java.util.ArrayList<NodeDept> Tolist()
	{
		java.util.ArrayList<NodeDept> list = new java.util.ArrayList<NodeDept>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeDept)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}