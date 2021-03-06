package BP.WF.Template;

import BP.En.Entities;
import BP.En.Entity;
import BP.En.QueryObject;
import BP.WF.Node;
import BP.WF.Nodes;

 /** 
  节点方向
  
 */
public class Directions extends Entities
{
	/** 
	 节点方向
	 
	*/
	public Directions()
	{
	}
	/** 
	 方向
	 
	 @param flowNo
	 * @throws Exception 
	*/
	public Directions(String flowNo) throws Exception
	{
		this.Retrieve(DirectionAttr.FK_Flow, flowNo);
	}
	/** 
	 节点方向
	 
	 @param NodeID 节点ID
	 @param dirType 类型
	 * @throws Exception 
	*/
	public Directions(int NodeID) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(DirectionAttr.Node,NodeID);
		 
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	 
	*/
	@Override
	public Entity getGetNewEntity()
	{
		return new Direction();
	}
	/** 
	 此节点的转向方向集合
	 
	 @param nodeID 此节点的ID
	 @param isLifecyle 是不是判断在节点的生存期内		 
	 @return 转向方向集合(ToNodes) 
	 * @throws Exception 
	*/
	public final Nodes GetHisToNodes(int nodeID, boolean isLifecyle) throws Exception
	{
		Nodes nds = new Nodes();
		QueryObject qo = new QueryObject(nds);
		qo.AddWhereInSQL(NodeAttr.NodeID,"SELECT ToNode FROM WF_Direction WHERE Node="+nodeID);
		qo.DoQuery();
		return nds;
	}
	/** 
	 转向此节点的集合的Nodes
	 
	 @param nodeID 此节点的ID
	 @return 转向此节点的集合的Nodes (FromNodes) 
	 * @throws Exception 
	*/
	public final Nodes GetHisFromNodes(int nodeID) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(DirectionAttr.ToNode,nodeID);
		qo.DoQuery();
		Nodes ens = new Nodes();
		for(Direction en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getNode()));
		}
		return ens;
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Direction> ToJavaList()
	{
		return (java.util.List<Direction>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final java.util.ArrayList<Direction> Tolist()
	{
		java.util.ArrayList<Direction> list = new java.util.ArrayList<Direction>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Direction)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}