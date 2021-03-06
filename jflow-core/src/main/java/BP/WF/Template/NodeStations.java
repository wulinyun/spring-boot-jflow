package BP.WF.Template;

import BP.En.EntitiesMM;
import BP.En.Entity;
import BP.En.QueryObject;
import BP.WF.Node;
import BP.WF.Nodes;
import BP.WF.Port.Emp;
import BP.WF.Port.Station;
import BP.WF.Port.Stations;

/** 
 节点工作岗位
 
*/
public class NodeStations extends EntitiesMM
{
	/** 
	 他的工作岗位
	 * @throws Exception 
	 
	*/
	public final Stations getHisStations() throws Exception
	{
		Stations ens = new Stations();
		for (NodeStation ns : this.ToJavaList())
		{
			ens.AddEntity(new Station(ns.getFK_Station()));
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
		for (NodeStation ns : this.ToJavaList())
		{
			ens.AddEntity(new Node(ns.getFK_Node()));
		}
		return ens;

	}
	/** 
	 节点工作岗位
	 
	*/
	public NodeStations()
	{
	}
	/** 
	 节点工作岗位
	 
	 @param nodeID 节点ID
	 * @throws Exception 
	*/
	public NodeStations(int nodeID) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeStationAttr.FK_Node, nodeID);
		qo.DoQuery();
	}
	/** 
	 节点工作岗位
	 
	 @param StationNo StationNo 
	 * @throws Exception 
	*/
	public NodeStations(String StationNo) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeStationAttr.FK_Station, StationNo);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	 
	*/
	@Override
	public Entity getGetNewEntity()
	{
		return new NodeStation();
	}
	/** 
	 取到一个工作岗位集合能够访问到的节点s
	 
	 @param sts 工作岗位集合
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
	 取到一个工作人员能够访问到的节点。
	 
	 @param empId 工作人员ID
	 @return 
	 * @throws Exception 
	*/
	public final Nodes GetHisNodes_del(String empId) throws Exception
	{
		Emp em = new Emp(empId);
		return this.GetHisNodes(em.getHisStations());
	}
	/** 
	 工作岗位对应的节点
	 
	 @param stationNo 工作岗位编号
	 @return 节点s
	 * @throws Exception 
	*/
	public final Nodes GetHisNodes(String stationNo) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeStationAttr.FK_Station, stationNo);
		qo.DoQuery();

		Nodes ens = new Nodes();
		for (NodeStation en : this.ToJavaList())
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
		qo.AddWhere(NodeStationAttr.FK_Node, nodeID);
		qo.DoQuery();

		Stations ens = new Stations();
		for (NodeStation en : this.ToJavaList())
		{
			ens.AddEntity(new Station(en.getFK_Station()));
		}
		return ens;
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeStation> ToJavaList()
	{
		return (java.util.List<NodeStation>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final java.util.ArrayList<NodeStation> Tolist()
	{
		java.util.ArrayList<NodeStation> list = new java.util.ArrayList<NodeStation>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeStation)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}