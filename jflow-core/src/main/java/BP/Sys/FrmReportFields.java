package BP.Sys;

import BP.DA.*;
import BP.En.*;

/** 
 表单报表设置数据存储表s
 
*/
public class FrmReportFields extends EntitiesMyPK
{

		
	/** 
	 表单报表s
	 
	*/
	public FrmReportFields()
	{
	}
	/** 
	 表单报表s
	 
	 @param fk_mapdata s
	 * @throws Exception 
	*/
	public FrmReportFields(String fk_mapdata) throws Exception
	{
		if (SystemConfig.getIsDebug())
		{
			this.Retrieve(FrmReportFieldAttr.FK_MapData, fk_mapdata);
		}
		else
		{
			this.RetrieveFromCash(FrmReportFieldAttr.FK_MapData, (Object)fk_mapdata);
		}
	}
	/** 
	 得到它的 Entity
	 
	*/
	@Override
	public Entity getGetNewEntity()
	{
		return new FrmReportField();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmReportField> ToJavaList()
	{
		return (java.util.List<FrmReportField>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final java.util.ArrayList<FrmReportField> Tolist()
	{
		java.util.ArrayList<FrmReportField> list = new java.util.ArrayList<FrmReportField>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmReportField)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}