package BP.WF.Template;

import BP.DA.DataType;
import BP.En.EnType;
import BP.En.Entity;
import BP.En.Map;
import BP.En.UAC;
import BP.Sys.GroupCtrlType;
import BP.Sys.GroupField;
import BP.Sys.GroupFieldAttr;
import BP.WF.Node;

/** 
 节点表单组件
 
*/
public class FrmNodeComponent extends Entity
{

		///#region 公共属性
	/** 
	 节点属性.
	 
	*/
	public final String getNo()
	{
		return "ND" + this.getNodeID();
	}
	public final void setNo(String value)
	{
		String nodeID = value.replace("ND", "");
		this.setNodeID(Integer.parseInt(nodeID));
	}
	/** 
	 节点ID
	 
	*/
	public final int getNodeID()
	{
		return this.GetValIntByKey(NodeAttr.NodeID);
	}
	public final void setNodeID(int value)
	{
		this.SetValByKey(NodeAttr.NodeID, value);
	}

		///#endregion


		
	/** 
	 控制
	 
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsUpdate=true;
		//uac.OpenForSysAdmin();
		uac.IsDelete = false;
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 重写主键
	 
	*/
	@Override
	public String getPK()
	{
		return "NodeID";
	}
	/** 
	 节点表单组件
	 
	*/
	public FrmNodeComponent()
	{
	}
	/** 
	 节点表单组件
	 
	 @param no
	 * @throws Exception 
	*/
	public FrmNodeComponent(String mapData) throws Exception
	{
		String mapdata = mapData.replace("ND", "");
		if (DataType.IsNumStr(mapdata) == false)
		{
		  //  this.HisFrmNodeComponentSta = FrmNodeComponentSta.Disable;
			return;
		}

		try
		{
			this.setNodeID(Integer.parseInt(mapdata));
		}
		catch (java.lang.Exception e)
		{
			return;
		}
		this.Retrieve();
	}
	/** 
	 节点表单组件
	 
	 @param no
	 * @throws Exception 
	*/
	public FrmNodeComponent(int nodeID) throws Exception
	{
		this.setNodeID(nodeID);
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

		Map map = new Map("WF_Node", "节点表单组件");
		map.Java_SetEnType(EnType.Sys);

		map.AddTBIntPK(NodeAttr.NodeID, 0, "节点ID", true, true);
		map.AddTBString(NodeAttr.Name, null, "节点名称", true,true, 0, 100, 10);

		FrmWorkCheck fwc = new FrmWorkCheck();
		map.AddAttrs(fwc.getEnMap().getAttrs());

		FrmSubFlow subflow = new FrmSubFlow();
		map.AddAttrs(subflow.getEnMap().getAttrs());

		FrmThread thread = new FrmThread();
		map.AddAttrs(thread.getEnMap().getAttrs());

			//轨迹组件.
		FrmTrack track = new FrmTrack();
		map.AddAttrs(track.getEnMap().getAttrs());

			//流转自定义组件.
		FrmTransferCustom ftt = new FrmTransferCustom();
		map.AddAttrs(ftt.getEnMap().getAttrs());

		this.set_enMap(map);
		return this.get_enMap();
	}

	@Override
	protected boolean beforeUpdate() throws Exception
	{
		GroupField gf = new GroupField();


			///#region 审核组件.
		FrmWorkCheck fwc = new FrmWorkCheck(this.getNodeID());
		fwc.Copy(this);

		if (fwc.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "FWC" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "FWC" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("FWC" + this.getNo());
				gf.setCtrlType(GroupCtrlType.FWC);
				gf.setLab("审核组件");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}


		FrmSubFlow subflow = new FrmSubFlow(this.getNodeID());
		subflow.Copy(this);

		if (subflow.getHisFrmSubFlowSta() == FrmSubFlowSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "SubFlow" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "SubFlow" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("SubFlow" + this.getNo());
				gf.setCtrlType(GroupCtrlType.SubFlow);
				gf.setLab("父子流程组件"); 
				gf.setIdx(0); 
				gf.Insert(); //插入.
			}
		}

		FrmTrack track = new FrmTrack(this.getNodeID());
		track.Copy(this);
		if (track.getFrmTrackSta() == FrmTrackSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "FrmTrack" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "FrmTrack" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("FrmTrack" + this.getNo());
				gf.setCtrlType(GroupCtrlType.Track);
				gf.setLab("轨迹");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}

		FrmThread thread = new FrmThread(this.getNodeID());
		thread.Copy(this);

		if (thread.getFrmThreadSta() == FrmThreadSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "FrmThread" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "FrmThread" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("FrmThread" + this.getNo());
				gf.setCtrlType(GroupCtrlType.Thread);
				gf.setLab("子线程");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}

		FrmTransferCustom ftc = new FrmTransferCustom(this.getNodeID());
		ftc.Copy(this);

		if (ftc.getFTCSta() == FTCSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "FrmFTC" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "FrmFTC" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("FrmFTC" + this.getNo());
				gf.setCtrlType(GroupCtrlType.FTC);
				gf.setLab("流转自定义");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}

		return super.beforeUpdate();
	}
	
	@Override
	 protected  void afterInsertUpdateAction() throws Exception
    {
        Node fl = new Node();
        fl.setNodeID(this.getNodeID());
        fl.RetrieveFromDBSources();
        fl.Update();

        super.afterInsertUpdateAction();
    }

}