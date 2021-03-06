package BP.Sys;

import java.util.ArrayList;

import BP.DA.*;
import BP.En.*;

/** 
 明细s
 
*/
public class MapDtls extends EntitiesNoName
{

	/** 
	 明细s
	 
	*/
	public MapDtls()
	{
	}
	/** 
	 明细s
	 
	 @param fk_mapdata s
	 * @throws Exception 
	*/
	public MapDtls(String fk_mapdata) throws Exception
	{
		//被周朋去掉. 为什么要过滤 = 0的数据.
		//this.Retrieve(MapDtlAttr.FK_MapData, fk_mapdata, MapDtlAttr.FK_Node, 0, MapDtlAttr.No);
		this.Retrieve(MapDtlAttr.FK_MapData, fk_mapdata);
	}
	/** 
	 得到它的 Entity
	 
	*/
	@Override
	public Entity getGetNewEntity()
	{
		return new MapDtl();
	}


		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapDtl> ToJavaList()
	{
		return (java.util.List<MapDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapDtl> Tolist()
	{
		ArrayList<MapDtl> list = new ArrayList<MapDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapDtl)this.get(i));
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	public static ArrayList<MapDtl> convertMapDtls(Object obj)
	{
		return (ArrayList<MapDtl>) obj;
	}
		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}