package BP.Sys;

import java.util.ArrayList;

import BP.En.EntitiesMyPK;
import BP.En.Entity;

/** 
 表单元素扩展DBs
 
*/
public class FrmEleDBs extends EntitiesMyPK
{
	/** 
	 表单元素扩展DBs
	 
	*/
	public FrmEleDBs()
	{
	}
	/** 
	 表单元素扩展DBs
	 
	 @param fk_mapdata
	 @param pkval
	 * @throws Exception 
	*/
	public FrmEleDBs(String fk_mapdata, String pkval) throws Exception
	{
		this.Retrieve(FrmEleDBAttr.FK_MapData, fk_mapdata, FrmEleDBAttr.EleID, pkval);
	}
	/** 
	 表单元素扩展DBs
	 
	 @param fk_mapdata s
	 * @throws Exception 
	*/
	public FrmEleDBs(String fk_mapdata) throws Exception
	{
		if (SystemConfig.getIsDebug())
		{
			this.Retrieve(FrmLineAttr.FK_MapData, fk_mapdata);
		}
		else
		{
			this.RetrieveFromCash(FrmLineAttr.FK_MapData, (Object)fk_mapdata);
		}
	}
	/** 
	 得到它的 Entity
	 
	*/
	@Override
	public Entity getGetNewEntity()
	{
		return new FrmEleDB();
	}
	public static ArrayList<FrmEleDB> convertFrmEleDBs(Object obj)
	{
		return (ArrayList<FrmEleDB>) obj;
	}
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmEleDB> ToJavaList()
	{
		return (java.util.List<FrmEleDB>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmEleDB> Tolist()
	{
		ArrayList<FrmEleDB> list = new ArrayList<FrmEleDB>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmEleDB)this.get(i));
		}
		return list;
	}
}