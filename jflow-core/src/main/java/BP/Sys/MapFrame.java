package BP.Sys;

import BP.DA.*;
import BP.En.*;
import BP.WF.DotNetToJavaStringHelper;

/** 
 框架
 
*/
public class MapFrame extends EntityMyPK
{

		///#region 属性
	@Override
	public UAC getHisUAC() throws Exception
	{
		UAC uac = new UAC();
		if (BP.Web.WebUser.getNo().equals("admin"))
		{
			uac.IsDelete = true;
			uac.IsUpdate = true;
		}
		return uac;
	}

	/** 
	 是否自适应大小
	 
	*/
	public final boolean getIsAutoSize()
	{
		return this.GetValBooleanByKey(MapFrameAttr.IsAutoSize);
	}
	public final void setIsAutoSize(boolean value)
	{
		this.SetValByKey(MapFrameAttr.IsAutoSize, value);
	}
	
	public final int getRowIdx()
	{
		return 0;
		 
	}
	
	
	/** 
	 名称
	 
	*/
	public final String getName()
	{
		return this.GetValStrByKey(MapFrameAttr.Name);
	}
	public final void setName(String value)
	{
		this.SetValByKey(MapFrameAttr.Name, value);
	}
	public final String getNoOfObj()
	{
		return this.GetValStrByKey(FrmEleAttr.EleID);
	}
	/** 
	 连接
	 
	*/
	public final String getURL()
	{
		String s= this.GetValStrByKey(MapFrameAttr.URL);
		if (DotNetToJavaStringHelper.isNullOrEmpty(s))
		{
			return "http://ccflow.org";
		}
		return s;
	}
	public final void setURL(String value)
	{
		this.SetValByKey(MapFrameAttr.URL, value);
	}
	/** 
	 高度
	 
	*/
	public final float getH()
	{
		return this.GetValFloatByKey(MapFrameAttr.H, 700);

	}
	public final void setH(float value)
	{
		this.SetValByKey(MapFrameAttr.H, value);
	}
	/** 
	 宽度
	 
	*/
	public final float getW()
	{
		return this.GetValFloatByKey(MapFrameAttr.W);
	}
	public final void setW(float value)
	{
		this.SetValByKey(MapFrameAttr.W, value);
	}
	
	/** 
	 X
	 
	*/
	public final float getX()
	{
		return this.GetValFloatByKey(MapFrameAttr.X);
	}
	public final void setX(float value)
	{
		this.SetValByKey(MapFrameAttr.X, value);
	}
	
	
	/** 
	 Y
	 
	*/
	public final float getY()
	{
		return this.GetValFloatByKey(MapFrameAttr.Y);
	}
	public final void setY(float value)
	{
		this.SetValByKey(MapFrameAttr.Y, value);
	}
	
	
	
	public final String getEleType()
	{
		return this.GetValStrByKey(MapFrameAttr.EleType);
	}
	public final void setEleType(String value)
	{
		this.SetValByKey(MapFrameAttr.EleType, value);
	}
	
	public final String getFrmID()
	{
		return this.GetValStrByKey(MapFrameAttr.FrmID);
	}
	public final void setFrmID(String value)
	{
		this.SetValByKey(MapFrameAttr.FrmID, value);
	}
	
	public boolean IsUse = false;
	public final String getFK_MapData()
	{
		return this.GetValStrByKey(MapFrameAttr.FK_MapData);
	}
	public final void setFK_MapData(String value)
	{
		this.SetValByKey(MapFrameAttr.FK_MapData, value);
	}

  
	/** 
	 框架
	 
	*/
	public MapFrame()
	{
	}
	/** 
	 框架
	 
	 @param no
	 * @throws Exception 
	*/
	public MapFrame(String mypk) throws Exception
	{
		this.setMyPK( mypk);
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
		Map map = new Map("Sys_MapFrame", "框架");//Sys_MapFrame
		map.Java_SetDepositaryOfEntity(Depositary.None);
		map.Java_SetDepositaryOfMap(Depositary.Application);
		map.Java_SetEnType(EnType.Sys);

		map.AddMyPK();
		map.AddTBString(MapFrameAttr.FK_MapData, null, "表单ID", true, true, 0, 100, 20);
		map.AddTBString(MapFrameAttr.Name, null, "名称", true, false, 0, 200, 20,true);
		map.AddTBString(MapFrameAttr.URL, null, "URL", true, false, 0, 3000, 20, true);
		map.AddTBString(MapFrameAttr.FrameURL, null, "URL", true, false, 0, 3000, 20, true);

		map.AddTBFloat(FrmEleAttr.X, 5, "X", true, false);
        map.AddTBFloat(FrmEleAttr.Y, 5, "Y", false, false);

        map.AddTBFloat(FrmEleAttr.H, 20, "H", true, false);
        map.AddTBFloat(FrmEleAttr.W, 20, "W", false, false);

		map.AddBoolean(MapFrameAttr.IsAutoSize, true, "是否自动设置大小", false, false);

		map.AddTBString(FrmEleAttr.EleType, null, "类型", false, false, 0, 50, 20, true);
		map.AddTBInt(MapFrameAttr.UrlSrcType, 0, "URL来源", true, false);

		   // map.AddTBInt(MapFrameAttr.RowIdx, 99, "位置", false, false);
		   // map.AddTBInt(MapFrameAttr.GroupID, 0, "GroupID", false, false);

		map.AddTBString(FrmBtnAttr.GUID, null, "GUID", false, false, 0, 128, 20);

		this.set_enMap(  map);
		return this.get_enMap();
	}

	/**
	 插入之后增加一个分组.	 
	 * @throws Exception 
	*/
	@Override
	protected void afterInsert() throws Exception
	{
		GroupField gf = new GroupField();
		gf.setFrmID(  this.getFK_MapData());
		gf.setCtrlID( this.getMyPK());
		gf.setCtrlType("Frame");
		gf.setLab(this.getName());
		gf.setIdx(0);
		gf.Insert(); //插入.

		super.afterInsert();
	}
	
	 @Override
	 protected   boolean beforeUpdate() throws Exception
     {
         GroupField gf = new GroupField();
         
         gf.Retrieve(GroupFieldAttr.CtrlID, this.getMyPK());
          gf.setLab(this.getName());
         gf.Update();

         return super.beforeUpdate();
     }
	 
	/** 
	 删除之后的操作
	 * @throws Exception 
	 
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		GroupField gf = new GroupField();
		gf.Delete(GroupFieldAttr.CtrlID, this.getMyPK());

		super.afterDelete();
	}

		///#endregion
}